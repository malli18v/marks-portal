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
