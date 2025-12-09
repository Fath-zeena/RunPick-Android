# RunPick Android App

This is the official Android application for RunPick, designed to provide a seamless, native-like experience for users of the RunPick web platform. The app serves as a native Android wrapper around the primary web application, enhancing its capabilities with deep OS integration.

## Features

The Android application bridges the gap between the web and native platforms by providing the following features:

- **Full WebView Integration:** Displays the responsive [RunPick web app](https://runpick.netlify.app/).
- **Swipe to Refresh:** Users can easily reload the web content with a standard swipe-down gesture.
- **Native Permissions Handling:** Manages runtime permissions for core device features required by the web app:
  - **Camera Access**
  - **Fine Location (GPS)**
  - **Push Notifications** (on Android 13+)
- **Biometric Authentication:** Secures the app with fingerprint or face authentication using the `BiometricPrompt` API.
- **SMS OTP Verification:** Automatically and securely retrieves one-time passcodes from SMS messages using the SMS User Consent API, simplifying the login process.
- **JavaScript Bridge:** A custom `WebAppInterface` allows for secure two-way communication between the web content and the native Android code.
- **Offline Error Handling:** Displays a user-friendly offline page if the device cannot connect to the web service.

## Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Architecture:** Single-Activity with a `WebView`
- **Core Components:**
  - `WebView` and `WebChromeClient` for web content rendering and permissions.
  - `BiometricPrompt` for secure authentication.
  - `SmsRetriever` for OTP handling.
  - `SwipeRefreshLayout` for the pull-to-refresh gesture.

## How to Build

1.  Clone this repository:
    ```sh
    git clone https://github.com/Fath-zeena/RunPick-Android.git
    ```
2.  Open the project in Android Studio.
3.  Let Gradle sync and download the required dependencies.
4.  Build and run the application on an Android device or emulator.
5.  You may have to change 'runpick' hyperlink to your preferred web app domain in 'MainActivity.kt' file.

