# Spring Boot AWS LocalStack Example

This project demonstrates how to use Spring Boot with AWS services (S3, SNS, SQS, SES) locally using [LocalStack](https://github.com/localstack/localstack) and also supports real AWS deployments via Terraform.

---

## Prerequisites

- [Docker](https://www.docker.com/products/docker-desktop)
- [AWS CLI](https://aws.amazon.com/cli/) (for AWS and LocalStack CLI commands)
- [Terraform](https://www.terraform.io/downloads)
- [Java 21](https://openjdk.org/)
- [Maven](https://maven.apache.org/download.cgi)

---

## 1. Start LocalStack and MailHog (SMTP)

From the project root, run:

```powershell
cd dev/docker-compose
# Start LocalStack and MailHog
# (use 'docker compose' if 'docker-compose' is not available)
docker-compose up -d
```

This starts LocalStack and MailHog in the background.

Check LocalStack health:

```powershell
curl http://localhost:4566/_localstack/health
```

MailHog UI is available at: [http://localhost:8025](http://localhost:8025)

---

## 2. Provision AWS Resources with Terraform

All Terraform files are under `dev/terraform/`.

### Initialize Terraform

```powershell
cd ../terraform/environments/localstack
terraform init
```

### Apply for LocalStack

```powershell
terraform apply -var="environment=localstack" --auto-approve
```

### Apply for AWS

> **Note:** For AWS, ensure your AWS credentials are configured (via `aws configure` or environment variables).

```powershell
cd ../aws
terraform init
terraform apply -var="environment=aws" --auto-approve
```

---

## 3. S3 Endpoints (LocalStack)

### Create a bucket

```powershell
aws --endpoint-url=http://localhost:4566 s3 mb s3://test-bucket
```

### List buckets

```powershell
aws --endpoint-url=http://localhost:4566 s3 ls
```

---

## 4. SNS/SES Commands

### AWS CLI (real AWS)

```powershell
aws ses verify-email-identity --email-address your.email@example.com --region eu-west-1
aws sns publish --topic-arn arn:aws:sns:eu-west-1:YOUR_AWS_ACCOUNT_ID:notifications_topic_sns --message "hello world" --subject "hello"
```

### LocalStack CLI

```powershell
aws --endpoint-url=http://localhost:4566 ses verify-email-identity --email-address your.email@example.com --region eu-west-1
aws --endpoint-url=http://localhost:4566 sns publish --topic-arn arn:aws:sns:eu-west-1:000000000000:notifications_topic_sns --message "hello world" --subject "hello"
```

---

## 5. Running the Spring Boot Application

Build and run the app from the project root:

```powershell
mvn clean install
mvn spring-boot:run
```

The app will start on [http://localhost:8080](http://localhost:8080).

---

## 6. Example API Endpoints

- **Upload File:**  
  `curl -X POST -F "file=@/path/to/your/local/file.txt" http://localhost:8080/s3/upload`

- **List Files:**  
  `curl http://localhost:8080/s3/list`

- **Download File:**  
  `curl -O http://localhost:8080/s3/download/YOUR_UPLOADED_FILE_NAME.txt`

- **Delete File:**  
  `curl -X DELETE http://localhost:8080/s3/delete/YOUR_UPLOADED_FILE_NAME.txt`

---

## 7. Clean Up

- **Stop Spring Boot App:**  
  Press `Ctrl+C` in the terminal.

- **Stop LocalStack and MailHog:**

  ```powershell
  cd dev/docker-compose
  docker-compose down
  ```

- **Destroy Terraform Resources:**

  - For LocalStack:

    ```powershell
    cd ../terraform/environments/localstack
    terraform destroy -var="environment=localstack" --auto-approve
    ```

  - For AWS:
    ```powershell
    cd ../terraform/environments/aws
    terraform destroy -var="environment=aws" --auto-approve
    ```

---

