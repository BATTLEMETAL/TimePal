# TimePal: Intelligent Task Manager for Android

TimePal is a feature-rich task management application for Android, designed to enhance productivity by combining traditional to-do list functionalities with AI-powered assistance and motivational tools. The app facilitates breaking down complex tasks into manageable steps and helps users stay focused on their deadlines.

---

### Key Features

*   **Advanced Task Management:** Create tasks with titles, detailed descriptions, and precise deadlines.
*   **Task Decomposition:** Break down large goals into a checklist of smaller, actionable sub-steps.
*   **AI-Powered Step Suggestions:** Leverage the OpenAI (GPT) API to automatically generate a list of necessary steps for any given task.
*   **Dedicated Focus Mode:** An immersive, distraction-free screen that displays a single task with its steps and a real-time countdown to the deadline.
*   **Productivity Modes:** Choose between three distinct modes (`Normal`, `Pressure`, `Hardcore`) to tailor the app's motivational feedback and challenges.
*   **Automated Reminders:** Receive timely notifications before deadlines to ensure tasks are completed on time.
*   **Productivity Analytics:** Track and visualize personal progress with an integrated statistics dashboard, including charts for task completion and daily progress.
*   **Customizable Interface:** Full support for both light and dark themes to match user preferences.

---

### Tech Stack & Architecture

This project was built using modern Android development practices and libraries, demonstrating proficiency in a range of essential technologies.

*   **Language:** **Java**
*   **Platform:** **Android SDK**
*   **Architecture:** A component-based architecture utilizing Android's core components (Activities, BroadcastReceivers) and principles of separation of concerns.
*   **Database:** **Room Persistence Library (Android Jetpack)** for robust, local SQLite database management.
*   **Networking:** **Retrofit & OkHttp** for type-safe, asynchronous communication with the OpenAI REST API.
*   **UI/UX:** **Material Design Components**, **RecyclerView**, and **ViewBinding** for creating a modern, responsive, and efficient user interface.
*   **Data Visualization:** **MPAndroidChart** library for rendering dynamic and informative charts in the statistics module.
*   **Asynchronous Operations:** **Java Executors** to perform database and network operations on background threads, ensuring a smooth and non-blocking UI.

---

### Local Setup & Installation

To build and run the project on your local machine, please follow these steps:

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/BATTLEMETAL/TimePal.git
    ```

2.  **Open the project in Android Studio.** The IDE will automatically handle the Gradle build and download the required dependencies.

3.  **Configure API Access:**
    The project requires an API key from OpenAI to enable the AI-powered features.
    *   Create a file named `local.properties` in the root directory of the project.
    *   Add your secret API key to the file in the following format:
      ```properties
      OPENAI_API_KEY="YOUR_OPENAI_API_KEY_HERE"
      ```

4.  **Build and run the application** on an Android emulator or a physical device.

---

