---
title: How these docs stay current
description: What's generated from the code, what CI checks, and how publishing works.
---

# How these docs stay current

This site lives in the app's repository (`site/`) and is built and published by GitHub Actions. Code changes and doc changes ship in the same pull request, and anything that can be generated from the code is.

## What's generated on every build

| Page | Generated from |
|---|---|
| [Screen reference](../reference/screens/index.md): one page per screen | The app's `ScreenCatalog` and `ImplementationStatus`, exported by `./gradlew :core:model:exportScreenCatalog` |
| API reference, one per API version | `openapi/v{N}/openapi.yaml` and `SOURCE.yaml` |
| Spec update history | `openapi/CHANGELOG.md` |

Hand-written help for each screen lives in `site/help/screens/<id>.md` and is merged into the generated page.

## What CI checks

On every pull request and push (`.github/workflows/android.yml`):

1. Unit tests, including that the navigation registry matches `ImplementationStatus`.
2. `scripts/check_api_coverage.py` confirms that every API call a screen makes exists in the pinned `openapi.yaml`. A spec change that removes or renames an endpoint fails the build until the app follows.
3. `site/scripts/generate.py` fails if a screen marked Available or Preview has no help page.
4. `mkdocs build --strict` fails on broken links and missing pages.

## Publishing

`.github/workflows/docs.yml` publishes with [mike](https://github.com/jimporter/mike) to the `gh-pages` branch:

| Trigger | Publishes |
|---|---|
| Push to `main` touching the app, `site/` or `openapi/` | the `dev` version |
| Tag `v0.1.0` | version `0.1`, and moves `latest` to it |
| Manual run | either, on demand |

Older versions stay published, so an older app's help links keep working.

## Picking up API changes

`.github/workflows/api-spec-sync.yml` runs daily, on demand, or when the API repository sends a `openapi-updated` dispatch. It:

1. fetches `docs/openapi.yaml` from `CompositeCode/substratalapps.com`
2. if it changed, updates `openapi/v1/`, adds a row to `openapi/CHANGELOG.md`, and runs the coverage check
3. opens a pull request with the operations that were added or removed

Merging that pull request publishes the new reference.

To make API changes arrive immediately, the API repository's docs workflow can send the dispatch:

```yaml
- run: gh api repos/CompositeCode/substratalapps-android/dispatches -f event_type=openapi-updated
  env: { GH_TOKEN: "${{ secrets.ANDROID_DOCS_DISPATCH_TOKEN }}" }
```

## Working on the docs locally

```bash
make docs-serve    # export the catalog, generate, and serve at http://127.0.0.1:8000
make docs          # strict build into site/build
```
