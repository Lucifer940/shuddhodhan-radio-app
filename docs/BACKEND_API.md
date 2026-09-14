# Radio Shuddhodhan — Backend API Contract

This Android app is a **complete client**. It runs fully functional in
**demo mode** (local Room database, on-device admin console) until a backend
server is connected in **Settings → Advanced → Backend server**. Once a base
URL is configured, every screen syncs from the server and the app becomes
cloud-controlled.

> **Security rule:** no administrator credentials, OpenAI keys, database
> credentials or any other secrets are stored in the app. All privileged
> actions are authorized server-side with a Bearer token.

## Architecture

```
ADMIN (in-app Admin console or any admin web UI)
        │
        ▼
BACKEND / DATABASE   (your server — REST + FCM + AI proxy)
        │
        ▼  sync: on app start, on connectivity regain, every 15 min, push (FCM)
USER APP (this Android client — Room cache + reactive UI)
```

## Content type

All requests/responses are JSON (`application/json`).
Authentication uses `Authorization: Bearer <token>`.

## Endpoints

### Public (user app)

| Method | Path | Response | Description |
|---|---|---|---|
| GET | `/api/v1/config` | `AppConfig` | Remote configuration (source of truth) |
| GET | `/api/v1/news?category=&q=&limit=` | `News[]` | Published news |
| GET | `/api/v1/news/{id}` | `News` | Single article |
| GET | `/api/v1/posts?limit=` | `Post[]` | Published posts |
| GET | `/api/v1/stations` | `Station[]` | Radio stations |
| GET | `/api/v1/stations/{id}/listeners` | `{stationId, count, updatedAt}` | Live listener count (drives the "x listening" badge) |
| GET | `/api/v1/events?from=&to=` | `Event[]` | Calendar events |
| GET | `/api/v1/announcements` | `Announcement[]` | Active announcements |
| GET | `/api/v1/social-links` | `SocialLink[]` | Social links (incl. Facebook Live) |
| GET | `/api/v1/notifications?since=` | `Notification[]` | Notification history |
| POST | `/api/v1/helpdesk` | `{ok, message}` | Submit helpdesk ticket |

### Authentication

| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/api/v1/auth/register` | `{name?, email?, phone?, password}` | Register; returns `{token, userId, name, email, phone}` |
| POST | `/api/v1/auth/login` | `{email?, phone?, password}` | Login; returns bearer token |

Google / Facebook / OTP sign-in require server-side OAuth configuration;
enable them in the remote config (`googleLoginEnabled`, `facebookLoginEnabled`)
and implement the corresponding OAuth endpoints on your backend.

### Push notifications

| Method | Path | Body | Description |
|---|---|---|---|
| POST | `/api/v1/devices/register` | `{pushToken, platform, preferences}` | Register FCM token + user's notification preferences |

To enable FCM push:
1. Create a Firebase project, add an Android app with package
   `com.radioshuddhodhan.app`.
2. Put `google-services.json` in `app/`.
3. In the root `build.gradle.kts` add
   `id("com.google.gms.google-services") version "4.4.2" apply false`
   and in `app/build.gradle.kts` apply the plugin.
4. Give the server the Firebase service-account credentials to send pushes.

Until then the app's `RadioFirebaseMessagingService` stays inert (guarded),
and admin notifications are delivered on-device (demo).

### Admin (require `Authorization: Bearer <adminToken>`)

| Method | Path | Description |
|---|---|---|
| POST | `/api/v1/admin/login` | `{password}` → `{token, isAdmin: true}` — **server-side authorization only** |
| PUT | `/api/v1/admin/config` | Save remote config |
| POST / PUT / DELETE | `/api/v1/admin/news[/{id}]` | News CRUD |
| POST / PUT / DELETE | `/api/v1/admin/stations[/{id}]` | Station CRUD (stream URL, order, enabled, featured) |
| POST / DELETE | `/api/v1/admin/events[/{id}]` | Event CRUD |
| POST / DELETE | `/api/v1/admin/announcements[/{id}]` | Announcement CRUD |
| GET / POST / DELETE | `/api/v1/admin/social-links[/{id}]` | Social link CRUD |
| GET / PUT | `/api/v1/admin/helpdesk[/{id}]` | Ticket inbox + reply (status: open/pending/resolved) |
| POST | `/api/v1/admin/notifications` | `{title, body, type}` → fan-out to all devices via FCM |
| POST | `/api/v1/admin/ai/generate` | AI proxy (see below) |

### AI News Assistant — secure proxy

```
POST /api/v1/admin/ai/generate
{ "action": "headline|summarize|rewrite|translate|script|draft",
  "input": "…", "language": "ne" }
