# --- Outputs ---
output "sqs_arn" {
  description = "The name of the SQS queue that was created."
  value       = aws_sqs_queue.notification_queue_sqs.arn
}
output "sns_arn" {
  description = "The name of the SNS toipc that was created."
  value       = aws_sns_topic.notifications_topic_sns.arn
}