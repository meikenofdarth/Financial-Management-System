# FinCore Banking System

**Course:** CSE 731: Software Testing
**Term:** Term I 2025 - '26
**Institution:** IIIT Bangalore

## Project Overview

FinCore is a robust, persistent Banking Web Application developed using Java and Spring Boot. It simulates core financial operations including customer registration, account management (Savings and Current), fund transfers, and algorithmic loan processing.

The primary engineering goal of this project was to establish a rigorous software testing environment. The project employs **Mutation Testing** (utilizing PITest) as the primary metric for quality assurance, aiming to create a test suite capable of detecting logic faults, boundary errors, and persistence failures.

## System Architecture

The application follows a modular **Model-View-Controller (MVC)** architecture layered with a Service-Repository pattern. This separation of concerns ensures testability and scalability.

### 1. Web Layer (Controller & DTOs)
Located in `com.fincore.controller` and `com.fincore.dto`.
*   **BankController.java:** The central request handler. It maps HTTP GET/POST requests to business logic. It handles the user interface flow, rendering HTML views and managing error handling.
*   **Data Transfer Objects (DTOs):** A collection of classes (`CustomerRegistrationDto`, `LoanApplicationDto`, `OpenAccountDto`, `TransactionDto`, `TransferDto`) used to encapsulate data sent from the web frontend. These ensure that internal domain models (`Account`, `Customer`) are decoupled from the HTTP request structure.

### 2. Service Layer (Business Logic)
Located in `com.fincore.service`.
*   **AccountService.java:** Orchestrates transactional operations. It handles deposits, withdrawals, and fund transfers. It ensures transactional integrity by modifying in-memory objects and immediately triggering the persistence layer to save state to the disk.
*   **LoanService.java:** Contains the decision matrix for loan approvals. It utilizes complex conditional logic to assign interest rates based on customer Credit Scores.

### 3. Repository Layer (Persistence)
Located in `com.fincore.repository`.
*   **DataStore.java:** Implements the **Singleton Design Pattern**. It acts as an in-memory database using HashMaps but provides persistence via **Java Serialization**.
*   **Persistence Mechanism:** The system state is serialized into a file named `bank_data.ser`. This ensures that customer data, account balances, and loan records survive application restarts.

### 4. Domain Model
Located in `com.fincore.model`.
*   **Account.java (Abstract):** The base class defining common attributes like balance and ID.
*   **SavingsAccount.java:** Implements logic for interest calculations and minimum balance requirements.
*   **CurrentAccount.java:** Implements logic for overdraft limits.
*   **Customer.java:** Represents user identity, contact details, and credit scoring data.
*   **Loan.java:** Represents an active liability with principal, tenure, and repayment tracking.
*   **Transaction.java:** An immutable record of financial events (timestamp, type, amount).

### 5. Utility Layer
Located in `com.fincore.util`.
*   **FinancialMath.java:** Contains static methods for high-precision financial calculations (EMI, Compound Interest).
*   **ValidationUtil.java:** Provides Regex-based validation for emails, passwords, and phone numbers.
*   **DateUtil.java:** Helper methods for date manipulation and difference calculations.

### 6. Application Entry Point
*   **FinCoreApplication.java:** The standard Spring Boot entry point that initializes the embedded web server.
*   **LegacyBankApp.java:** Preserved code from the initial CLI version of the application (deprecated).

---

## Testing Strategy and Test Suite

The project utilizes **JUnit 5** for test execution and **PITest 1.16.1** for mutation analysis. The test suite is structured to target specific architectural layers.

### Unit Testing
Located in `com.fincore.util` and `com.fincore.model`.
*   **FinancialMathTest.java:** Verifies mathematical formulas to prevent arithmetic errors.
*   **ValidationUtilTest.java:** Ensures input validation logic correctly identifies malformed data.
*   **ModelTest.java:** Verifies the integrity of POJOs (Plain Old Java Objects) to ensure getters, setters, and constructors function correctly.

### Integration Testing
Located in `com.fincore.service` and `com.fincore.repository`.
*   **AccountServiceTest.java:** Tests the interaction between the Service layer and the DataStore. It verifies that transfers correctly deduct from one account and add to another.
*   **LoanServiceTest.java:** Verifies the loan approval logic and interest rate assignment.
*   **DataStoreTest.java:** Specifically targets the Persistence Layer. It performs file system checks to ensure that data written to memory is successfully committed to the `bank_data.ser` file.

### Advanced Testing Components
To achieve a high Mutation Score, specialized tests were implemented:
*   **BoundaryKillerTest.java:** Designed to kill "Conditionals Boundary Mutants". It targets exact edge cases (e.g., Credit Score = 300, Balance = 0.0) to ensure strict adherence to business rules.
*   **CoverageBoosterTest.java:** A saturation test designed to execute every line of code in the DTOs and Utility classes, eliminating "No Coverage" mutants and ensuring the mutation analysis focuses on logic rather than unreachable code.

---

## Mutation Analysis Results

The system underwent rigorous Mutation Testing to ensure the quality of the test suite.

*   **Mutation Engine:** PITest
*   **Mutation Score:** ~73% (Exceeding the 70% requirement)
*   **Operators Targeted:**
    *   **Math Mutator:** Replaced arithmetic operations (Verified in `FinancialMath`).
    *   **Void Method Call Mutator:** Removed calls to `saveData()` (Caught by `DataStoreTest`).
    *   **Conditionals Boundary Mutator:** Changed relational operators (Caught by `BoundaryKillerTest`).
    *   **Negate Conditionals Mutator:** Inverted boolean logic (Caught by `AccountServiceTest`).

---

## Prerequisites and Tools

*   **Java Development Kit (JDK):** Version 17 or higher.
*   **Build Tool:** Maven 3.6+.
*   **Framework:** Spring Boot 3.2.2.
*   **Dependencies:** Spring Web, Thymeleaf, JUnit 5, PITest.

## Execution Instructions

### 1. Running the Web Application
To start the application server:
```bash
mvn spring-boot:run
```
Once started, the application is accessible via a web browser at: **http://localhost:8080**

### 2. Running the Test Suite
To execute all Unit and Integration tests:
```bash
mvn test
```

### 3. Running Mutation Analysis
To generate the mutation coverage report:
```bash
mvn test pitest:mutationCoverage
```
The resulting HTML report will be generated in `target/pit-reports/index.html`.
## Team Contribution

| Student | Role | Contributions |
| :--- | :--- | :--- |
| **[Student 1]** | **Core Architecture & Persistence** | Designed the Model layer, implemented the Singleton `DataStore` with Serialization, and wrote the `ModelTest` and `CoverageBooster` suites. |
| **[Student 2]** | **Service Logic & Mutation Strategy** | Implemented `AccountService` and `LoanService`, migrated the app to Spring Boot, configured PITest in `pom.xml`, and wrote `IntegrationTests` to handle boundary mutants. |

---

### Tools Used
*   **Java 22:** Core Language.
*   **Spring Boot 3.2:** Web Framework.
*   **Thymeleaf:** Frontend Templating.
*   **Maven:** Build Tool.
*   **JUnit 5:** Testing Framework.
*   **PITest 1.16.1:** Mutation Testing Engine.