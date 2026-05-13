# TimePal — AI-Powered Android Task Manager

[![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84?logo=android)](.)
[![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk)](.)
[![Status](https://img.shields.io/badge/Status-Active%20Development-blue)](.)

Android task manager that uses GPT-4 to automatically decompose goals into actionable micro-steps,
with a lifecycle-aware Focus Engine for enforcing deep work sessions.

---

## Features

| Feature | Description |
|---|---|
| AI Task Decomposition | GPT-4 breaks complex goals into prioritized micro-steps |
| Focus Engine | Lifecycle-aware timer — no memory leaks, no ANR, 60 FPS UI |
| Analytics | Progress visualization with MPAndroidChart |
| Offline Fallback | Local heuristics when OpenAI API is unavailable |

---

## Architecture

```
UI Layer (XML)
      |
ViewModel + LiveData
      |
Repository Pattern
   /       \
Room DB    Retrofit + OkHttp
(offline)   (OpenAI API)
      |
MPAndroidChart (analytics)
```

**Pattern:** MVVM + Repository | **Storage:** Room (offline-first) | **Networking:** Retrofit 2 + OkHttp

---

## Key Engineering Decisions

**ANR Prevention:**
Room database operations are offloaded to a background `ExecutorService`, ensuring the main thread never blocks — guaranteed 60 FPS UI.

**Memory Leak Prevention:**
The Focus Engine timer is strictly bound to the Activity lifecycle (`onDestroy`), cancelling all threads and preventing ghost processes.

---

## Tech Stack

| Component | Technology |
|---|---|
| Platform | Android SDK 34, Java 17+ |
| Persistence | Room Database (offline-first) |
| Networking | Retrofit 2 + OkHttp |
| AI | OpenAI API (GPT-4) with local fallback |
| Visualization | MPAndroidChart |
| Architecture | MVVM + Repository Pattern |

---

## Quick Start

```bash
git clone https://github.com/BATTLEMETAL/TimePal.git
# Open in Android Studio
# Add your OpenAI API key to local.properties:
# OPENAI_API_KEY=sk-...
# Build → Run on emulator or device (API 26+)
```

---

## Implemented

- ✅ Room DB with async concurrency (ExecutorService)
- ✅ OpenAI GPT-4 task decomposition with offline fallback
- ✅ Lifecycle-aware Focus Engine (Normal / Pressure / Hardcore modes)
- ✅ MVVM + Repository architecture
- ✅ MPAndroidChart analytics dashboard
