# Substratal for Android

The Android management app for the [Substratal Apps Platform API](https://compositecode.github.io/substratalapps.com/). It's the Android build of the Substratal Hub defined in [substratalapps-applications-specs](https://github.com/CompositeCode/substratalapps-applications-specs), built admin-first, plus a new Operations section for monitoring the API.

Help pages live in the **Substratal Hub help center**, shared by every Hub app: <https://compositecode.github.io/substratalapps-applications-specs/help/> (source: `help/` in [substratalapps-applications-specs](https://github.com/CompositeCode/substratalapps-applications-specs)). Every screen's **?** button opens its page there.

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

## Help and documentation

- **Help links.** `HelpLinks` opens `screens/<id>.html` in the help center for design-package screens. Screens this app adds on its own (the `OPS-*` preview) open the help home, because the spec's `OPS-*` IDs are different web-only Console screens.
- **Build status.** `make help-status` writes `build/help/android.json`, listing the screens this app has built. On every merge to `main`, `.github/workflows/help-status.yml` opens or updates a PR putting it at `help/apps/android.json` in the specs repo, then asks the help center to rebuild. Each screen's help page then shows "Android: Available in 0.1.0". It needs a `SPECS_DOCS_TOKEN` secret.
- **API contract.** `openapi/v1/` pins the spec this build is checked against. `make api-coverage` fails if a screen calls an operation that isn't in it, and `api-spec-sync.yml` opens a PR here when the API's spec changes.
- **Adding a screen.** Register it in `implementedScreens` and `ImplementationStatus`; unit tests fail if they disagree. Write its usage notes in the help center's `help/screens/<id>.md`.

## Status

See [docs/PREREQUISITES.md](docs/PREREQUISITES.md) for what was verified before the build started and what's still outstanding, and [docs/PLAN-management-app.md](docs/PLAN-management-app.md) for the plan.
