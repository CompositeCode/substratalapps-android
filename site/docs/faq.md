---
title: FAQ
description: Answers to common questions about the Substratal management apps.
---

# FAQ

??? question "I can't see Manage, Users or Applications. Why?"
    The app only shows what your account can use. **Manage** appears once you have any administration, developer or operations rights. Ask an administrator for the role you need; see [Permissions and roles](reference/permissions.md).

??? question "A screen says Planned. Is something broken?"
    No. Planned screens aren't built in this app yet. They still open, show what they'll do, and link to their help page. See the [screen reference](reference/screens/index.md) for what's available.

??? question "Why does Operations show sample numbers?"
    The Overview's layout is ready, but the API doesn't have the monitoring endpoints it needs yet. The banner at the top says so. Only the API's own status is live.

??? question "Can I use an API key to sign in to the app?"
    No. The app uses your own account. API keys are for servers and agents, and the API refuses them from browsers.

??? question "Does the app work offline?"
    Partly. Screens you've opened keep showing what was last loaded, but every change needs a connection. That way, turning access off always takes effect immediately.

??? question "Someone's access is off, but they're still in the app. Why?"
    The application is told at once (`access.revoked`) and should end their session. App tokens already issued expire within 5 minutes. If they're still in after that, the application isn't handling the webhook. See [Applications, keys and webhooks](guides/applications.md).

??? question "Why is there no /v1 in the API address any more?"
    The API version moved from the URL into the `Substratal-Version` header, so addresses stay the same across versions. `/v1/...` paths now return `404`. See [API versions and changes](developers/api/versions.md).

??? question "What happens when a new API version comes out?"
    The app keeps working: the old version is supported for at least 6 months. When it's deprecated, the app shows an *Update the app* banner. Update before the date it gives.

??? question "Which help version am I reading?"
    The menu at the top shows it. Help opened from the app matches the app's version. **latest** is the newest release, and **dev** is what's being built now.

??? question "How do I report a problem?"
    Note the **request ID** from the error's details, if there is one, and contact your Substratal administrator or support.
