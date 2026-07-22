# Guía de uso — Sistema SOA de Monitoreo Ambiental (HRI)

Sistema basado en **Arquitectura Orientada a Servicios (SOA)** para el monitoreo y análisis
de enfermedades asociadas a la contaminación ambiental en el Hospital Regional de Ica.

---

## 1. Accesos y credenciales

El sistema crea automáticamente tres usuarios de prueba al iniciar por primera vez:

| Rol | Correo | Contraseña |
|-----|--------|------------|
| **Administrador** | `admin@hri.com` | `admin123` |
| **Médico** | `medico@hri.com` | `medico123` |
| **Supervisor** | `supervisor@hri.com` | `supervisor123` |

> Estas credenciales son solo para desarrollo/demostración. En producción deben cambiarse.

**Rutas de acceso:**

- Frontend (dashboard): `http://localhost:5173` en local, o la URL del despliegue en Vercel.
- API REST (backend): `http://localhost:8080/api`
- Documentación de la API (Swagger): `http://localhost:8080/swagger-ui.html`
- pgAdmin (administración de la BD): `http://localhost:5050`

---

## 2. Qué puede hacer cada rol

| Acción | Administrador | Médico | Supervisor |
|--------|:---:|:---:|:---:|
| Ver dashboard, mapa y estadísticas | ✅ | ✅ | ✅ |
| Registrar/editar pacientes y diagnósticos | ✅ | ✅ | ✅ |
| Gestionar alertas | ✅ | ✅ | ✅ |
| Gestionar catálogos (enfermedades, zonas, datos ambientales) | ✅ | ✅ | ✅ |
| Ejecutar análisis y exportar reportes | ✅ | ✅ | ✅ |
| **Gestionar usuarios del sistema** | ✅ | ❌ | ❌ |

El módulo **Usuarios** solo es visible y accesible para el Administrador.

---

## 3. Alcance del funcionamiento (módulos)

### Dashboard
Panel principal con tarjetas de resumen (pacientes, enfermedades, datos ambientales,
alertas pendientes), **mapa interactivo** de contaminación por zona (Leaflet), **mapa de
calor de dengue**, **gráficos** de contaminantes (Chart.js), **simulador** de datos y el
panel de **zonas críticas**.

### Mapa de calor
Visualiza la **concentración real de casos de dengue** por distrito de Ica, con **datos
abiertos oficiales del MINSA** (2015–2024: 68,620 casos en 39 distritos). Incluye un
selector de año y una tabla de los distritos más afectados. Es la evidencia de que el
sistema puede trabajar con datos verídicos, no solo simulados.

### Pacientes
Registro, edición y eliminación de pacientes, cada uno asociado a un distrito (zona).
Listado paginado.

### Diagnósticos
Vincula un paciente con una enfermedad, una zona y una fecha. Estos registros son los que
alimentan el análisis epidemiológico.

### Alertas
Alertas **manuales** y **automáticas**. Se pueden filtrar por estado y rango de fechas,
atender, eliminar y **exportar a CSV**.

### Catálogos
Gestión CRUD completa de:
- **Enfermedades** (con código CIE-10 y contaminante asociado)
- **Zonas geográficas** (distritos de Ica con nivel de riesgo)
- **Datos ambientales** (calidad del aire por zona: PM2.5, PM10, O3, NO2, ICA)

### Integraciones (conectores externos simulados)
- **SENAMHI** — consulta simulada de calidad del aire (mock REST).
- **ANA** — carga de calidad del agua por archivo CSV.
- **Municipalidad de Ica** — registro manual de zonas de riesgo por residuos sólidos.

### Usuarios (solo Administrador)
Crear usuarios, activar/desactivar y eliminar.

---

## 4. Cómo funciona el análisis automático (el corazón del sistema)

El **Servicio de Análisis** cruza los diagnósticos de cada zona (últimos 7 días) con la
calidad del aire de esa zona y calcula un nivel de riesgo. Si una zona supera **5 casos en
7 días**, genera una **alerta automáticamente**.

- Corre solo cada **30 minutos** (tarea programada).
- Se puede disparar manualmente con el botón **"Ejecutar análisis ahora"** en el dashboard.

**Demostración rápida:** la zona *Subtanjalla* viene con 6 diagnósticos de ejemplo, por lo
que al ejecutar el análisis se genera una alerta de inmediato.

---

## 5. Cómo ejecutarlo en local

Requisitos: **Docker Desktop** y **Node.js 18+**.

```bash
# 1. Base de datos (PostgreSQL)
docker compose up -d db

# 2. Backend (Spring Boot, se compila dentro de Docker)
docker compose --profile full up -d backend

# 3. Frontend (React + Vite)
cd frontend
npm install      # solo la primera vez
npm run dev
```

Luego abrir `http://localhost:5173` e iniciar sesión con las credenciales de la sección 1.

---

## 6. Despliegue en Vercel

Vercel aloja el **frontend** (React). El backend (Spring Boot + PostgreSQL) debe hospedarse
por separado en un servicio que ejecute Java/Docker. La URL del backend se configura en
Vercel mediante la variable de entorno **`VITE_API_URL`**, que sobrescribe el valor local.

El archivo `vercel.json` de la raíz ya define el build del frontend y las reescrituras SPA
para que las rutas funcionen al recargar la página.

---

## 7. Stack tecnológico

| Capa | Tecnología |
|------|-----------|
| Backend | Java 17, Spring Boot 3, Spring Data JPA, Spring Security + JWT, Swagger/OpenAPI |
| Frontend | React, Vite, Leaflet.js (+ leaflet.heat), Chart.js, Axios |
| Base de datos | PostgreSQL 16 (relacional, 9 tablas) |
| Infraestructura | Docker / Docker Compose |
| Pruebas | JUnit 5, Mockito, H2 (29 pruebas automatizadas) |
