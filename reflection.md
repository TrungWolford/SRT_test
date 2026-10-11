# AI Usage Reflection

## How did you break down the problem before prompting?

Before writing any prompt, I followed a structured engineering process:

- **Clarify requirements & define scope** — Read the spec to identify exactly what to build and pick the right tech stack (Spring Boot + React + PostgreSQL).
- **Identify actors & prioritize features** — Mapped the main actors (authenticated user, guest) and ranked features by priority to know which needed the most careful review.
- **Define input/output per feature** — Wrote down the exact request/response shape for every endpoint before writing any code.
- **Design system architecture** — Chose a layered backend (Controller → Service → Repository) and Redux + Axios-interceptor frontend so the AI had a clear skeleton to fill.
- **Design the database schema** — Sketched the `users` and `tasks` tables with fields and FK relationship on paper first.
- **Write workflows & sequence diagrams** — Traced auth and CRUD flows step-by-step to catch edge cases (soft-delete, JWT subject = UUID).
- **Draft the API contract** — Listed every route, method, params, and response wrapper before prompting.
- **Set up the environment** — Created the Git repo and installed packages so the AI filled in logic, not scaffolding.

## What did the AI get wrong, and how did you fix it?

1. **Timezone mismatch in tests** — AI generated `TaskServiceImplTest` with hardcoded dates that broke under different JVM timezones. I fixed it by adding `TimeZone.setDefault(UTC)` in `@BeforeAll` and setting `hibernate.jdbc.time_zone=UTC` in `application.properties`.

2. **Axios response shape mismatch** — The backend returns `{ success: boolean }` but the frontend types expected `{ status: 'success'|'error' }`. AI generated both layers in isolation and never aligned them. I manually wrote the interceptor that transforms the field, since it required seeing both sides at once.

## What did you deliberately NOT delegate to AI, and why?

- **System architecture design** — I never let AI decide the overall architecture. AI generates code in a "fast-food" style based on familiar patterns; it has no strategic view of long-term maintainability, scalability, or system-wide resource trade-offs. That decision had to be mine.
- **Security & sensitive credential handling** — I did not delegate security-critical logic to AI unsupervised. AI frequently produces code with subtle vulnerabilities (SQL Injection, XSS) or accidentally hard-codes API keys, passwords, and auth tokens directly into source files. I reviewed every security boundary — JWT filter, BCrypt usage, CORS config, and per-user data isolation — by hand before trusting the output.

## What would you do differently with more time?

- **Integrate caching** — I would add a caching layer (e.g., Redis) to reduce repeated database hits on frequently-read task queries and improve overall response time.
- **Add performance testing with Gatling** — I would write Gatling scenarios to cover load tests, smoke tests, and stress tests, giving concrete data on how the system behaves under different traffic conditions before going to production.
