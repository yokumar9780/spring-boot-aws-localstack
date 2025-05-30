# --- Variables ---
variable "environment" {
  description = "The deployment environment (aws or localstack)"
  type        = string
  default     = "localstack" # Set your desired default here
  validation {
    condition = contains(["aws", "localstack"], var.environment)
    error_message = "Environment must be 'aws' or 'localstack'."
  }
}

variable "aws_s3_bucket_name" {
  description = "Name of the S3 bucket for AWS (must be globally unique)"
  type        = string
  default     = "my-localstack-demo-s3-bucket" # <<-- IMPORTANT: Change this to a globally unique name!
}

