# Guía de Presentación - Todo App (TechnoPartner)

## 1. Resumen del Proyecto

**To-Do App** es una aplicaciónfull-stack para gestión de tareas personales con verificación de cuenta mediante OTP.

### Tecnologías
- **Backend:** Spring Boot 4.0.5 + Java 21
- **Frontend:** Angular 21
- **Base de datos:** PostgreSQL
- **Caché/OTP:** Redis
- **Documentación API:** Swagger OpenAPI

---

## 2. Arquitectura del Sistema

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  Frontend  │────▶│  REST API   │────▶│ PostgreSQL │
│  Angular  │     │  Spring    │     │            │
└─────────────┘     └──────┬──────┘     └─────────────┘
                          │
                    ┌─────┴─────┐
                    │  Redis   │
                    │(caché+OTP)│
                    └──────────┘
```

---

## 3. Características Principales

### Autenticación
- Registro/Login con JWT
- **Verificación de cuenta con OTP** (6 dígitos por email)
- Solo usuarios verificados pueden iniciar sesión

### Gestión de Tareas
- CRUD completo de tareas
- **Estados:** PENDING → IN_PROGRESS → COMPLETED | CANCELLED
- Campos calculados: días restantes, vencida, días de retraso
- **Vista de base de datos** (`v_task_details`) para queries optimizados

### cach
- Redis para caché de lista de tareas (TTL: 1-2 minutos)
- Invalidación automática en modificaciones

### Seguridad
- Spring Security con JWT Bearer
- Protección por usuario (no puedes acceder a tareas de otros)
- Rate limiting en OTP (3 intentos máx, 5 min de expiración)

---

## 4. Estructura de Código (Backend)

```
src/main/java/com/technopartner/todo_app/
├── controller/    # AuthController, TaskController
├── service/      # AuthService, TaskService
├── entity/      # User, Task
├── dto/         # Request/Response objects
├── repository/  # JPA repositories
├── security/    # JWT filter, utils
├── config/      # Security, Redis, CORS, Swagger
├── enums/       # TaskStatus, TaskStatusTransition
└── exception/  # GlobalExceptionHandler, ApiException
```

---

## 5. Puntos Clave para la Entrevista

### ¿Qué destacar?

1. ** Sistema OTP con Redis**
   - Código de 6 dígitos, expires en 5 min
   - Rate limiting (3 intentos)
   - Email asíncrono

2. **Transiciones de Estado**
   - FSM (Finite State Machine) para tareas
   - Solo permiten transiciones válidas via enum `TaskStatusTransition`

3. **Vista de Base de Datos**
   - `v_task_details` calcula días restantes/vencida en SQL
   - Optimiza queries en lugar de calcular en Java

4. **Caché con Spring Data Redis**
   - TTL corto para datos que cambian frecuentemente
   - Invalidación manual

5. **Separación de Responsabilidades**
   - DTOs para request/response
   - Servicios con @Transactional

---

## 6. Endpoints Principales

| Método | Endpoint | Descripción |
|--------|---------|------------|
| POST | `/api/auth/register` | Registrar usuario |
| POST | `/api/auth/verify` | Verificar con OTP |
| POST | `/api/auth/login` | Iniciar sesión |
| POST | `/api/tasks` | Crear tarea |
| GET | `/api/tasks` | Listar tareas |
| PATCH | `/api/tasks/{id}/status` | Cambiar estado |
| GET | `/api/tasks/details` | Vista con campos calculados |

**Swagger:** `http://localhost:9090/swagger-ui.html`

---

## 7. Cómo Demo en Vivo

1. **Iniciar servicios:**
   ```bash
   # PostgreSQL + Redis
   docker-compose up -d
   
   # Backend
   ./mvnw spring-boot:run
   
   # Frontend
   cd todo-app-web && npm start
   ```

2. **Registrar usuario** (recibes OTP por email)

3. **Verificar cuenta** con OTP

4. **Crear tarea** y cambiar estados

5. **Probar endpoints** en Swagger

---

## 8. Preguntas Técnicas Posibles

### Q: ¿Por qué usar OTP en lugar de solo contraseña?
**R:** Añade una capa extra de seguridad verificando que el email es válido.

### Q: ¿Cómo manejan la concurrencia en OTP?
**R:** Redis con TTL automático expira códigos. Rate limiting controla intentos.

### Q: ¿Qué pasa si el usuario no recibe el email OTP?
**R:** Endpoint `/api/auth/resend-otp` permite reenviar (1 cada 5 min).

### Q: ¿Cómo optimizaron las queries de tareas?
**R:** Vista SQL `v_task_details` con campos calculados evita operaciones en Java.

### Q: ¿Por qué separate cache layer?
**R:** Redis almacena código OTP y caché de tareas. Separa concerns con diferentes TTLs.

### Q: ¿Cómo validan transiciones de estado?
**R:** Enum `TaskStatusTransition` define todas las transiciones válidas.

### Q: ¿Qué pasa si dos usuarios crean tarea con mismo título?
**R:** Cada usuario tiene sus propias tareas. Filtrado por `user_id`.

---

## 9. Mejoras Futuras (sugerencias)

- Tests unitarios y de integración
- Frontend Angular más completo
- Notificaciones remindes
- Roles (admin, user)
- Deploy con Docker/K8s

---

## 10. Links Importantes

- **Swagger API:** `http://localhost:9090/swagger-ui.html`
- **Documentación API:** `API_DOCUMENTATION.md`
- **Código Backend:** `todo-app/src/main/java/`
- **Código Frontend:** `todo-app-web/src/app/`

---

*¡Buena suerte con la entrevista!*