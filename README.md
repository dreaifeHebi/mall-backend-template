# Mall Backend Template

Lightweight Spring Boot mall backend template.

Fork from [mall](https://github.com/macrozheng/mall)

## Core Modules

```text
mall-common      shared API, utilities, logging, Redis and mail helpers
mall-mbg         MyBatis generated models and mappers
mall-security    Spring Security + JWT support
mall-admin       admin API, catalog, orders, users, settings, uploads
mall-portal      user storefront API, cart, orders, payment callbacks
```

Removed from the default template:

- `mall-search` / Elasticsearch
- `mall-demo`
- MinIO container
- RabbitMQ delayed queue
- MongoDB member interaction persistence
- server-specific Lightsail and HTTPS scripts

## Default Runtime

- Java 8
- Spring Boot 2.7.5
- PostgreSQL 16
- Redis 7
- Local file upload under `mall.upload.path`

## Local Configuration

Development profiles default to:

```text
PostgreSQL: jdbc:postgresql://localhost:5432/mall
Redis: localhost:6379
Admin API: :8080
Portal API: :8085
Uploads: ./uploads
```

## Build

```bash
mvn -DskipTests package
```

## Lightweight Deployment

```bash
cd deployment
cp .env.example .env
cp ../mall-admin/target/*.jar mall-admin.jar
cp ../mall-portal/target/*.jar mall-portal.jar
docker compose up -d --build
```

## Optional Extensions

Use `extensions/` for services that are useful in larger deployments but too heavy for the default template:

- S3/R2/external MinIO object storage
- Aliyun OSS upload policy
- Elasticsearch product search
- RabbitMQ delayed order cancellation
