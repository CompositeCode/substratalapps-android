# Prerequisites: verified 2026-10-23

What was checked before building the Android app, and what's still outstanding.

## Ready

| Area | Check | Result |
|---|---|---|
| Production API | `GET https://api.substratalapps.com/health` | `200 {"status":"ok"}` |
| Header versioning | `GET /users` with no header | `400 api_version_required`, `details.supported: ["1"]` |
| | `Substratal-Version: 2` | `400 unsupported_api_version` |
| | `Substratal-Version: 1`, no token | `401 unauthenticated`; response echoes `substratal-version: 1` |
| | `GET /v1/health` | `404`. URL versioning is gone |
| Sign-in from the app | Android emulator → production `POST /auth/login` with wrong credentials | `401 invalid_credentials`, shown as the right copy |
| API contract | `openapi.yaml` 1.0.0-draft.9, 124 operations, pinned in `openapi/v1/` | Every API call in the screen catalog exists in it (`scripts/check_api_coverage.py`); 123 of 124 operations are used. The one not used is `POST /auth/oauth/token`, which launched apps call, not the Hub |
| Local API | `make up && make migrate && make seed && make run && make quickstart` in the API repo | All quickstart steps passed. The app signed in as the local superadmin and loaded Users and Applications |
| Design package | Theme (`SubstratalThemes.kt`, 10 schemes), brand assets, `status-semantics.json`, 75 screen specs | All reused as-is |
| JDK | 21 (Microsoft and Corretto installed) | OK. The build uses a Gradle toolchain of 21 |
| Android SDK | Platforms 35, 36 (installed), 37.0 (installed: current AndroidX needs compileSdk 37); build-tools 36; platform-tools | OK |
| Gradle | Wrapper 9.8.2; AGP 9.4.1 with built-in Kotlin 2.4.21 | Builds; 11 unit tests pass |
| Emulator | AVD `il_pixel` (API 35) | Ran the app on phone and tablet sizes, light and dark |
| Docs toolchain | Python 3.10; MkDocs 1.6.1, Material 9.7.7, mike 2.2.0, swagger-ui-tag 0.8.1 (pinned) | `mkdocs build --strict` passes |
| Docker / Go / jq | Docker 20.10, Go 1.24 (auto-fetches the 1.26 toolchain), jq 1.8 | OK for the local API |

## Already resolved in the API since the first plan

These were tracked as `[api-gaps]` issues in `CompositeCode/substratalapps.com` and are now built:

- G1 CORS (#38)
- G14 revoke all sessions (#51)
- G17 filter users by role (#54)
- G19 audit export (#56)
- self-service application registration (#32)
- test-user expiry (#30)
- SCIM (#34)

`docs/PLAN-api-gaps.md` is superseded by those issues.

## Outstanding

| # | Item | Blocks | Owner |
|---|---|---|---|
| 1 | **No GitHub remote for this repo.** CI, docs publishing and spec sync are written but can't run until the repo exists (`gh repo create CompositeCode/substratalapps-android`). GitHub Pages on a private repo needs a paid plan; otherwise make the repo public, or publish the built site to S3 + CloudFront | Docs site, CI | You |
| 2 | `dashboard.substratalapps.com` doesn't resolve. Verified App Links need `/.well-known/assetlinks.json` there; until then `https://dashboard…` links open in the browser. `substratal://` links work | Emailed links opening the app | Web app (`substratalapps.com-web`) |
| 3 | A Play signing key and its SHA-256 fingerprint, for `assetlinks.json` and Play internal testing | App Links, distribution | You |
| 4 | Firebase project for `app_hub` + FCM credential uploaded through the API (APL-07) | Push (milestone M3) | You / API issue #24 |
| 5 | Register `app_hub` and Substratal's own applications | Push, Home | API issue #25 |
| 6 | SES out of the sandbox | Invitations and resets reaching real people | API issue #22 |
| 7 | Stripe set up | Plan & Billing screens | API issue #23 |
| 8 | Operations endpoints and permissions | OPS-01..10 live data | API issues #43–#48 |
| 9 | Design package still shows `/v1` paths and lacks the new operations | Spec consistency | API issue #40 (G3) |
| 10 | The production superadmin's credentials, to test against production | End-to-end checks in production | You |
