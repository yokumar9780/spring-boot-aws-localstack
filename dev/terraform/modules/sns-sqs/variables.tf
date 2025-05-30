variable "account_id" {
  description = "aws account id."
  type        = string
}

variable "tags" {
  description = "A map of tags to assign to the bucket."
  type = map(string)
  default = {}
}

variable "sqs_name" {
  description = "The name for the SQS queue."
  type        = string
}
variable "sns_name" {
  description = "The name for the sns subscription."
  type        = string
}