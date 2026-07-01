# Sistema SOA de Monitoreo y Análisis de Enfermedades por Contaminación Ambiental
### Hospital Regional de Ica

Plataforma basada en **Arquitectura Orientada a Servicios (SOA)** que integra datos clínicos
y ambientales del Hospital Regional de Ica para detectar zonas críticas y generar alertas
tempranas ante enfermedades asociadas a la contaminación (respiratorias, dérmicas, gastrointestinales).

## Stack tecnológico

| Capa        | Tecnología                                              |
|-------------|---------------------------------------------------------|
| Backend     | Java 17 + Spring Boot 3 (REST, JPA, Spring Security/JWT) |
| Frontend    | React + Vite, Leaflet.js (mapas), Chart.js (gráficos)   |
| Base de datos | PostgreSQL 16                                         |
| Documentación API | Swagger / OpenAPI (springdoc)                     |
| Infra local | Docker Compose                                          |

## Estructura del proyecto

```
mvp-monitoreo-ambiental-hri/
├── backend/            # API REST Spring Boot
├── frontend/           # Dashboard React + Vite
├── docker-compose.yml  # PostgreSQL + pgAdmin (+ backend opcional)
└── README.md
```

## Inventario de servicios SOA

| Servicio | Responsabilidad |
|---|---|
| Servicio de pacientes | CRUD de pacientes, ubicación y distrito de residencia |
| Servicio de enfermedades | Catálogo de enfermedades (respiratorias, dérmicas, gastrointestinales) |
| Servicio ambiental | Registro de calidad del aire por zona y fecha |
| **Servicio de análisis** | Orquesta Pacientes + Ambiental: correlaciona casos por zona y calcula nivel de riesgo |
| Servicio de alertas | Alertas manuales y automáticas (generadas cada 30 min al superar umbral) |
| Servicio de autenticación | Login, JWT, roles (Administrador, Médico, Supervisor) |

---

## Requisitos previos

- **Docker** (obligatorio para la BD; recomendado para el backend).
- **Node.js 18+** (para el frontend).
- **JDK 17 + Maven** *solo* si quieres ejecutar el backend sin Docker.
  > Nota: el backend usa Spring Boot 3, que requiere **Java 17+**. Si tu máquina tiene
  > Java 8, usa el modo Docker (la imagen compila con JDK 17 internamente).

---

## 1) Levantar la base de datos

Desde la raíz del proyecto:

```bash
docker compose up -d db
```

Esto expone PostgreSQL en `localhost:5432`:

- Base de datos: `monitoreo_db`
- Usuario: `monitoreo`
- Contraseña: `monitoreo123`

Opcional — pgAdmin en http://localhost:5050 (`admin@hri.com` / `admin123`).

## 2) Levantar el backend (Spring Boot)

**Opción A — Docker (no requiere Java/Maven local):**

```bash
docker compose --profile full up -d backend
```

**Opción B — Local (requiere JDK 17 + Maven):**

```bash
cd backend
mvn spring-boot:run
```

API disponible en http://localhost:8080/api
Documentación interactiva (Swagger UI): http://localhost:8080/swagger-ui.html

Al primer arranque se cargan usuarios y datos de ejemplo (distritos de Ica: Ica, Subtanjalla,
Parcona, La Tinguiña, Los Aquijes), con 6 diagnósticos concentrados en Subtanjalla para
demostrar la generación automática de alertas:

| Rol | Email | Contraseña |
|---|---|---|
| Administrador | `admin@hri.com` | `admin123` |
| Médico | `medico@hri.com` | `medico123` |
| Supervisor | `supervisor@hri.com` | `supervisor123` |

## 3) Levantar el frontend (React + Vite)

```bash
cd frontend
npm install     # solo la primera vez
npm run dev
```

Dashboard en http://localhost:5173 (login con cualquiera de los usuarios de prueba).

---

## Endpoints principales

| Método | Ruta                         | Descripción                       | Auth |
|--------|------------------------------|-----------------------------------|------|
| POST   | `/api/auth/register`         | Registrar usuario                 | No   |
| POST   | `/api/auth/login`            | Login (devuelve JWT)              | No   |
| GET/POST/PUT/DELETE | `/api/pacientes`      | CRUD pacientes                | JWT  |
| GET/POST/PUT/DELETE | `/api/enfermedades`   | CRUD enfermedades              | JWT  |
| GET/POST         | `/api/zonas`               | Catálogo de zonas/distritos     | JWT  |
| GET/POST/PUT/DELETE | `/api/ambiental`      | Registro de datos ambientales por zona | JWT |
| GET/POST         | `/api/diagnosticos`        | Vincula paciente + enfermedad + zona + fecha | JWT |
| GET    | `/api/analisis/zonas-criticas` | Correlación por zona (casos 7 días + ICA promedio) | JWT |
| POST   | `/api/analisis/ejecutar`   | Dispara el análisis y genera alertas manualmente | JWT |
| GET/POST/PATCH/DELETE | `/api/alertas`     | Alertas (manuales o automáticas) | JWT |
| GET/PATCH/DELETE | `/api/usuarios`          | Gestión de usuarios              | JWT (ADMIN) |

Todas las rutas (excepto `/api/auth/**` y `/swagger-ui/**`) requieren cabecera:
`Authorization: Bearer <token>`

### Generación automática de alertas (RF09)

Un job programado (`@Scheduled`) corre cada **30 minutos**: recalcula la correlación por
zona y, si una zona supera **5 casos en los últimos 7 días** (RN02), genera una alerta
automáticamente (evitando duplicados si ya existe una pendiente para esa zona). Puedes
forzar la ejecución sin esperar con `POST /api/analisis/ejecutar`.

---

## Variables de entorno

**Backend** (con valores por defecto en `application.yml`):
`DB_URL`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_EXPIRATION_MS`, `CORS_ORIGINS`.

**Frontend** (`frontend/.env`): `VITE_API_URL` (por defecto `http://localhost:8080/api`).

> En producción, define `JWT_SECRET` como un secreto Base64 largo y único; no uses el valor por defecto.
