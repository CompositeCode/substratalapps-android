---
title: Operations
description: Monitoring the Platform API — health, traffic, errors, background work, alerts, configuration and cost.
---

# Operations

Operations is for whoever keeps the Platform API running. It's new in the management app; the shared design package doesn't have it yet.

!!! note "What works today"
    **Overview** is in preview: the layout is final, and it shows sample data apart from the API's own status. The other Operations screens are planned. They wait on the API's operations endpoints and permissions: issues [#43](https://github.com/CompositeCode/substratalapps.com/issues/43), [#44](https://github.com/CompositeCode/substratalapps.com/issues/44), [#45](https://github.com/CompositeCode/substratalapps.com/issues/45), [#46](https://github.com/CompositeCode/substratalapps.com/issues/46), [#47](https://github.com/CompositeCode/substratalapps.com/issues/47), [#48](https://github.com/CompositeCode/substratalapps.com/issues/48).

| Screen | Shows |
|---|---|
| [Overview](../reference/screens/ops-01.md) | Component health, 24-hour traffic, error rate, latency, alerts, budget, deployed version |
| [Traffic & latency](../reference/screens/ops-02.md) | Requests and p50/p95/p99 by route over time, against the 150 ms target for hot paths |
| [Errors](../reference/screens/ops-03.md) | 4xx and 5xx by route and code, with request IDs |
| [Usage](../reference/screens/ops-04.md) | Requests by application, key and route; rate-limit hits |
| [Jobs & delivery](../reference/screens/ops-05.md) | Webhook and push delivery health, dead letters, scheduled jobs |
| [Alerts](../reference/screens/ops-06.md) | Firing and resolved alarms; acknowledging |
| [Security events](../reference/screens/ops-07.md) | Failed sign-ins, lockouts, refresh-token reuse, rate-limit hits |
| [Configuration](../reference/screens/ops-08.md) | Feature flags, rate limits, CORS origins, maintenance mode |
| [Integrations](../reference/screens/ops-09.md) | Where alerts go; MCP server and agent keys |
| [Cost](../reference/screens/ops-10.md) | AWS spend against the monthly budgets |

## Who can see it

Operations needs the proposed `operations.view` permission (and `operations.manage` to act). Until the API adds them, the app shows Operations to accounts that hold both `applications.manage` and `roles.manage`, which in practice means superadmins.
