# AWS provider configuration for Localstack
provider "aws" {
  alias = "aws" # Explicit alias for clarity when passing to modules
  region = "eu-west-1"

}