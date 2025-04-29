# Satellites-Project — *Back in Blackout*

A Java 11 simulation of an **Earth-orbit satellite communication network** with a RESTful API.  
The project models devices and satellites in polar orbits, tracks their motion, and enforces line-of-sight rules for file transfer.

---
## ✨ Key Features
| Domain object | Variants | Highlights |
| --- | --- | --- |
| **Satellites** | *Standard, Relay, Teleporting* | Orbital mechanics, height & angular velocity, dynamic visibility cones |
| **Devices** | *Handheld, Laptop, Desktop* | Optional mobility, local file storage |
| **File Transfer** | Device ↔ Satellite, Satellite ↔ Satellite | Chunked transmission, bandwidth & range checks, custom exceptions |
| **Simulation Engine** | Minute-by-minute | Updates positions, recalculates connectivity graph |
| **REST API** | Spark Java | Create / query / simulate entities, upload & send files |

---
## 🗂 Project Layout
```
src/
 ├─ main/
 │   ├─ java/
 │   │   └─ unsw/
 │   │       ├─ blackout/        # Domain logic (Satellites, Devices, Controller)
 │   │       ├─ response/models/ # DTOs returned by the API
 │   │       ├─ utils/           # Angle maths & helpers
 │   │       └─ App.java         # SparkJava entry-point
 │   └─ resources/               # Static web client (optional)
 └─ test/blackout/               # JUnit 5 test-suite
build.gradle                      # Gradle 7 project file (Java 11)
```

---
## 🚀 Getting Started
> **Prerequisites:** JDK 11+, Git, and Gradle installed.

```bash
# 1  Clone the repository
$ git clone https://github.com/Manjot44/Satellites-Project.git
$ cd Satellites-Project

# 2  Run the simulation server (default port 4567)
$ gradle run
```
The backend server and web interface will now be running.

---
## ⚙️ Core API Endpoints
| Verb | Path | Query / Body Params | Purpose |
| --- | --- | --- | --- |
| `PUT` | `/api/device` | `deviceId, type, position, isMoving` | Create a device |
| `PUT` | `/api/satellite` | `satelliteId, type, height, position` | Create a satellite |
| `DELETE` | `/api/device` / `/api/satellite` | `id` | Remove an entity |
| `GET` | `/api/entity/info` | `id` | Return `EntityInfoResponse` |
| `GET` | `/api/entity/entitiesInRange` | `id` | List communicable neighbours |
| `POST` | `/api/sendFile` | `fileName, fromId, toId` | Begin a file transfer |
| `POST` | `/api/simulate` | `n` (minutes, optional) | Advance the simulation |

---
## 🧪 Running Tests
JUnit 5 tests validate orbital maths, connectivity rules, and file-transfer edge cases.

```bash
# Execute the full test-suite
$ ./gradlew test

# View the HTML report (build/reports/tests/test/index.html)
$ open build/reports/tests/test/index.html   # Example for macOS
```

---
## 📝 Code Quality
* **Checkstyle** enforced via `checkstyle.xml` → `./gradlew check`
* Java 11 source-compatibility (`build.gradle`)
* Uses **Gson** for JSON serialization and **SparkJava** for the embedded HTTP server.

---
## 📄 Design Notes
A detailed UML class diagram describing the system architecture is available in the included **design.pdf** file.  

Key design decisions:
* **Controller pattern**: `BlackoutController` orchestrates domain objects and acts as the API façade.
* **Strategy–style polymorphism** for satellite/device types (`extends Satellite`, `extends Device`).
* **Pure functions in `utils`** for spherical trigonometry, allowing easy unit-testing.


---
## 🙏 Acknowledgements
* UNSW School of Computer Science & Engineering for the original assignment brief.
* **SparkJava**, **Gson**, and **JUnit 5** open-source communities.

---
