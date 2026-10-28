# TaskFlow AI

A phone-first Android to-do app starter with a Glance home-screen widget and GitHub Actions CI.

## Current starter features
- Add, complete, and delete tasks in the Android app.
- Persist tasks locally on-device.
- Home-screen widget displays up to five tasks.
- Tap a checkbox in the widget to toggle a task.
- Unit tests validate task-title normalization.
- GitHub Actions runs unit tests and builds a debug APK on pushes to `main`, pull requests, and manual workflow dispatches.

## Build automatically from GitHub
1. Create a new public or private repository named `TaskFlowAI` under your GitHub account.
2. Upload/extract these project files into the repository root (the `.github/workflows/android-ci.yml` file must remain in that exact path).
3. Commit the files to the `main` branch.
4. Open the repository's **Actions** tab and select **Android CI - Test and Build APK**.
5. When the workflow succeeds, open its run, scroll to **Artifacts**, and download `taskflow-ai-debug-apk`.
6. Extract the downloaded artifact ZIP on your Android phone and install the APK. Android may ask you to allow installation from that source.

## Cloud build details
- JDK 17
- Gradle 8.11.1 installed by GitHub Actions
- Android SDK platform 35
- Debug APK artifact retained for 14 days

## Important limitations
- This is an early MVP, not a finished AI assistant.
- AI scheduling, due dates, recurring tasks, reminder notifications, and a polished priority editor are not implemented yet.
- Automated unit tests and a successful APK build do not replace real-device testing. Check widget resizing, taps, and refresh behavior on the target phone.
- The app currently uses local SharedPreferences storage and requires no account or AI API key.

## Development notes
Do not commit API keys or passwords. Future AI integration should use a protected backend or another secure approach; secrets embedded in an APK can be extracted.
