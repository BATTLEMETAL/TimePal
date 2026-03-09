# ⏳ TimePal - Your Ultimate Productivity Assistant

<div align="center">
  <img src="app/src/main/res/mipmap-xxxhdpi/ic_launcher.png" alt="TimePal Logo" width="128"/>
  <br>
  <strong>Boost your productivity with AI-driven task management and focus tracking.</strong>
  <br>

</div>

---

## 🌟 Overview

**TimePal** is a smart Android application designed to help you manage your time, break down complex goals into actionable steps, and stay focused. Leveraging the power of OpenAI, TimePal intelligently breaks down tasks for you, sets smart deadlines, and visualizes your productivity with interactive charts.

## ✨ Key Features

- **🤖 AI-Powered Task Breakdown**: Powered by OpenAI API, TimePal automatically creates detailed, actionable sub-steps for any complex task.
- **🎯 Focus Mode**: Distraction-free environment built to help you concentrate on one task at a time.
- **📊 Real-time Statistics**: Interactive charts (powered by MPAndroidChart) to track your focus sessions and completed tasks over time.
- **⏰ Smart Deadlines & Reminders**: Reliable scheduling and notifications via Android's AlarmManager to keep you on track.
- **💾 Local Storage**: All tasks and stats are securely saved locally on your device via Room Database.

## 🛠️ Tech Stack

- **Language:** Java
- **UI:** Android XML / Material Design
- **Local Database:** Room
- **Networking:** Retrofit, OkHttp, GSON
- **AI Integration:** OpenAI API
- **Charts:** MPAndroidChart Library
- **Architecture/Patterns:** MVVM (Model-View-ViewModel) inspired, Coroutines for async operations

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug (or newer)
- Minimum Android SDK: 24 (Target: 34)
- An active [OpenAI API Key](https://platform.openai.com/)

### Installation

1. **Clone the repository:**
   ```bash
   git clone https://github.com/BATTLEMETAL/TimePal.git
   ```
2. **Open the project in Android Studio.**
3. **Add your API Key:**
   Create or edit the `local.properties` file in the root directory and add your OpenAI key:
   ```properties
   OPENAI_API_KEY=sk-your_openai_api_key_here
   ```
   > **Note:** The `local.properties` file is git-ignored and should never be pushed to your repository.

4. **Build and Run the project** on your emulator or physical Android device.

## 📸 Screenshots

*(Add screenshots here)*

| Home Screen | AI Task Breakdown | Focus Mode | Statistics |
| :---: | :---: | :---: | :---: |
| <img src="assets/home_placeholder.png" width="200" alt="Home Screen"/> | <img src="assets/ai_placeholder.png" width="200" alt="AI Breakdown"/> | <img src="assets/focus_placeholder.png" width="200" alt="Focus Mode"/> | <img src="assets/stats_placeholder.png" width="200" alt="Stats"/> |

*(Create an `assets` folder in the root directory and place your `.png` screenshots there to replace these placeholders).*

## 📜 License

This project is licensed under the [MIT License](LICENSE).
