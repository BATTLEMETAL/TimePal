# TimePal: AI-Powered Task Manager

[![Android](https://img.shields.io/badge/Android-SDK%2034-3DDC84?logo=android)](.)
[![Java](https://img.shields.io/badge/Java-17+-ED8B00?logo=openjdk)](.)
[![Status](https://img.shields.io/badge/Status-Active%20R%26D-blue)](.)

**TimePal** to Android task manager ktory uzywa GPT do automatycznego rozbijania celow na mikro-kroki
i lifecycle-aware Focus Engine do wymuszania skupienia.

---

## Funkcje

| Funkcja | Opis |
|---|---|
| AI Task Decomposition | GPT rozbija zlozone cele na actionable micro-steps |
| Focus Engine | Licznik czasu z lifecycle-aware zarzadzaniem watkami (brak memory leaks) |
| Analytics | Wizualizacja postepow z MPAndroidChart |
| Offline Fallback | Lokalna heurystyka gdy API niedostepne |

---

## Architektura

```
UI Layer (XML -> Jetpack Compose migration)
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

## Tech Stack

- **Platform:** Android SDK 34, Java 17+
- - **Persistence:** Room Database (Offline-First)
  - - **Networking:** Retrofit 2 + OkHttp
    - - **AI:** OpenAI API (GPT-4)
      - - **Visualization:** MPAndroidChart
        - - **Architecture:** MVVM + Repository Pattern
         
          - ## Kluczowe rozwiazania techniczne
         
          - **Problem ANR (Application Not Responding):**
          - Room operations sa offloadowane na background thread pool przez Executor Service,
          - gwarantujac 60 FPS UI bez blokowania main thread.
         
          - **Memory Leak Prevention:**
          - Timer jest scisle powiazany z cyklem zycia Activity (onDestroy),
          - cancellujac wszystkie watki i zapobiegajac ghost processes.
         
          - ## Quick Start
         
          - ```bash
            git clone https://github.com/BATTLEMETAL/TimePal.git
            # Otworz w Android Studio
            # Dodaj swoj klucz OpenAI API w local.properties:
            # OPENAI_API_KEY=sk-...
            # Build -> Run na emulatorze lub urzadzeniu (API 26+)
            ```

            ## Roadmap

            - [x] Room DB + async concurrency
            - [ ] - [x] OpenAI task decomposition + fallback
            - [ ] - [x] Lifecycle-aware Focus Engine
            - [ ] - [ ] Migration to Jetpack Compose
            - [ ] - [ ] Firebase real-time sync
            - [ ] - [ ] Firebase Analytics dashboard
            - [ ] 
