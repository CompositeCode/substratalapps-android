#!/usr/bin/env python3
"""Generates the parts of the documentation site that come from the code.

Inputs (all in this repository, so docs can't drift from what's built):
  site/.generated/screens.json   ./gradlew :core:model:exportScreenCatalog (the app's screen catalog)
  openapi/v<N>/openapi.yaml      the pinned API contract for each API version (Substratal-Version: N)
  openapi/v<N>/SOURCE.yaml       where and when it was synced from the API repository
  site/help/screens/<id>.md      hand-written help for a screen (required once a screen ships)

Outputs (git-ignored, rebuilt on every docs build):
  site/docs/reference/screens/index.md and one page per screen
  site/docs/developers/api/v<N>/openapi.yaml and index.md (Swagger UI for that version)

Exits 1 when a screen the app has shipped (Available or Preview) has no hand-written help, so a
screen can't reach main undocumented.
"""
import json
import pathlib
import re
import shutil
import sys

import yaml

ROOT = pathlib.Path(__file__).resolve().parents[2]
SITE = ROOT / "site"
DOCS = SITE / "docs"
CATALOG = SITE / ".generated" / "screens.json"
HELP = SITE / "help" / "screens"
API_ISSUES = "https://github.com/CompositeCode/substratalapps.com/issues/"
DASHBOARD = "https://dashboard.substratalapps.com"

SECTION_ORDER = ["OPERATIONS", "ADMINISTRATION", "DEVELOPER", "PERSONAL", "AUTH", "GLOBAL"]
SECTION_TITLES = {
    "OPERATIONS": "Operations", "ADMINISTRATION": "Administration", "DEVELOPER": "Developer",
    "PERSONAL": "Personal", "AUTH": "Sign-in", "GLOBAL": "Shell",
}
STATE_LABEL = {"AVAILABLE": "Available", "PREVIEW": "Preview", "PLANNED": "Planned"}
STATE_NOTE = {
    "AVAILABLE": None,
    "PREVIEW": '!!! note "Preview"\n    The layout is in the app, but it shows sample data until the API endpoints it needs exist.',
    "PLANNED": '!!! info "Planned"\n    This screen isn\'t built in the Android app yet. You can still open it by its link; the app shows what it will do.',
}
PHASE_LABEL = {"MVP": "MVP", "PHASE_2": "Phase 2", "PHASE_3": "Phase 3", "PROPOSED": "Proposed"}


def norm(path):
    return re.sub(r"\{[^}]+\}", "{}", path)


def api_versions():
    """Every pinned API version: openapi/v1, openapi/v2, … (the Substratal-Version header value)."""
    out = []
    for d in sorted((ROOT / "openapi").glob("v*")):
        spec = yaml.safe_load((d / "openapi.yaml").read_text())
        source = yaml.safe_load((d / "SOURCE.yaml").read_text()) if (d / "SOURCE.yaml").exists() else {}
        out.append((d.name, spec, source))
    return out


def operation_index(spec):
    index = {}
    for path, item in spec["paths"].items():
        for method, op in item.items():
            if method in ("get", "post", "put", "patch", "delete"):
                index[(method.upper(), norm(path))] = {
                    "id": op.get("operationId", ""),
                    "tag": (op.get("tags") or ["Other"])[0],
                    "phase": op.get("x-substratal-phase", ""),
                }
    return index


def screen_page(entry, ops_index, current_api):
    s = entry["spec"]
    sid, state = s["id"], entry["buildState"]
    lines = [
        "---",
        f"title: \"{s['id']} {s['title']}\"",
        f"description: \"{s['purpose'].replace(chr(34), '')}\"",
        "---",
        "",
        f"# {s['title']}",
        "",
        f"`{sid}` · **{STATE_LABEL[state]}** in the Android app · {PHASE_LABEL[s['phase']]} · {SECTION_TITLES[s['section']]}",
        "",
    ]
    if s["purpose"]:
        lines += [s["purpose"], ""]
    if STATE_NOTE[state]:
        lines += [STATE_NOTE[state], ""]

    help_file = HELP / f"{sid.lower()}.md"
    if help_file.exists():
        lines += [help_file.read_text().strip(), ""]

    lines += ["## At a glance", "", "| | |", "|---|---|"]
    lines.append(f"| Who can use it | {s['who'] or 'Everyone signed in'} |")
    if s["route"].startswith("("):
        lines.append(f"| Opens as | {s['route'].strip('()')} (no link of its own) |")
    else:
        lines.append(f"| Link | `{DASHBOARD}{s['route']}` |")
        lines.append(f"| App link | `substratal://{entry['navRoute']}` |")
    lines += [f"| Kind of screen | {s['archetype'].capitalize()} |", ""]

    if s["operations"]:
        lines += [
            "## API calls",
            "",
            f"Sent with `Substratal-Version: {current_api}`. See the [API reference](../../developers/api/v{current_api}/index.md) for request and response details.",
            "",
            "| Call | Operation | Does |",
            "|---|---|---|",
        ]
        for op in s["operations"]:
            meta = ops_index.get((op["method"], norm(op["path"])), {})
            lines.append(f"| `{op['method']} {op['path']}` | `{meta.get('id', '—')}` | {op['summary']} |")
        lines.append("")

    if s["apiGaps"]:
        lines += ["## Waiting on the API", ""]
        for ref in s["apiGaps"]:
            lines.append(f"- [Issue {ref}]({API_ISSUES}{ref.lstrip('#')}) in CompositeCode/substratalapps.com")
        lines.append("")
    return "\n".join(lines)


