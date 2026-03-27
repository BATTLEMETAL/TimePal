# TimePal: AI-Powered Android Task Manager

**TimePal** uses GPT to break complex goals into micro-steps with a lifecycle-aware Focus Engine.

## Features
* AI Task Decomposition: GPT breaks goals into micro-steps
* * Focus Engine: countdown timer with no memory leaks
* * Analytics: MPAndroidChart visualization
* * Offline Fallback: local heuristics when API unavailable

* ## Tech Stack
* * Platform: Android SDK 34, Java 17+
* * Persistence: Room Database offline-first
* * Networking: Retrofit 2 + OkHttp
* * AI: OpenAI API GPT-4
* * Architecture: MVVM + Repository Pattern

* ## Key Solutions
* ANR Prevention: Room ops offloaded to background thread pool via Executor Service.
* Memory Leak Prevention: Timer bound to Activity lifecycle (onDestroy).

* ## Quick Start
* git clone https://github.com/BATTLEMETAL/TimePal.git
* Open in Android Studio, add OPENAI_API_KEY to local.properties
* Build and Run on device or emulator API 26+

* ## Roadmap
* * Room DB async concurrency (done)
* * OpenAI task decomposition (done)
* * Focus Engine (done)
* * Jetpack Compose migration (planned)
* * Firebase sync (planned)
* 
