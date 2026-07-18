variable "cluster_name" {
  type    = string
  default = "autoservice-local"
}

variable "kubeconfig_path" {
  type        = string
  default     = "~/.kube/config"
  description = "Path to kubeconfig used by Kubernetes/Helm providers. k3d will write here when created locally."
}

variable "namespace" {
  type    = string
  default = "autoservice"
}

variable "postgres_password" {
  type        = string
  sensitive   = true
  default     = "change-me-before-deploy"
  description = "POSTGRES_PASSWORD do Secret autoservice-postgres-secret (mesmo do CI). Usuário/DB ficam no ConfigMap k8s (postgres/autoservice)."
}

variable "jwt_secret" {
  type        = string
  sensitive   = true
  default     = "change-me-before-deploy"
  description = "JWT signing secret. Override in terraform.tfvars (gitignored)."
}

variable "mail_username" {
  type        = string
  default     = ""
  description = "Optional SMTP username."
}

variable "mail_password" {
  type        = string
  sensitive   = true
  default     = ""
  description = "Optional SMTP password. Override in terraform.tfvars (gitignored)."
}

variable "deploy_app" {
  type        = bool
  default     = true
  description = "Se true, aplica ConfigMap/Deployment/Service/HPA da app (mesmos YAMLs do CI). Se false, só cluster + Postgres + metrics-server."
}

variable "ghcr_username" {
  type        = string
  default     = ""
  description = "Usuário GHCR para criar ghcr-pull-secret (mesmo nome do CI). Vazio = Deployment sem imagePullSecrets."
}

variable "ghcr_token" {
  type        = string
  sensitive   = true
  default     = ""
  description = "Token GHCR (read:packages). Vazio = sem pull secret."
}
