# TimePal: AI-Powered Android Task Manager

[![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84?logo=android)](https://github.com/BATTLEMETAL/TimePal)
[![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk)](https://github.com/BATTLEMETAL/TimePal)
[![Status](https://img.shields.io/badge/Status-Active%20R%26D-blue)](https://github.com/BATTLEMETAL/TimePal)

**TimePal** is an Android task manager that uses GPT to decompose complex goals into micro-steps,
with a lifecycle-aware Focus Engine to enforce execution.

---

## Features

| Feature | Description |
|---|---|
| AI Task Decomposition | GPT breaks complex goals into actionable micro-steps |
| Focus Engine | Countdown timer with lifecycle-aware thread management (no memory leaks) |
| Analytics | Progress visualization with MPAndroidChart |
| Offline Fallback | Local heuristics when API unavailable |

---

## Architecture

```
UI Layer (XML -> Jetpack Compose migration)
        |
ViewModel + LiveData
        |
Repository Pattern
    /         \\
Room DB    Retrofit + OkHttp
(offline)   (OpenAI API)
        |
MPAndroidChart (analytics)
```

## Tech Stack

- **Platform:** Android SDK 34, Java 17+
- - **Persistence:** Room Database (Offline-First)
  - - **Networking:** Retrofit 2 + OkHttp
    - - **AI:** OpenAI API (GPT-4)
      - - **Visualization:** MPAndroidChart
        - - **Architecture:** MVVM + Repository Pattern
         
          - ## Key Technical Solutions
         
          - **ANR Prevention:**
          - Room operations are offloaded to a background thread pool via Executor Service,
          - guaranteeing smooth 60 FPS UI without blocking the main thread.
         
          - **Memory Leak Prevention:**
          - The timer is tightly bound to the Activity lifecycle (onDestroy),
          - canceling all threads to prevent ghost processes.
         
          - ## Quick Start
         
          - ```bash
            git clone https://github.com/BATTLEMETAL/TimePal.git
            # Open in Android Studio
            # Add OPENAI_API_KEY to local.properties
            # Build and Run on device (API 26+)
            ```

            ## Roadmap

            - [x] Room DB + async concurrency
            - [ ] - [x] OpenAI task decomposition + fallback
            - [ ] - [x] Lifecycle-aware Focus Engine
            - [ ] - [ ] Migration to Jetpack Compose
            - [ ] - [ ] Firebase real-time sync
            - [ ] - [ ] Firebase Analytics dashboard
            - [ ] 
