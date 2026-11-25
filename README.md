# Dorm Management System

A Java application for dormitory operations, supporting anonymous maintenance complaint submission, room capacity management, student community discussions, inspection scheduling, socket client-server networking, and MySQL database persistence.

## Who is it for?

- **Students:** Submit anonymous maintenance complaints about dorm issues (clogged sinks, ceiling leaks, heating/electrical problems), participate in community forum discussions, and view administrative notices.
- **Administrators:** Review incoming complaints, assign maintenance personnel, update issue resolution lifecycle, manage room occupancy/allocations, and schedule room inspections.

---

## How to Run

### Step 1: Open Terminal in Project Directory
Make sure your terminal is inside the project folder:
```powershell
cd C:\Users\girja\IdeaProjects\myproject
```

> **Note:** A local `mvn.cmd` wrapper is included in the project root that automatically uses IntelliJ's bundled Maven and JDK 21. Use `.\mvn` instead of `mvn` in PowerShell.

---

### Option A: Run via Terminal (PowerShell)

#### 1. Run All Tests (30 tests)
```powershell
.\mvn clean test
```

#### 2. Run Console Application (Student Mode — default)
```powershell
.\mvn exec:java "-Dexec.mainClass=com.dormmanager.Main"
```

#### 3. Run Console Application (Admin Mode)
```powershell
.\mvn exec:java "-Dexec.mainClass=com.dormmanager.Main" "-Dexec.args=--mode=admin"
```

#### 4. Run with Custom Config File
```powershell
.\mvn exec:java "-Dexec.mainClass=com.dormmanager.Main" "-Dexec.args=--config=src/main/resources/config-admin.properties"
```

---

### Option B: Run Inside IntelliJ IDEA (No Terminal Needed)
1. Open IntelliJ IDEA.
2. In the Project tree, navigate to `src/main/java/com/dormmanager/Main.java`.
3. Click the **green Play button** ▶ next to `public class Main` or `public static void main`.
4. To pass arguments like `--mode=admin`:
   - Click the run configuration dropdown at the top &rarr; **Edit Configurations...**
   - In **Program arguments**, enter: `--mode=admin`
   - Click **OK** and Run.

---

