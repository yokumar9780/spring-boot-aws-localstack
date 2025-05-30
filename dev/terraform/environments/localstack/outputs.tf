# --- Outputs ---
output "s3_bucket_name" {
  value = module.s3_bucket.s3_bucket_name_created
}

output "s3_bucket_arn_created" {
  value = module.s3_bucket.s3_bucket_arn_created
}
output "account_id" {
  value = local.account_id
}

output "sqs_arn" {
  description = "The name of the SQS queue that was created."
  value       = module.sns_sqs.sqs_arn
}
output "sns_arn" {
  description = "The name of the SNS toipc that was created."
  value       = module.sns_sqs.sns_arn
}