def screen_index(entries):
    counts = {k: sum(1 for e in entries if e["buildState"] == k) for k in STATE_LABEL}
    lines = [
        "---",
        "title: Screen reference",
        "description: Every screen in the Substratal management apps, what it does, who can use it and the API calls behind it.",
        "---",
        "",
        "# Screen reference",
        "",
        "Every screen in the Substratal management apps, by its stable ID. The IDs and links are the same on every platform, "
        "so `/reference/screens/usr-01/` is the help page for the Users screen everywhere.",
        "",
        f"In the Android app: **{counts['AVAILABLE']} available**, **{counts['PREVIEW']} in preview**, "
        f"**{counts['PLANNED']} planned**. This page is generated from the app's code on every build.",
        "",
    ]
    for section in SECTION_ORDER:
        rows = [e for e in entries if e["spec"]["section"] == section]
        if not rows:
            continue
        lines += [f"## {SECTION_TITLES[section]}", "", "| ID | Screen | Android | Phase |", "|---|---|---|---|"]
        for e in rows:
            s = e["spec"]
            lines.append(
                f"| `{s['id']}` | [{s['title']}]({s['id'].lower()}.md) | {STATE_LABEL[e['buildState']]} | {PHASE_LABEL[s['phase']]} |"
            )
        lines.append("")
    return "\n".join(lines)


def api_reference_page(name, spec, source):
    number = name.lstrip("v")
    ops = sum(1 for item in spec["paths"].values() for m in item if m in ("get", "post", "put", "patch", "delete"))
    commit = source.get("source_commit", "")
    return f"""---
title: API reference (version {number})
description: OpenAPI / Swagger reference for the Substratal Platform API, version {number}.
---

# API reference — version {number}

| | |
|---|---|
| Send | `Substratal-Version: {number}` on every request |
| Base URL | `{spec["servers"][0]["url"]}` (no version in the path) |
| Spec | `openapi.yaml` {spec["info"]["version"]}, OpenAPI {spec["openapi"]}, {ops} operations |
| Synced from | [{source.get("source_repo", "")}@{commit[:7]}](https://github.com/{source.get("source_repo", "")}/blob/{commit}/{source.get("source_path", "")}) on {str(source.get("synced_at", ""))[:10]} |
| Download | [openapi.yaml](openapi.yaml) |

Authenticate with **Authorize** using a user access token. API keys are refused from browsers
(`400 api_key_in_browser`), and the production API only accepts browser calls from first-party
origins, so use **Try it out** against a local devserver, or copy the request as `curl`.

The Platform API's own documentation is authoritative where the two differ:
[compositecode.github.io/substratalapps.com](https://compositecode.github.io/substratalapps.com/).

<swagger-ui src="openapi.yaml" docExpansion="none" filter="true" persistAuthorization="true"/>
"""


def main():
    if not CATALOG.exists():
        sys.exit(f"{CATALOG} is missing. Run ./gradlew :core:model:exportScreenCatalog first (make docs does).")
    catalog = json.loads(CATALOG.read_text())
    entries = catalog["screens"]
    versions = api_versions()
    if not versions:
        sys.exit("No pinned API versions under openapi/.")

    # API reference: one folder per API version.
    for name, _spec, _source in versions:
        target = DOCS / "developers" / "api" / name
        target.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(ROOT / "openapi" / name / "openapi.yaml", target / "openapi.yaml")
        (target / "index.md").write_text(api_reference_page(name, _spec, _source))

    current = catalog["apiVersion"]
    ops_index = operation_index(next(spec for name, spec, _ in versions if name == f"v{current}"))

    out = DOCS / "reference" / "screens"
    if out.exists():
        shutil.rmtree(out)
    out.mkdir(parents=True)
    (out / "index.md").write_text(screen_index(entries))
    for entry in entries:
        (out / f"{entry['spec']['id'].lower()}.md").write_text(screen_page(entry, ops_index, current))

    undocumented = [
        e["spec"]["id"] for e in entries
        if e["buildState"] != "PLANNED" and not (HELP / f"{e['spec']['id'].lower()}.md").exists()
    ]
    print(f"Generated {len(entries)} screen pages and {len(versions)} API reference version(s).")
    if undocumented:
        print("These screens are in the app but have no help page in site/help/screens/:")
        print("  " + ", ".join(undocumented))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
