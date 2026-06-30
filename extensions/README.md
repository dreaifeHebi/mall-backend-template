# Optional Backend Extensions

Core backend intentionally avoids heavyweight services. Add optional implementations here when a project actually needs them.

Suggested extension folders:

- `storage-s3/`
- `storage-aliyun-oss/`
- `search-elasticsearch/`
- `messaging-rabbitmq/`

Each extension should document the code changes, environment variables, compose override, and migration steps needed to enable it.
