# WatchFace Studio ⌚

A complete, production-grade Android application for creating custom smartwatch watch faces and synchronizing them via Bluetooth Low Energy (BLE).

Built with **Jetpack Compose**, **Material 3**, **SQLite OpenHelper & Kotlin Flows**, and **Bluetooth Low Energy (BLE)**.

---

## 📱 Features & Implemented Screens

1. **Splash Screen**: Animated watch orbital glow, branded typography, and loading indicator.
2. **Onboarding / Setup**: 4-step first-launch guide explaining Android Photo Picker and Bluetooth connection.
3. **Home Dashboard**:
   - Active connected smartwatch card with live status & battery indicator.
   - Large gradient "+ Create Watch Face" CTA.
   - 2x2 Quick Actions grid (From Gallery, Choose Template, Recent Designs, My Watch Faces).
   - Horizontal carousel of Recent Designs.
   - Smartwatch Compatibility guide cards (Noise, boAt, Fire-Boltt, Samsung, Amazfit).
4. **Create Watch Face**:
   - Android Photo Picker integration (zero invasive storage permissions).
   - "AI Generate" concept prompt.
   - Categorized templates (All, Digital, Analog, Minimal, Nature, Luxury, Sports, AMOLED).
5. **Photo Crop & Transform**:
   - 3x3 rule-of-thirds alignment grid.
   - Aspect ratio presets: Fit, 1:1, 4:5, 3:4, 9:16.
   - 90° rotation, horizontal flip, and reset controls.
6. **Design Editor**:
   - Live interactive WatchFace Canvas matching round, square, and rectangular watch dimensions.
   - Real-time compositing with undo/redo stack (25 states).
   - Quick bottom toolbar toggling between editing panels.
7. **Time Settings**:
   - 3x3 watch alignment grid (Top-Left, Center, Bottom-Right, etc.).
   - Clock size slider (XS to XL).
   - Typography font selector (Default, Serif, Monospace, Sans-Serif).
   - Color picker palette and opacity control.
8. **Layer Management**:
   - Multi-layer stack (Background, Foreground cutout, Digital Time, Date, Widgets).
   - Layer reordering (Bring Forward, Send Backward).
   - Layer visibility toggle and lock toggle.
9. **Complications & Widgets**:
   - 2-column complication cards for Date, Steps, Heart Rate, Battery, Day, Weather, Calories, Distance.
   - Visual toggle and real-time positioning on the watch canvas.
10. **Bluetooth Low Energy (BLE) Device Discovery**:
    - Animated pulsing radar scanner.
    - Automatic device detection matching against Noise, boAt, Fire-Boltt, Samsung Galaxy Watch, Amazfit.
    - Connection state handling (Scanning, Connecting, Connected, Disconnected).
11. **Apply & Sync Screen**:
    - High-fidelity watch mockup previewing the exported face.
    - Sync progress animation with success badge.
    - Direct export and companion app handoff options.

---

## 📦 APK Download & Build

The prebuilt, installable debug APK is located at:
- **`WatchFaceStudio-debug.apk`** (Root directory)
- **`app/build/outputs/apk/debug/app-debug.apk`**

### Building from Source
```bash
./gradlew assembleDebug --no-daemon
```

---

## 🛠️ Tech Stack
- **OS Target**: Android 14+ (compileSdk 36, targetSdk 36, minSdk 26)
- **Language**: Kotlin 2.3.20
- **UI Framework**: Jetpack Compose BOM 2025.02.00, Material 3
- **Image Loading**: Coil 3 (Compose)
- **Persistence**: Android SQLiteOpenHelper with reactive Kotlin `Flow` triggers & Jetpack DataStore Preferences
- **Hardware Integration**: Android BluetoothLeScanner, BluetoothGatt, BroadcastReceiver for adapter state
