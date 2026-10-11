You are a Senior Software Engineer and experienced with Spring Boot and React. You always write clean, maintainable, and well-documented code.

I need you to build a full-stack To-Do List web application using React (Vite + TypeScript) for the frontend and Spring Boot (Java 21) for the backend, with PostgreSQL as the database.

## Tech Stack
- **Frontend**: React 19, Vite, TypeScript, Tailwind CSS, Redux Toolkit, React Router DOM, Axios, Radix UI, Lucide React, Sonner
- **Backend**: Spring Boot 3, Java 21, Spring Security, JWT, Spring Data JPA / Hibernate, PostgreSQL
- **DevOps**: Docker, Docker Compose

---

## Core Features Required

### 1. Authentication System
- User Registration with fields: accountName (login ID), fullName, email, password (BCrypt-encrypted)
- User Login using accountName + password, returns a signed JWT
- JWT-based stateless authentication: every protected request must include `Authorization: Bearer <token>` header
- Frontend stores JWT in localStorage/Redux, auto-attaches token via Axios interceptors

### 2. Task Management (CRUD)
- **Create**: POST endpoint to add a new task with title (required), description (optional), dueDate (ISO-8601 optional), important (boolean)
- **Read**: GET endpoint returning paginated task list filtered by status (active/completed/important/deleted), with optional search by title, sorted by createdDate DESC
- **Update**: PUT endpoint to edit all fields of a task
- **Delete (soft delete)**: Mark task as `deleted=true`, NOT permanent removal
- **Restore**: PATCH endpoint to un-delete a task
- **Hard Delete**: DELETE endpoint to permanently remove a task from DB
- **Toggle Completion**: PATCH endpoint to mark a task as completed/uncompleted

### 3. Data Persistence
- PostgreSQL database with two tables: `users` and `tasks`
- `tasks` table has: id (UUID), title, description, dueDate, important, completed, deleted, createdDate, userId (FK)
- Use Spring Data JPA with `@ManyToOne(fetch = FetchType.LAZY)` on Task entity pointing to User

### 4. Security & Authorization
- All task endpoints require valid JWT
- Each user can only access their own tasks (filter by userId extracted from JWT subject)
- CORS configured to allow frontend origin (localhost:5173/5174)
- Passwords hashed with BCryptPasswordEncoder

---

## Frontend UI Requirements

### Pages
1. **Login Page** (`/login`): Form with accountName + password fields, show/hide password toggle, error display, link to Register
2. **Register Page** (`/register`): Form with accountName, fullName, email, password, confirmPassword; client-side validation; password strength hint
3. **Dashboard** (`/dashboard`): Main todo app view after login

### Dashboard Layout
- **Sidebar** (collapsible on desktop, drawer on mobile) with:
  - User greeting with avatar initials
  - Navigation tabs: All Active, Important, Completed, Deleted/Trash
  - Logout button
- **Main content area**:
  - Header with app title, search bar (debounced 500ms), dark/light mode toggle
  - Task creation panel: input for title, description, date picker, important star toggle, submit button
  - Task list with cards showing: checkbox (toggle complete), title, description, dueDate badge, star (important), edit button, delete/restore button
  - Pagination controls (Previous/Next, page info)
  - Edit modal/panel: prefilled form to edit all task fields

### UX Details
- Dark mode toggle persisted in localStorage
- Search debounce 500ms to avoid spam API calls
- Toast notifications (Sonner) for all create/update/delete/restore actions
- Loading states on all async operations
- Responsive layout: works on mobile (375px) and desktop (1280px+)
- Empty state messages when task list is empty

---

## Backend API Endpoints

```
POST /api/auth/register    - Register new user
POST /api/auth/login       - Login, return JWT
GET  /api/tasks            - Get paginated tasks (params: userName, title, status, page, size)
POST /api/tasks            - Create task
PUT  /api/tasks/{id}       - Update task
DELETE /api/tasks/{id}     - Hard delete task
PATCH /api/tasks/{id}/completion?completed=true  - Toggle completion
PATCH /api/tasks/{id}/restore                    - Restore deleted task
```

All responses use a standard wrapper:
```json
{
  "status": "success" | "error",
  "message": "...",
  "data": { ... }
}
```

Paginated responses wrap data in:
```json
{
  "content": [...],
  "totalPages": 5,
  "totalElements": 50,
  "number": 0,
  "size": 10
}
```

---

## Docker Setup
- `docker-compose.yml` at root: starts postgres (port 5434) and backend (port 8080)
- Backend Dockerfile: multi-stage build (Maven JDK 21 for build, JRE 21 slim for run)
- `init-sample-data.sql` mounted into postgres container for initial seed data
- Health check on postgres using `pg_isready` before starting backend

---

## Code Quality
- Backend: layered architecture (Controller → Service Interface + Impl → Repository → Entity)
- DTOs for all request/response objects (no exposing Entity directly)
- Input validation with `@Valid` and `@NotBlank`
- Unit tests: JUnit 5 + Mockito for AuthServiceImpl and TaskServiceImpl
- Frontend: TypeScript strict, ESLint configured, API layer separated in `/api/`, types in `/types/`

---

## Additional Notes
- The `userName` field in the system is the UUID of the user (used as JWT subject), NOT the display name
- The `accountName` is what users type to log in
- Frontend Axios interceptor normalizes backend response: converts `success: true/false` boolean → `status: 'success'/'error'` string
- Backend application.properties sets timezone to UTC to avoid date parsing issues
- Frontend runs on port 5173 (Vite default), backend on 8080
