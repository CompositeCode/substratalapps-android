# Common tasks. The Android build itself is plain Gradle (./gradlew …).
PY      ?= site/.venv/bin/python
MKDOCS  ?= site/.venv/bin/mkdocs
GRADLE  ?= ./gradlew

.PHONY: build test check install install-local docs-deps catalog docs docs-serve api-coverage

build:                ## Debug APK
	$(GRADLE) assembleDebug

test:                 ## Unit tests
	$(GRADLE) testDebugUnitTest :core:model:test :core:network:test

install:              ## Install on a connected device (production API)
	$(GRADLE) installDebug

install-local:        ## Install, pointed at the API devserver on the emulator's host
	$(GRADLE) installDebug -Psubstratal.apiEnvironment=LOCAL

docs-deps:            ## Create the docs virtualenv
	python3 -m venv site/.venv && site/.venv/bin/pip install -q -r site/requirements.txt

catalog:              ## Export the app's screen catalog for the docs
	$(GRADLE) -q :core:model:exportScreenCatalog

api-coverage: catalog ## Every API call a screen makes must exist in the pinned spec
	$(PY) scripts/check_api_coverage.py site/.generated/screens.json openapi/v1/openapi.yaml

docs: api-coverage    ## Generate and build the docs site (strict)
	$(PY) site/scripts/generate.py
	cd site && .venv/bin/mkdocs build --strict

docs-serve: api-coverage ## Generate and serve the docs at http://127.0.0.1:8000
	$(PY) site/scripts/generate.py
	cd site && .venv/bin/mkdocs serve

check: test docs      ## Everything CI runs
