# Substratal for Android

The Android management app for the [Substratal Apps Platform API](https://compositecode.github.io/substratalapps.com/). It's the Android build of the Substratal Hub defined in [substratalapps-applications-specs](https://github.com/CompositeCode/substratalapps-applications-specs), built admin-first, plus a new Operations section for monitoring the API.

It also hosts **Substratal Help** (`site/`), the documentation site the management apps on every platform link to.

## API versioning: no `/v1` in the URL

The Platform API is versioned with a **request header**, not the URL:

```http
GET https://api.substratalapps.com/users/me
Substratal-Version: 1
```

- Base URL: `https://api.substratalapps.com`. Paths like `/v1/users` now return `404`.
- A missing header returns `400 api_version_required`, and an unknown version returns `400 unsupported_api_version`.
- `GET /health`, `POST /auth/oauth/token`, `/.well-known/*` and `/scim/v2` are exempt.
- A deprecated version gets `Deprecation` and `Sunset` response headers. The app turns them into an "Update the app" banner (`ApiVersionMonitor`).

The app sends the header from `ApiVersioning.CURRENT` (`core/model`) in `ApiHeadersInterceptor` (`core/network`). The pinned contract is `openapi/v1/openapi.yaml`; the folder name is the header value.

> The design package's `API-SCREEN-MAP.csv` and older planning docs still show `/v1/...` paths. They predate the change (API issue [#40](https://github.com/CompositeCode/substratalapps.com/issues/40)). This repo's screen catalog uses unversioned paths, and CI checks them against the pinned spec.

## Layout

```
app/                 Shell, navigation (all 85 screens routable + deep-linked), screens, help links
core/model/          ScreenCatalog (source of truth for screens), ApiVersioning, ImplementationStatus
core/network/        OkHttp/Retrofit client: version header, auth, single-flight refresh, errors
core/designsystem/   Substratal themes (5 light, 5 dark), brand assets, status badges, components
core/data/           Session + Keystore-encrypted token store, capabilities, preferences
openapi/v1/          Pinned openapi.yaml + where it came from; CHANGELOG.md lists every sync
site/                Substratal Help (MkDocs Material + mike + Swagger UI)
scripts/             API coverage check, spec sync
docs/                Planning documents
```

## Build

Needs JDK 21 and the Android SDK with platform 37 (compile) and 36 (target).

```bash
./gradlew assembleDebug        # or: make build
make test                      # unit tests
make install                   # install, using the production API
make install-local             # install, using the API devserver at 10.0.2.2:8080
```

To run against a local API, start the devserver in [CompositeCode/substratalapps.com](https://github.com/CompositeCode/substratalapps.com):

```bash
make up && make migrate && make seed && make run && make quickstart
```

Then sign in as `admin@example.com` with the password `local admin passphrase`.

## Docs

```bash
make docs-deps     # once: Python venv with the pinned toolchain
make docs-serve    # http://127.0.0.1:8000
make docs          # strict build, as CI runs it
```

What's generated, what CI enforces, and how publishing and API-spec syncing work are covered in [site/docs/developers/docs-workflow.md](site/docs/developers/docs-workflow.md).

## Status

See [docs/PREREQUISITES.md](docs/PREREQUISITES.md) for what was verified before the build started and what's still outstanding, and [docs/PLAN-management-app.md](docs/PLAN-management-app.md) for the plan.
