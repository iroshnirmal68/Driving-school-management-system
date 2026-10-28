# DriveSafe Pro — Spring Boot + HTML frontend

A Maven project for IntelliJ IDEA using Java 17, Spring Boot, a responsive static HTML/CSS/JavaScript frontend, REST endpoints, and a file-backed H2 database.

## Open and run
1. Extract `DriveSafePro-IntelliJ.zip`.
2. Open the `DriveSafePro-IntelliJ` folder in IntelliJ IDEA.
3. Use JDK 17 or newer and allow IntelliJ to import the Maven project.
4. Run `src/main/java/com/drivesafe/pro/DriveSafeApplication.java`, or run `mvn spring-boot:run` from the project root.
5. Open http://localhost:8080/

## Demo login
- Username: `admin`
- Password: `password`
- Choose Administrator, Instructor, or Student in the UI. The current demo endpoint validates the shared demo account and returns the selected role.

## Backend endpoints
- `GET /api/health` — health check
- `GET /api/data` — retrieve students, instructors, vehicles, lessons and payments
- `PUT /api/data` — persist all five collections
- `POST /api/auth/login` — demo authentication

H2 console: http://localhost:8080/h2-console  
JDBC URL: `jdbc:h2:file:./data/drivesafe` | User: `sa` | Password: (blank)

## Important security note
This is a coursework/demo foundation, not production authentication. The demo uses a shared plaintext credential and does not issue a session/JWT or enforce backend role authorization. Before public deployment, implement Spring Security, BCrypt password hashes, per-user accounts, session/JWT handling, server-side role authorization, CSRF protections, input validation, and audit logging. Do not expose the H2 console in production.

## Notes
The original DriveSafe Pro interface is kept in `src/main/resources/static/index.html`. Data loads from the Spring Boot API and saves to H2; browser localStorage is retained only as a fallback cache if the backend is unavailable. Internet access is required for the existing Font Awesome and Google Fonts CDN references.
