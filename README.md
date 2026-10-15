# Hostel Management
backend/ (Spring Boot, Java 17) + frontend/ (HTML/CSS/JS). In production the backend serves the frontend, so it's one service.
Logins: admin/admin123 (full access), student/student123 (read-only)

## Run locally
- `cd backend && mvn spring-boot:run` -> http://localhost:8080 (frontend is NOT bundled locally; run it separately:)
- `cd frontend && python3 -m http.server 3000` -> http://localhost:3000 (auto-uses API at :8080)
Local DB = embedded H2 file, no setup.

## Deploy on Render (Blueprint)
1. Push this folder (containing `render.yaml`, `Dockerfile`, `backend/`, `frontend/`) to a GitHub repo.
2. Render dashboard -> New -> Blueprint -> select the repo -> Apply.
   It creates a free Postgres DB (`hostel-db`) and the web service, auto-generates `APP_SECRET`, and wires `DATABASE_URL`.
3. Open the service URL once build finishes (~5 min). Done.

Notes: free web services sleep after inactivity (first request is slow); free Postgres expires after 30 days on Render's free tier - upgrade or recreate if needed.
Change the default passwords after first login/seed (edit seed in HostelApplication.java before deploying).
