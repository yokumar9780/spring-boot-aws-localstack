# AWS provider configuration for Localstack
provider "aws" {
  alias = "localstack" # Explicit alias for clarity when passing to modules
  region                      = "eu-west-1"
  access_key                  = "test"
  secret_key                  = "test"
  s3_use_path_style           = true
  skip_credentials_validation = true
  skip_metadata_api_check     = true
  skip_requesting_account_id  = true
  endpoints {
    s3  = "http://localhost:4566"
    sqs = "http://localhost:4566"
    sns = "http://localhost:4566"
    ses = "http://localhost:4566"
  }
}