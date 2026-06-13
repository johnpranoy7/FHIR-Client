# HL7 FHIR Client

**Live Demo:** [https://fhir-client.onrender.com/](https://fhir-client.onrender.com/)

A **Health IT portfolio project** that demonstrates patient search and clinical document retrieval using the [HL7® FHIR®](https://www.hl7.org/fhir/) standard. The app connects to the public [HAPI FHIR R4 test server](http://hapi.fhir.org/baseR4/), fetches FHIR resources through a Spring Boot API, and presents results in a Vue 3 web UI.

**Developer:** [John Pranoy Yalla](https://github.com/johnpranoy7/) · [LinkedIn](https://www.linkedin.com/in/johnpranoy7/)

## Features

- Search patients by given name and/or family name
- View clinical documents (`DocumentReference`) for a selected patient
- Download binary document content when available
- Responsive dark UI with project info panel (endpoints, data flow, stack)
- Collapsible patient search bar after selection

## Tech Stack

| Layer | Technologies |
| --- | --- |
| Backend | Java 17, Spring Boot 4, HAPI FHIR R4 |
| Frontend | Vue 3, Vite, Element Plus, Axios |
| FHIR Server | HAPI FHIR R4 (`http://hapi.fhir.org/baseR4/`) |

## Architecture

```
FHIR Test Server → FHIRRestClient → FhirService → Mappers → DTOs → REST API → Vue UI
```

1. The backend calls the HAPI FHIR server and parses JSON with the HAPI FHIR parser (not Jackson).
2. FHIR resources are mapped to lightweight DTOs for the frontend.
3. The Vue app proxies API requests to Spring Boot during development.

## Prerequisites

- **Java 17+**
- **Maven** (or use the included `./mvnw` wrapper)
- **Node.js 18+** and npm

## Getting Started

### 1. Start the backend (port 8081)

From the project root:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

### 2. Start the frontend (port 5173)

```bash
cd frontend
npm install
npm run dev
```

If `npm install` fails with SSL certificate errors (common on some university networks):

```bash
npm install --strict-ssl=false
```

Open **http://localhost:5173** in your browser.

## API Endpoints

The Spring Boot app exposes these endpoints (also proxied via `/api` in development):

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/Patient?name={firstName}&family={lastName}` | Search patients |
| `GET` | `/DocumentReference?patient={patientId}` | List documents for a patient |
| `GET` | `/Binary/{id}` | Download binary document content |

## Project Structure

```
FHIR-Client/
├── src/main/java/com/jy/medical/fhirclient/
│   ├── api/FHIRRestClient.java      # FHIR server REST calls
│   ├── controller/ClientController.java
│   ├── service/FhirService.java
│   ├── mapper/                      # FHIR → DTO mapping
│   ├── dto/                         # API response models
│   └── config/                      # FHIR context, CORS
├── frontend/
│   ├── src/
│   │   ├── api/fhirApi.js
│   │   ├── components/              # PatientSearch, DocumentList, LeftNav
│   │   └── App.vue
│   └── vite.config.js               # Proxies /api → localhost:8081
└── pom.xml
```

## Configuration

| Setting | Location | Default |
| --- | --- | --- |
| Backend port | `src/main/resources/application.properties` | `8081` |
| FHIR server URL | `FHIRRestClient.java` | `http://hapi.fhir.org/baseR4/` |
| Frontend dev port | `frontend/vite.config.js` | `5173` |
| API proxy | `frontend/.env.development` | `/api` |

## Notes

- This app uses **public test data** on `hapi.fhir.org`. MRN and document availability may vary by patient.
- Binary downloads are only enabled when a document has a resolvable `binaryId` and is marked downloadable.
- CORS is configured for `http://localhost:5173` in `WebConfig.java`.

## Deploy to Render (Docker)

This project ships as **one Docker container**: Spring Boot serves the Vue UI and the REST API on the same URL.

### Prerequisites

- GitHub repo with this project pushed
- [Render](https://render.com) account (free Hobby plan)

### Steps

1. Push the repo to GitHub.
2. In Render: **New → Web Service**.
3. Connect your GitHub repo.
4. Configure:
   - **Runtime:** Docker
   - **Instance type:** Free
   - **Health check path:** `/health`
5. Click **Create Web Service** and wait for the first build (may take several minutes).

Render sets the `PORT` environment variable automatically. The app listens on that port.

### Free tier behavior

- The service **spins down after ~15 minutes** of inactivity.
- The **first request after idle** may take **30–90 seconds** while Spring Boot starts.
- Fine for portfolio demos; not intended for production traffic.

### Local Docker test (optional)

```bash
docker build -t fhir-client .
docker run -p 8081:8081 -e PORT=8081 fhir-client
```

Open **http://localhost:8081** — the UI and API are both served from this URL.

## Trademark Notice

HL7®, FHIR®, and the FHIR flame design are registered trademarks of [Health Level Seven International](https://www.hl7.org/). Their use in this project does not constitute endorsement by HL7.

## License

This project is for educational and portfolio purposes.
