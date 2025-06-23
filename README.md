# ToTravel 🧳✈️

A comprehensive travel sharing Android application built with Kotlin and Jetpack Compose for the Mobile Application Development course at Politecnico di Torino (PoliTo).

## 📱 About

ToTravel is a feature-rich travel companion app that allows users to create, discover, and join travel experiences. Users can organize trips, connect with fellow travelers, and manage their travel adventures all in one place.

## ✨ Key Features

### 🗺️ Travel Management
- **Create Travel Proposals**: Plan and organize trips with detailed itineraries
- **Browse & Discover**: Explore travel opportunities created by other users
- **Join Adventures**: Apply to join trips that match your interests
- **Manage Applications**: Handle requests to join your organized trips

### 👥 Social Features
- **User Profiles**: Personalized profiles with travel preferences and interests
- **Real-time Chat**: Communicate with travel companions
- **Review System**: Rate and review travel experiences and companions
- **Push Notifications**: Stay updated on travel activities and messages

### 🔧 Advanced Functionality
- **Interactive Maps**: Google Maps integration for location services
- **Smart Matching**: Algorithm-based travel recommendations based on interests and destinations
- **Photo Sharing**: Capture and share travel memories
- **Widget Support**: Home screen widget for upcoming travels
- **Offline Support**: Network connectivity awareness

## 🛠️ Technical Stack

### Frontend
- **Kotlin** (97% of codebase)
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Latest Material Design components
- **Navigation Compose** - Type-safe navigation
- **CameraX** - Camera functionality
- **Coil** - Image loading library

### Backend & Services
- **Firebase Ecosystem**:
  - Firebase Authentication
  - Firestore Database
  - Firebase Storage
  - Firebase Cloud Messaging (FCM)
  - Firebase Crashlytics
  - Firebase Performance Monitoring
- **Google Maps Platform**:
  - Maps SDK
  - Places API
  - Location Services

### Architecture
- **MVVM Architecture** with ViewModels
- **Repository Pattern** for data management
- **Coroutines & Flow** for asynchronous programming
- **Dependency Injection** ready structure

## 🚀 Getting Started

### Prerequisites
- Android Studio (latest version recommended)
- Android SDK API 31+
- Google Services configuration

### Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/mathieutreves/ToTravel.git
   cd ToTravel
   ```

2. **Configure Firebase**
   - Create a new Firebase project
   - Add your `google-services.json` file to `android_app/app/`
   - Enable required Firebase services (Auth, Firestore, Storage, FCM)

3. **Configure Google Maps**
   - Obtain a Google Maps API key
   - Create `secrets.properties` file in the project root:
     ```properties
     MAPS_API_KEY=your_api_key_here
     ```

4. **Build and Run**
   ```bash
   cd android_app
   ./gradlew assembleDebug
   ```

## 📁 Project Structure

```
ToTravel/
├── android_app/                 # Main Android application
│   ├── app/src/main/java/com/example/travelsharingapp/
│   │   ├── ui/screens/         # Compose UI screens
│   │   ├── ui/widget/          # Home screen widgets
│   │   ├── utils/              # Utility classes
│   │   └── MainApplication.kt  # Application class
│   └── build.gradle.kts        # App-level build configuration
└── functions/                  # Firebase Cloud Functions
    └── index.js               # Notification handling logic
```

## 🎯 Core Screens

- **Travel Proposal List** - Browse available trips
- **Travel Creation/Management** - Create and edit travel proposals
- **Travel Details** - View trip information and apply
- **User Profile** - Manage personal information and preferences
- **Chat System** - Real-time messaging between travelers
- **Reviews** - Rate and review travel experiences
- **Notifications** - Stay updated on travel activities

## 🔐 Features Implemented

### Authentication & User Management
- Email/password authentication
- Profile setup and management
- Email verification
- Password reset functionality

### Travel System
- Trip creation with interactive maps
- Itinerary planning with multiple stops
- Application management (accept/reject participants)
- Travel status tracking
- Smart recommendation system

### Communication
- Real-time chat between trip participants
- Push notifications for important updates
- In-app notification system

### Additional Features
- Camera integration for photo capture
- Location services and GPS integration
- Offline connectivity handling
- Home screen widgets
- Performance monitoring and crash reporting

## 🎨 UI/UX Highlights

- **Material 3 Design** with dynamic theming
- **Responsive layouts** for different screen sizes
- **Smooth animations** and transitions
- **Dark/Light theme** support
- **Accessibility** considerations

## 📊 Performance & Quality

- **ProGuard optimization** for release builds
- **Crashlytics integration** for error tracking
- **Performance monitoring** with Firebase
- **Memory-efficient image loading** with Coil
- **Network state awareness**

## 🤝 Contributing

This project was developed as part of the Mobile Application Development course at Politecnico di Torino. 

## 📄 License

This project is part of an academic course at PoliTo and is intended for educational purposes.

---

*Built with ❤️ using Kotlin and Jetpack Compose*
