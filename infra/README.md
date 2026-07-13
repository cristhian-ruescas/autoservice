Este diretório contém definições Terraform para provisionar um cluster local (k3d), um namespace Kubernetes, e um PostgreSQL via Helm.

Requisitos:
- Terraform 1.3+
- k3d (https://k3d.io/) instalado e no PATH
- kubectl (opcional para ver recursos)

Usar:
1. Copiar terraform.tfvars.example para terraform.tfvars e ajustar senhas/valores.
2. terraform init
3. terraform apply

Após apply o k3d será criado localmente, o namespace e o PostgreSQL via Helm serão instalados.
Para destruir: terraform destroy
