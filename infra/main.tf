locals {
  k8s_dir = "${path.module}/../k8s"

  postgres_manifests = [
    "20-configmap-postgres.yaml",
    "21-initdb-postgres.yaml",
    "22-pvc-postgres.yaml",
    "23-deployment-postgres.yaml",
    "24-service-postgres.yaml",
  ]

  app_manifests_before_deploy = [
    "10-configmap-app.yaml",
    "31-service-app.yaml",
  ]

  create_ghcr_pull_secret = var.ghcr_username != "" && var.ghcr_token != ""

  app_deployment_raw = yamldecode(file("${local.k8s_dir}/30-deployment-app.yaml"))

  app_deployment = merge(local.app_deployment_raw, {
    spec = merge(local.app_deployment_raw.spec, {
      template = merge(local.app_deployment_raw.spec.template, {
        spec = {
          for k, v in local.app_deployment_raw.spec.template.spec : k => v
          if k != "imagePullSecrets" || local.create_ghcr_pull_secret
        }
      })
    })
  })
}

resource "null_resource" "k3d_cluster" {
  triggers = {
    cluster_name = var.cluster_name
  }

  provisioner "local-exec" {
    command = "k3d cluster create ${self.triggers.cluster_name} --wait --agents 1"
  }

  provisioner "local-exec" {
    when    = destroy
    command = "k3d cluster delete ${self.triggers.cluster_name} || true"
  }
}

resource "kubernetes_namespace" "app" {
  metadata {
    name = var.namespace
  }
  depends_on = [null_resource.k3d_cluster]
}

resource "kubernetes_secret" "postgres" {
  metadata {
    name      = "autoservice-postgres-secret"
    namespace = var.namespace
  }
  data = {
    POSTGRES_PASSWORD = var.postgres_password
  }
  depends_on = [kubernetes_namespace.app]
}

resource "kubernetes_secret" "app" {
  metadata {
    name      = "autoservice-app-secret"
    namespace = var.namespace
  }
  data = {
    AUTOSERVICE_JWT_SECRET = var.jwt_secret
    MAIL_USERNAME          = var.mail_username
    MAIL_PASSWORD          = var.mail_password
  }
  depends_on = [kubernetes_namespace.app]
}

resource "kubernetes_secret" "ghcr_pull" {
  count = local.create_ghcr_pull_secret ? 1 : 0

  metadata {
    name      = "ghcr-pull-secret"
    namespace = var.namespace
  }
  type = "kubernetes.io/dockerconfigjson"
  data = {
    ".dockerconfigjson" = jsonencode({
      auths = {
        "ghcr.io" = {
          username = var.ghcr_username
          password = var.ghcr_token
          auth     = base64encode("${var.ghcr_username}:${var.ghcr_token}")
        }
      }
    })
  }
  depends_on = [kubernetes_namespace.app]
}

resource "kubernetes_manifest" "postgres" {
  for_each = toset(local.postgres_manifests)

  manifest = yamldecode(file("${local.k8s_dir}/${each.value}"))

  depends_on = [
    kubernetes_namespace.app,
    kubernetes_secret.postgres,
  ]
}

resource "helm_release" "metrics_server" {
  name             = "metrics-server"
  repository       = "https://kubernetes-sigs.github.io/metrics-server"
  chart            = "metrics-server"
  namespace        = "kube-system"
  create_namespace = true
  take_ownership   = true

  set = [
    {
      name  = "args[0]"
      value = "--kubelet-insecure-tls"
    },
    {
      name  = "args[1]"
      value = "--kubelet-preferred-address-types=InternalIP"
    }
  ]

  depends_on = [null_resource.k3d_cluster]
}

resource "kubernetes_manifest" "app_before_deploy" {
  for_each = var.deploy_app ? toset(local.app_manifests_before_deploy) : toset([])

  manifest = yamldecode(file("${local.k8s_dir}/${each.value}"))

  depends_on = [
    kubernetes_namespace.app,
    kubernetes_secret.app,
    kubernetes_manifest.postgres,
    helm_release.metrics_server,
  ]
}

resource "kubernetes_manifest" "app_deployment" {
  count = var.deploy_app ? 1 : 0

  manifest = local.app_deployment

  depends_on = [
    kubernetes_namespace.app,
    kubernetes_secret.app,
    kubernetes_secret.postgres,
    kubernetes_secret.ghcr_pull,
    kubernetes_manifest.postgres,
    kubernetes_manifest.app_before_deploy,
    helm_release.metrics_server,
  ]
}

resource "kubernetes_manifest" "app_hpa" {
  count = var.deploy_app ? 1 : 0

  manifest = yamldecode(file("${local.k8s_dir}/32-hpa-app.yaml"))

  depends_on = [
    kubernetes_manifest.app_deployment,
    helm_release.metrics_server,
  ]
}
