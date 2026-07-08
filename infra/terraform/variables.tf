variable "cluster_name" {
  description = "Nome do cluster Kubernetes (kind)."
  type        = string
  default     = "autoservice"
}

variable "namespace" {
  description = "Namespace onde a aplicação será provisionada."
  type        = string
  default     = "autoservice"
}

variable "app_image" {
  description = "Imagem Docker da aplicação (registry/nome:tag)."
  type        = string
  default     = "autoservice:latest"
}

variable "app_replicas" {
  description = "Número inicial de réplicas da aplicação."
  type        = number
  default     = 2
}

variable "hpa_min_replicas" {
  description = "Mínimo de réplicas para o HPA."
  type        = number
  default     = 2
}

variable "hpa_max_replicas" {
  description = "Máximo de réplicas para o HPA."
  type        = number
  default     = 6
}

variable "hpa_cpu_target" {
  description = "Meta de utilização de CPU (%) para o HPA."
  type        = number
  default     = 70
}

variable "hpa_memory_target" {
  description = "Meta de utilização de memória (%) para o HPA."
  type        = number
  default     = 80
}

variable "postgres_db" {
  description = "Nome do banco PostgreSQL."
  type        = string
  default     = "autoservice"
}

variable "postgres_user" {
  description = "Usuário do PostgreSQL."
  type        = string
  default     = "postgres"
}

variable "postgres_password" {
  description = "Senha do PostgreSQL."
  type        = string
  default     = "postgres"
  sensitive   = true
}

variable "jwt_secret" {
  description = "Segredo usado para assinar tokens JWT."
  type        = string
  default     = "troque-por-um-segredo-forte-e-aleatorio-em-producao"
  sensitive   = true
}

variable "app_base_url" {
  description = "Base URL pública da aplicação (usada nos links de orçamento)."
  type        = string
  default     = "http://autoservice.local"
}
