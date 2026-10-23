#!/usr/bin/env python3
"""Updates openapi/v1/ from a freshly fetched openapi.yaml and prints a PR summary.

Usage: sync_openapi.py <new-openapi.yaml> <api-commit-sha>
Writes openapi/v1/openapi.yaml and SOURCE.yaml, and adds a row to openapi/CHANGELOG.md.
If the new document serves a different API version, it stops: a new API version is a deliberate
change (openapi/v2/, ApiVersioning.CURRENT, the docs nav), not an automatic one.
"""
import datetime
import pathlib
import shutil
import sys

import yaml

ROOT = pathlib.Path(__file__).resolve().parents[1]
TARGET = ROOT / "openapi" / "v1"
API_REPO = "CompositeCode/substratalapps.com"


def operations(spec):
    return {
        (m.upper(), p): op.get("operationId", "")
        for p, item in spec["paths"].items()
        for m, op in item.items()
        if m in ("get", "post", "put", "patch", "delete")
    }


def served_versions(spec):
    param = spec.get("components", {}).get("parameters", {}).get("ApiVersion", {})
    return param.get("schema", {}).get("enum", [])


def main():
    new_path, commit = pathlib.Path(sys.argv[1]), sys.argv[2]
    old = yaml.safe_load((TARGET / "openapi.yaml").read_text())
    new = yaml.safe_load(new_path.read_text())

    if "1" not in served_versions(new):
        sys.exit(f"The new spec no longer serves version 1 ({served_versions(new)}). Handle this by hand.")

    old_ops, new_ops = operations(old), operations(new)
    added = sorted(set(new_ops) - set(old_ops))
    removed = sorted(set(old_ops) - set(new_ops))
    today = datetime.date.today().isoformat()

    shutil.copyfile(new_path, TARGET / "openapi.yaml")
    (TARGET / "SOURCE.yaml").write_text(
        f'api_version: "1"\n'
        f'spec_version: "{new["info"]["version"]}"\n'
        f"source_repo: {API_REPO}\n"
        f"source_path: docs/openapi.yaml\n"
        f"source_commit: {commit}\n"
        f'synced_at: "{datetime.datetime.now(datetime.timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")}"\n'
    )

    notes = []
    if added:
        notes.append(f"{len(added)} added")
    if removed:
        notes.append(f"{len(removed)} removed")
    note = (", ".join(notes) or "no operation changes") + f"; {len(new_ops)} operations."
    with open(ROOT / "openapi" / "CHANGELOG.md", "a") as f:
        f.write(
            f"| {today} | 1 | {new['info']['version']} | "
            f"[`{commit[:7]}`](https://github.com/{API_REPO}/commit/{commit}) | {note} |\n"
        )

    print(f"Updates the pinned Platform API spec from `{old['info']['version']}` to `{new['info']['version']}` "
          f"([{API_REPO}@{commit[:7]}](https://github.com/{API_REPO}/commit/{commit})).\n")
    for title, rows in (("Added", added), ("Removed", removed)):
        if rows:
            print(f"### {title}\n")
            print("\n".join(f"- `{m} {p}` ({new_ops.get((m, p)) or old_ops.get((m, p))})" for m, p in rows))
            print()
    if removed:
        print("Removed operations fail the API coverage check if a screen still calls them. Update "
              "`ScreenCatalog` before merging.\n")
    print("After merging, the docs workflow republishes the API reference.")


if __name__ == "__main__":
    main()
