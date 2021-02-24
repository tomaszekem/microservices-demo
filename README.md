###Abstract
This is a sample demo project demonstrating two microservices communicating with each other 
via Apache Kafka message broker in an asynchronous way.

The projects consist of two services:
1. user-service
2. notification-service

The services are built using Sprint Boot and Java 11.

### user-service
User-service handles bulk operations on users:
- registration
- edition
- deletion (soft deletion)
- read

The service uses an in-memory H2 database (for simplicity as this is a demo project)

### notification-service
Notification-service listens to Kafka topic. When a message from Kafka arrives, the service
sends an email notification to users specified in the message.

###Running with Docker Compose

1. In docker-compose/.env file set **MAIL_USERNAME** and **MAIL_PASSWORD** variables so that
notification-service knows from which account email notifications will be sent.

2. Build the Docker images for the **notification-service**, **user-service** apps by running the following command in root directory of each module.
```bash
./mvnw -ntp -Pprod verify jib:dockerBuild
```
**WARNING - Please make sure that entrypoint.sh, .env,  files have LF line separators - this is necessary for the containers to work**

You can use -DskipTests flag to omit the tests execution.
Once all the services are built, cd into the docker-compose directory and run them:
```bash
docker-compose up
```
### API documentation
API docs are available only for **user-service** as only this service exposes a REST API.
Once the service is running, you can access the docs here:

http://localhost:8081/swagger-ui.html
