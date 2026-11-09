# Zenith Backend

**Zenith** is the backend for a full-stack e-commerce marketplace. Built with Java and Spring Boot, it provides REST APIs for user authentication, product management, shopping cart operations, order processing, payments, and administrative functionality.

The backend uses MySQL for data persistence and integrates with external services such as Razorpay, Cloudinary, and Gmail SMTP.

## Features

### Authentication & User Management
- User registration and login.
- JWT-based authentication.
- Password hashing using BCrypt.
- Role-based authorization for administrative operations.
- User profile retrieval and updates.
- Welcome emails after registration.

### Product Management
- Retrieve all available products.
- Fetch individual product details.
- Search products.
- Add, update, and delete products.
- Upload product images using Cloudinary.
- Restrict product management operations to administrators.

### Shopping Cart
- Add products to the cart.
- Retrieve the authenticated user's cart.
- Remove products from the cart.
- Associate cart items with individual users.

### Order Management
- Place orders using cart items.
- Retrieve customer order history.
- Update order fulfillment status.
- Track payment information.
- Automatically update inventory when orders are placed.
- Send order confirmation and status update emails.

### Payment Integration
- Create Razorpay payment orders.
- Verify payment signatures on the backend.
- Store payment IDs and payment statuses.
- Support Cash on Delivery through the order workflow.

### Admin Dashboard APIs
- Retrieve dashboard statistics.
- View inventory.
- View customer orders.
- Update order statuses.
- Access financial transaction information.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend programming language |
| Spring Boot 4.0.5 | Application framework |
| Spring Web MVC | REST API development |
| Spring Security | Application security |
| Spring Data JPA | Database operations |
| MySQL | Relational database |
| Hibernate | ORM |
| JWT (JJWT) | Token-based authentication |
| BCrypt | Password hashing |
| Lombok | Boilerplate code reduction |
| Cloudinary | Product image storage |
| Razorpay | Payment gateway |
| Spring Mail | Email notifications |
| Maven | Dependency and build management |

## Project Structure

```text
zenith-backend/
│
├── src/
│   ├── main/
│   │   ├── java/com/zenith_backend/
│   │   │   ├── config/          # Application configuration
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── model/           # Entity classes
│   │   │   ├── repository/      # Database access layer
│   │   │   ├── security/        # JWT authentication
│   │   │   ├── service/         # Business logic
│   │   │   └── BackendApplication.java
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── pom.xml
├── mvnw
└── mvnw.cmd
```

## API Overview

The backend exposes REST endpoints under the `/api` namespace.

| Module | Endpoint | Description |
|---|---|---|
| Users | `/api/users` | Register and retrieve users |
| Authentication | `/api/login` | Authenticate users |
| User Profile | `/api/users/me` | Retrieve and update profile |
| Products | `/api/products` | Retrieve and manage products |
| Product Search | `/api/products/search` | Search products |
| Cart | `/api/cart` | Add, retrieve and remove cart items |
| Orders | `/api/orders` | Place orders and retrieve order history |
| Payments | `/api/payment/create-order` | Create Razorpay orders |
| Payments | `/api/payment/verify-payment` | Verify payment signatures |

Administrative endpoints are also available for inventory, order management, dashboard statistics, and financial information.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 17 or later
- MySQL Server
- Git
- Maven (optional, as Maven Wrapper is included)

### 1. Clone the Repository

```bash
git clone https://github.com/Ishaan-2589/zenith-backend.git
cd zenith-backend
```

### 2. Create the Database

Create a MySQL database:

```sql
CREATE DATABASE zenith;
```

### 3. Configure Environment Variables

The application reads its configuration from environment variables.

Configure the following variables:

```env
DB_URL=jdbc:mysql://localhost:3306/zenith
DB_USERNAME=your_mysql_username
DB_PASSWORD=your_mysql_password

EMAIL_USER=your_email@gmail.com
EMAIL_PASSWORD=your_email_app_password

CLOUDINARY_CLOUD_NAME=your_cloudinary_cloud_name
CLOUDINARY_API_KEY=your_cloudinary_api_key
CLOUDINARY_API_SECRET=your_cloudinary_api_secret

RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret
```

Use your own credentials. Do not commit passwords, API secrets, or other sensitive configuration values to GitHub.

### 4. Run the Application

On Windows:

```bash
mvnw.cmd spring-boot:run
```

On macOS/Linux:

```bash
./mvnw spring-boot:run
```

The backend starts on:

```text
http://localhost:8080
```

You can check whether the server is running by visiting:

```text
http://localhost:8080/
```

## Docker

The repository includes a Dockerfile.

Build the image:

```bash
docker build -t zenith-backend .
```

Run the container:

```bash
docker run -p 8080:8080 zenith-backend
```

Ensure the required environment variables and database connectivity are configured.

## Related Repository

**Frontend:** [Zenith Frontend](https://github.com/Ishaan-2589/zenith-frontend)

## Future Improvements

- Expanded automated test coverage.
- Improved API documentation.
- Enhanced validation and exception handling.
- Further security and deployment improvements.

---

**Zenith Backend** — powering the server-side operations of the Zenith marketplace.
