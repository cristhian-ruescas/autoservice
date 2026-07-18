output "kubeconfig_path" {
  value       = var.kubeconfig_path
  description = "Path to kubeconfig used by the providers"
}

output "cluster_name" {
  value       = var.cluster_name
  description = "Nome do cluster k3d provisionado"
}

output "namespace" {
  value       = kubernetes_namespace.app.metadata[0].name
  description = "Namespace da aplicação (alinhado ao CI)"
}

output "postgres_secret_name" {
  value       = kubernetes_secret.postgres.metadata[0].name
  description = "Secret do Postgres (autoservice-postgres-secret)"
}

output "app_secret_name" {
  value       = kubernetes_secret.app.metadata[0].name
  description = "Secret da app (autoservice-app-secret)"
}

output "deploy_app" {
  value       = var.deploy_app
  description = "Se a app foi aplicada via Terraform (mesmos manifests do CI)"
}

output "postgres_password" {
  value     = var.postgres_password
  sensitive = true
}

output "next_steps" {
  value = <<-EOT
    Cluster e manifests alinhados ao CI (k8s/ numerados + secrets com os mesmos nomes).
    Validar: kubectl -n ${var.namespace} get pods,svc,hpa
    Kustomize (mesmo conjunto): kubectl apply -k ../k8s
    CI em main/master atualiza a imagem: ghcr.io/<org>/autoservice:sha-<commit>
  EOT
}
