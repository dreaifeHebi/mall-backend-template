# Mall Backend Lightweight Deployment

This core deployment starts only PostgreSQL, Redis, mall-admin, and mall-portal.

Not included by default:

- MinIO or other object-storage containers
- RabbitMQ
- Elasticsearch / mall-search
- MongoDB
- Logstash / ELK

File upload defaults to a local mounted volume. Use an optional storage extension when production needs S3, R2, MinIO, or Aliyun OSS.

## Usage

```bash
cp .env.example .env
# build jars first and copy them here:
#   mall-admin.jar
#   mall-portal.jar
docker compose up -d --build
```

## Services

- Admin API: http://localhost:8080
- Portal API: http://localhost:8085
- PostgreSQL: localhost:5432
- Redis: localhost:6379
