# Hostel Management
backend/ (Spring Boot, Java 17) + frontend/ (HTML/CSS/JS). In production the backend serves the frontend, so it's one service.

## Roles and features
- Owner login: `owner` / `owner123` on a fresh database. Set `OWNER_PASSWORD` before the first startup to choose a different password.
- Hostelers can create their own accounts or be added by the owner. The fresh-database demo login is `hosteler` / `hosteler123`; set `HOSTELER_PASSWORD` before first startup to change it.
- Owners can create hosteler accounts, assign rooms, message residents, create fee records, verify payment requests, manage meal times, and respond to complaints.
- Hostelers can view their assigned room, inbox, fees, meal schedule, and complaint status, submit payment requests, and file complaints.
- Fee records do not process money. A hosteler submits a payment request and the owner confirms or declines it after receiving payment through an external method.

The legacy `admin` account is treated as an owner if it already exists in an older database. Demo passwords should be changed before deployment.

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

## Push updates
From this repository directory, stage and push a change:
```powershell
git add .
git commit -m "Describe the change"
git push
```

## Create a ZIP
From this repository directory, create an archive of the latest commit without the `.git` folder or local generated data:
```powershell
git archive --format=zip --output=../hostel-management.zip HEAD
```
