# ListenNow App

ListenNow is a modern Android Music Player that allows you to manage your local library and seamlessly download/synchronize songs from YouTube Music through a dedicated API.

## 🚀 Features

- **YouTube Music Integration:** Search and download songs directly from YouTube Music.
- **Cloud Synchronization:** Your music library is synced with the [ListenNow API](https://github.com/GeovaniTech/Listennow-Api). If you reinstall the app or delete local files, your library is automatically restored.
- **Playlist Management:** Create and organize your favorite songs into custom playlists.
- **Smart Background Playback:** Features a foreground service with media notifications and MediaSession integration for seamless control from the lock screen or Bluetooth devices.
- **Audio Intelligence:** 
    - Automatically pauses when headphones are disconnected.
    - Supports voice commands for hands-free interaction.
- **Seamless UX:** 
    - Dark/Light mode support.
    - Intuitive swipe gestures for navigation.
    - Modern UI built with Material Design components.

## 📱 SDK Support

- **Minimum SDK:** 26 (Android 8.0 Oreo)
- **Target SDK:** 36 (Android 16)

## 🛠 Tech Stack

- **Language:** [Kotlin](https://kotlinlang.org/)
- **Architecture:** MVVM (Model-View-ViewModel) with Repository pattern.
- **Firebase Crashlytics:** [Crashlytics](https://firebase.google.com/products/crashlytics) for monitoring logs.
- **Dependency Injection:** [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Database:** [Room](https://developer.android.com/training/data-storage/room) for local persistence.
- **Networking:** [Retrofit](https://square.github.io/retrofit/) & [Moshi](https://github.com/square/moshi) for API communication.
- **Image Loading:** [Glide](https://github.com/bumptech/glide) & [Coil](https://coil-kt.github.io/coil/)
- **Navigation:** [Jetpack Navigation Component](https://developer.android.com/guide/navigation)
- **UI:** Fragments, ViewBinding, and DataBinding.

## 📸 Screenshots

### Home - Songs already added
![Home - Songs Already Synced](https://github.com/user-attachments/assets/0362086c-ee7b-414e-b503-f79ac9ea6c07)

### Search for new songs to sync
![search for new songs to sync](https://github.com/user-attachments/assets/02609259-adce-4d63-a9eb-1fa7f3eb66dc)

### Select a playlist
![select a playlist](https://github.com/user-attachments/assets/91503240-8d07-4f52-9903-dfa1a35dac64)

### Inside a playlist
![inside a playlist](https://github.com/user-attachments/assets/0bd608ea-83be-429e-99d4-f53a054189bd)


