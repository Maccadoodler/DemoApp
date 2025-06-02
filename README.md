# Task Average API
This project is a simple Spring Boot REST API that exposes endpoints to manage the calculation of
average task durations.  
It demonstrates creating RESTful endpoints with JSON input/output, basic error handling
and security (Okta) integration. Data is persisted in a database.


## Build and deploy

1. Clone the repository:
   git clone https://github.com/Maccadoodler/DemoApp.git
   cd myproject

2. Configure your database:
    ### Dev
    Uses built in H2 database. But this can be overridden to use a real db as described in QA section.

    ### QA
    Create a MySQL database and update src/main/resources/application-qa.properties:

```bash
   spring.datasource.url=jdbc:mysql://localhost:3306/myprojectdb?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=yourpassword
   spring.sql.init.mode=always
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

3. Build the project:

   ./mvn clean install

4. Run the application:

   Dev -   ./mvn spring-boot:run
   QA -    ./mvn spring-boot:run -Dspring-boot.run.profiles=qa


## Endpoints

### 1. Post Task Duration

**POST** `/task/duration`

- **Description:** updates the duration details for the specified task.
- **Path Parameter:**
    none
- **Body:**
```json
{
  "task": "<taskID>",
  "duration": 12345
}
```
- **Response:**
```bash

  Task <task> processed successfully.

```

### 2. Get Average Duration for a Task

**GET** `/task/average/<taskID>`

- **Description:** Returns the average duration for the specified task.
- **Path Parameter:**
    - `taskID` (string): The ID of the task.

- **Response:**

```json
{
  "task": "<taskID>",
  "average": 12345
}
```

The value is and integer typically measured in units of your choice. 
If no data is available for the task, it may return 404.

## Security
In order to provide authentication, an Oauth identity provider like OKTA should be set up. https://developer.okta.com/

```bash
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://trial-3595923.okta.com/oauth2/default
```
and for HTTPs a cert needs to be placed in the classpath.  This means the enoints are on https://localhost:8443/...

```bash
server.port=8443
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=changeit
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=myserver
```
Unsigned cert can be created using something like this : 
```bash
keytool -genkeypair -alias myserver  -keyalg RSA  -keysize 2048  -storetype PKCS12  
    -keystore keystore.p12 -validity 365   -storepass changeit
```
## Running Unit / H2 Tests

./mvn test

### Postman
A Postman test file in "postman/CoolPlanet.postman_collection.json" can be run in QA or DEV mode to verify 
functionality. Please ensure the "Security Token" collectionstep is configured with 
    * Basic Authentication
    * Okta created 'client_id' as username and 'client_secret' as password.
    
Depending on the deployment set the CoolPlanet environment variable 'base_url' to reflect the required setup.
    eg https://localhost:8443 or http://localhost:8086 depending on your 


## Docker 
To build docker container, find the root directory of the project.
Using for example Docker Desktop, run

```bash
docker build -t spring-app .
docker run -d -p 8086:8086 --name coolplanet spring-app --spring.profiles.active=docker
```
In this profile the https has been removed as the assumption is that the docker container will run behind 
a reverse proxy which should terminate the https.




## Contributing

This is unmonitored as it is a demo.

1. Fork this repo
2. Create your feature branch: `git checkout -b feature/your-feature`
3. Commit your changes
4. Push to the branch: `git push origin feature/your-feature`
5. Open a pull request

## License

This project is licensed under the MIT License.

## Contact

Created by @maccadoodler – This is unmonitored as it is a demo.!