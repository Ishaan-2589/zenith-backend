# ⚡ Zenith | E-Commerce & Payment Gateway Platform

**Zenith** is a production-grade, full-stack E-Commerce platform featuring real-time authentication, dynamic cart management, and seamless end-to-end payment gateway integration via **Razorpay**.

Designed with modular backend API routes, secure cross-origin resource sharing (CORS), token-based authentication workflows, and cloud-hosted data persistence.

## 🚀 Key Features

* **Authentication & Authorization**: Secure User Registration and Login flow powered by **JSON Web Tokens (JWT)** and **bcrypt** password hashing.

* **Dynamic Cart Operations**: Real-time state synchronization for adding, updating, and removing items, bound directly to authenticated user sessions.

* **Integrated Razorpay Checkout**: Fully functional payment gateway integration utilizing server-side order creation and client-side webhooks/callbacks.

* **Resilient Client Architecture**: Vanilla JavaScript SPA/PWA architecture with modular API layers, resilient token fallback handling, and real-time state listeners (`auth-expired`, dynamic cart re-renders).

* **Production Deployment Ready**: Pre-configured cross-origin routing for seamless integration between isolated frontend and backend environments (e.g., Vercel, Render, Railway).

## 🛠️ Tech Stack

### Frontend

* **Core**: HTML5, CSS3, JavaScript (ES6+ Modules)
* **Styling**: Tailwind CSS / Custom Responsive Layouts
* **Payment SDK**: Razorpay Checkout JS SDK

### Backend

* **Runtime**: Node.js
* **Framework**: Express.js
* **Authentication**: JSON Web Tokens (`jsonwebtoken`), `bcryptjs`
* **Security & Middleware**: `cors`, `dotenv`

### Database & Hosting

* **Database**: MongoDB / PostgreSQL / MySQL (Configurable ORM/ODM)
* **Hosting Capabilities**: Serverless / Node Runtime (Vercel, Render, Railway, AWS)

## 📂 Project Architecture

```
Zenith/
├── frontend/
│   ├── index.html          # Storefront entry point
│   ├── cart.html           # Dynamic user cart page
│   ├── checkout.html       # Payment handling & verification page
│   ├── css/                # Global styles and layout definitions
│   └── js/
│       ├── api.js          # Centralized API fetch wrapper & auth interceptors
│       ├── cart.js         # Cart UI rendering & state management
│       ├── checkout.js     # Razorpay SDK initialization & payload dispatch
│       └── main.js         # Auth listeners and DOM orchestration
└── backend/
    ├── server.js           # Server entry point & Express configuration
    ├── config/             # DB & payment credentials initializers
    ├── middleware/         # JWT verification & CORS policy enforcement
    ├── routes/
    │   ├── auth.js         # Register / Login endpoint routes
    │   ├── cart.js         # User cart CRUD routes
    │   └── payment.js      # Razorpay order generation & signature verification
    └── models/             # Schema definitions
```

## ⚙️ Environment Variables

To run Zenith locally or in production, configure the following variables in your server environments:

### Backend (`.env`)

```env
PORT=5000
MONGODB_URI=your_database_connection_string
JWT_SECRET=your_jwt_super_secret_key

# Razorpay Keys
RAZORPAY_KEY_ID=your_razorpay_key_id
RAZORPAY_KEY_SECRET=your_razorpay_key_secret

# CORS Settings
ALLOWED_ORIGIN=https://your-frontend-domain.vercel.app
```

### Frontend Configuration

Ensure your API base URL point in `frontend/js/api.js` points to your backend URL:

```javascript
export const API_BASE_URL = "https://your-backend-api.onrender.com/api";
```

## 🚦 Quick Start Guide

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/zenith.git
cd zenith
```

### 2. Setup & Start Backend

```bash
cd backend
npm install
npm start
```

*The backend server should now be running on `http://localhost:5000`.*

### 3. Setup & Launch Frontend

You can serve the `frontend/` directory using any static file server, Live Server (VS Code extension), or standard HTTP server:

```bash
cd ../frontend
npx serve .
```

## 🔒 Security Best Practices Implemented

1. **Bearer Token Authorization**: Sensitive endpoints are protected via `Authorization: Bearer <token>` middleware verification.
2. **Dynamic Session Expiry Handling**: Client automatically clears expired tokens and dispatches global auth events upon `401 Unauthorized` responses.
3. **Environment Security**: Sensitive operational keys (Razorpay secrets, database strings, JWT signatures) are kept off client bundles and injected at runtime via environment configuration.

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.
