---
title: Android app architecture
description: How the Substratal Android app is built — modules, stack, and running it against a local API.
---

# Android app architecture

## Stack

| Concern | Choice |
|---|---|
| UI | Kotlin, Jetpack Compose, Material 3, `material3-adaptive-navigation-suite` |
| SDK | minSdk 26, targetSdk 36, compiled against 37; edge-to-edge; predictive back |
| Architecture | MVVM with unidirectional data flow (ViewModel + `StateFlow`), Hilt, Navigation Compose |
| Network | OkHttp 5 + Retrofit 3 + kotlinx.serialization |
| Session | Refresh token encrypted with an Android Keystore AES-GCM key, in DataStore; access token in memory |
| Build | Gradle 9.8, AGP 9.4 (built-in Kotlin), Kotlin 2.4, version catalog |

## Modules

```
app/                  Navigation, shell, screens (feature/*), Hilt wiring, help links
core/model/           Screen catalog (85 screens), API versioning constants, implementation status. Pure Kotlin
core/network/         API client: version header, auth, single-flight refresh, error decoding. Pure Kotlin
core/designsystem/    The ten Substratal themes, brand assets, status badges, shared components
core/data/            Session, secure token store, capabilities, preferences
```

**`ScreenCatalog`** in `core:model` is the source of truth for screens. Navigation registers a route and deep links for every screen in it, so all 85 are reachable. Screens without a built UI show their spec. The docs site's screen reference is generated from it too.

## The version header

`ApiHeadersInterceptor` sends `Substratal-Version` (from `ApiVersioning.CURRENT`) on every request. It records what the server echoes, and any `Deprecation` and `Sunset` headers. `ApiVersionMonitor` turns a deprecation into the *Update the app* banner. Base URLs carry no version.

## Build and run

```bash
./gradlew assembleDebug            # build
./gradlew test                     # unit tests (catalog, API client, navigation registry)
./gradlew installDebug             # install on a device or emulator (production API)

# Against a local API (CompositeCode/substratalapps.com: make up && make migrate && make seed && make run)
./gradlew installDebug -Psubstratal.apiEnvironment=LOCAL
# sign in as admin@example.com / "local admin passphrase"
```

Debug builds allow plain HTTP to `10.0.2.2` (the emulator's host) only.

## Adding a screen

1. Build the composable under `app/.../feature/<area>/`.
2. Register it in `implementedScreens` (`HubNavHost.kt`).
3. Add its ID to `ImplementationStatus.available` (or `preview`).
4. Write `site/help/screens/<id>.md`.

The unit tests fail if steps 2 and 3 disagree, and the docs build fails if step 4 is missing.
