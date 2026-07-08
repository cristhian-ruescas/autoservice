output "cluster_name" {
  description = "Nome do cluster Kubernetes provisionado."
  value       = kind_cluster.this.name
}

output "kubeconfig_path" {
  description = "Caminho do kubeconfig gerado pelo kind."
  value       = kind_cluster.this.kubeconfig_path
}

output "namespace" {
  description = "Namespace da aplicação."
  value       = kubernetes_namespace.this.metadata[0].name
}

output "app_service" {
  description = "Service interno da aplicação."
  value       = "${kubernetes_service.app.metadata[0].name}.${kubernetes_namespace.this.metadata[0].name}.svc.cluster.local"
}

output "port_forward_hint" {
  description = "Comando para acessar a aplicação localmente."
  value       = "kubectl -n ${kubernetes_namespace.this.metadata[0].name} port-forward svc/${kubernetes_service.app.metadata[0].name} 8088:80"
}
