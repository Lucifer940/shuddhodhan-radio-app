# Radio Shuddhodhan — Live backend

One zero-dependency Node server that keeps the **Android app** and the **website** in sync:

1. **Serves the website** — static files from `../web`.
2. **Implements the REST API** the app already speaks (same contract as `docs/BACKEND_API.md`).
3. **Pushes changes live** — every admin change is broadcast over Server-Sent Events
   (`GET /api/v1/events`), so an open website updates the instant the admin saves something.

## Run

```bash
node server/server.js
```

- Website: http://localhost:8080/
- API base: http://localhost:8080/api/v1/...
- Admin password (default): `shuddhodhan951`

Override the port / password with env vars:

```bash
PORT=8080 ADMIN_PASSWORD=your-secret node server/server.js
```

## How "live change on both sides" works

```
Admin (app admin console  OR  website admin) 
        │  saves config
        ▼
  server.js  (persists to server/data.json)
        │  broadcasts SSE "change" event
        ├──────────────►  Website (EventSource → re-fetches → re-renders instantly)
        └──────────────►  Android app (SyncManager refetches on start/connectivity/
                          15-min cycle/pull-to-refresh → Room → UI updates)
```

- **Website admin** is at *Settings → Admin console* (register/login first, then enter the
  admin password). Branding fields (operator, tagline ×2, Nepali/English address, frequency,
  stream URL, phone, email, current programme) save to `/api/v1/admin/config` and update
  every viewer instantly.
- **App admin** is the in-app *Remote configuration* screen — it writes to the same backend,
  so both sides converge on the same data.

Data is auto-seeded on first run into `server/data.json` (git-ignored).
