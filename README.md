# Cash Card API

<img src="./Congratulations.png" alt="Congratulations">

## Spring Boot REST API

This project is a fully functional, robust, and secure REST API built using Spring Boot. The project leverages the strengths of Spring's
core components, providing a well-structured, secure, and test-driven application.

### Key Features

1. **Comprehensive REST Support**:

   - Supports common HTTP methods including GET, PUT, PATCH, and POST.
   - HTTP response status codes are utilized effectively, such as:
     - `200 OK` for successful requests.
     - `201 CREATED` for resource creation.
     - `204 NO CONTENT` for operations without a response body.

2. **Layered Architecture**:

   - **Spring Security**: Handles authentication and authorization at the API’s entry point, ensuring secure access to the endpoints.
   - **Spring Web**: Manages HTTP communication, enabling seamless interaction between the API and clients.
   - **Spring Data**: Provides integration with relational databases, handling all the data access operations.

3. **Spring Boot Advantages**:

   - Inversion of Control (IoC) with Spring's powerful container.
   - Extensive use of annotations for a clean, minimal-configuration codebase.
   - Auto-configuration to minimize boilerplate configuration tasks.

4. **Test-Driven Development**:

   - Built with a test-first approach, ensuring the application is thoroughly tested and functions as expected.
   - Tests were used to guide development and ensure coverage of all functionalities.

5. **Steel Thread Architecture Validation**:

   - Early integration and validation of critical paths in the application, reducing the risk of potential issues during development.

6. **Red, Green, Refactor Cycle**:
   - Continuous improvement of both code and tests using the Red, Green, Refactor development process to ensure code quality and
     maintainability.

### Requirements

- Java 17 or higher
- Maven or Gradle for dependency management
- Spring Boot framework
- A relational database (e.g., MySQL, PostgreSQL) for data storage

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/abdulbasit-dev/cashcard.git
   ```
2. Navigate to the project directory:
   ```bash
   cd cashcard
   ```
3. Build the project:
   ```bash
   mvn clean install
   ```
4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

### Configuration

Configuration can be managed through the `application.properties` file located in the `src/main/resources/` directory. Database connection,
security settings, and other application properties can be modified here.

### Testing

The project includes unit and integration tests to validate the functionality of the application. To run the tests:

```bash
mvn test
```

### License

This project is licensed under the MIT License.
