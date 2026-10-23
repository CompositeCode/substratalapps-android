# Common tasks. The Android build itself is plain Gradle (./gradlew …).
GRADLE  ?= ./gradlew

.PHONY: build test check install install-local help-status api-coverage

build:                ## Debug APK
	$(GRADLE) assembleDebug

test:                 ## Unit tests
	$(GRADLE) testDebugUnitTest :core:model:test :core:network:test

install:              ## Install on a connected device (production API)
	$(GRADLE) installDebug

install-local:        ## Install, pointed at the API devserver on the emulator's host
	$(GRADLE) installDebug -Psubstratal.apiEnvironment=LOCAL

help-status:          ## Write build/help/screens.json and build/help/android.json (help center status)
	$(GRADLE) -q :core:model:exportHelp

api-coverage: help-status ## Every API call a screen makes must exist in the pinned spec (needs PyYAML)
	python3 scripts/check_api_coverage.py build/help/screens.json openapi/v1/openapi.yaml

check: test api-coverage ## Everything CI runs, apart from lint
