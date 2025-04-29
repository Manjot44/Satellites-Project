# Satellites‑Project — *Back in Blackout*

A Java 11 simulation of an **Earth‑orbit satellite communication network** with a RESTful API.  
Originally built for the UNSW COMP2511 assignment **“Back in Blackout”**, the project models devices and satellites in polar orbits, tracks their motion, and enforces line‑of‑sight rules for file transfer.

---
## ✨ Key Features
| Domain object | Variants | Highlights |
| --- | --- | --- |
| **Satellites** | *Standard, Relay, Teleporting* | Orbital mechanics, height & angular velocity, dynamic visibility cones |
| **Devices** | *Handheld, Laptop, Desktop* | Optional mobility, local file storage |
| **File Transfer** | Device ↔ Satellite, Satellite ↔ Satellite | Chunked transmission, bandwidth & range checks, custom exceptions |
| **Simulation Engine** | Minute‑by‑minute | Updates positions, recalculates connectivity graph |
| **REST API** | Spark Java | Create / query / simulate entities, upload & send files |

---
## 🗂 Project Layout
```
src/
 ├─ main/
 │   ├─ java/
 │   │   └─ unsw/
 │   │       ├─ blackout/        # Domain logic (Satellites, Devices, Controller)
 │   │       ├─ response/models/ # DTOs returned by the API
 │   │       ├─ utils/           # Angle maths & helpers
 │   │       └─ App.java         # SparkJava entry‑point
 │   └─ resources/               # Static web client (optional)
 └─ test/blackout/               # JUnit 5 test‑suite
build.gradle                      # Gradle 7 project file (Java 11)
```

---
## 🚀 Getting Started
> **Prerequisites:** JDK 11+, Git, and an Internet connection (Gradle downloads dependencies on first run).

```bash
# 1  Clone
$ git clone https://github.com/Manjot44/Satellites-Project.git
$ cd Satellites-Project

# 2  Run the simulation server (default port 4567)
$ ./gradlew run        # Linux / macOS
# or
$ gradlew.bat run      # Windows
```
After the build completes you’ll see SparkJava start‑up logs like:
```
== Spark has ignited ... Listening on 0.0.0.0:4567 ==
```

---
## ⚙️ Core API End‑points
| Verb | Path | Query / Body Params | Purpose |
| --- | --- | --- | --- |
| `PUT` | `/api/device` | `deviceId, type, position, isMoving` | Create a device |
| `PUT` | `/api/satellite` | `satelliteId, type, height, position` | Create a satellite |
| `DELETE` | `/api/device` / `/api/satellite` | `id` | Remove entity |
| `GET` | `/api/entity/info` | `id` | Return `EntityInfoResponse` |
| `GET` | `/api/entity/entitiesInRange` | `id` | List communicable neighbours |
| `POST` | `/api/sendFile` | `fileName, fromId, toId` | Begin a file transfer |
| `POST` | `/api/simulate` | `n` (minutes, optional – default 1) | Advance the simulation |

### Quick cURL example
```bash
# create a Standard satellite 35786 km high at 0 rad
curl -X PUT "localhost:4567/api/satellite?\
    satelliteId=SAT1&type=StandardSatellite&height=35786&position=0"

# create a Laptop at 1.0 rad
curl -X PUT "localhost:4567/api/device?\
    deviceId=LAP1&type=LaptopDevice&position=1.0&isMoving=false"

# run the sim for 10 minutes
curl -X POST "localhost:4567/api/simulate?n=10"
```
> **Tip:** A lightweight HTML visualiser lives in `src/main/resources/app` – open `index.html` while the server is running to watch orbits in real‑time.

---
## 🧪 Running Tests
JUnit 5 tests validate orbital maths, connectivity rules, and file‑transfer edge cases.

```bash
# Execute the full test‑suite
$ ./gradlew test

# View the HTML report (build/reports/tests/test/index.html)
$ open build/reports/tests/test/index.html   # macOS example
```
The current suite covers **Task 1 & Task 2** specifications and serves as regression protection during refactors.

---
## 📝 Code Quality
* **Checkstyle** enforced via `checkstyle.xml` → `./gradlew check`.
* Java 11 source‑compatibility (`build.gradle`).
* Uses **Gson** for JSON serialization (custom `Angle` adapter) and **SparkJava** for the embedded HTTP server.

---
## 📄 Design Notes
A high‑level UML class diagram and behavioural discussion are available in **design.pdf**.  
Key design decisions:
* **Controller pattern**: `BlackoutController` orchestrates domain objects and acts as the API façade.
* **Strategy–style polymorphism** for satellite/device types (`extends Satellite`, `extends Device`).
* **Pure functions in `utils`** for spherical trigonometry, allowing easy unit‑testing.

---
## 📚 Assignment Context
This repository implements the publicly‑released *Back in Blackout* specification (link in the legacy README).  
Task 3 hooks (`createDevice` mobility & sloped terrain) are scaffolded but not required for core functionality.

---
## 🖋 License & Attribution
Educational project – © 2025 Manjot Singh & contributors.  
*Not an official UNSW release.* Use freely for learning or demonstration purposes.

---
## 🙏 Acknowledgements
* UNSW School of Computer Science & Engineering for the original assignment brief.
* **SparkJava**, **Gson**, and **JUnit 5** open‑source communities.
