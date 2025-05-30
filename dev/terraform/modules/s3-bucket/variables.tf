variable "bucket_name" {
  description = "The name for the S3 bucket."
  type        = string
}

variable "account_id" {
  description = "aws account id."
  type        = string
}

variable "tags" {
  description = "A map of tags to assign to the bucket."
  type = map(string)
  default = {}
}

