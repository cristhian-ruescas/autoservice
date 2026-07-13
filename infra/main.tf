resource "null_resource" "k3d_cluster" {
  triggers = {
    cluster_name = var.cluster_name
  }

  provisioner "local-exec" {
    command = "k3d cluster create ${self.triggers.cluster_name} --wait"
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

resource "helm_release" "postgres" {
  name             = "postgresql"
  repository       = "https://charts.bitnami.com/bitnami"
  chart            = "postgresql"
  namespace        = var.namespace
  create_namespace = false
  values = [
    yamlencode({
      auth = {
        username = var.postgres_username
        password = var.postgres_password
        database = var.postgres_database
      }
      primary = {
        service = { port = var.postgres_port }
        initdb  = {
          scriptsConfigMap = "autoservice-postgres-init"
        }
      }
    })
  ]
  depends_on = [kubernetes_namespace.app, kubernetes_manifest.postgres_initdb]
}

resource "kubernetes_manifest" "postgres_initdb" {
  manifest = yamldecode(file("${path.module}/../k8s/21-initdb-postgres.yaml"))
  depends_on = [kubernetes_namespace.app]
}

# Deploy application manifests
resource "kubernetes_manifest" "postgres_secret" {
  manifest = yamldecode(file("${path.module}/../k8s/postgres-secret.yaml"))
  depends_on = [kubernetes_namespace.app]
}

resource "kubernetes_manifest" "app_service" {
  manifest = yamldecode(file("${path.module}/../k8s/service.yaml"))
  depends_on = [kubernetes_namespace.app]
}

resource "kubernetes_manifest" "app_deployment" {
  manifest = yamldecode(file("${path.module}/../k8s/deployment.yaml"))
  depends_on = [
    kubernetes_namespace.app,
    helm_release.postgres,
    kubernetes_manifest.postgres_secret
  ]
}

# Deploy metrics server (required for HPA)
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

resource "kubernetes_manifest" "app_hpa" {
  manifest = yamldecode(file("${path.module}/../k8s/hpa.yaml"))
  depends_on = [
    kubernetes_manifest.app_deployment,
    helm_release.metrics_server
  ]
}
