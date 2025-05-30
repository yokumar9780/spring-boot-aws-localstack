# AWS S3 Bucket
resource "aws_s3_bucket" "my_bucket" {
  #count = var.environment == "aws" ? 1 : 0 # Only create if environment is "aws"
  bucket = var.bucket_name
  tags   = var.tags
  #provider = var.environment == "aws" ? aws.default : aws.localstack
}

resource "aws_s3_bucket_ownership_controls" "ownership" {
  bucket = aws_s3_bucket.my_bucket.id

  rule {
    object_ownership = "BucketOwnerEnforced" # disables ACLs
  }
}


resource "aws_s3_bucket_policy" "bucket_policy" {
  bucket = aws_s3_bucket.my_bucket.id
  policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Sid    = "AllowAccountAccess"
        Effect = "Allow"
        Principal = {
          # bucket policy allowing read/write access for your account
          AWS = "arn:aws:iam::${var.account_id}:root"
        }
        Action = [
          "*"
        ]
        Resource = "${aws_s3_bucket.my_bucket.arn}/*"

      }
    ]
  })
}