### (Optional) Make global `mvn` work anywhere in PowerShell
If you want to type bare `mvn` from any folder without `.\`:
Run this one line in PowerShell once:
```powershell
[Environment]::SetEnvironmentVariable("PATH", $env:PATH + ";C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.3\plugins\maven-plugin\lib\maven3\bin", [EnvironmentVariableTarget]::User)
```

---

## Final Project Requirements Status (Menu: 0 to 13 Points)

| # | Requirement | Points | Status | Where it lives in the project |
|---|-------------|--------|--------|-------------------------------|
| 1 | **Presentation** | 2p | Pending | Presented live during final evaluation |
| 2 | **Tests** (&ge;1 test/constructor, &ge;3 behavior tests) | 2p | ✅ **Implemented** | 30 tests in `src/test/java/com/dormmanager/` covering every entity constructor (`ComplaintTest`, `RoomTest`, `StudentTest`, `DormAdminTest`, `MaintenanceStaffTest`, `RoomInspectionTest`, `AnnouncementTest`, `ForumTest`) and behavioral flows (`DormServiceTest`, `DormSocketAndThreadTest`). |
| 3 | **Threads** (speed up processing / serve several clients) | 1p* | ✅ **Implemented** | [`DormServer`](src/main/java/com/dormmanager/net/DormServer.java) uses an `ExecutorService` thread pool to serve multiple concurrent incoming client socket connections simultaneously. |
| 4 | **Sockets** (client-server communication) | 1p* | ✅ **Implemented** | Dedicated TCP Socket client-server protocol implemented in [`DormServer`](src/main/java/com/dormmanager/net/DormServer.java) and [`DormClient`](src/main/java/com/dormmanager/net/DormClient.java). Verified via concurrent automated socket tests in [`DormSocketAndThreadTest`](src/test/java/com/dormmanager/net/DormSocketAndThreadTest.java). |
| 5 | **Database** (tables for domain model) | 0p* | ✅ **Implemented** | Relational MySQL schema in [`src/main/resources/schema.sql`](src/main/resources/schema.sql) defining tables for `rooms`, `students`, `complaints` (anonymous), `administrators`, `maintenance_staff`, `announcements`, `forum_posts`, `forum_replies`, and `room_inspections`. |
| 6 | **JDBC: Query data** | 1p* | ✅ **Implemented** | Querying methods (`findAll()`, `findById()`, `findAllPosts()`) in [`ComplaintDbRepository`](src/main/java/com/dormmanager/db/ComplaintDbRepository.java), [`RoomDbRepository`](src/main/java/com/dormmanager/db/RoomDbRepository.java), and [`ForumDbRepository`](src/main/java/com/dormmanager/db/ForumDbRepository.java). |
| 7 | **JDBC: Insert or update data** | 1p* | ✅ **Implemented** | Data modification methods (`insert()`, `updateStatus()`, `assignStaff()`, `insertPost()`, `insertReply()`) in the repository classes using prepared statements. |
| 8 | **Users / roles with access levels** | 1p* | ✅ **Implemented** | Differentiated access levels for Student vs Administrator handled across model, configuration (`AppConfig`), and interface workflows. |
| 9 | **Input validation before DB** | 1p* | ✅ **Implemented** | Strict parameter sanitization and validation in [`InputValidator`](src/main/java/com/dormmanager/validation/InputValidator.java) before any data is passed into SQL statements. |
| 10 | **JavaFX scenes (3 scenes)** | 3p* | ⏳ *Next Step* | Planned: Login scene, Student anonymous submission scene, Admin dispatch dashboard scene. |
| 11 | **REST endpoints** | 3p* | — | *Alternative option (we chose option 10 - JavaFX).* |
| 12 | **Thymeleaf views** | 3p* | — | *Alternative option (we chose option 10 - JavaFX).* |

---

## Project Structure

```
src/
├── main/
│   ├── java/com/dormmanager/
│   │   ├── Main.java
│   │   ├── config/
│   │   │   └── AppConfig.java
│   │   ├── db/
│   │   │   ├── ComplaintDbRepository.java
│   │   │   ├── DatabaseManager.java
│   │   │   ├── ForumDbRepository.java
│   │   │   └── RoomDbRepository.java
│   │   ├── exception/
│   │   │   ├── DataLoadException.java
│   │   │   ├── DuplicateComplaintException.java
│   │   │   ├── InvalidInputException.java
│   │   │   ├── InvalidRoomException.java
│   │   │   └── StudentNotFoundException.java
│   │   ├── interfaces/
│   │   │   ├── Manageable.java
│   │   │   └── Reportable.java
│   │   ├── model/
│   │   │   ├── Announcement.java
│   │   │   ├── Complaint.java
│   │   │   ├── ComplaintCategory.java
│   │   │   ├── ComplaintStatus.java
│   │   │   ├── DormAdmin.java
│   │   │   ├── ForumPost.java
│   │   │   ├── ForumReply.java
│   │   │   ├── InspectionStatus.java
│   │   │   ├── MaintenanceStaff.java
│   │   │   ├── Priority.java
│   │   │   ├── Room.java
│   │   │   ├── RoomInspection.java
│   │   │   └── Student.java
│   │   ├── net/
│   │   │   ├── DormClient.java
│   │   │   └── DormServer.java
│   │   ├── service/
│   │   │   ├── AnnouncementService.java
│   │   │   ├── ComplaintService.java
│   │   │   ├── FileStorageService.java
│   │   │   ├── ForumService.java
│   │   │   ├── InspectionService.java
│   │   │   ├── RoomService.java
│   │   │   └── StudentService.java
│   │   └── validation/
│   │       └── InputValidator.java
│   └── resources/
│       ├── config-admin.properties
│       ├── config.properties
│       ├── db.properties
│       └── schema.sql
└── test/
    └── java/com/dormmanager/
        ├── model/
        │   ├── AnnouncementTest.java
        │   ├── ComplaintTest.java
        │   ├── DormAdminTest.java
        │   ├── ForumTest.java
        │   ├── MaintenanceStaffTest.java
        │   ├── RoomInspectionTest.java
        │   ├── RoomTest.java
        │   └── StudentTest.java
        ├── net/
        │   └── DormSocketAndThreadTest.java
        └── service/
            └── DormServiceTest.java
```
