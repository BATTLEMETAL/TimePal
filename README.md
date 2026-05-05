[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=flat&logo=openjdk&logoColor=white)](https://openjdk.org)
[![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84?style=flat&logo=android&logoColor=white)](https://developer.android.com)
[![Room](https://img.shields.io/badge/Room-Database-4479A1?style=flat&logo=sqlite&logoColor=white)](https://developer.android.com/training/data-storage/room)
[![OpenAI](https://img.shields.io/badge/OpenAI-API-412991?style=flat&logo=openai&logoColor=white)](https://openai.com)
[![License](https://img.shields.io/badge/License-MIT-green?style=flat)](LICENSE)
[![Status](https://img.shields.io/badge/Status-Active%20R%26D-blue?style=flat)](./README.md)

# TimePal — AI-Powered Android Task Manager

> An Android productivity app that uses OpenAI to automatically decompose complex goals into actionable micro-steps, then enforces execution through a lifecycle-aware Focus Engine with escalating pressure modes.

---

## 🚀 Key Features

### 1. AI Task Decomposition (OpenAI Integration)
When a user adds a complex goal, TimePal calls the **OpenAI API** to automatically break it into concrete micro-steps — eliminating "paralysis by analysis":

```java
// OpenAiApi.java — dynamic step generation
// Sends task title to GPT → returns structured list of actionable steps
// Fallback: local heuristic-based decomposition on API failure / throttle
```

- **Orchestrator pattern:** App calls OpenAI, parses response, stores steps in Room DB
- **Resilient fallback:** Network failure or API throttle → instantly serves local heuristic steps, no user-blocking

### 2. Focus Engine — 3 Pressure Modes

The core of TimePal is its **lifecycle-aware countdown engine** with adaptive psychological pressure:

| Mode | Behaviour |
|---|---|
| 🟢 **Normal** | Clean countdown. Calm reminder messages. |
| 🟠 **Pressure** | Escalating toast messages every 30 ticks: mild → intense |
| 💀 **Hardcore** | 500ms tick interval + audio beep alerts. **Back button disabled.** Steps reset to zero on exit. |

Pressure message progression (context-aware by time of day and weekday):
```
Level 0-1: "⏳ Pamiętaj o celu."
Level 2-3: "🔥 Działaj już teraz!"
Level 4+:  "💀 Mało czasu! DZIAŁAJ TERAZ!"
```

### 3. Memory-Safe Countdown Timer

A common Android pitfall: `CountDownTimer` running after Activity destruction causes memory leaks and ghost processes. TimePal solves this with deterministic resource teardown:

```java
@Override
protected void onDestroy() {
    super.onDestroy();
    if (countDownTimer != null) {
        countDownTimer.cancel(); // explicit cancel → no memory leak
    }
    if (beepSound != null) beepSound.release(); // MediaPlayer released
}
```

### 4. Real-Time Progress Tracking

Live step completion progress bar — updates on every step checkbox toggle without full screen reload.

### 5. Smart Notifications

- **Deadline reminders** — `DeadlineReceiver` fires before task deadline
- **General reminders** — `ReminderReceiver` with configurable schedule
- Both survive app restarts via `BroadcastReceiver` + `AlarmManager`

### 6. Analytics Dashboard (`StatsActivity`)

Productivity statistics screen — task completion rates, streaks, and historical performance visualised with **MPAndroidChart**.

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────┐
│                      TimePal App                        │
├───────────────────────┬─────────────────────────────────┤
│   UI Layer            │   Data Layer                    │
│   ├── MainActivity    │   ├── Room Database             │
│   ├── AddTaskActivity │   │   ├── Task.java (entity)    │
│   ├── FocusModeActivity│  │   ├── TaskStep.java         │
│   ├── StatsActivity   │   │   ├── TaskDao               │
│   ├── SettingsActivity│   │   └── TaskStepDao           │
│   └── OnboardingActivity  │                             │
├───────────────────────┤   └── SharedPreferences         │
│   Network Layer       │       (mode, user settings)     │
│   ├── Retrofit + OkHttp│                                │
│   └── OpenAI API      │                                 │
├───────────────────────┴─────────────────────────────────┤
│   Notifications                                         │
│   ├── DeadlineReceiver  (deadline alerts)               │
│   └── ReminderReceiver  (scheduled reminders)           │
└─────────────────────────────────────────────────────────┘
```

**Pattern:** Classic Android MVC — Activities as controllers, Room as data source, Retrofit for network.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 17+ |
| **Platform** | Android SDK 34 (min API 24 — Android 7.0+) |
| **Persistence** | Room Database (SQLite ORM) — offline-first |
| **Networking** | Retrofit + OkHttp |
| **AI Integration** | OpenAI API (GPT task decomposition) |
| **Charts** | MPAndroidChart (productivity analytics) |
| **Notifications** | AlarmManager + BroadcastReceiver |
| **Build** | Gradle (Kotlin DSL) |

---

## 📁 Project Structure

```
TimePal/
├── app/src/main/java/com/example/timepal/
│   ├── MainActivity.java          # Task list — main entry point
│   ├── AddTaskActivity.java       # Task creation + OpenAI step generation (11KB)
│   ├── FocusModeActivity.java     # Focus engine — 3 modes, countdown, memory-safe (6.7KB)
│   ├── StatsActivity.java         # Analytics dashboard — MPAndroidChart (7.3KB)
│   ├── SettingsActivity.java      # Mode selection, notification config
│   ├── OnboardingActivity.java    # First-run onboarding flow
│   ├── Task.java                  # Room entity
│   ├── TaskStep.java              # Room entity — AI-generated steps
│   ├── TaskDao.java               # DAO — task queries
│   ├── TaskStepDao.java           # DAO — step queries
│   ├── TaskDatabase.java          # Room database singleton
│   ├── TaskAdapter.java           # RecyclerView adapter
│   ├── TaskStepAdapter.java       # RecyclerView adapter + progress callback
│   ├── DeadlineReceiver.java      # BroadcastReceiver — deadline alerts
│   ├── ReminderReceiver.java      # BroadcastReceiver — scheduled reminders
│   └── OpenAiApi.java             # Retrofit interface for OpenAI
└── docs/                          # Additional documentation
```

---

## 🚀 Quick Start

```bash
# 1. Clone the repo
git clone https://github.com/BATTLEMETAL/TimePal.git

# 2. Open in Android Studio

# 3. Add your OpenAI API key to local.properties:
OPENAI_API_KEY=sk-...

# 4. Build and run on device/emulator (API 24+)
```

---

## 📋 Roadmap

- [x] Room database with offline-first architecture
- [x] OpenAI task decomposition with fallback
- [x] 3-mode Focus Engine (Normal / Pressure / Hardcore)
- [x] Deadline + reminder notifications
- [x] Stats dashboard (MPAndroidChart)
- [ ] Jetpack Compose UI migration (in progress)
- [ ] Firebase cloud sync + multi-device support
- [ ] Widget for home screen quick-add

---

*R&D project exploring AI-assisted productivity on Android. Focus Engine and pressure system built to study behavioural compliance patterns in task management.*
