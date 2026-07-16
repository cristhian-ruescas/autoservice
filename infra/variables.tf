variable "cluster_name" {
  type    = string
  default = "autoservice-local"
}

variable "kubeconfig_path" {
  type    = string
  default = "~/.kube/config"
  description = "Path to kubeconfig used by Kubernetes/Helm providers. k3d will write here when created locally."
}

variable "namespace" {
  type    = string
  default = "autoservice"
}

variable "postgres_username" {
  type    = string
  default = "autoservice"
}

variable "postgres_password" {
  type        = string
  sensitive   = true
  default     = "change-me-before-deploy"
  description = "Override in terraform.tfvars (gitignored). Do not commit real passwords."
}

variable "postgres_database" {
  type    = string
  default = "autoservicedb"
}

variable "postgres_port" {
  type    = number
  default = 5432
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
