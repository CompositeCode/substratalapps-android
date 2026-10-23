---
title: Check on the API
description: See whether the Platform API is healthy, and what to look at when it isn't.
---

# Check on the API

**Operations** shows how the Platform API is doing: its components, traffic, errors, background jobs, alerts and cost. It's visible to people with operations access.

1. Go to **Manage › Overview** (on tablets: **Operations › Overview**).
2. The top of the screen shows each component's status: the API, MCP server, Stripe webhook, delivery worker, scheduled jobs and the database.
3. Below that are the last 24 hours of requests, the server error rate, and p95 latency against the 150 ms target.
4. **Alerts** lists anything firing. Tap one for details.

!!! note "Preview"
    The Overview's layout is in the app, but apart from the API's own status it shows **sample data** until the API's operations endpoints exist (issues [#43](https://github.com/CompositeCode/substratalapps.com/issues/43), [#44](https://github.com/CompositeCode/substratalapps.com/issues/44), [#47](https://github.com/CompositeCode/substratalapps.com/issues/47)).

## Quick check without the app

The health endpoint needs no token and no version header, so uptime monitors can call it as is:

```bash
curl -s https://api.substratalapps.com/health
# {"status":"ok"}
```

Operational alarms (5xx rate, Lambda errors and throttles, database capacity, webhook dead letters) also go out by email from AWS CloudWatch.
