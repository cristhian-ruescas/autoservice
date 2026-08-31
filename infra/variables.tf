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
  type    = string
  default = "change-me-before-deploy"
  description = "Change this in terraform.tfvars for production/use a secret manager"
}

variable "postgres_database" {
  type    = string
  default = "autoservicedb"
}

variable "postgres_port" {
  type    = number
  default = 5432
}
