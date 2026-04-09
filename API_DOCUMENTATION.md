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

## Swagger

Accede a la documentación interactiva en:
```
http://localhost:9090/swagger-ui.html
```