# Resource: AWS SQS Queue (EmailQueue)
# This block defines an SQS queue named 'notification_queue'.
resource "aws_sqs_queue" "notification_queue_sqs" {
  name = "notification_queue_sqs"
  tags = var.tags
  # Optional: Add other SQS queue properties if needed, e.g.:
  # delay_seconds          = 0
  # max_message_size       = 262144
  # message_retention_seconds = 345600
  # receive_wait_time_seconds = 0
  # visibility_timeout_seconds = 30
  # fifo_queue             = false
  # content_based_deduplication = false

}
# Resource: AWS SNS Topic (EmailTopic)
# This block defines an SNS topic named 'email-notifications'.
resource "aws_sns_topic" "notifications_topic_sns" {
  name = "notifications_topic_sns"
  tags = var.tags
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
  protocol  = "sqs"
  endpoint = aws_sqs_queue.notification_queue_sqs.arn # References the ARN of the SQS queue created above
  topic_arn = aws_sns_topic.notifications_topic_sns.arn # References the ARN of the SNS topic created above

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
  queue_url = aws_sqs_queue.notification_queue_sqs.id # References the URL of the SQS queue
  policy = jsonencode({
    Version = "2012-10-17"
    Id      = "${aws_sqs_queue.notification_queue_sqs.name}/SQSDefaultPolicy"
    Statement = [
      {
        Sid    = "AllowSNSTopicToSendMessage"
        Effect = "Allow"
        Principal = {
          Service = "sns.amazonaws.com"
        }
        Action   = "sqs:SendMessage"
        Resource = aws_sqs_queue.notification_queue_sqs.arn
        Condition = {
          ArnEquals = {
            "aws:SourceArn" = aws_sns_topic.notifications_topic_sns.arn
          }
        }
      }
    ]
  })
}