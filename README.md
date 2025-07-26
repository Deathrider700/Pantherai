# Panther AI - Android App

A modern, production-ready Android app that provides access to multiple AI models through a beautiful, intuitive interface. Built with Jetpack Compose, Material 3, and integrated with a4f.co APIs and Supabase backend.

## 🚀 Features

### Core Features
- **Multi-Model AI Support**: Chat, Image Generation, Video Generation, Text-to-Speech, Audio Transcription
- **Modern UI/UX**: Material 3 design with glassmorphism, gradients, and smooth animations
- **User Authentication**: Secure sign-up/login with Supabase Auth
- **History Management**: Save and view all AI interactions
- **Model Selector**: Easy switching between different AI models
- **Side Navigation**: Drawer menu with user profile, history, and settings
- **Real-time Chat**: Interactive chat interface with AI models
- **File Upload**: Support for audio transcription
- **Dark/Light Mode**: Automatic theme switching

### AI Models Supported
- **Chat Models**: ChatGPT-4o, GPT-4, Gemini 2.5 Pro, Mistral Large
- **Image Generation**: Flux 1 Schnell, Imagen 3/4, Sana 1.5 Flash
- **Video Generation**: Wan 2.1
- **Text-to-Speech**: TTS 1, Sonic 2, Sonic
- **Audio Transcription**: Whisper 1, Distil Whisper

## 🛠️ Tech Stack

### Frontend
- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material 3** - Design system
- **Navigation Compose** - Navigation
- **Hilt** - Dependency injection
- **ViewModel + StateFlow** - State management
- **Coil** - Image loading
- **ExoPlayer** - Video/audio playback

### Backend
- **Supabase** - Authentication, database, storage
- **PostgreSQL** - Database (via Supabase)
- **Row Level Security (RLS)** - Data privacy

### APIs
- **a4f.co** - AI model APIs
- **Retrofit** - HTTP client
- **OkHttp** - Network layer

## 📱 Screenshots

[Add screenshots here once the app is built]

## 🏗️ Project Structure

```
PantherAI/
├── app/
│   ├── src/main/
│   │   ├── java/com/pantherai/app/
│   │   │   ├── data/
│   │   │   │   ├── model/          # Data models
│   │   │   │   ├── remote/         # API services
│   │   │   │   └── repository/     # Repository layer
│   │   │   ├── di/                 # Dependency injection
│   │   │   ├── ui/
│   │   │   │   ├── components/     # Reusable UI components
│   │   │   │   ├── navigation/     # Navigation setup
│   │   │   │   ├── screens/        # App screens
│   │   │   │   │   ├── auth/       # Authentication screens
│   │   │   │   │   ├── chat/       # Chat interface
│   │   │   │   │   ├── home/       # Home screen
│   │   │   │   │   └── ...         # Other screens
│   │   │   │   └── theme/          # App theming
│   │   │   └── viewmodel/          # ViewModels
│   │   └── res/                    # Resources
│   └── build.gradle.kts
├── supabase/                       # Backend setup
│   ├── schema.sql                  # Database schema
│   └── README.md                   # Supabase setup guide
└── README.md                       # This file
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 24+
- Kotlin 1.9.20+
- Supabase account
- a4f.co API key

### 1. Clone the Repository
```bash
git clone https://github.com/Deathrider700/Pantherai.git
cd PantherAI
```

### 2. Set Up Supabase Backend
1. Go to [https://app.supabase.com](https://app.supabase.com)
2. Create a new project
3. Copy your Supabase URL and anon key
4. Run the SQL from `supabase/schema.sql` in the Supabase SQL Editor
5. See `supabase/README.md` for detailed setup instructions

### 3. Configure the App
1. Open `app/src/main/java/com/pantherai/app/data/remote/SupabaseService.kt`
2. Replace `YOUR_SUPABASE_URL` and `YOUR_SUPABASE_ANON_KEY` with your actual values
3. The a4f.co API key is already configured in the repository

### 4. Build and Run
```bash
# In Android Studio
1. Open the project
2. Sync Gradle files
3. Build the project
4. Run on device/emulator
```

## 🔧 Configuration

### Environment Variables
The app uses the following configuration:
- `SUPABASE_URL`: Your Supabase project URL
- `SUPABASE_ANON_KEY`: Your Supabase anon/public key
- `A4F_API_KEY`: a4f.co API key (pre-configured)

### API Keys
- **a4f.co API Key**: `ddc-a4f-4c0658a7764c432c9aa8e4a6d409afb3` (already configured)
- **Supabase**: You need to add your own keys

## 📱 Usage

### Authentication
1. Launch the app
2. Sign up with email/password or log in
3. Your account will be created in Supabase

### Using AI Models
1. **Home Screen**: Browse available AI models by category
2. **Chat**: Select a chat model and start conversing
3. **Image Generation**: Enter a prompt and generate images
4. **Video Generation**: Create videos from text descriptions
5. **Text-to-Speech**: Convert text to natural speech
6. **Audio Transcription**: Upload audio files for transcription

### Navigation
- **Side Menu**: Access history, settings, and model selector
- **History**: View all your AI interactions
- **Settings**: Configure app preferences

## 🎨 Design Features

### Modern UI Elements
- **Glassmorphism**: Translucent cards and overlays
- **Gradients**: Beautiful color transitions
- **Animations**: Smooth transitions and micro-interactions
- **Material 3**: Latest Material Design principles
- **Responsive**: Works on phones and tablets

### Color Scheme
- **Primary**: Deep Blue (#1E3A8A)
- **Secondary**: Purple (#7C3AED)
- **Tertiary**: Emerald (#059669)
- **Error**: Red (#DC2626)
- **Surface**: Light/Dark adaptive

## 🔒 Security

### Data Protection
- **Row Level Security (RLS)**: Users can only access their own data
- **Encrypted Storage**: Sensitive data is encrypted
- **Secure Authentication**: Supabase Auth with email/password
- **API Key Security**: Keys are stored securely

### Privacy
- **User Data**: Stored in Supabase with RLS
- **History**: Private to each user
- **No Data Mining**: Your conversations stay private

## 🚀 Production Deployment

### Android App Store
1. Generate signed APK/AAB
2. Test thoroughly on multiple devices
3. Submit to Google Play Store
4. Configure app signing

### Backend Scaling
- **Supabase**: Automatically scales with usage
- **Database**: PostgreSQL with automatic backups
- **Storage**: Supabase Storage for file uploads
- **CDN**: Global content delivery

## 🐛 Troubleshooting

### Common Issues

#### Build Errors
```bash
# Clean and rebuild
./gradlew clean
./gradlew build
```

#### Supabase Connection
- Verify your Supabase URL and key
- Check network connectivity
- Ensure RLS policies are set up correctly

#### API Errors
- Verify a4f.co API key is valid
- Check internet connection
- Review API rate limits

### Debug Mode
- Enable debug logging in `NetworkModule.kt`
- Check Logcat for detailed error messages
- Use Android Studio's debugger

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🙏 Acknowledgments

- **a4f.co** for providing AI model APIs
- **Supabase** for backend infrastructure
- **Google** for Material Design and Android tools
- **JetBrains** for Kotlin and Android Studio

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Check the documentation
- Review the troubleshooting section

---

**Built with ❤️ using modern Android development practices** 