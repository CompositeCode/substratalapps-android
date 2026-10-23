#!/usr/bin/env python3
"""Cross-checks the app's screen catalog against the pinned OpenAPI document.

Fails (exit 1) when a screen calls an operation that the API spec doesn't have, so a spec
update that removes or renames an endpoint can't merge without the app and docs following.
Also writes a coverage report: spec operations no screen uses yet.

Usage: check_api_coverage.py <screens.json> <openapi.yaml> [--report out.md]
"""
import json
import re
import sys

import yaml


def norm(path: str) -> str:
    # Parameter names differ between documents ({id} vs {userId}); compare shapes only.
    return re.sub(r"\{[^}]+\}", "{}", path.rstrip("/") or "/")


def main() -> int:
    screens_path, spec_path = sys.argv[1], sys.argv[2]
    report = sys.argv[sys.argv.index("--report") + 1] if "--report" in sys.argv else None
    catalog = json.load(open(screens_path))
    spec = yaml.safe_load(open(spec_path))

    spec_ops = {}
    for path, item in spec["paths"].items():
        for method, op in item.items():
            if method in ("get", "post", "put", "patch", "delete"):
                spec_ops[(method.upper(), norm(path))] = (path, op.get("operationId", ""), op.get("summary", ""))

    used, missing = set(), []
    for entry in catalog["screens"]:
        spec_screen = entry["spec"]
        for op in spec_screen["operations"]:
            key = (op["method"], norm(op["path"]))
            if key in spec_ops:
                used.add(key)
            else:
                missing.append(f'{spec_screen["id"]}: {op["method"]} {op["path"]}')

    unused = sorted(k for k in spec_ops if k not in used)
    print(f"{len(spec_ops)} operations in the spec, {len(used)} used by screens, {len(unused)} not used yet.")
    if report:
        with open(report, "w") as f:
            f.write("| Method | Path | operationId | Summary |\n|---|---|---|---|\n")
            for k in unused:
                path, op_id, summary = spec_ops[k]
                f.write(f"| `{k[0]}` | `{path}` | `{op_id}` | {summary} |\n")
    if missing:
        print("Screens call operations the API spec doesn't have:")
        print("\n".join("  " + m for m in missing))
        return 1
    return 0


if __name__ == "__main__":
    sys.exit(main())
