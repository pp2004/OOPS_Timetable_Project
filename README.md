# Time Table Builder

## Overview
The **Time Table Builder** is a Java Swing-based desktop application designed to automate the creation and management of academic timetables. It supports three user roles—**Admin**, **Teacher**, and **Student**—and enforces constraints to prevent scheduling conflicts for instructors and classrooms.

### Key Features
- **Role-based Authentication** (Admin, Student)
- **Classroom Management** (capacity, AV requirements, computer/kits count)
- **Course Management** (lecture/lab hours, assigned instructors)
- **Conflict Detection** for instructors, courses, and rooms
- **Manual Routine Editing** with real-time validation
- **Auto-suggestion Engine** with **Next** / **Previous** suggestions
- **Persistence**: save/load timetable in custom file format
- **OOPS Concepts** demonstrated with explicit file mappings

---
## Prerequisites
1. **Java Development Kit (JDK) 11+** installed and `java`/`javac` in PATH.
2. **Visual Studio Code** with the following extensions:
   - **Language Support for Java™ by Red Hat**
   - **Debugger for Java**
   - **Java Test Runner**
3. (Optional) **Git** for version control

---
## Project Structure
```
TimeTableBuilder/
├── README.md
├── .vscode/
│   └── launch.json       # Debug & run configurations
├── src/
│   ├── model/            # Data classes (POJOs)
│   │   ├── Classroom.java
│   │   ├── Course.java
│   │   ├── Instructor.java
│   │   ├── Student.java
│   │   ├── User.java      # base class for all users
│   │   └── TimetableEntry.java
│   ├── view/             # Swing GUI panels
│   │   ├── LoginPanel.java
│   │   ├── AdminPanel.java
│   │   ├── TeacherPanel.java
│   │   ├── StudentPanel.java
│   │   ├── CoursePanel.java
│   │   └── ClassroomPanel.java
│   ├── controller/       # Business logic & generators
│   │   ├── TimetableGenerator.java
│   │   ├── ConflictChecker.java
│   │   ├── CSVReader.java
│   │   └── PersistenceManager.java
│   └── util/             # Utility classes & exceptions
│       ├── AppConfig.java
│       ├── ValidationException.java
│   └── FileFormatException.java
└── data/                  # Sample input CSVs & output files
    ├── classrooms.csv
    ├── courses.csv
    └── instructors.csv
```

---
## Setup & Run in VS Code
1. **Clone** (or download) the repository:
   ```bash
   git clone https://github.com/your-repo/TimeTableBuilder.git
   cd TimeTableBuilder
   ```

2. **Open** the folder in VS Code:
   - `File → Open Folder... → Select TimeTableBuilder`

3. **Import** the project when prompted by the Java Extension Pack.

4. **Review** `.vscode/launch.json` for run/debug configurations:
   ```json
   {
     "configurations": [
       {
         "type": "java",
         "name": "Launch App",
         "request": "launch",
         "mainClass": "view.LoginPanel"
       }
     ]
   }
   ```

5. **Compile & Run**:
   - Press F5 or click **Run → Start Debugging**.
   - The **Login** window appears.

6. **Test Credentials**:
   - **Student**: `student` / `student123`
   - **Admin**:   `admin`   / `admin123`

---
## Data Format (CSV)
**classrooms.csv**
| id   | building | floor | capacity | hasAV | computers |
|------|----------|-------|----------|-------|-----------|
| C101 | D        | 0     | 100      | true  | 20        |

**courses.csv**
| code   | name                              | lectures | labs | students | lecturer1 | lecturer2 |
|--------|-----------------------------------|----------|------|----------|-----------|-----------|
| EEEF311| Communication Systems             | 3        | 0    | 120      | InstA     | InstB     |

**instructors.csv**
| id    | name       | role    | username | password |
|-------|------------|---------|----------|----------|
| InstA | Dr. Sharma | lecturer| sharma   | pass123  |

Place these files under `data/` before first run.

---
## Application Flow
1. **Login Panel** (`LoginPanel.java`)
   - Prompts for username/password.
   - Loads user role (Admin, Teacher, Student).

2. **Dashboard**
   - **AdminPanel**: Manage classrooms, courses, instructors; import/export CSV.
   - **TeacherPanel**: View personal timetable.
   - **StudentPanel**: Select courses, detect conflicts, generate personal timetable.

3. **TimetableGenerator** (`controller/TimetableGenerator.java`)
   - Auto-suggests valid timetable instances.
   - Supports **Next** / **Previous** suggestions.

4. **ConflictChecker** (`controller/ConflictChecker.java`)
   - Validates no overlapping for instructors, rooms, and course prerequisites.
   - Throws ValidationException on conflict.

5. **PersistenceManager** (`controller/PersistenceManager.java`)
   - Saves/loads timetable in custom `.ttb` format.
   - Wraps I/O errors in `FileFormatException`.

---

## Object-Oriented Design
| Concept                    | File(s)                                                                                         |
|----------------------------|-------------------------------------------------------------------------------------------------|
| Classes & Objects          | model/Classroom.java, model/Course.java, model/Instructor.java, model/Student.java, model/TimetableEntry.java |
| Inheritance & Polymorphism | model/User.java, model/Admin.java, model/Teacher.java, model/Student.java                       |
| Encapsulation & Data Hiding| All model classes use private fields with public getters/setters (e.g., Classroom.java)         |
| Methods & Messages         | controller/TimetableGenerator.java, controller/ConflictChecker.java                             |
| Exception Handling         | util/ValidationException.java, util/FileFormatException.java                                    |

---
## Extending & Customizing
- Add new elective categories by updating AppConfig.java.
- Extend CSV format via CSVReader.parseLine().
- Modify UI layout in Swing by editing *.java in view/.

---
## Troubleshooting
- "Class not found": Ensure src/ is marked as Java source folder.
- CSV parse errors: Check delimiter consistency (,).
- UI layout issues: Adjust setLayout() calls in panels.

---
## License
MIT License. See LICENSE for details.

Enjoy building and customizing your academic timetable!
