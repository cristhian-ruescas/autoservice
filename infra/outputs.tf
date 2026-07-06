output "kubeconfig_path" {
  value       = var.kubeconfig_path
  description = "Path to kubeconfig used by the providers"
}

output "postgres_release_name" {
  value       = helm_release.postgres.name
  description = "Helm release name for PostgreSQL"
}

output "postgres_namespace" {
  value = kubernetes_namespace.app.metadata[0].name
}

output "postgres_password" {
  value     = var.postgres_password
  sensitive = true
}
