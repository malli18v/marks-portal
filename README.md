# Marks Portal

Full-stack CRUD marks portal with:

- Spring Boot backend
- JWT authentication with Spring Security
- Teacher admin role
- Student login by roll number
- Student CRUD with pagination
- Teacher mark create, update, delete
- Student self-service marks view
- React frontend

## Demo Accounts

| Role | Username | Password |
| --- | --- | --- |
| Teacher | `teacher` | `teacher123` |
| Student | `R001` | `student123` |
| Student | `R002` | `student123` |

New students created by the teacher can log in with their roll number. If no password is entered during creation, the backend uses the roll number as the initial password.

## Backend

Requirements:

- Java 17 or newer
- Maven

```bash
cd backend
mvn spring-boot:run
```

Backend URL:

```text
http://localhost:8080
```

Useful API routes:

- `POST /api/auth/login`
- `GET /api/teacher/students?page=0&size=5`
- `POST /api/teacher/students`
- `PUT /api/teacher/students/{id}`
- `DELETE /api/teacher/students/{id}`
- `GET /api/teacher/students/{rollNumber}/marks`
- `POST /api/teacher/students/{studentId}/marks`
- `PUT /api/teacher/marks/{markId}`
- `DELETE /api/teacher/marks/{markId}`
- `GET /api/student/me/marks`

## Frontend

Requirements:

- Node.js
- npm

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

```text
http://localhost:5173
```

To point the frontend at a different backend URL:

```bash
set VITE_API_URL=http://localhost:8080/api
npm run dev
```

For PowerShell:

```powershell
$env:VITE_API_URL="http://localhost:8080/api"
npm run dev
```

## Run Both Apps

Open two terminals.

Terminal 1:

```bash
cd backend
mvn spring-boot:run
```

Terminal 2:

```bash
cd frontend
npm install
npm run dev
```

Then open:

```text
http://localhost:5173
```

## Database

The app uses an in-memory H2 database for quick development. Data is seeded on startup and resets when the backend restarts.

H2 console:

```text
http://localhost:8080/h2-console
```

JDBC URL:

```text
jdbc:h2:mem:marksdb
```
## Docker

Build and run the full application with Docker Compose:

```bash
docker compose up --build
```

Then open:

```text
http://localhost
```

The frontend Nginx container serves React and proxies `/api` requests to the Spring Boot backend container.

## AWS EC2 Deployment

This is the simplest AWS deployment path for this project.

1. Create an EC2 instance.
   - Recommended AMI: Ubuntu Server 22.04 LTS or 24.04 LTS
   - Instance size for demo: `t2.micro` or `t3.micro`
   - Security group inbound rules: SSH `22` from your IP, HTTP `80` from anywhere

2. SSH into the instance:

```bash
ssh -i your-key.pem ubuntu@your-ec2-public-ip
```

3. Install Docker and Git:

```bash
sudo apt update
sudo apt install -y docker.io docker-compose-v2 git
sudo usermod -aG docker ubuntu
newgrp docker
```

4. Clone the repository:

```bash
git clone https://github.com/malli18v/marks-portal.git
cd marks-portal
```

5. Start the app:

```bash
export JWT_SECRET="replace-with-a-long-random-secret"
docker compose up --build -d
```

6. Open the app:

```text
http://your-ec2-public-ip
```

Useful server commands:

```bash
docker compose ps
docker compose logs -f
docker compose down
git pull
docker compose up --build -d
```

For a production deployment, replace the in-memory H2 database with Amazon RDS PostgreSQL or MySQL so data survives container restarts.
