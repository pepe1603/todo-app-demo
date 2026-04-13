# Guía de Despliegue en VPS (Hostinger)

## Requisitos Previos
- VPS con Ubuntu 20.04+ o Debian
- Acceso SSH (PuTTY o terminal)
- Docker instalado en el VPS

---

## Paso 1: Conectar al VPS por SSH

```bash
ssh root@tu_ip_vps
```

---

## Paso 2: Instalar Docker (si no lo tienes)

```bash
# Actualizar paquetes
apt update && apt upgrade -y

# Instalar Docker
curl -fsSL https://get.docker.com | sh

# Habilitar Docker al iniciar
systemctl enable docker

# Verificar instalación
docker --version
```

---

## Paso 3: Clonar el proyecto

```bash
# Instalar git si no lo tienes
apt install -y git

# Clonar el repositorio
git clone https://github.com/pepe1603/todo-app-demo.git
cd todo-app-demo
```

---

## Paso 4: Configurar variables de entorno

Crea un archivo `.env`:
```bash
nano .env
```

Pega esto (reemplaza los valores con los tuyos):
```env
URL_DATABASE_POSTGRESQL=jdbc:postgresql://TU_HOST:TU_PORT/TU_DB?ssl=require
DB_USERNAME=TU_USUARIO
DB_PASSWORD=TU_PASSWORD

MAIL_HOST=smtp.resend.com
MAIL_PORT=587
MAIL_USERNAME=resend
MAIL_PASSWORD=TU_API_KEY
MAIL_FROM=onboarding@resend.dev

REDIS_HOST=TU_UPSTASH_HOST
REDIS_PORT=6379
REDIS_PASSWORD=TU_UPSTASH_PASSWORD
REDIS_SSL_ENABLED=true

JWT_SECRET=TU_JWT_SECRET
JWT_ACCESS_TOKEN_EXPIRATION=900000
```

Guarda: `Ctrl + O`, `Enter`, `Ctrl + X`

---

## Paso 5: Construir y ejecutar

```bash
# Construir la imagen
docker build -t todo-app .

# Ejecutar el contenedor
docker run -d \
  --name todo-app \
  -p 9090:9090 \
  --env-file .env \
  todo-app
```

---

## Paso 6: Verificar que funciona

```bash
# Ver logs
docker logs todo-app

# Ver estado
docker ps

# Probar la API
curl http://localhost:9090/api-docs
```

---

## Configurar Nginx como Proxy Inverso (Opcional)

Si quieres usar el puerto 80/443:

```bash
# Instalar Nginx
apt install -y nginx

# Crear configuración
nano /etc/nginx/sites-available/todo-app
```

Pega esto:
```nginx
server {
    listen 80;
    server_name tu-dominio.com;

    location / {
        proxy_pass http://localhost:9090;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

```bash
# Habilitar sitio
ln -s /etc/nginx/sites-available/todo-app /etc/nginx/sites-enabled/

# Reiniciar Nginx
nginx -t && systemctl restart nginx
```

---

## Comandos Útiles

| Comando | Descripción |
|---------|-------------|
| `docker stop todo-app` | Detener |
| `docker start todo-app` | Iniciar |
| `docker restart todo-app` | Reiniciar |
| `docker logs -f todo-app` | Ver logs en tiempo real |
| `docker rm -f todo-app` | Eliminar contenedor |

---

## Firewall (Opcional)

```bash
# Abrir puertos
ufw allow 22    # SSH
ufw allow 80   # HTTP
ufw allow 443  # HTTPS
ufw allow 9090 # API
ufw enable
```
