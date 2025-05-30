Setup and Run Instructions
Follow these steps to set up and run the Spring Boot S3 application with Localstack and AWS.

1. Project Setup
   Create Project Directory:
   Create a new directory for your project, e.g., spring-boot-s3-app.

Spring Boot Files:
Place the pom.xml, application.properties, S3Config.java, S3Service.java, S3Controller.java, and SpringBootS3Application.java files into the appropriate directory structure:

pom.xml in the root of spring-boot-s3-app/

src/main/resources/application.properties

src/main/java/com/example/springboot_s3_app/S3Config.java

src/main/java/com/example/springboot_s3_app/S3Service.java

src/main/java/com/example/springboot_s3_app/S3Controller.java

src/main/java/com/example/springboot_s3_app/SpringBootS3Application.java

2. Localstack Setup
   Create docker-compose.yml:
   In the root of your spring-boot-s3-app/ directory, create a file named docker-compose.yml and paste the updated content provided above.

Start Localstack:
Open your terminal in the spring-boot-s3-app/ directory and run:

docker-compose up -d


This will start the Localstack container in detached mode. It might take a minute for Localstack services to be ready.

Troubleshooting Device or resource busy or Dashboard access issues:
If you encounter an OSError: [Errno 16] Device or resource busy: '/tmp/localstack' or similar, or cannot access the Localstack dashboard (usually at http://localhost:8080 or http://127.0.0.1:8080, or http://localhost:8081 or http://127.0.0.1:8081):

1. Stop and Remove all related Docker containers:

docker-compose down --volumes --remove-orphans
docker stop $(docker ps -aq)
docker rm $(docker ps -aq)


2. Clean up Docker volumes (use with caution):

docker volume prune -f


3. Perform a full Docker Desktop Reset (Windows/macOS):

Quit Docker Desktop from the system tray.

Restart Docker Desktop.

Go to Docker Desktop Settings -> Troubleshoot -> Clean / Purge data. (WARNING: This deletes ALL Docker images and containers!) Confirm the purge.

Restart Docker Desktop after the purge.

4. Try starting Localstack again:

docker-compose up -d


3. Terraform Setup (Combined)
   Create Terraform Directory:
   Inside your spring-boot-s3-app/ directory, create a new folder named terraform.

Create main.tf:
Inside terraform/, create main.tf and paste the content from the terraform-combined-main-tf code immersive.

Update main.tf Variables:
Important: Before initializing, modify the aws_s3_bucket_name variable's default value in terraform/main.tf to a globally unique name (e.g., your-name-spring-boot-s3-bucket). S3 bucket names must be unique across all AWS accounts.

Initialize Terraform:
Navigate to the terraform/ directory in your terminal:

cd terraform
terraform init


Apply Terraform for Localstack or AWS:

To create resources in Localstack:

terraform apply -var="environment=localstack" --auto-approve


To create resources in AWS:
Note: Ensure your AWS credentials are configured (e.g., via AWS CLI aws configure, environment variables, or IAM roles) for the default AWS provider.

terraform apply -var="environment=aws" --auto-approve


You should see a message indicating the bucket was created.

4. Running the Spring Boot Application
   Configure application.properties:

For Localstack:
Set app.s3.environment=localstack.
Ensure cloud.aws.credentials.access-key and cloud.aws.credentials.secret-key are set to dummy values (e.g., test).

For AWS:
Set app.s3.environment=aws.
Crucially, update cloud.aws.credentials.access-key and cloud.aws.credentials.secret-key in application.properties with your actual AWS IAM user credentials that have S3 access. Also, set cloud.aws.region.static to your desired AWS region.

Build and Run:
Navigate back to the root of your spring-boot-s3-app/ directory in the terminal:

cd .. # If you are in terraform directory
mvn clean install
mvn spring-boot:run


The application will start on http://localhost:8080.

5. Testing the Application
   You can use curl, Postman, or any API client to test the endpoints.

Example Endpoints:

Upload File:

curl -X POST -F "file=@/path/to/your/local/file.txt" http://localhost:8080/s3/upload


Replace /path/to/your/local/file.txt with an actual file on your machine.

List Files:

curl http://localhost:8080/s3/list


Download File:
(First, upload a file and get its name from the list endpoint)

curl -O http://localhost:8080/s3/download/YOUR_UPLOADED_FILE_NAME.txt


Delete File:
(First, upload a file and get its name from the list endpoint)

curl -X DELETE http://localhost:8080/s3/delete/YOUR_UPLOADED_FILE_NAME.txt


6. Clean Up
   Stop Spring Boot App: Press Ctrl+C in the terminal where the app is running.

Stop Localstack:

docker-compose down


Destroy S3 Bucket (Localstack or AWS):
Navigate to the terraform/ directory and run the terraform destroy command for the environment you created the bucket in:

For Localstack:

cd terraform
terraform destroy -var="environment=localstack" --auto-approve


For AWS:

cd terraform
terraform destroy -var="environment=aws" --auto-approve

