# Project Name

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Description

Provide a brief description of your project here. Explain what the project does, its features, and the main purpose.

## Table of Contents

- [Project Name](#project-name)
  - [Description](#description)
  - [Table of Contents](#table-of-contents)
  - [Prerequisites](#prerequisites)
  - [Installation](#installation)
  - [Running the Application](#running-the-application)
  - [API Endpoints](#api-endpoints)
  - [Testing](#testing)
  - [Contributing](#contributing)
  - [License](#license)

## Prerequisites

List the software and versions that are required to run this project.

- Java 17 or higher
- Maven 3.6.0 or higher
- PostgreSQL Driver

## Installation

### Clone the repository

```bash
git clone https://github.com/assetmanagement-tragiangphan/assetmanagement_backend.git
cd assetmanagement_backend
```

### Set up the database

1. Create a new database in PostgreSQL (or any other database you're using).
2. Update the `application.yml` file with your database configuration.

### Build the project

```bash
mvn clean install
```

## Running the Application

### Using Maven

```bash
mvn spring-boot:run
```

## API Endpoints

##### LOGIN FEATURE
- `POST     /api/v1/auth/signIn` - User is able to login
- `POST     /api/v1/auth/logout` - User is able to logout

##### USER FEATURE
- `POST     /api/v1/users` - Create new user by user has role admin
- `GET      /api/v1/users` - Get all user except/include her/himself
- `GET      /api/v1/users/{staffCode}` - Get one user by **staffCode**
- `PUT      /api/v1/users/{staffCode}` - User is able to change information
- `PATCH    /api/v1/users/password` - User is able to change their password
- `DELETE   /api/v1/users/{staffCode}` - User is able to disable other user

##### CATEGORY FEATURE
- `POST     /api/v1/categories` - Create new category by user has role admin
- `GET      /api/v1/categories` - Get all categories was created by admin

##### ASSET FEATURE
- `POST     /api/v1/assets` - Create new asset by user has role admin
- `GET      /api/v1/assets` - Get all assets was created by admin has same location
- `GET      /api/v1/assets/{assetCode}` - Get one asset by **assetCode**
- `GET      /api/v1/assets/history/{assetCode}` - Get all assigned asset history by **assetCode**
- `PATCH    /api/v1/assets/{assetCode}` - Update asset information
- `DELETE   /api/v1/assets/{assetCode}` - User is able to disable assets

##### ASSIGNMENT FEATURE
- `POST     /api/v1/assignments` - Create new assignment by user has role admin
- `GET      /api/v1/assignments` - Get all assignments was created by admin
- `GET      /api/v1/assignments/own` - Get all him/her assignment
- `GET      /api/v1/assignments/{id}` - Get one assignment by **id**
- `GET      /api/v1/assignments/valid/{id}` - Check assignment valid state by **id**
- `PATCH    /api/v1/assignments/{id}` - Update assignemnt information
- `PATCH    /api/v1/assignments/response/{id}` - Response him/her assignment (accepted/declined)
- `DELETE   /api/v1/assignments/{id}` - User is able to disable assignments

##### REQUEST FOR RETURNING FEATURE
- `POST     /api/v1/return-request` - Create new request for returning by admin
- `GET      /api/v1/return-request` - Get all request for returning was created by admin
- `PUT      /api/v1/return-request/{id}` - Update request for returning information
- `DELETE   /api/v1/return-request/{id}` - User is able to disable request for returning 

##### REQUEST FOR RETURNING FEATURE
- `GET      /api/v1/report/view` - Get all asset was create by him/herself order by category (for view)
- `GET      /api/v1/report/export` - Get all asset was create by him/herself order by category (for export)

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make your changes.
4. Commit your changes (`git commit -am 'Add new feature'`).
5. Push to the branch (`git push origin feature-branch`).
6. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
