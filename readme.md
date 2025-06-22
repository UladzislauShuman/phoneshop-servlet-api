# PhoneShop: E-commerce Web Application

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;An e-commerce web application built from scratch using the classic Java EE stack, without relying on external frameworks. The primary focus was on creating a reliable, scalable, and secure architecture while implementing core e-commerce business processes.

## Core Features & Achievements

*   **Architecture**:
    *   Designed and implemented a multi-layered application architecture (MVC) with a clear separation of concerns:
        *   Web Layer (Servlets, JSP)
        *   Service Layer
        *   Data Access Layer (DAO)
*   **Thread-Safety**:
    *   Ensured thread-safety of critical stateful components (DAOs, Services) using `java.util.concurrent.locks.ReadWriteLock`.
*   **"Products" Module**:
    *   Implemented an In-Memory DAO for storing product data.
    *   Developed a sophisticated search logic for the product catalog that ranks results (full matches appear before partial ones) and supports sorting by various fields (price, description).
    *   Added functionality to track product price history.
*   **"Cart" Module**:
    *   Implemented the full business logic for the shopping cart (adding items, updating quantities, removing items).
    *   The user's cart is stored in `HttpSession`, ensuring persistence throughout the user's session.
    *   Implemented real-time stock availability checks (`OutOfStockException`) when adding items to the cart.
*   **"Orders" Module**:
    *   Developed the checkout process with user data validation, including phone number validation.
    *   Implemented order persistence and the generation of a unique secure ID to view order details.
*   **Security**:
    *   Implemented a mechanism for DoS (Denial-of-Service) protection using a custom servlet filter (`DosFilter`), which tracks and limits the number of requests from a single IP address.
*   **Web Interface (UI)**:
    *   Created dynamic web pages using JSP, JSTL, and custom tags for reusable UI components (e.g., product card, price history).
    *   Configured the application lifecycle, servlet mappings, and error handling (404, 500) via `web.xml`.
*   **Testing**:
    *   Wrote a comprehensive suite of unit tests using JUnit 5 and Mockito, ensuring code stability and reliability.
*   **Additional Features**:
    *   Implemented a "recently viewed products" feature, with data also stored in the user's session.

## Tech Stack

**Backend**: Java 17, Java Servlets API, JSP, JSTL  
**Testing**: JUnit 5, Mockito  
**Build & Runtime**: Maven, Jetty  
**Logging**: SLF4J, Logback  
**Frontend**: HTML, CSS, Custom JSP Tags  
**Principles & Patterns**: MVC, DAO, Singleton, Thread-Safety, Dependency Injection (via ServletContext).

## Design Choices

**Why use an In-Memory DAO instead of a database?**  
The goal of this project was to focus on business logic, architecture, and concurrency, rather than on database setup and configuration. However, the implemented architecture allows for an easy swap to another implementation (e.g., `JdbcProductDao`) without changing the service layer.

## Getting Started

1.  Clone the repository: `git clone <repository_url>`
2.  Navigate to the project directory: `cd phoneshop-servlet-api`
3.  Run the application using the appropriate script:
    *   On Linux/macOS: `./run.sh`
    *   On Windows: `run.bat`
4.  Open your browser and navigate to: `http://localhost:8080/phoneshop/products`