# 🏬 Mess Manager (মেস ম্যানেজার)

[![Android CI/CD](https://github.com/JeetSimanto/mess_management_system/actions/workflows/build.yml/badge.svg)](https://github.com/JeetSimanto/mess_management_system/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/JeetSimanto/mess_management_system?color=00E5A0&label=Version)](https://github.com/JeetSimanto/mess_management_system/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, high-performance, dark-themed Android app for effortless shared bachelor mess management in Bangladesh. Track daily groceries, utility bills, individual meal counts, cash deposits, physical item borrowings, multi-mess switching, and real-time financial settlements.

---

## 📥 Download the App

Click the button below to download the latest compiled Android APK directly:

[<img src="https://img.shields.io/badge/Download_Latest_APK-v1.4.1-00E5A0?style=for-the-badge&logo=android&logoColor=black" height="50">](https://github.com/JeetSimanto/mess_management_system/releases/latest)

> 💡 **Auto-Update Enabled**: Once installed, the app automatically checks for new GitHub releases on launch and includes a **Quick Check Update** option in Settings so you never miss an update!

---

## ✨ Key Features & Improvements (v1.4.1)

### 🏢 Multi-Mess Management & Instant Switching
- **Create, Join, and Switch**: Users can easily create new messes, join existing ones via 6-character invite codes, and switch between active messes directly from **Settings**.

### 📊 Role-Tailored Dashboards & Settlement Dialogs
- **Member View**:
  - 2 Circular Stat Widgets displaying **My Meals** and **My Contribution**.
  - Real-time **Manager's Fund Balance (Money Remains)** card showing total cash reserve.
  - Recent 5 grocery purchases list.
- **Manager View**:
  - Hero Total Expense Card (Grocery vs. Utility breakdown).
  - 4 Key Metric Chips: **Meal Rate (৳/meal)**, **Active Members**, **Total Meals**, **Fund Balance**.
  - **Settlement Breakdown Dialog**: Granular member financial balance list (`GET` / `PAY` / `SETTLED`) with clean, absolute currency formatting.

### 🍱 Interactive Tabular Meal Grid (`MealCalendarGrid`)
- **Fractional Precision**: Clean display of meal counts as fractions (`½`, `1`, `1½`, `2`, `2½`, `3`) instead of raw decimals.
- **Transposed Grid & Sticky Headers**: Sticky top header for member columns with synchronized horizontal and vertical scrolling across dates.
- **Tactile Feedback & Color Density**: Spring press micro-animations with dense green color coding based on consumption levels.

### 🛒 Grocery & Shared Utility Tracking
- Track item name, quantity, unit, cost in BDT (৳), buyer, date, and custom notes.
- Utility categories: House Rent, Electricity, Gas, Water, Waste, Transport, WiFi, and Custom.
- Equal utility split calculation among all active members.

### 📦 Zero-Cost Physical Item Borrow System
- Send and manage physical item borrow requests (e.g. Eggs, Oil, Salt).
- **Strict Return Policy**: Due dates set on approval with automated FCM daily reminder notifications.

### 🎨 Carbon Mint Dark Theme & High-Contrast White Typography
- OLED dark backgrounds (`#0A0E14`, `#141B22`) paired with **pure white typography (`#FFFFFF`)** for crisp readability.
- Richer, denser green (`#00E5A0`) and amber (`#FFB547`) accent fields.

---

## 🖼️ Visual Tutorial & How to Use

### 1️⃣ Sign In & Mess Setup
- Sign in securely using **Google Authentication**.
- **Create a New Mess** or **Join an Existing Mess** using a 6-character invite code.
- Switch between active messes anytime from **Settings**.

```
[ Welcome Screen ] ──> [ Google Auth ] ──> [ Create / Join / Switch Mess ] ──> [ Dashboard ]
```

### 2️⃣ Daily Operations Workflow

| Action | Who | How |
| :--- | :--- | :--- |
| **Record Grocery** | Manager | Tap `+` FAB on Grocery Tab -> Enter item, cost (৳), buyer -> Save |
| **Record Utility** | Manager | Tap `+` FAB on Utility Tab -> Select Category (Rent/Wifi/etc.) -> Save |
| **Log Meals** | Manager | Tap cell in Meal Grid -> Cycle counts (0 -> 1 -> 2 -> 3 -> 0.5...) |
| **Record Deposit**| Manager | Tap `+` FAB on Deposits Tab -> Select Member & Amount -> Save |
| **Borrow Item** | Member | Open Top Bar Borrow Icon -> Send Request (Item & Quantity) |
| **Check Updates** | Everyone| Open Settings -> Tap "Quick check update" |

---

## 🛠️ Architecture & Tech Stack

- **Architecture**: Clean Architecture + MVVM + Unidirectional Data Flow (`StateFlow`)
- **Language**: Kotlin 1.9.22 (`compileSdk 35`, `targetSdk 35`)
- **UI Framework**: Jetpack Compose + Material 3 (Carbon Mint Dark Theme)
- **Dependency Injection**: Hilt (Dagger)
- **Backend & Database**: Firebase Auth, Cloud Firestore, Firebase Cloud Messaging (FCM)
- **CI/CD**: GitHub Actions (Automated APK Compilation & Release Tag Publishing)

---

## 💻 Local Building & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/JeetSimanto/mess_management_system.git
   cd mess_management_system
   ```
2. **Open in Android Studio** (Ladybug or newer).
3. **Gradle Sync**: Let Gradle download dependencies listed in `gradle/libs.versions.toml`.
4. **Firebase Setup**: Ensure your `google-services.json` is placed in the `app/` directory.
5. **Run the App**: Connect your Android device or emulator and press **Run (Shift + F10)**.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.