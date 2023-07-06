# Kotlin / REST version of Spring PetClinic Sample Application (spring-framework-petclinic extend ) 

 backend version of the Spring Petclinic application only provides a REST API implemented by Kotlin. **There is no UI**.
The [spring-petclinic-angular project](https://github.com/spring-petclinic/spring-petclinic-angular) is a Angular front-end application which consumes the REST API.

## Understanding the Spring Petclinic application with a few diagrams

[See the presentation of the Spring Petclinic Framework version](http://fr.slideshare.net/AntoineRey/spring-framework-petclinic-sample-application)

### Petclinic ER Model

![alt petclinic-ermodel](petclinic-ermodel.png)

## Running petclinic locally

### With maven command line
```
git clone https://github.com/junoyoon/spring-petclinic-rest-kotlin.git
cd spring-petclinic-rest-kotlin
./gradle bootRun
```

You can then access petclinic here: [http://localhost:8080/](http://localhost:8080/)

There are actuator health check and info routes as well: 
* [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)
* [http://localhost:8080/actuator/info](http://localhost:8080/actuator/info)

## OpenAPI REST API documentation presented here (after application start):

You can reach the swagger UI with this URL
[http://localhost:8080/](http://localhost:8080/swagger-ui.html).

You then can get the Open API description reaching this URL [localhost:8080/v3/api-docs](localhost:8080/v3/api-docs).

## Screenshot of the Angular client

<img width="1427" alt="spring-petclinic-angular2" src="https://cloud.githubusercontent.com/assets/838318/23263243/f4509c4a-f9dd-11e6-951b-69d0ef72d8bd.png">


## Database configuration

In its default configuration, Petclinic uses an in-memory database (HSQLDB) which
gets populated at startup with data.



## Security configuration
In its default configuration, Petclinic doesn't have authentication and authorization enabled.

### Basic Authentication
In order to use the basic authentication functionality, turn in on from the application.properties file
```
petclinic.security.enable=true
```
This will secure all APIs and in order to access them, basic authentication is required.
Apart from authentication, APIs also require authorization. This is done via roles that a user can have.
The existing roles are listed below with the corresponding permissions 
* OWNER_ADMIN -> OwnerController, PetController, PetTypeController (getAllPetTypes and getPetType), VisitController
* VET_ADMIN   -> PetTypeController, SpecialityController, VetController
* ADMIN       -> UserController

There is an existing user with the username `admin` and password `admin` that has access to all APIs.
 In order to add a new user, please use the following API:
```
POST /api/users
{
    "username": "secondAdmin",
    "password": "password",
    "enabled": true,
    "roles": [
    	{ "name" : "OWNER_ADMIN" }
	]
}
```


