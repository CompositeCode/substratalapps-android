---
title: Linking to help from an app
description: The URL contract every Substratal app uses to open the right help page for its version.
---

# Linking to help from an app

Every Substratal management app (Android, Web, iOS, macOS, Windows and Linux) opens help pages from this site using the same URL pattern. Keep to it, and help links keep working across platforms and releases.

## The pattern

```
{base}{docs-version}/{path}
```

| Part | Value |
|---|---|
| `base` | `https://compositecode.github.io/substratalapps-android/` (configurable per build) |
| `docs-version` | The app's minor version (`0.1`) for release builds, `dev` for development builds, or `latest` |
| `path` | A page path ending in `/` |

## Stable paths

| Page | Path |
|---|---|
| A screen's help | `reference/screens/{screen-id-lowercase}/`, e.g. `reference/screens/usr-01/` |
| Help home | *(empty)* |
| Getting started | `getting-started/` |
| Sign-in help | `getting-started/sign-in/` |
| Tutorials | `tutorials/` |
| FAQ | `faq/` |
| API reference | `developers/api/v{N}/` |

Screen IDs come from the shared design package (for example `USR-01`) and never change meaning, so a screen's help link is the same on every platform.

## How the Android app does it

`HelpLinks.forScreen(screen)` builds the link from `BuildConfig.HELP_BASE_URL`, `BuildConfig.DOCS_VERSION` and the screen's `docsPath`, and opens it in a Custom Tab. Every screen's top bar has a **?** that does this. Set a different base at build time with `-Psubstratal.helpBaseUrl=…`.

## Versions that don't exist yet

A release build links to its own minor version, which is published when that version is tagged. Builds made before their docs version is published should link to `latest`.
