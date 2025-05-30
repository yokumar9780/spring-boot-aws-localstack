# --- Providers ---
# Default AWS provider configuration (for real AWS)
# This provider will attempt to validate credentials against real AWS.
# Ensure your AWS CLI or environment variables have valid credentials configured
# if you intend to use 'environment = "aws"'.
# Conditionally skip credential validation if targeting localstack to avoid errors.
provider "aws" {
  region = "eu-west-1" # Or your desired AWS region
  alias                      = "default"
  skip_credentials_validation = var.environment == "localstack" # Skip validation if environment is localstack
  skip_requesting_account_id = var.environment == "localstack"
  skip_region_validation     = var.environment == "localstack"
  # AWS credentials are typically managed via AWS CLI config, environment variables, or IAM roles.
  # Do NOT hardcode sensitive credentials here for production use.
}


# AWS provider configuration for Localstack (using an alias)
# These settings ensure the provider interacts correctly with Localstack
# and skips real AWS credential validation.
provider "aws" {
  alias                       = "localstack"
  region = "eu-west-1" # Localstack generally ignores region, but set for consistency
  access_key = "test"      # Dummy credentials for Localstack
  secret_key = "test"      # Dummy credentials for Localstack
  s3_use_path_style = true        # Important for Localstack S3
  skip_credentials_validation = true        # Crucial for Localstack to bypass real STS validation
  #skip_request_md5_check      = true        # Helps with Localstack compatibility
  skip_metadata_api_check = true        # Helps with Localstack compatibility
  skip_requesting_account_id  = true
  endpoints {
    s3 = "http://localhost:4566" # Localstack S3 endpoint
    sns = "http://localhost:4566" # Localstack SNS endpoint
    ses = "http://localhost:4566" # Localstack SES endpoint
  }

}

# --- Resources ---

# AWS S3 Bucket
resource "aws_s3_bucket" "test_bucket" {
  #count = var.environment == "aws" ? 1 : 0 # Only create if environment is "aws"
  bucket = var.aws_s3_bucket_name
  tags = {
    Environment = "Production"
    ManagedBy   = "Terraform"
  }
  provider = var.environment == "aws" ? aws.default : aws.localstack
}

resource "aws_s3_bucket_acl" "aws_bucket_acl" {
  #count  = var.environment == "aws" ? 1 : 0
  bucket   = aws_s3_bucket.test_bucket.id
  acl      = "private"
  provider = var.environment == "aws" ? aws.default : aws.localstack
}


# Resource: AWS SQS Queue (EmailQueue)
# This block defines an SQS queue named 'email-notification-queue'.
resource "aws_sqs_queue" "email_queue" {
  name     = "email-notification-queue"
  provider = var.environment == "aws" ? aws.default : aws.localstack
  # Optional: Add other SQS queue properties if needed, e.g.:
  # delay_seconds          = 0
  # max_message_size       = 262144
  # message_retention_seconds = 345600
  # receive_wait_time_seconds = 0
  # visibility_timeout_seconds = 30
  # fifo_queue             = false
  # content_based_deduplication = false
  tags = {
    Environment = "Production"
    ManagedBy   = "Terraform"
  }
}
# Resource: AWS SNS Topic (EmailTopic)
# This block defines an SNS topic named 'email-notifications'.
resource "aws_sns_topic" "email_topic" {
  name     = "email-notifications"
  provider = var.environment == "aws" ? aws.default : aws.localstack
  # Optional: Add other SNS topic properties if needed, e.g.:
  # display_name = "Email Notifications"
  # policy       = <<POLICY
  # {
  #   "Version": "2012-10-17",
  #   "Statement": [
  #     {
  #       "Effect": "Allow",
  #       "Principal": {
  #         "AWS": "*"
  #       },
  #       "Action": "SNS:Publish",
  #       "Resource": "${aws_sns_topic.email_topic.arn}"
  #     }
  #   ]
  # }
  # POLICY

}

# Resource: AWS SNS Topic Subscription (SnsSubscription)
# This block subscribes the SQS queue to the SNS topic.
# The 'protocol' is 'sqs' and the 'endpoint' is the ARN of the SQS queue.
# The 'topic_arn' is the ARN of the SNS topic.
resource "aws_sns_topic_subscription" "sns_subscription" {
  protocol = "sqs"
  endpoint = aws_sqs_queue.email_queue.arn # References the ARN of the SQS queue created above
  topic_arn = aws_sns_topic.email_topic.arn # References the ARN of the SNS topic created above
  provider = var.environment == "aws" ? aws.default : aws.localstack

  # Optional: Set to true if you want to skip the confirmation step for the subscription.
  # This is generally recommended for automated deployments.
  # If set to false, the SQS queue will receive a confirmation message that needs to be processed.
  # For localstack, auto_confirm_deliveries is often true.
  # For production, consider the security implications of auto-confirmation.
  # In a real-world scenario, you might have a Lambda function or other service
  # that processes the subscription confirmation message.
  # For simple localstack setup, setting it to true is common.
  # auto_confirm_deliveries = true
}

# Optional: Add an SQS Queue Policy to allow the SNS topic to send messages to the queue.
# This is crucial for the SNS-SQS integration to work correctly.
resource "aws_sqs_queue_policy" "email_queue_policy" {
  queue_url = aws_sqs_queue.email_queue.id # References the URL of the SQS queue
  provider = var.environment == "aws" ? aws.default : aws.localstack
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "${aws_sqs_queue.email_queue.name}/SQSDefaultPolicy"
    Statement = [
      {
        Sid    = "AllowSNSTopicToSendMessage"
        Effect = "Allow"
        Principal = {
          Service = "sns.amazonaws.com"
        }
        Action   = "sqs:SendMessage"
        Resource = aws_sqs_queue.email_queue.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.email_topic.arn
          }
        }
      }
    ]
  })
}
