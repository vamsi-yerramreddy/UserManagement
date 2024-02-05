# User Authentication System

This is a basic user authentication system built with Java and Spring Boot

## Features

- User registration
- User login
- JWT authentication
- Password encryption with BCrypt
- Password reset functionality with email

## Tech Stack

- Java
- Spring Boot
- Spring Security
- Jwt
- Maven
- MySQL

## Setup and Installation

1. Clone the repository: `git clone <repository-url>`
2. Navigate into the project directory: `cd user-management`
3. Install the dependencies: `mvn install`
4. Run the application: `mvn spring-boot:run`

## Usage

The application exposes the following endpoints:

- `/auth/login`: Authenticate a user.
- `/auth/signup`: Register a new user.
- `/auth/validate`: Validate a JWT token.
- `/auth/reset-password`: Send a password reset email.
- `/auth/change-password`: Change a user's password.

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](https://choosealicense.com/licenses/mit/)