→ { "output": "…" }
```

The server calls OpenAI with the API key kept **server-side**. The Android
app never sees the key. AI output is saved in the app only as an
**unpublished draft** — administrator approval is required before publishing.

## Data models

### AppConfig

```json
{
  "liveRadioEnabled": true, "newsEnabled": true, "calendarEnabled": true,
  "helpdeskEnabled": true, "postsEnabled": true, "stationsEnabled": true,
  "socialEnabled": true, "maintenanceMode": false,
  "featuredStationId": null,
  "primaryStreamUrl": "https://…", "primaryStationName": "Radio Shuddhodhan",
  "homeBannerText": "", "homeBannerTextNe": "",
  "currentProgram": "", "currentProgramNe": "",
  "contactPhone": "", "contactEmail": "", "contactWhatsapp": "",
  "contactWebsite": "", "aboutText": "", "aboutTextNe": "",
  "appLogoUrl": "", "googleLoginEnabled": false,
  "facebookLoginEnabled": false, "phoneLoginEnabled": true,
  "stationFrequency": "95.1 MHz",
  "stationAddress": "Shuddhodhan-4, Pharsatikar, Rupandehi, Nepal",
  "taglineNe": "हरेक नेपालीको मन रेडियो शुद्धोधन 95.1 मेगाहर्ज.",
  "taglineEn": "In every Nepali's heart — Radio Shuddhodhan 95.1 MHz.",
  "teamMembers": [
    {"roleKey": "manager", "role": "Station Manager", "roleNe": "स्टेशन प्रमुख",
     "name": "Ravi Rana", "contact": "", "sortOrder": 0},
    {"roleKey": "technician", "role": "Technician", "roleNe": "प्राविधिक",
     "name": "", "contact": "", "sortOrder": 1},
    {"roleKey": "marketing", "role": "Marketing Manager", "roleNe": "मार्केटिङ प्रमुख",
     "name": "", "contact": "", "sortOrder": 2}
  ],
  "updatedAt": 0
}
```

Unknown fields are ignored, missing fields fall back to the app defaults —
the config is backwards compatible.

### News / Post

```json
{ "id": "…", "title": "…", "summary": "…", "content": "…",
  "category": "समाचार", "imageUrl": null, "author": "…",
  "publishedAt": 0, "updatedAt": 0,
  "isPublished": true, "isFeatured": false, "isBreaking": false }
```

### Station

```json
{ "id": "…", "name": "…", "nameNe": "…", "description": "…",
  "streamUrl": "https://…", "logoUrl": null,
  "isEnabled": true, "isFeatured": false, "sortOrder": 0 }
```

### Event

```json
{ "id": "…", "title": "…", "titleNe": "…", "description": "…",
  "adDate": "2026-09-26", "timeLabel": "19:00", "location": "…",
  "isFeatured": false }
```

`adDate` is a Gregorian ISO date; the app converts to Bikram Sambat
automatically.

### Announcement / SocialLink / HelpdeskTicket / Notification

See `app/src/main/java/com/radioshuddhodhan/app/data/remote/ApiModels.kt`
for the exact shapes — they map 1:1 to the Retrofit interface
`ApiService.kt`, which is the single integration point of the client.

## Real-time behaviour

- The app pulls all content: on launch, when connectivity returns, every
  15 minutes (WorkManager) and whenever the user refreshes.
- **Instant** delivery to user devices requires the FCM setup above; a push
  with `data.type` and the content id triggers an immediate refresh.
- Because every screen observes the local Room database reactively, any
  change that arrives — sync or push — updates the UI with no restart and
  no APK update.
