# Documentación completa del proyecto

## Sistema basado en Arquitectura Orientada a Servicios (SOA) para el monitoreo y análisis de enfermedades asociadas a la contaminación ambiental en el Hospital Regional de Ica

---

## Tabla de contenido

1. [El tema del proyecto](#1-el-tema-del-proyecto)
2. [El problema que se resuelve](#2-el-problema-que-se-resuelve)
3. [La solución planteada](#3-la-solución-planteada)
4. [Objetivos](#4-objetivos)
5. [Arquitectura del sistema (SOA)](#5-arquitectura-del-sistema-soa)
6. [Tecnologías utilizadas y por qué](#6-tecnologías-utilizadas-y-por-qué)
7. [Estructura de carpetas y su creación](#7-estructura-de-carpetas-y-su-creación)
8. [La base de datos](#8-la-base-de-datos)
9. [Los servicios y sus funcionalidades](#9-los-servicios-y-sus-funcionalidades)
10. [El análisis epidemiológico automático](#10-el-análisis-epidemiológico-automático)
11. [Seguridad](#11-seguridad)
12. [Cómo ejecutar el proyecto](#12-cómo-ejecutar-el-proyecto)
13. [Pruebas](#13-pruebas)
14. [Alcances y limitaciones](#14-alcances-y-limitaciones)
15. [Despliegue](#15-despliegue)

---

## 1. El tema del proyecto

El proyecto trata sobre la **vigilancia epidemiológica ambiental**: relacionar los datos de
salud de los pacientes con los datos de contaminación ambiental (aire, agua, suelo) para
detectar, de forma temprana, enfermedades causadas o agravadas por la contaminación.

Se ubica en el contexto del **Hospital Regional de Ica**, una región con fuerte actividad
agroindustrial (uso intensivo de agroquímicos, presión sobre el agua y el suelo) que genera
riesgos ambientales con impacto directo en la salud de la población.

---

## 2. El problema que se resuelve

Hoy, en el hospital y su entorno, **la información está fragmentada**:

- Los **datos clínicos** (pacientes, diagnósticos) viven en los sistemas del hospital.
- Los **datos ambientales** (calidad del aire, agua, residuos) los generan entidades externas
  distintas (SENAMHI, ANA, Municipalidad).

Al estar separados, **nadie puede cruzarlos**. Como consecuencia:

- No se pueden identificar zonas geográficas con alta incidencia de enfermedades.
- No hay forma de correlacionar los brotes con la contaminación de esa zona.
- Las alertas, cuando existen, se generan tarde y de forma manual.
- La toma de decisiones en salud es reactiva, no preventiva.

**En una frase:** el problema es la *ausencia de integración e interoperabilidad* entre la
información clínica y la ambiental.

---

## 3. La solución planteada

Se propone un **sistema web basado en Arquitectura Orientada a Servicios (SOA)** que:

1. **Integra** los datos clínicos y ambientales en una sola plataforma, usando la **zona
   geográfica** como punto de unión entre ambos mundos.
2. **Analiza automáticamente** la correlación entre casos de enfermedad y calidad ambiental
   por zona.
3. **Genera alertas tempranas** cuando una zona supera umbrales de riesgo.
4. **Visualiza** todo en un dashboard con mapas y gráficos para apoyar la toma de decisiones.

La elección de **SOA** no es casual: al dividir el sistema en servicios independientes y
desacoplados que se comunican por contratos estándar (REST/JSON), se resuelve directamente
el problema de fondo —la falta de interoperabilidad— y se deja el sistema preparado para
integrarse en el futuro con los sistemas reales de las entidades externas.

---

## 4. Objetivos

**Objetivo general:** desarrollar un sistema basado en SOA que permita el monitoreo, análisis
y gestión de enfermedades relacionadas con la contaminación ambiental en el HRI.

**Objetivos específicos:**

- Diseñar una arquitectura SOA que integre datos clínicos y ambientales.
- Implementar servicios para el registro y consulta de enfermedades.
- Desarrollar un módulo de análisis geográfico de zonas afectadas.
- Implementar un sistema de alertas tempranas.
- Mejorar la toma de decisiones mediante información centralizada.

---

## 5. Arquitectura del sistema (SOA)

El sistema se organiza en **tres capas**, cada una independiente:

```
┌───────────────────────────────────────────────┐
│  CAPA DE PRESENTACIÓN  —  React + Vite (:5173)  │
│  Dashboard, mapa (Leaflet), gráficos (Chart.js) │
└───────────────────────────────────────────────┘
        │  HTTP / JSON  +  token JWT (Axios)
        ▼
┌───────────────────────────────────────────────┐
│  CAPA DE SERVICIOS  —  Spring Boot (:8080)      │
│  12 servicios REST independientes y desacoplados│
│  Controller → Service → Repository              │
└───────────────────────────────────────────────┘
        │  Spring Data JPA / JDBC (SQL)
        ▼
┌───────────────────────────────────────────────┐
│  CAPA DE DATOS  —  PostgreSQL (:5432, Docker)   │
│  9 tablas relacionadas                          │
└───────────────────────────────────────────────┘
```

**Cómo se aplica SOA en concreto:**

- **Cada dominio es un servicio REST independiente** (pacientes, enfermedades, ambiental,
  diagnósticos, análisis, alertas, etc.), con su propia URL. Se pueden modificar sin afectar
  a los demás → *bajo acoplamiento*.
- **Orquestación de servicios:** el Servicio de Análisis no tiene datos propios; combina la
  información de los servicios de Diagnósticos y Ambiental, y llama al de Alertas → *composición*.
- **API-First e interoperabilidad:** todos los contratos están documentados con OpenAPI/Swagger
  y usan JSON como formato universal.

---

## 6. Tecnologías utilizadas y por qué

| Capa | Tecnología | Por qué se eligió |
|------|-----------|-------------------|
| Backend | **Java 17 + Spring Boot 3** | Estándar para construir APIs REST (base de SOA); despliega cada servicio de forma autónoma |
| | **Spring Data JPA (Hibernate)** | Traduce objetos Java a SQL automáticamente; menos código y menos errores |
| | **Spring Security + JWT** | Autenticación *stateless*: cada servicio valida el token por sí solo |
| | **Swagger / OpenAPI** | Documenta y permite probar los contratos de los servicios |
| Frontend | **React + Vite** | Interfaz por componentes reutilizables, desacoplada del backend |
| | **Leaflet.js** | Mapas interactivos open-source (visualización geográfica por zona) |
| | **Chart.js** | Gráficos estadísticos de contaminantes |
| | **Axios** | Cliente HTTP que inyecta el token JWT en cada petición |
| Base de datos | **PostgreSQL 16** | Relacional, robusto y open-source; garantiza integridad de los datos |
| Infraestructura | **Docker / Docker Compose** | Levanta todo con un comando; incluye Java 17 sin instalarlo en la máquina |
| Pruebas | **JUnit + Mockito + H2** | Validan la lógica sin depender de la base de datos real |

Todas comparten tres virtudes clave para SOA: son **open-source**, favorecen el
**desacoplamiento** y son **estándar de la industria** (mantenibles a futuro).

---

## 7. Estructura de carpetas y su creación

El proyecto separa físicamente el backend, el frontend y la infraestructura. Esta separación
**es la esencia de SOA**: cada parte se desarrolla, despliega y escala por separado.

```
mvp-monitoreo-ambiental-hri/
├── backend/            → API REST (Spring Boot)
├── frontend/           → Dashboard (React + Vite)
├── docker-compose.yml  → Orquestación de contenedores
├── vercel.json         → Configuración de despliegue del frontend
└── *.md                → Documentación
```

### 7.1 Backend — organizado por capas (responsabilidad)

Ruta: `backend/src/main/java/com/hri/monitoreo/`

```
com/hri/monitoreo/
├── MonitoreoApplication.java   → Punto de arranque de la aplicación
├── controller/   (12 archivos) → APIs REST: reciben las peticiones HTTP
├── service/      (11 archivos) → Lógica de negocio y reglas
├── repository/   ( 9 archivos) → Acceso a la base de datos (JPA)
├── entity/       (12 archivos) → Tablas y enumeraciones del dominio
├── dto/          (11 archivos) → Objetos de entrada/salida de la API
├── security/     ( 3 archivos) → JWT y autenticación
├── config/       ( 2 archivos) → Seguridad, CORS y datos iniciales
└── exception/    ( 2 archivos) → Manejo centralizado de errores
```

**Por qué se creó cada carpeta:**

| Carpeta | Motivo de su creación |
|---------|------------------------|
| `controller` | Aislar la "puerta" HTTP de la lógica. Mantiene los endpoints claros y documentables |
| `service` | Concentrar las reglas de negocio (ej. el umbral de alertas). Se pueden probar sin la web ni la BD |
| `repository` | Abstraer el acceso a datos: se declara la consulta y JPA genera el SQL |
| `entity` | Definir el modelo del dominio mapeado a las tablas. Fuente única de la estructura de datos |
| `dto` | Desacoplar la API de las tablas internas y validar la entrada. Evita exponer/alterar entidades directamente |
| `security` | Agrupar el filtro JWT y la validación de tokens, separando la seguridad del resto |
| `config` | Configuración transversal: reglas de seguridad, CORS y carga de datos de ejemplo |
| `exception` | Respuestas HTTP consistentes ante errores (404, 400, 403, 409, etc.) |

**El flujo de una petición** recorre estas capas en orden:
`Controller → Service → Repository → Base de datos`.

### 7.2 Frontend — organizado por rol en la interfaz

Ruta: `frontend/src/`

```
src/
├── main.jsx        → Punto de arranque de React
├── App.jsx         → Definición de las rutas
├── api/            → Configuración de Axios (inyecta el token JWT)
├── services/       → Funciones que llaman a cada endpoint del backend
├── context/        → Estado global de autenticación (token, usuario)
├── pages/          → Pantallas (Dashboard, Pacientes, Catálogos, etc.)
├── components/     → Piezas reutilizables (mapa, gráfico, tablas, paginador)
└── assets/         → Imágenes y recursos estáticos
```

**Por qué se creó cada carpeta:**

| Carpeta | Motivo de su creación |
|---------|------------------------|
| `api` | Centralizar la comunicación con el backend en un solo lugar |
| `services` | Separar "cómo se piden los datos" de "cómo se muestran" |
| `context` | Compartir la sesión en toda la app sin pasarla manualmente entre componentes |
| `pages` | Cada archivo es una pantalla completa ligada a una ruta |
| `components` | Reutilizar piezas (mapa, gráfico, gestor CRUD) en varias pantallas |

**La idea de fondo:** el backend se agrupa por *capa técnica* (estándar de Spring Boot) y el
frontend por *rol en la UI* (estándar de React). Ambos aplican el mismo principio: separar
responsabilidades para que el sistema sea mantenible y escalable.

---

## 8. La base de datos

Es una base de datos **relacional** (PostgreSQL), elegida porque los datos de salud tienen
relaciones estrictas que deben respetarse (un diagnóstico *pertenece* a un paciente y a una
zona). El modelo relacional garantiza esa integridad mediante **claves foráneas**.

Son **9 tablas**, cada una representa un concepto distinto (principio de normalización):

| Tabla | Descripción |
|-------|-------------|
| `usuarios` | Quién accede al sistema, con su rol (Admin, Médico, Supervisor) |
| `pacientes` | Personas atendidas y su ubicación |
| `enfermedades` | Catálogo de patologías con código CIE-10 |
| **`zonas`** | Distritos de Ica. **Tabla central** del modelo |
| `datos_ambientales` | Mediciones de calidad del aire por zona |
| `diagnosticos` | Puente que une paciente + enfermedad + zona + fecha |
| `alertas` | Avisos generados, manuales o automáticos |
| `calidad_agua` | Datos del conector externo ANA (agua) |
| `reportes_residuos` | Datos del conector Municipalidad (residuos) |

**Por qué `zonas` es la tabla central:** cinco de las nueve tablas la referencian. La zona
geográfica es lo que permite cruzar el mundo clínico (pacientes, diagnósticos) con el mundo
ambiental (aire, agua, residuos). Sin ella, el sistema no podría responder su pregunta
fundamental: *¿qué está enfermando a la gente en esta zona y por qué?*

---

## 9. Los servicios y sus funcionalidades

| Servicio | Endpoint base | Función |
|----------|---------------|---------|
| Autenticación | `/api/auth` | Login, registro y emisión de tokens JWT |
| Pacientes | `/api/pacientes` | CRUD de pacientes (paginado) |
| Enfermedades | `/api/enfermedades` | Catálogo de enfermedades |
| Zonas | `/api/zonas` | Catálogo de distritos de Ica |
| Ambiental | `/api/ambiental` | Datos de calidad del aire por zona |
| Diagnósticos | `/api/diagnosticos` | Vínculo paciente-enfermedad-zona-fecha |
| Análisis | `/api/analisis` | Correlación por zona y generación de alertas |
| Alertas | `/api/alertas` | Alertas manuales y automáticas + reportes |
| Integraciones | `/api/integraciones` | Conectores SENAMHI, ANA, Municipalidad |
| Reportes | `/api/reportes` | Exportación de reportes en CSV |
| Usuarios | `/api/usuarios` | Gestión de usuarios (solo Administrador) |
| Simulación | `/api/simulacion` | Generación de datos de prueba |

En el frontend, estos servicios se presentan como los módulos: **Dashboard, Pacientes,
Diagnósticos, Alertas, Catálogos, Integraciones y Usuarios**.

---

## 10. El análisis epidemiológico automático

Es el **núcleo funcional** del sistema y el mejor ejemplo de orquestación SOA.

**Cómo funciona:**

1. El Servicio de Análisis consulta los **diagnósticos** de cada zona (últimos 7 días).
2. En paralelo, consulta los **datos ambientales** (calidad del aire) de esa misma zona.
3. Cruza ambos y calcula un **nivel de riesgo** por zona.
4. Si una zona supera **más de 5 casos en 7 días**, invoca al Servicio de Alertas y **genera
   una alerta automáticamente**.

**Cuándo se ejecuta:**

- Automáticamente cada **30 minutos** (tarea programada `@Scheduled`).
- Manualmente con el botón **"Ejecutar análisis ahora"** en el dashboard.

Los datos de ejemplo concentran 6 diagnósticos en la zona *Subtanjalla*, de modo que al
ejecutar el análisis se demuestra la generación automática de una alerta al instante.

---

## 11. Seguridad

- **Autenticación con JWT:** al iniciar sesión, el backend entrega un token firmado que el
  frontend adjunta en cada petición.
- **Autenticación stateless:** el servidor no guarda sesiones; cada servicio valida el token
  por sí mismo (ideal para SOA).
- **Control de acceso por roles (RBAC):** Administrador, Médico y Supervisor. Por ejemplo, la
  gestión de usuarios está restringida solo al Administrador (responde 403 a los demás).
- **Validación de entradas** en los endpoints y **manejo centralizado de errores** con
  respuestas HTTP claras (401, 403, 404, 409, etc.).

---

## 12. Cómo ejecutar el proyecto

Requisitos: **Docker Desktop** y **Node.js 18+**.

```bash
# 1. Base de datos (PostgreSQL)
docker compose up -d db

# 2. Backend (Spring Boot; se compila dentro de Docker con Java 17)
docker compose --profile full up -d backend

# 3. Frontend (React + Vite)
cd frontend
npm install      # solo la primera vez
npm run dev
```

Acceder a `http://localhost:5173`.

**Credenciales de prueba:**

| Rol | Correo | Contraseña |
|-----|--------|------------|
| Administrador | `admin@hri.com` | `admin123` |
| Médico | `medico@hri.com` | `medico123` |
| Supervisor | `supervisor@hri.com` | `supervisor123` |

Documentación de la API: `http://localhost:8080/swagger-ui.html`

---

## 13. Pruebas

El backend cuenta con **29 pruebas automatizadas** que se ejecutan sobre una base de datos en
memoria (H2), sin necesidad de la base real:

- **Unitarias:** lógica del análisis (umbral de alertas, cálculo de riesgo) y del servicio JWT.
- **De integración:** endpoints de autenticación, pacientes y conectores externos.
- **De seguridad:** control de acceso por rol (un médico no puede entrar a gestión de usuarios).

Se ejecutan con `mvn test` (dentro del contenedor Maven).

---

## 14. Alcances y limitaciones

**El sistema cubre (alcance del MVP):**

- Los 12 requerimientos funcionales del proyecto (RF01–RF12).
- CRUD completo de las entidades del dominio desde la interfaz.
- Análisis automático, alertas tempranas, reportes exportables y conectores externos simulados.

**Queda fuera (definido como fase futura):**

- Conexión con **fuentes de datos reales** (se usan datos simulados por las restricciones de
  acceso a información clínica y ambiental real).
- **Alta disponibilidad** (una sola instancia, sin balanceo ni réplicas).
- **Integración real con el MINSA/GalenHos** (vía WSDL/SOAP o HL7 FHIR).

Declarar estos límites es una buena práctica: enfoca el MVP en validar la viabilidad técnica
de la arquitectura SOA y deja un camino claro hacia producción.

---

## 15. Despliegue

- **Local:** con Docker Compose (base de datos + backend) y Vite (frontend).
- **Vercel:** aloja el **frontend**. El backend debe hospedarse por separado en un servicio
  que ejecute Java/Docker, y su URL pública se configura en Vercel mediante la variable de
  entorno `VITE_API_URL`. El archivo `vercel.json` de la raíz ya define el build del frontend
  y las reescrituras SPA para que las rutas funcionen al recargar.

---

*Repositorio:* [github.com/DSRVIN/mvp-monitoreo-ambiental-hri](https://github.com/DSRVIN/mvp-monitoreo-ambiental-hri)
