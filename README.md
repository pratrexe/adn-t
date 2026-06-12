<<<<<<< HEAD
# Adnt AdBlocker

Adnt is a high-performance ad-blocking and privacy protection application for Android. It combines network-level DNS filtering with an intelligent accessibility-based ad-skipper to provide a comprehensive ad-free experience across the entire operating system.

## Key Features

### Universal Ad-Skip Service
- Automatic skipping: Detects and clicks "Skip Ad", "Close", and "X" buttons across all applications, including the official YouTube app.
- Instant response: Uses Android Accessibility Services to react to ad-related UI changes in real-time.
- Smart detection: Pattern matching for common ad-skipping button IDs and text labels across multiple languages.

### System-Wide DNS Filtering
- Local VPN integration: Intercepts DNS queries on UDP port 53 using a local VpnService.
- Domain blocking: Prevents ad-delivery and tracking domains from resolving at the network level.
- Custom DNS providers: Support for Google DNS, Cloudflare DNS, and AdGuard DNS.

### Modern User Interface
- Material You: Dynamic coloring that adapts to the system wallpaper.
- Material 3 Dashboard: A clean, animated interface showing real-time statistics for blocked ads and trackers.
- Detailed logs: View recently blocked domains directly from the home screen.

## Technical Architecture

### Core Components
- AdSkipperService: An AccessibilityService implementation that monitors window state changes to identify interactable ad elements.
- AdntVpnService: A VpnService implementation that establishes a TUN interface for DNS interception.
- DnsPacketParser: A utility for parsing raw UDP packets to extract domain names for filtering.
- SettingsManager: Manages user preferences and persistence using SharedPreferences.

### Build Configuration
- Minimum SDK: 26 (Android 8.0)
- Target SDK: 36
- Language: Kotlin
- UI Framework: Jetpack Compose

## Installation and Setup

1. Enable Protection: Toggle the main shield on the dashboard to start the local VPN service.
2. Enable Universal Auto-Skip: Grant the Accessibility permission via the dashboard button to allow the app to skip ads automatically.
3. Configure DNS: Select your preferred DNS provider in the settings menu.

## Disclaimer

This application is intended for personal use and privacy protection. Users should be aware that some applications may have Terms of Service that prohibit the use of ad-blocking software.
=======
# adn-t
system-wide ad-blocking and privacy tool for Android that provides a premium, ad-free experience without requiring root access.
>>>>>>> f5b93dd3c6143514e66752577443178e8218d121
