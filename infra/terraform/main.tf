########################################
# Cluster Kubernetes (kind) - reproduzível localmente.
# Para provedores gerenciados (EKS/GKE/AKS), troque este recurso pelo
# módulo do provedor e ajuste o bloco "provider kubernetes".
########################################
resource "kind_cluster" "this" {
  name           = var.cluster_name
  wait_for_ready = true

  kind_config {
    kind        = "Cluster"
    api_version = "kind.x-k8s.io/v1alpha4"

    node {
      role = "control-plane"

      kubeadm_config_patches = [
        "kind: InitConfiguration\nnodeRegistration:\n  kubeletExtraArgs:\n    node-labels: \"ingress-ready=true\"\n"
      ]

      extra_port_mappings {
        container_port = 80
        host_port      = 80
      }
    }

    node {
      role = "worker"
    }
  }
}

provider "kubernetes" {
  host                   = kind_cluster.this.endpoint
  cluster_ca_certificate = kind_cluster.this.cluster_ca_certificate
  client_certificate     = kind_cluster.this.client_certificate
  client_key             = kind_cluster.this.client_key
}

resource "kubernetes_namespace" "this" {
  metadata {
    name = var.namespace
    labels = {
      "app.kubernetes.io/part-of" = "autoservice"
    }
  }
}

########################################
# Configuração da aplicação
########################################
resource "kubernetes_config_map" "app" {
  metadata {
    name      = "autoservice-config"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_URL                       = "jdbc:postgresql://autoservice-postgres:5432/${var.postgres_db}"
    APP_BASE_URL                                = var.app_base_url
    MAIL_HOST                                   = "mailhog"
    MAIL_PORT                                   = "1025"
    MAIL_SMTP_AUTH                              = "false"
    MAIL_SMTP_STARTTLS                          = "false"
    MAIL_FROM                                   = "orcamentos@autoservice.local"
    AUTOSERVICE_NOTIFICACAO_SIMULADA_HABILITADA = "true"
    JAVA_OPTS                                   = "-XX:MaxRAMPercentage=75.0"
  }
}

resource "kubernetes_secret" "app" {
  metadata {
    name      = "autoservice-secret"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  data = {
    SPRING_DATASOURCE_USERNAME          = var.postgres_user
    SPRING_DATASOURCE_PASSWORD          = var.postgres_password
    AUTOSERVICE_JWT_SECRET              = var.jwt_secret
    MAIL_USERNAME                       = ""
    MAIL_PASSWORD                       = ""
    AUTOSERVICE_NOTIFICACAO_WEBHOOK_URL = ""
  }

  type = "Opaque"
}

########################################
# Banco de dados PostgreSQL
########################################
resource "kubernetes_secret" "postgres" {
  metadata {
    name      = "autoservice-postgres-secret"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  data = {
    POSTGRES_DB       = var.postgres_db
    POSTGRES_USER     = var.postgres_user
    POSTGRES_PASSWORD = var.postgres_password
  }

  type = "Opaque"
}

resource "kubernetes_persistent_volume_claim" "postgres" {
  metadata {
    name      = "autoservice-postgres-pvc"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  spec {
    access_modes = ["ReadWriteOnce"]
    resources {
      requests = {
        storage = "2Gi"
      }
    }
  }

  wait_until_bound = false
}

resource "kubernetes_stateful_set" "postgres" {
  metadata {
    name      = "autoservice-postgres"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "autoservice-postgres"
    }
  }

  spec {
    service_name = "autoservice-postgres"
    replicas     = 1

    selector {
      match_labels = {
        "app.kubernetes.io/name" = "autoservice-postgres"
      }
    }

    template {
      metadata {
        labels = {
          "app.kubernetes.io/name" = "autoservice-postgres"
        }
      }

      spec {
        container {
          name  = "postgres"
          image = "postgres:15"

          port {
            container_port = 5432
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.postgres.metadata[0].name
            }
          }

          env {
            name  = "PGDATA"
            value = "/var/lib/postgresql/data/pgdata"
          }

          resources {
            requests = {
              cpu    = "100m"
              memory = "256Mi"
            }
            limits = {
              cpu    = "500m"
              memory = "512Mi"
            }
          }

          volume_mount {
            name       = "postgres-data"
            mount_path = "/var/lib/postgresql/data"
          }
        }

        volume {
          name = "postgres-data"
          persistent_volume_claim {
            claim_name = kubernetes_persistent_volume_claim.postgres.metadata[0].name
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "postgres" {
  metadata {
    name      = "autoservice-postgres"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  spec {
    selector = {
      "app.kubernetes.io/name" = "autoservice-postgres"
    }
    port {
      port        = 5432
      target_port = 5432
    }
    cluster_ip = "None"
  }
}

########################################
# Aplicação
########################################
resource "kubernetes_deployment" "app" {
  metadata {
    name      = "autoservice-app"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "autoservice"
    }
  }

  spec {
    replicas = var.app_replicas

    selector {
      match_labels = {
        "app.kubernetes.io/name" = "autoservice"
      }
    }

    template {
      metadata {
        labels = {
          "app.kubernetes.io/name" = "autoservice"
        }
      }

      spec {
        container {
          name  = "autoservice"
          image = var.app_image

          port {
            name           = "http"
            container_port = 8088
          }

          env_from {
            config_map_ref {
              name = kubernetes_config_map.app.metadata[0].name
            }
          }

          env_from {
            secret_ref {
              name = kubernetes_secret.app.metadata[0].name
            }
          }

          resources {
            requests = {
              cpu    = "250m"
              memory = "512Mi"
            }
            limits = {
              cpu    = "1"
              memory = "1Gi"
            }
          }

          startup_probe {
            http_get {
              path = "/actuator/health/liveness"
              port = "http"
            }
            failure_threshold = 30
            period_seconds    = 5
          }

          liveness_probe {
            http_get {
              path = "/actuator/health/liveness"
              port = "http"
            }
            initial_delay_seconds = 20
            period_seconds        = 15
          }

          readiness_probe {
            http_get {
              path = "/actuator/health/readiness"
              port = "http"
            }
            initial_delay_seconds = 20
            period_seconds        = 10
          }
        }
      }
    }
  }
}

resource "kubernetes_service" "app" {
  metadata {
    name      = "autoservice-app"
    namespace = kubernetes_namespace.this.metadata[0].name
    labels = {
      "app.kubernetes.io/name" = "autoservice"
    }
  }

  spec {
    selector = {
      "app.kubernetes.io/name" = "autoservice"
    }
    port {
      name        = "http"
      port        = 80
      target_port = "http"
    }
    type = "ClusterIP"
  }
}

resource "kubernetes_horizontal_pod_autoscaler_v2" "app" {
  metadata {
    name      = "autoservice-app"
    namespace = kubernetes_namespace.this.metadata[0].name
  }

  spec {
    scale_target_ref {
      api_version = "apps/v1"
      kind        = "Deployment"
      name        = kubernetes_deployment.app.metadata[0].name
    }

    min_replicas = var.hpa_min_replicas
    max_replicas = var.hpa_max_replicas

    metric {
      type = "Resource"
      resource {
        name = "cpu"
        target {
          type                = "Utilization"
          average_utilization = var.hpa_cpu_target
        }
      }
    }

    metric {
      type = "Resource"
      resource {
        name = "memory"
        target {
          type                = "Utilization"
          average_utilization = var.hpa_memory_target
        }
      }
    }
  }
}
