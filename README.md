# Maybank Transaction Service

## Overview
The **Maybank Transaction Service** is a Spring Boot-based application designed to manage and process financial transactions. It provides RESTful APIs for transaction management, user authentication, and searching/filtering transactions based on various criteria. The project is built using Java, Spring Boot, and Maven, and includes unit and integration tests to ensure reliability.

## Features
- **User Authentication**: Register and log in users with JWT-based authentication.
- **Transaction Management**:
  - Search transactions by customer ID, account numbers, or description.
  - Retrieve all transactions with pagination and sorting.
  - Update transaction descriptions.
- **Postman API Collection**: A Postman collection is provided for testing the APIs.

## Technologies Used
- **Java 17**: Programming language.
- **Spring Boot 3.1.5**: Framework for building the application.
- **Maven 3.8.6**: Dependency management and build tool.
- **JUnit 5.9.2**: Unit testing framework.
- **Mockito 4.8.0**: Mocking framework for testing.
- **Postman 11.45.3**: API testing tool.

## Project Structure
- `src/main/java`: Contains the main application code.
  - `com.maybank.transaction.entity`: Entity classes for database models.
  - `com.maybank.transaction.repository`: Repository interfaces for database operations.
  - `com.maybank.transaction.service`: Service layer for business logic.
  - `com.maybank.transaction.web.rest`: REST controllers for API endpoints.
- `src/test/java`: Contains unit tests.
- `Maybank Transaction.postman_collection.json`: Postman collection for testing the APIs.

## API Endpoints
### Authentication
1. **Register a New User**
   - **URL**: `POST /auth/register`
   - **Request Body**:
     ```json
     {
       "username": "newuser",
       "password": "12345678"
     }
     ```
   - **Description**: Registers a new user.

2. **Login and Get JWT Token**
   - **URL**: `POST /auth/login`
   - **Request Body**:
     ```json
     {
       "username": "newuser",
       "password": "12345678"
     }
     ```
   - **Description**: Logs in a user and returns a JWT token.

### Transactions
1. **Get All Transactions (Paginated)**
   - **URL**: `POST /api/transactions/searchAll`
   - **Request Body**:
     ```json
     {
       "page": 0,
       "size": 20,
       "sortBy": "TRX_DATE",
       "direction": "DESC"
     }
     ```
   - **Description**: Retrieves all transactions with pagination and sorting.

2. **Filter by Customer ID**
   - **URL**: `GET /api/transactions/search/customer/{customerId}`
   - **Request Body**:
     ```json
     {
       "page": 0,
       "size": 5,
       "sortBy": "TRX_DATE",
       "direction": "DESC"
     }
     ```
   - **Description**: Retrieves transactions for a specific customer ID.

3. **Filter by Account Numbers**
   - **URL**: `POST /api/transactions/search/accounts`
   - **Request Body**:
     ```json
     {
       "accountNumbers": ["8872838283", "8872838299"],
       "page": 0,
       "size": 10,
       "sortBy": "TRX_AMOUNT",
       "direction": "ASC"
     }
     ```
   - **Description**: Retrieves transactions for specific account numbers.

4. **Search by Description**
   - **URL**: `POST /api/transactions/search/description`
   - **Request Body**:
     ```json
     {
       "description": "FUND TRANSFER",
       "page": 0,
       "size": 5,
       "sortBy": "TRX_DATE",
       "direction": "DESC"
     }
     ```
   - **Description**: Searches transactions by description.

5. **Update Transaction Description**
   - **URL**: `PUT /api/transactions/{transactionId}`
   - **Request Body**:
     ```json
     "Updated transaction description"
     ```
   - **Description**: Updates the description of a specific transaction.

## Running the Project
1. Clone the repository.
2. Build the project using Maven:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```
4. Access the APIs at `http://localhost:8080`.

## Testing
- **Unit Tests**: Run the unit tests using:
  ```bash
  mvn test
  ```
- **Postman Collection**: Import the `Maybank Transaction.postman_collection.json` file into Postman to test the APIs.

## Postman Collection
The provided Postman collection includes the following requests:
- **Authentication**:
  - Register a new user.
  - Login and retrieve a JWT token.
- **Transaction Management**:
  - Retrieve all transactions.
  - Search transactions by customer ID, account numbers, or description.
  - Update a transaction's description.

## Design Patterns Used

### 1. **Repository Pattern**
- **Where Used**: `UserRepository`, `TransactionRepository`
- **Why Chosen**: The repository pattern abstracts the data access layer, providing a clean interface for database operations. It allows you to focus on business logic without worrying about database queries.
- **Advantages**:
  - Decouples the data access logic from the business logic.
  - Makes the code more testable by mocking repositories in unit tests.
  - Simplifies database operations with Spring Data JPA.

### 2. **Service Layer Pattern**
- **Where Used**: `TransactionService`, `AuthService`
- **Why Chosen**: The service layer encapsulates business logic and acts as a bridge between controllers and repositories.
- **Advantages**:
  - Centralizes business logic, making it reusable and maintainable.
  - Improves separation of concerns by keeping controllers lightweight.
  - Facilitates unit testing by isolating business logic.

### 3. **Singleton Pattern**
- **Where Used**: Spring-managed beans (e.g., `TransactionService`, `AuthService`, `JwtTokenUtil`)
- **Why Chosen**: Spring Boot inherently uses the singleton pattern for its beans to ensure a single instance is created and shared across the application.
- **Advantages**:
  - Reduces memory usage by reusing the same instance.
  - Ensures consistent state across the application.

### 4. **Factory Pattern**
- **Where Used**: Spring's dependency injection mechanism (e.g., creating beans like `AuthenticationManager`).
- **Why Chosen**: Spring Boot uses the factory pattern to create and manage beans, ensuring proper initialization and configuration.
- **Advantages**:
  - Simplifies object creation and dependency management.
  - Promotes loose coupling by injecting dependencies.

### 5. **Builder Pattern**
- **Where Used**: Entity classes (e.g., `Transaction`, `User`) with Lombok's `@Builder` annotation.
- **Why Chosen**: The builder pattern simplifies the creation of complex objects with multiple fields.
- **Advantages**:
  - Improves code readability and reduces boilerplate code.
  - Makes object creation flexible and avoids constructor overloading.

### 6. **Decorator Pattern**
- **Where Used**: Spring Security filters and JWT token processing.
- **Why Chosen**: The decorator pattern is used to add authentication and authorization logic to HTTP requests.
- **Advantages**:
  - Enhances functionality without modifying existing code.
  - Promotes modularity and reusability.

### 7. **Template Method Pattern**
- **Where Used**: Spring Batch jobs.
- **Why Chosen**: The template method pattern defines the skeleton of a batch job while allowing customization of specific steps.
- **Advantages**:
  - Promotes code reuse by defining common steps in a base class.
  - Simplifies the implementation of batch processes.