# To-Do App API - TechnoPartner

Documentación de endpoints de la API REST.

## Autenticación

### Registro de Usuario

Registra un nuevo usuario en el sistema.

**Endpoint:** `POST /api/auth/register`

**Request:**
```json
{
  "fullName": "Jose Perez",
  "email": "jose@example.com",
  "password": "password123"
}
```

**Response - Éxito (201):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "jose@example.com",
  "fullName": "Jose Perez"
}
```

**Response - Error (400):**
```json
{
  "status": 400,
  "message": "Error de validación",
  "errors": {
    "fullName": "El nombre es requerido",
    "email": "Email inválido",
    "password": "La contraseña debe tener al menos 6 caracteres"
  },
  "timestamp": "2026-04-09T14:00:00"
}
```

**Response - Error (400):**
```json
{
  "status": 400,
  "message": "El email ya está registrado",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

### Login de Usuario

Inicia sesión y retorna un token JWT.

**Endpoint:** `POST /api/auth/login`

**Request:**
```json
{
  "email": "jose@example.com",
  "password": "password123"
}
```

**Response - Éxito (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "userId": 1,
  "email": "jose@example.com",
  "fullName": "Jose Perez"
}
```

**Response - Error (401):**
```json
{
  "status": 401,
  "message": "Credenciales inválidas",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

## Tareas (Endpoints Protegidos)

Todos los endpoints de tareas requieren el header `Authorization: Bearer <token>`

### Crear Tarea

Crea una nueva tarea para el usuario autenticado.

**Endpoint:** `POST /api/tasks`

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "dueDate": "2026-04-15T18:00:00"
}
```

**Response - Éxito (201):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "PENDING",
  "completedAt": null,
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

**Response - Error (401):**
```json
{
  "status": 401,
  "message": "Error interno del servidor",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

### Listar Tareas

Obtiene todas las tareas del usuario autenticado.

**Endpoint:** `GET /api/tasks`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
[
  {
    "id": 1,
    "title": "Terminar proyecto",
    "description": "Completar el desarrollo del API",
    "status": "PENDING",
    "completedAt": null,
    "createdAt": "2026-04-09T14:00:00",
    "dueDate": "2026-04-15T18:00:00"
  },
  {
    "id": 2,
    "title": "Hacer pruebas",
    "description": "Testing unitario",
    "status": "COMPLETED",
    "completedAt": "2026-04-09T15:30:00",
    "createdAt": "2026-04-09T14:00:00",
    "dueDate": "2026-04-10T18:00:00"
  }
]
```

---

### Actualizar Tarea

Actualiza una tarea existente.

**Endpoint:** `PUT /api/tasks/{id}`

**Headers:** `Authorization: Bearer <token>`

**Request:**
```json
{
  "title": "Terminar proyecto actualizado",
  "description": "Nueva descripción",
  "dueDate": "2026-04-20T18:00:00"
}
```

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto actualizado",
  "description": "Nueva descripción",
  "status": "PENDING",
  "completedAt": null,
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-20T18:00:00"
}
```

**Response - Error (404):**
```json
{
  "status": 404,
  "message": "Tarea no encontrada",
  "timestamp": "2026-04-09T14:00:00"
}
```

**Response - Error (403):**
```json
{
  "status": 403,
  "message": "No tienes permiso para modificar esta tarea",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

### Cambiar Estado de Tarea

Cambia el estado de una tarea usando parámetro de query.

**Endpoint:** `PATCH /api/tasks/{id}/status?status=COMPLETED`

**Headers:** `Authorization: Bearer <token>`

**Valores válidos para status:**
- `PENDING` - Pendiente
- `IN_PROGRESS` - En progreso
- `COMPLETED` - Completada
- `CANCELLED` - Cancelada

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "COMPLETED",
  "completedAt": "2026-04-09T15:45:00",
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

**Response - Error (400) - Transición inválida:**
```json
{
  "status": 400,
  "message": "No se puede transiciónar de 'Completada' a 'En progreso'",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

### Iniciar Tarea

Cambia el estado a IN_PROGRESS.

**Endpoint:** `PATCH /api/tasks/{id}/start`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "IN_PROGRESS",
  "completedAt": null,
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

---

### Completar Tarea

Cambia el estado a COMPLETED.

**Endpoint:** `PATCH /api/tasks/{id}/complete`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "COMPLETED",
  "completedAt": "2026-04-09T15:45:00",
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

---

### Cancelar Tarea

Cambia el estado a CANCELLED.

**Endpoint:** `PATCH /api/tasks/{id}/cancel`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "CANCELLED",
  "completedAt": null,
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

---

### Reabrir Tarea

Cambia el estado a PENDING (solo desde CANCELLED).

**Endpoint:** `PATCH /api/tasks/{id}/reopen`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
{
  "id": 1,
  "title": "Terminar proyecto",
  "description": "Completar el desarrollo del API",
  "status": "PENDING",
  "completedAt": null,
  "createdAt": "2026-04-09T14:00:00",
  "dueDate": "2026-04-15T18:00:00"
}
```

**Response - Error (400):**
```json
{
  "status": 400,
  "message": "No se puede transiciónar de 'Completada' a 'Pendiente'",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

### Eliminar Tarea

Elimina una tarea existente.

**Endpoint:** `DELETE /api/tasks/{id}`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (204):**
```
(no content)
```

**Response - Error (404):**
```json
{
  "status": 404,
  "message": "Tarea no encontrada",
  "timestamp": "2026-04-09T14:00:00"
}
```

---

## Diagrama de Transiciones de Estado

```
                    ┌─────────────┐
                    │   PENDING   │
                    └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
     ┌────────────┐ ┌───────────┐ ┌───────────┐
     │IN_PROGRESS │ │ CANCELLED │ │  (other)  │
     └──────┬─────┘ └─────┬─────┘ └───────────┘
            │            │
     ┌──────┴──────┐     │
     ▼             ▼     ▼
┌─────────┐  ┌─────────┐  ┌─────────┐
│COMPLETED│  │ PENDING │  │ (other) │
└─────────┘  └─────────┘  └─────────┘
                │
                ▼
         ┌───────────┐
         │  CANCELLED│
         └─────┬─────┘
               │
               ▼
         ┌───────────┐
         │  PENDING  │
         └───────────┘

Estados finales (sin salida): COMPLETED
```

## Códigos de Error Comunes

| Código | Descripción |
|--------|--------------|
| 400 | Bad Request - Error de validación o transición inválida |
| 401 | Unauthorized - Token no válido o no proporcionado |
| 403 | Forbidden - Sin permiso para acceder al recurso |
| 404 | Not Found - Recurso no encontrado |
| 500 | Internal Server Error - Error interno del servidor |

---

## Vista de Tareas Detalladas

Endpoints que consumen la vista `v_task_details` con campos calculados.

**Nota:** Requiere crear la vista en PostgreSQL primero:
```sql
CREATE OR REPLACE VIEW v_task_details AS
SELECT 
    t.id, t.title, t.description, t.status,
    t.created_at, t.due_date, t.completed_at,
    t.user_id, u.email AS user_email, u.full_name AS user_full_name,
    CASE WHEN t.due_date IS NULL OR t.status = 'COMPLETED' THEN NULL 
         ELSE EXTRACT(DAY FROM (t.due_date - CURRENT_DATE)) END AS days_remaining,
    CASE WHEN t.due_date IS NULL OR t.status = 'COMPLETED' THEN false 
         WHEN t.due_date < CURRENT_DATE THEN true ELSE false END AS is_overdue,
    CASE WHEN t.due_date IS NULL OR t.status = 'COMPLETED' THEN NULL 
         WHEN t.due_date < CURRENT_DATE THEN EXTRACT(DAY FROM (CURRENT_DATE - t.due_date)) 
         ELSE 0 END AS days_overdue
FROM tasks t INNER JOIN users u ON t.user_id = u.id;
```

### Listar Tareas Detalladas

Obtiene todas las tareas del usuario con campos calculados (días restantes, vencida, etc.).

**Endpoint:** `GET /api/tasks/details`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
[
  {
    "id": 1,
    "title": "Terminar proyecto",
    "description": "Completar el desarrollo del API",
    "status": "PENDING",
    "createdAt": "2026-04-09T14:00:00",
    "dueDate": "2026-04-15T18:00:00",
    "completedAt": null,
    "userId": 1,
    "userEmail": "jose@example.com",
    "userFullName": "Jose Perez",
    "daysRemaining": 5,
    "isOverdue": false,
    "daysOverdue": 0
  },
  {
    "id": 2,
    "title": "Tarea vencida",
    "description": "Esta tarea ya pasó",
    "status": "PENDING",
    "createdAt": "2026-04-01T10:00:00",
    "dueDate": "2026-04-05T18:00:00",
    "completedAt": null,
    "userId": 1,
    "userEmail": "jose@example.com",
    "userFullName": "Jose Perez",
    "daysRemaining": -4,
    "isOverdue": true,
    "daysOverdue": 4
  }
]
```

---

### Listar Tareas por Estado

Obtiene tareas filtradas por estado específico.

**Endpoint:** `GET /api/tasks/details/status/{status}`

**Headers:** `Authorization: Bearer <token>`

**Estados válidos:** `PENDING`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`

**Response - Éxito (200):**
```json
[
  {
    "id": 1,
    "title": "Terminar proyecto",
    "description": "Completar el desarrollo del API",
    "status": "PENDING",
    "createdAt": "2026-04-09T14:00:00",
    "dueDate": "2026-04-15T18:00:00",
    "completedAt": null,
    "userId": 1,
    "userEmail": "jose@example.com",
    "userFullName": "Jose Perez",
    "daysRemaining": 5,
    "isOverdue": false,
    "daysOverdue": 0
  }
]
```

---

### Listar Tareas Vencidas

Obtiene todas las tareas que han excedido su fecha límite.

**Endpoint:** `GET /api/tasks/details/overdue`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
[
  {
    "id": 2,
    "title": "Tarea vencida",
    "description": "Esta tarea ya pasó",
    "status": "PENDING",
    "createdAt": "2026-04-01T10:00:00",
    "dueDate": "2026-04-05T18:00:00",
    "completedAt": null,
    "userId": 1,
    "userEmail": "jose@example.com",
    "userFullName": "Jose Perez",
    "daysRemaining": null,
    "isOverdue": true,
    "daysOverdue": 4
  }
]
```

---

### Listar Todas las Tareas (Admin)

Obtiene todas las tareas de todos los usuarios (sin filtro).

**Endpoint:** `GET /api/tasks/details/all`

**Headers:** `Authorization: Bearer <token>`

**Response - Éxito (200):**
```json
[
  {
    "id": 1,
    "title": "Terminar proyecto",
    "status": "PENDING",
    "userId": 1,
    "userEmail": "jose@example.com",
    "userFullName": "Jose Perez",
    "daysRemaining": 5,
    "isOverdue": false
  },
  {
    "id": 3,
    "title": "Tarea de otro usuario",
    "status": "IN_PROGRESS",
    "userId": 2,
    "userEmail": "admin@example.com",
    "userFullName": "Admin User",
    "daysRemaining": 2,
    "isOverdue": false
  }
]
```

---

## Descripción de Campos de la Vista

| Campo | Tipo | Descripción |
|-------|------|--------------|
| `id` | Long | ID de la tarea |
| `title` | String | Título de la tarea |
| `description` | String | Descripción de la tarea |
| `status` | String | Estado de la tarea (PENDING, IN_PROGRESS, COMPLETED, CANCELLED) |
| `createdAt` | DateTime | Fecha de creación |
| `dueDate` | DateTime | Fecha límite |
| `completedAt` | DateTime | Fecha de completado (nullable) |
| `userId` | Long | ID del usuario |
| `userEmail` | String | Email del usuario |
| `userFullName` | String | Nombre completo del usuario |
| `daysRemaining` | Integer | Días restantes para vencer (null si no hay fecha límite o ya completada) |
| `isOverdue` | Boolean | true si la tarea está vencida |
| `daysOverdue` | Integer | Días de retraso (0 si no está vencida) |

## Swagger

Accede a la documentación interactiva en:
```
http://localhost:9090/swagger-ui.html
```