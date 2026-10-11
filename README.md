# SRT Todo Application

A modern, production-grade full-stack Todo Management application built with **Spring Boot (Java 21)**, **React 19 (TypeScript & Vite)**, and **PostgreSQL 17**.

The application features secure stateless **JWT authentication**, per-user data isolation, comprehensive task management (CRUD, priority flag, soft delete & trash bin recovery), server-side pagination and sorting, debounced search, responsive design with dark mode, and seamless **Docker Compose** deployment.

---

## 📌 Table of Contents

- [Tech Stack](#-tech-stack)
- [Key Features](#-key-features)
- [Quick Start Test Credentials](#-quick-start-test-credentials)
- [Running the Application](#-running-the-application)
  - [Option 1: Run with Docker Compose (Recommended)](#option-1-run-with-docker-compose-recommended)
  - [Option 2: Run Locally on Host Machine](#option-2-run-locally-on-host-machine)
- [API Reference](#-api-reference)
- [Testing & Quality Assurance](#-testing--quality-assurance)
- [Project Structure](#-project-structure)

---

## 🛠️ Tech Stack

### 1. Frontend (`/client`)
- **Core**: React 19, TypeScript, Vite
- **State Management**: Redux Toolkit (`authSlice`) for global authentication state and token management
- **Styling**: Tailwind CSS (v3) with full Dark/Light theme toggle support
- **HTTP Client**: Axios with global response interceptors for standardized API handling and token attachment
- **Icons & Notifications**: Lucide React icons, Sonner toast notifications
- **Routing**: React Router DOM (v7) with protected route guards

### 2. Backend (`/server`)
- **Core**: Java 21, Spring Boot 3
- **Architecture**: Layered Architecture (`Controller` -> `Service` -> `Repository`)
- **Security**: Spring Security + JWT (JSON Web Tokens), `BCryptPasswordEncoder` for salted password hashing
- **Data & Persistence**: Spring Data JPA / Hibernate, PostgreSQL 17
- **Validation**: Jakarta Bean Validation (`@Valid`, custom regex patterns)
- **Unit Testing**: JUnit 5, Mockito

### 3. Database & DevOps
- **Database**: PostgreSQL 17
- **Containerization**: Multi-stage Dockerfile (Maven build + JRE 21 runtime)
- **Orchestration**: Docker Compose with health checks and persistent volume storage

---

## 🌟 Key Features

### 📝 Core Task Management
- **Full CRUD Operations**: Create tasks with title, description, due date (ISO-8601), and priority.
- **Priority Marking**: Toggle the **Important** status (starred) for urgent tasks.
- **Completion Toggle**: Mark tasks as completed or revert back to active with a single click.
- **Soft Delete & Trash Bin**: Tasks are moved to trash to prevent accidental data loss. Users can restore tasks or permanently delete them.

### 🔍 Search, Filter & Pagination
- **Search with Debouncing**: Real-time title search debounced at 500ms to eliminate redundant API calls.
- **Multi-Tab Filtering**: Filter tasks by **Active**, **Important**, **Completed**, and **Deleted (Trash)**.
- **Server-Side Pagination**: Efficient pagination via Spring Data `Pageable` (`page`, `size`) with total elements and page count metadata.
- **Automatic Sorting**: Tasks are sorted chronologically by creation date in descending order (`createdDate DESC`).

### 🔐 Security & Access Control
- **Stateless Authentication**: Protected API endpoints guarded by `JwtAuthenticationFilter`.
- **Per-User Data Isolation**: Queries and mutations strictly enforce that users can only access their own tasks (`createdBy = currentUser`).
- **Robust Input Validation**: Strict validation rules for account names, emails, and passwords on registration.

---

## 🧪 Quick Start Test Credentials

When starting with a fresh database, you can immediately register a test account on the **Register** page (`/register`), or use the following recommended test profile:

| Field | Value | Validation Rules |
| :--- | :--- | :--- |
| **Account Name** | `testuser` | 3–50 chars, letters, numbers, `_`, `.` |
| **Full Name** | `ShopBack Reviewer` | 2–100 chars |
| **Email** | `testuser@example.com` | Valid email format |
| **Password** | `Password123!` | Min 8 chars, at least 1 uppercase, 1 lowercase, 1 number, 1 special symbol (`@$!%*?&`) |

> **Note**: To test immediately, click **"Chưa có tài khoản? Đăng ký ngay"** on the login page and use the credentials above. Once registered, log in to access the task dashboard.

---

## 🚀 Running the Application

### Prerequisites
- **Docker** and **Docker Desktop** (for Docker deployment)
- **Java 21+** and **Maven** (for local backend development)
- **Node.js (v18+)** and **pnpm** (or **npm**)
- **PostgreSQL 17** (for local database setup)

---

### Option 1: Run with Docker Compose (Recommended)

This method starts the **PostgreSQL database** and the **Spring Boot backend** inside containers with a single command.

#### Step 1: Start Backend and Database
From the project root directory (where `docker-compose.yml` is located):

```bash
cd Todo-app
docker compose up -d --build
```

- **PostgreSQL**: Running on port `5434` (mapped from container `5432`).
- **Backend API**: Running on `http://localhost:8080`.

#### Step 2: Start Frontend
Open a new terminal, navigate to the `client/` directory, install dependencies and launch the dev server:

```bash
cd client
pnpm install
pnpm dev
```
*(Or use `npm install` and `npm run dev`)*

The frontend client will be available at:
👉 **`http://localhost:5173`** (or `http://localhost:5174`)

---

### Option 2: Run Locally on Host Machine

#### Step 1: Start PostgreSQL
Ensure PostgreSQL is running locally on port **`5434`** with a database named `srt`:
- **Host**: `localhost`
- **Port**: `5434`
- **Database**: `srt`
- **Username**: `admin`
- **Password**: `123`

*(Alternatively, update database credentials in `server/src/main/resources/application.properties` to match your local setup).*

#### Step 2: Start Spring Boot Backend
Navigate to the `server/` directory and run:

```bash
cd server
mvn spring-boot:run
```

The server will start listening at:
👉 **`http://localhost:8080`**

#### Step 3: Start Frontend
Navigate to the `client/` directory and run:

```bash
cd client
pnpm install
pnpm dev
```

The frontend client will be available at:
👉 **`http://localhost:5173`**

---

## 📡 API Reference

### Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Description | Auth Required |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/register` | Register a new user account | No |
| `POST` | `/api/auth/login` | Authenticate with credentials and receive JWT | No |
| `POST` | `/api/auth/refresh` | Refresh access token using refresh token | No |
| `POST` | `/api/auth/logout` | Invalidate user session | No |

### Task Endpoints (`/api/tasks`)
| Method | Endpoint | Query / Body Params | Description | Auth Required |
| :--- | :--- | :--- | :--- | :--- |
| `GET` | `/api/tasks` | `search`, `status`, `page`, `size` | Retrieve paginated list of tasks | Yes (Bearer Token) |
| `POST` | `/api/tasks` | `{ title, description, dueDate, important, completed }` | Create a new task | Yes (Bearer Token) |
| `PUT` | `/api/tasks/{id}` | `{ title, description, dueDate, important, completed, deleted }` | Update an existing task | Yes (Bearer Token) |
| `DELETE` | `/api/tasks/{id}` | - | Soft delete task (move to trash) | Yes (Bearer Token) |
| `PATCH` | `/api/tasks/{id}/completion` | `{ completed: boolean }` | Toggle task completion status | Yes (Bearer Token) |
| `PATCH` | `/api/tasks/{id}/restore` | - | Restore a deleted task from trash | Yes (Bearer Token) |

---

## 🧪 Testing & Quality Assurance

### Backend Unit Tests
Unit tests cover core service logic (`AuthServiceImplTest`, `TaskServiceImplTest`) with **JUnit 5** and **Mockito**:

```bash
cd server
mvn test
```

### Frontend Code Quality & Build Verification
The frontend code adheres strictly to ESLint rules and TypeScript types with **0 errors and 0 warnings**:

```bash
cd client
# Run ESLint check
pnpm lint

# Run Production Build
pnpm build
```

---

## 📂 Project Structure

```bash
Todo-app/
├── docker-compose.yml          # Container configuration for Postgres & Spring Boot
├── init-sample-data.sql        # Database initialization script
├── README.md                   # Project documentation
├── client/                     # Frontend Application (React 19, Vite, TypeScript)
│   ├── src/
│   │   ├── api/                # API client services (authApi.ts, taskApi.ts)
│   │   ├── config/             # Constants and route configurations
│   │   ├── libs/               # Axios instance with interceptors, utility functions
│   │   ├── pages/              # Views (Login.tsx, Register.tsx, Dashboard.tsx)
│   │   ├── routes/             # AppRoutes with ProtectedRoute wrapper
│   │   ├── store/              # Redux Toolkit store and slices (authSlice.ts)
│   │   ├── types/              # TypeScript definitions and interfaces
│   │   ├── App.tsx             # Root component
│   │   └── main.tsx            # Application entry point
│   ├── package.json
│   ├── tailwind.config.js
│   └── vite.config.ts
└── server/                     # Backend Application (Spring Boot, Java 21)
    ├── src/main/java/SRT/test/
    │   ├── common/             # Standard API & pagination response wrappers
    │   ├── config/             # SecurityConfig, CorsConfig
    │   ├── controller/         # REST Controllers (AuthController, TaskController)
    │   ├── dto/                # Request & Response DTOs with validation rules
    │   ├── entity/             # JPA Entities (User, Task)
    │   ├── exception/          # Global exception handling & error codes
    │   ├── repo/               # Spring Data JPA repositories (IUserRepository, ITaskRepository)
    │   ├── security/           # JWT utility, UserDetailsService, JwtAuthFilter
    │   └── service/            # Business logic interfaces & implementations
    ├── src/test/               # JUnit 5 & Mockito test suites
    ├── Dockerfile              # Multi-stage Docker build configuration
    └── pom.xml                 # Maven dependencies & plugins
```