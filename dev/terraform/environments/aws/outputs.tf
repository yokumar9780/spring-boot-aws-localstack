# --- Outputs ---
output "s3_bucket_name" {
  value = module.s3_bucket.s3_bucket_name_created
}

output "s3_bucket_arn_created" {
  value = module.s3_bucket.s3_bucket_arn_created
}
output "account_id" {
  value = data.aws_caller_identity.current.account_id
}