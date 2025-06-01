# Spring Boot AWS LocalStack Demo

This project demonstrates how to use Spring Boot with AWS services (S3, SQS, SNS, SES) locally using LocalStack, Terraform, and Docker Compose. It includes a REST API for S3 file operations and a notification system using SQS/SNS/SES, all fully automated with CI/CD via GitHub Actions (`ci.yml`).

---

## Features

- **S3 File Operations**: Upload, download, list, and delete files via REST endpoints.
- **Notification System**: Process, list, and purge SQS/SNS/SES notifications.
- **LocalStack Integration**: All AWS resources are emulated locally.
- **Terraform Infrastructure**: S3 buckets, SQS queues, and SNS topics are provisioned via Terraform.
- **MailHog Integration**: SES emails are viewable in MailHog.
- **CI/CD**: Automated build, infrastructure provisioning, and endpoint testing via GitHub Actions (`ci.yml`).

---

## Project Structure

```
- dev/
  - docker-compose/docker-compose.yaml   # LocalStack & MailHog setup
  - terraform/                           # Terraform modules & environments
  - postman/                              # Postman colletion
- src/
  - main/java/com/example/localstack/    # Spring Boot source code
  - main/resources/application.yml       # App configuration
- .github/workflows/ci.yml               # CI/CD pipeline
```

---

## Quick Start

### 1. Prerequisites

- Docker & Docker Compose
- Java 21
- Maven
- Terraform (automatically installed in CI)

### 2. Start LocalStack & MailHog

```sh
docker compose -f dev/docker-compose/docker-compose.yaml up -d
```

- LocalStack dashboard: http://localhost:8081
- MailHog UI: http://localhost:8025

### 3. Provision AWS Resources (LocalStack)

```sh
cd dev/terraform/environments/localstack
terraform init
terraform apply -auto-approve
```

### 4. Build & Run Spring Boot App

```sh
chmod +x ./mvnw
./mvnw clean package -DskipTests
java -jar target/*.jar
```

- App will be available at: http://localhost:8080

### 5. Test S3 Endpoints

```sh
# Upload a file
curl -F "file=@README.md" http://localhost:8080/s3/upload
# List files
curl http://localhost:8080/s3/list
# Download a file
curl -O http://localhost:8080/s3/download/<FILENAME>
# Delete a file
curl -X DELETE http://localhost:8080/s3/delete/<FILENAME>
```

### 6. Test Notification Endpoints

```sh
curl http://localhost:8080/process
curl http://localhost:8080/list
curl http://localhost:8080/purge
```

---

## CI/CD Pipeline (`.github/workflows/ci.yml`)

- Runs on every push, PR, or schedule.
- Steps:
  1. Checks out code, sets up Java & Maven.
  2. Starts LocalStack & MailHog with Docker Compose.
  3. Installs AWS CLI and Terraform, configures dummy AWS credentials.
  4. Provisions AWS resources in LocalStack using Terraform.
  5. Builds and starts the Spring Boot app.
  6. Waits for app readiness, then tests all S3 endpoints with `curl`.
  7. Shuts down Docker Compose.
- Ensures all infrastructure and endpoints work as expected in a local AWS-like environment.

---

## Troubleshooting

- If LocalStack or MailHog fail to start, try:
  ```sh
  docker compose -f dev/docker-compose/docker-compose.yaml down --volumes --remove-orphans
  docker volume prune -f
  ```
- For persistent issues, see the [setup-instructions.md](setup-instructions.md) for detailed troubleshooting.

---

## Clean Up

- Stop the app: `Ctrl+C` in the terminal.
- Stop LocalStack & MailHog:
  ```sh
  docker compose -f dev/docker-compose/docker-compose.yaml down
  ```
- Destroy resources:
  ```sh
  cd dev/terraform/environments/localstack
  terraform destroy -auto-approve
  ```

---

## References

- [LocalStack Docs](https://docs.localstack.cloud/)
- [MailHog](https://github.com/mailhog/MailHog)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Terraform](https://www.terraform.io/)

---

## License

MIT
