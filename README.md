<div align="center">
<img width="1200" height="475" alt="GHBanner" src="https://ai.google.dev/static/site-assets/images/share-ais-513315318.png" />
</div>

# রক্তবন্ধু - Roktobondhu Blood Donation App

This is an Android application built with Kotlin and Jetpack Compose for blood donation management using Google Gemini AI and Firebase.

View your app in AI Studio: https://ai.studio/apps/99b52394-2d1a-4a5c-a408-14cad4a06394

## Prerequisites

- [Android Studio](https://developer.android.com/studio) (Latest version recommended)
- Java 11 or higher
- Gradle 8.0+
- Firebase account with a project
- Gemini API key from [Google AI Studio](https://ai.google.dev/)

## Setup Instructions

### 1. Clone and Open the Project

```bash
git clone <repository-url>
cd blood
```

- Open Android Studio
- Select **File** → **Open** and choose this project directory
- Allow Android Studio to sync and fix any incompatibilities

### 2. Configure Environment Variables

Create a `.env` file in the project root directory (copy from `.env.example`):

```bash
cp .env.example .env
```

Edit `.env` and add your credentials:

```env
# Gemini AI API
GEMINI_API_KEY=your_gemini_api_key_here

# Firebase Configuration
FIREBASE_API_KEY=your_firebase_api_key
FIREBASE_AUTH_DOMAIN=your-project.firebaseapp.com
FIREBASE_PROJECT_ID=your-project-id
FIREBASE_STORAGE_BUCKET=your-project.appspot.com
FIREBASE_MESSAGING_SENDER_ID=your_sender_id
FIREBASE_APP_ID=your_app_id

# Debug Keystore (Optional)
DEBUG_STORE_PASSWORD=android
DEBUG_KEY_ALIAS=androiddebugkey
DEBUG_KEY_PASSWORD=android
```

### 3. Set Up Firebase

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project or select existing one
3. Add an Android app with package name: `com.rahadhossain.roktobondhu`
4. Download `google-services.json` and place it in `app/` directory
5. Enable Firestore Database and Authentication (Email/Password and Google Sign-In)

### 4. Run the Application

#### On Emulator:
1. Open **AVD Manager** in Android Studio
2. Create or select an emulator (API 24 or higher)
3. Click the green **Run** button or press `Shift + F10`

#### On Physical Device:
1. Enable Developer Mode and USB Debugging on your device
2. Connect device via USB
3. Click the green **Run** button
4. Select your device from the list

### 5. For Production Builds

To create a release build:

1. Generate a signing key:
```bash
keytool -genkey -v -keystore my-upload-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias upload
```

2. Set environment variables:
```bash
export KEYSTORE_PATH=/path/to/my-upload-key.jks
export STORE_PASSWORD=your_password
export KEY_ALIAS=upload
export KEY_PASSWORD=your_password
```

3. Build release APK:
```bash
./gradlew clean assembleRelease
```

Or build release AAB (for Google Play):
```bash
./gradlew clean bundleRelease
```

## Project Structure

```
blood/
├── app/                           # Main application module
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/             # Kotlin source code
│   │   │   └── res/              # Resources (layouts, strings, etc)
│   │   ├── test/                 # Unit tests
│   │   └── androidTest/          # Instrumentation tests
│   └── build.gradle.kts          # App module dependencies
├── gradle/                        # Gradle wrapper
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Project settings
├── gradle.properties             # Gradle properties
├── .env.example                  # Environment variables template
└── README.md                      # This file
```

## Key Dependencies

- **Jetpack Compose** - Modern UI toolkit
- **Firebase Authentication** - User authentication
- **Firebase Firestore** - Cloud database
- **Retrofit + Moshi** - HTTP client and JSON parsing
- **Google Credentials** - Google Sign-In
- **Gemini AI** - AI-powered features
- **Room Database** - Local data persistence
- **Coroutines** - Asynchronous programming

## Security Best Practices

✅ **Implemented:**
- Environment variables for sensitive credentials
- Separate debug and release signing configurations
- Minification enabled for release builds
- Debug mode disabled in production

⚠️ **Important:**
- Never commit `.env` file to version control
- Never hardcode API keys or passwords
- Always use environment variables for secrets
- Keep `google-services.json` secure
- Rotate signing keys regularly

## Troubleshooting

### Build Errors

**Problem:** "Could not connect to Kotlin compile daemon"
- **Solution:** Already configured in `gradle.properties` with `kotlin.compiler.execution.strategy=in-process`

**Problem:** Firebase dependency resolution issues
- **Solution:** Ensure you're using the correct `google-services.json` file for your Firebase project

### Runtime Issues

**Problem:** API key not found
- **Solution:** Verify `.env` file exists in project root with all required variables

**Problem:** Firebase authentication fails
- **Solution:** Check Firebase console for proper authentication methods and app configuration

## Development Tips

1. Use the Secrets panel in AI Studio to manage sensitive credentials
2. Test on both emulator and real device
3. Monitor Firestore usage in Firebase console
4. Use Firebase Emulator Suite for local development
5. Check ProGuard rules before release builds

## Contributing

1. Create a feature branch: `git checkout -b feature/your-feature`
2. Make your changes
3. Test thoroughly
4. Commit with clear messages: `git commit -m "feat: description"`
5. Push and create a Pull Request

## License

This project is open source and available under the MIT License.

## Support

For issues and questions:
- Check the troubleshooting section above
- Review Firebase documentation
- Check Gemini API documentation
- Create an issue on GitHub

---

**Last Updated:** June 2026
