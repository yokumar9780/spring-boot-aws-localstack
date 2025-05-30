locals {
  today_date = formatdate("YYYY-MM-DD", timestamp())
}

data "aws_caller_identity" "current" {}


# --- Module Calls for LocalStack ---

# Call the S3 bucket module
module "s3_bucket" {
  source = "../../modules/s3-bucket" # Path to your S3 bucket module
  bucket_name = "${var.project_name}-${local.today_date}-bucket"
  account_id  = data.aws_caller_identity.current.account_id
  tags = {
    Environment = "Development"
    ManagedBy   = "Terraform"
    Project     = var.project_name
  }
  # Explicitly pass the 'localstack' AWS provider to this module
  providers = {
    aws = aws.aws
  }
}

# Call the SNS/SQS email system module
module "sns_sqs" {
  source = "../../modules/sns-sqs" # Path to your SNS/SQS module
  sqs_name = "${var.project_name}-sqs-queue"
  sns_name = "${var.project_name}-sns-topic"
  tags = {
    Environment = "Development"
    ManagedBy   = "Terraform"
    Project     = var.project_name
  }
  account_id  = data.aws_caller_identity.current.account_id
  #auto_confirm_deliveries = true # Often set to true for LocalStack convenience
  # Explicitly pass the 'localstack' AWS provider to this module
  providers = {
    aws = aws.aws
  }
}