# OlympusSpring (Java Edition)

OlympusSpring is a backend for the [OlympusBlog](https://github.com/sentrionic/OlympusBlog) stack using [Spring](https://spring.io/).

## Stack

- Spring related packages for everything
- PostgreSQL
- Redis to store the sessions
- S3 to store files
- Gmail for sending password reset mails.
- [Mapstruct](https://mapstruct.org/) for easier mapping from DTOs to entities
- [Lombok](https://projectlombok.org/) to generate boilerplate

## Getting started

1. Install Java 17 and Maven
2. Clone this repository
3. Install Postgres and Redis.
4. Open the project in IntelliJ to get all the dependencies.
5. Rename `appsettings.properties.example` in `src/main/resources` to `appsettings.properties`
   and fill out the values. AWS is only required if you want file upload,
   Gmail if you want to send reset emails.
6. Run `mvn spring-boot:run`.

### Tests

Examples on how tests would be written are in the `test` directory.

Tests in the `repository` directory additionally need a running test db to work.

## Credits

- [Spring Reddit Clone](https://programmingtechie.com/2020/05/14/building-a-reddit-clone-with-spring-boot-and-angular/): Based on this tutorial series.
