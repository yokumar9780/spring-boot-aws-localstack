# --- Outputs ---
output "s3_bucket_name_created" {
  description = "The name of the S3 bucket that was created."
  /*value = var.environment == "aws" ? (
    length(aws_s3_bucket.aws_bucket) > 0 ? aws_s3_bucket.aws_bucket[0].id : "No AWS bucket created"
  ) : (
    length(aws_s3_bucket.localstack_bucket) > 0 ? aws_s3_bucket.localstack_bucket[0].id : "No Localstack bucket created"
  )*/
  value = aws_s3_bucket.test_bucket.id
}

output "s3_bucket_arn_created" {
  description = "The ARN of the S3 bucket that was created."
  value       = aws_s3_bucket.test_bucket.arn
  /*value       = var.environment == "aws" ? (
    length(aws_s3_bucket.aws_bucket) > 0 ? aws_s3_bucket.aws_bucket[0].arn : "No AWS bucket ARN"
  ) : (
    length(aws_s3_bucket.localstack_bucket) > 0 ? aws_s3_bucket.localstack_bucket[0].arn : "No Localstack bucket ARN"
  )*/
}