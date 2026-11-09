# Mall Backend Template

A lightweight Spring Boot backend for a small online store: an admin API and a storefront API, running on PostgreSQL and Redis only.

## Part of the Mall Template

Mall Template is split into four repositories that deploy together as a complete store:

| Repository | Role |
| --- | --- |
| [mall-template-docs](https://github.com/dreaifeHebi/mall-template-docs) | Entry point: architecture overview and a Docker Compose setup for the whole stack |
| **mall-backend-template** (this repo) | Spring Boot backend: admin API (`mall-admin`) and storefront API (`mall-portal`) |
| [mall-admin-template](https://github.com/dreaifeHebi/mall-admin-template) | Vue 3 admin console, talks to `mall-admin` |
| [mall-user-template](https://github.com/dreaifeHebi/mall-user-template) | Vue 3 storefront, talks to `mall-portal` |

To run everything together, start from [mall-template-docs](https://github.com/dreaifeHebi/mall-template-docs).

## Features

### Admin API (`mall-admin`)

- Admin login with JWT, role-based menus and API resources
- Products, SKUs and stock, brands, categories and product attributes
- Orders: filtering, CSV export, receiver/fee edits, payment confirmation, delivery, notes, close and delete
- Refund review and processing
- Mall members, admin users and roles
- Home banners, email templates and runtime system settings
- File uploads stored on the local disk
- A seeded read-only demo account (`demo_viewer`) with access to catalog, banners, settings and email templates only

### Storefront API (`mall-portal`)

- Member registration and login, profile, password change and reset, email verification
- Home content, product search and detail, brands and categories
- Cart, order confirmation, order creation and order lifecycle
- H5 payment records with a pluggable provider. The default `LOCAL_SELF` provider confirms payments locally, so checkout works without a payment account. GlobePay card, Alipay H5 and WeChat H5 code paths are included but untested in the template; Stripe Checkout is a placeholder.
- Refund requests and status tracking
- Shipping addresses, favorites, brand follows and browsing history

### Not included by default

To keep the template small, these upstream services are left out: `mall-search` / Elasticsearch, `mall-demo`, the MinIO container, RabbitMQ delayed queues, MongoDB member interaction storage, Logstash / ELK, and server-specific deployment scripts. See [Optional extensions](#optional-extensions).

## Tech Stack

- Java 8, Spring Boot 2.7.5
- Spring Security + JWT
- MyBatis, MyBatis Generator, PageHelper
- PostgreSQL 16, Redis 7
- Docker images based on `eclipse-temurin:8-jre-alpine`

| Module | Purpose |
| --- | --- |
| `mall-common` | Shared API types, utilities, logging, Redis and mail helpers |
| `mall-mbg` | MyBatis generated models and mappers |
| `mall-security` | Spring Security + JWT support |
| `mall-admin` | Admin API |
| `mall-portal` | Storefront API |

## Quick Start

You need a JDK (8 or newer; CI builds with JDK 8) and Maven, or Docker only (see the Maven container command in [mall-template-docs](https://github.com/dreaifeHebi/mall-template-docs)).

Build the two services:

```bash
mvn -B -DskipTests package -pl mall-admin,mall-portal -am
```

Run the backend with PostgreSQL and Redis using the Compose file in [`deployment/`](./deployment/):

```bash
cd deployment
cp .env.example .env
cp ../mall-admin/target/mall-admin-1.0-SNAPSHOT.jar mall-admin.jar
cp ../mall-portal/target/mall-portal-1.0-SNAPSHOT.jar mall-portal.jar
docker compose up -d --build
```

On first start PostgreSQL runs [`deployment/postgres/init/01-init-database.sql`](./deployment/postgres/init/01-init-database.sql), which creates the schema, a small English sample catalog and these accounts:

| Account | Password | Used in |
| --- | --- | --- |
| `admin` | `123456` | Admin console, super admin |
| `demo_viewer` | `123456` | Admin console, read-only demo |
| `demo_user` | `123456` | Storefront |

Change the `admin` password before exposing a deployment publicly.

| Service | Address |
| --- | --- |
| Admin API | `http://localhost:8080` |
| Storefront API | `http://localhost:8085` |
| PostgreSQL | `localhost:5432` |
| Redis | `localhost:6379` |

Ports can be changed in `deployment/.env` (`ADMIN_PORT`, `PORTAL_PORT`, `POSTGRES_PORT`, `REDIS_PORT`).

## Configuration

Both services ship with a `dev` profile (the default) and a `prod` profile (used by the Docker images). The `dev` profile expects:

```text
PostgreSQL: jdbc:postgresql://localhost:5432/mall  (user: mall)
Redis:      localhost:6379
Admin API:  :8080
Portal API: :8085
Uploads:    ./uploads
```

Common settings are overridable through environment variables:

| Variable | Purpose |
| --- | --- |
| `SPRING_PROFILES_ACTIVE` | `dev` or `prod` |
| `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` | PostgreSQL connection |
| `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`, `SPRING_REDIS_PASSWORD` | Redis connection |
| `MALL_UPLOAD_PATH`, `MALL_UPLOAD_URL` | Upload directory and its public URL prefix |
| `MALL_DOMAIN` | Public base URL of the service, used in generated links |
| `FRONTEND_BASE_URL` | Storefront URL used in email links (registration, password reset) |
| `MALL_ADMIN_BASE_URL` | How `mall-portal` reaches `mall-admin` |
| `SPRING_MAIL_FROM`, `SES_SMTP_USERNAME`, `SES_SMTP_PASSWORD` | Outgoing email; the SMTP host is set in `application-*.yml` |
| `H5_PAYMENT_PROVIDER` | Payment provider for `mall-portal`, default `LOCAL_SELF` |
| `GLOBEPAY_*` | GlobePay merchant settings and callback URLs for the card, Alipay H5 and WeChat H5 channels |
| `STRIPE_*` | Settings read by the Stripe Checkout placeholder; it needs an implementation before it can take payments |

Before going to production, also replace the JWT secrets (`jwt.secret` in each module's `application.yml`) and the placeholder payment credentials.

## Optional Extensions

[`extensions/`](./extensions/) is the place for integrations that larger deployments may need but the core does not: S3 / R2 / external MinIO storage, Aliyun OSS uploads, Elasticsearch product search, and RabbitMQ delayed order cancellation. None of these ship yet: the folder only lists the suggested extensions. The core backend stays runnable without them.

## Known Limitations

- `LOCAL_SELF` marks orders as paid immediately; it is meant for demos and development. The GlobePay channels need merchant settings and callback testing; Stripe Checkout needs an implementation in `H5PaymentServiceImpl.processStripeCheckoutPayment` first.
- Member levels and points are hidden from the template; their tables remain only so the existing data model keeps working.

## License

Licensed under the [Apache License 2.0](./LICENSE).

Derived from [macrozheng/mall](https://github.com/macrozheng/mall) (Apache-2.0). Thanks to the original authors.
