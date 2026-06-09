# distributed-ticketing-UserBEsystem

## Overview

This repository contains the user management backend for the distributed ticketing platform.
It is one of the three coordinated repositories that form the full project:

- distributed-ticketing-FrontEnd: user interface
- distributed-ticketing-DBsystem: ticketing and reservation backend
- distributed-ticketing-UserBEsystem: authentication, account lifecycle, and user profile backend

This service is responsible for authentication, registration, email verification, password recovery, and user-token validation. It is consumed directly by the frontend and indirectly by the ticketing flow when reservations need to be associated with a logged-in account.

## Role in the Full System

The platform relies on this backend to confirm who the user is and whether the account is verified before the client can complete login-sensitive flows.
The frontend stores the generated user token locally and validates it against this service when determining login state.

## Main Responsibilities

- Register new users
- Authenticate users and generate session tokens
- Validate stored user tokens
- Expose public user information by token
- Handle email verification flows
- Handle password reset flows
- Maintain secure password storage with hashing
- Support account-related communication through MailGun and HTTP integrations

## Architecture

This project is a Spring Boot 3.5.6 application packaged as a WAR file and built for Java 17.
It uses Spring Web, Spring Data JPA, Spring Data REST, Spring Security crypto utilities, WebSocket support, and Microsoft SQL Server as the backing database.

The application is configured to run behind HTTPS and is targeted by the frontend proxy on `https://localhost:8081`.

## UML Sequence Diagram

The control flow diagram is split into three images:

### Part 1

Image placeholder for the first part of the UML sequence diagram.

### Part 2

Image placeholder for the second part of the UML sequence diagram.

### Part 3

Image placeholder for the third part of the UML sequence diagram.

## Key Endpoints

- `/users/login`: authenticate a user and issue a session token
- `/users/register`: create a new account
- `/external/checkUserToken`: validate a token from the frontend
- `/external/getUserInfoEmail`: retrieve user information by token
- password reset and email verification endpoints exposed by the user API

## Security Notes

- Passwords are stored as hashes, not in plain text.
- User tokens are generated dynamically at login.
- The frontend must not trust local state alone and should validate the token against this service.
- Registration and login inputs are validated before persistence or authentication.
- SQL access is handled through the persistence layer rather than raw string concatenation.

## Tech Stack

- Java 17
- Spring Boot 3.5.6
- Spring Web
- Spring Data JPA
- Spring Data REST
- Spring Security crypto
- SQL Server
- MailGun email integration
- Apache HttpClient

## Prerequisites

- JDK 17
- Maven Wrapper or Maven
- A reachable SQL Server instance
- HTTPS certificates or local trust configuration compatible with the development setup

## Local Setup

1. Configure the SQL Server connection in the application properties.
2. Start this backend before launching the frontend.
3. Ensure the frontend proxy points to `https://localhost:8081` for user routes.
4. Verify that the other repository backends use compatible token and email-verification flows.

## Integration With the Other Repositories

- The frontend calls this backend for login, registration, verification, and password recovery.
- The ticketing backend expects user tokens produced here when anonymous reservations are adopted by a logged-in user.
- The three repositories must remain aligned so that token exchange, email flows, and reservation ownership work consistently.

## Related Repositories

- distributed-ticketing-DBsystem -> `https://github.com/SobrinoS29/distributed-ticketing-DBsystem`
- distributed-ticketing-UserBEsystem -> `https://github.com/SobrinoS29/distributed-ticketing-UserBEsystem`
- distributed-ticketing-FrontEnd -> `https://github.com/SobrinoS29/distributed-ticketing-FrontEnd`

## Security

This backend is responsible for the authentication boundary of the platform, so the following protections are especially relevant:

1. HTTPS is enabled with locally trusted certificates generated with `mkcert`, allowing the user backend to run securely during development.
2. The frontend should keep `userToken` values out of URLs and store them in `sessionStorage` instead.
3. The platform uses BCrypt to hash and validate passwords, so credentials are never handled as plain text.
4. SQL injection is reduced by using parameterized JPA queries instead of dynamic SQL.
5. Email verification and password change flows use expiring tokens.
6. Registration responses are intentionally generic so they do not reveal whether an account already exists.
7. The password hash table uses a neutral name, such as Orion, to avoid exposing its purpose unnecessarily.
8. Email masking can be applied so only authorized DBA users can view full addresses when required.
9. A database trigger can assign roles automatically based on approved emails, and another trigger can keep the `updated_at` timestamp in sync.
10. The system avoids exposing sensitive internal names or direct table manipulation paths through the UI.
11. Reservation-related tokens exchanged with the ticketing backend should remain short-lived and validated server-side.

## License

Educational project. Images are used for educational purposes only.

## Author

Javier Sobrino Ocaña