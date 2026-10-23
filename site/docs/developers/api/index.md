---
title: API overview
description: Base URL, version header, authentication and conventions for the Substratal Platform API.
---

# API overview

The Platform API handles accounts, sign-in, roles, per-app access, organizations, settings, billing and push notifications for applications built on Substratal Apps. This page summarizes what an integration needs. The full contract is in the [API reference](v1/index.md), and the API's own documentation at [compositecode.github.io/substratalapps.com](https://compositecode.github.io/substratalapps.com/) is authoritative.

## Base URL and version

```
https://api.substratalapps.com
```

The URL has **no version segment**. Each request names the API version it was written for in a header instead:

```http
Substratal-Version: 1
```

- **Required.** A missing header returns `400 api_version_required`, and an unknown version returns `400 unsupported_api_version`. Both list the supported versions in `details.supported`. There's no default.
- **Echoed.** Every response carries `Substratal-Version` with the version that served it.
- **Exempt:** `GET /health`, `POST /auth/oauth/token`, `/.well-known/*` and SCIM (`/scim/v2`).

Paths like `/v1/users` were retired with the move to header versioning and now return `404`. See [API versions and changes](versions.md).

## Authentication

```bash
curl -s -X POST https://api.substratalapps.com/auth/login \
  -H "Substratal-Version: 1" -H "Content-Type: application/json" \
  -d '{"email": "you@example.com", "password": "…"}'
```

| Response | Next step |
|---|---|
| `{"access_token", "refresh_token", "expires_in": 900, …}` | Send `Authorization: Bearer <access_token>` |
| `{"mfa_required": true, "mfa_token", …}` | `POST /auth/mfa/verify` with `mfa_token` and `code` |

- Access tokens last **15 minutes**. Renew them with `POST /auth/token/refresh`.
- Refresh tokens **rotate**: store the new one before using the new access token, and never refresh twice in parallel. Reusing a rotated token revokes the session.
- **API keys** (`satk_live_…`, `satk_test_…`) are for servers and agents. They're refused from browsers.

## Conventions

| Topic | Rule |
|---|---|
| IDs | Opaque strings with a type prefix: `usr_`, `app_`, `ent_`, `role_`, `key_`, `evt_`… Use `me` for yourself |
| Lists | `?limit=` and `?cursor=`. The response is `{"data": [...], "page": {"next_cursor", "has_more"}}` |
| Creates | Send `Idempotency-Key: <uuid>`. It's required on grants and checkout. A retry with the same key returns the original result |
| Updates | Send `If-Match` with the `ETag` from your last read. A stale one returns `409 version_conflict` |
| Errors | `{"error": {"code", "message", "details"}}`. Branch on `code`, not `message`. Field errors are in `details.fields` |
| Rate limits | 100 requests a minute per key or session (more on hot paths, less on sign-in). `RateLimit-*` headers on every response; `Retry-After` on `429` |
| Wrong method | `405 method_not_allowed`, with an `Allow` header |

## Getting started fast

- Run the whole API locally: clone `CompositeCode/substratalapps.com`, then `make up && make migrate && make seed && make run`. It serves `http://localhost:8080`.
- Generate a client from [`openapi.yaml`](v1/openapi.yaml). It declares the version header, so generated clients send it.
