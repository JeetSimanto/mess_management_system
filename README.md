# 🏬 Mess Manager (মেস ম্যানেজার)

[![Android CI/CD](https://github.com/JeetSimanto/mess_management_system/actions/workflows/build.yml/badge.svg)](https://github.com/JeetSimanto/mess_management_system/actions/workflows/build.yml)
[![Latest Release](https://img.shields.io/github/v/release/JeetSimanto/mess_management_system?color=00E5A0&label=Version)](https://github.com/JeetSimanto/mess_management_system/releases/latest)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Backend-Firebase-FFCA28?logo=firebase)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

A modern, high-performance, dark-themed Android app for effortless shared bachelor mess management in Bangladesh. Track daily groceries, utility bills, individual meal counts, cash deposits, physical item borrowings, and real-time financial settlements.

---

## 📥 Download the App

Click the button below to download the latest compiled Android APK directly:

[<img src="https://img.shields.io/badge/Download_Latest_APK-v1.0.0-00E5A0?style=for-the-badge&logo=android&logoColor=black" height="50">](https://github.com/JeetSimanto/mess_management_system/releases/latest)

> 💡 **Auto-Update Enabled**: Once installed, the app automatically checks for new GitHub releases on launch and notifies you whenever a new version is pushed!

---

## ✨ Features at a Glance

### 📊 Role-Tailored Dashboards
- **Member View**: 
  - 2 Circular Stat Widgets displaying **My Meals** and **My Contribution**.
  - Real-time **Manager's Fund Balance (Money Remains)** card showing cash reserve in manager's hands.
  - Recent 5 grocery purchases list at the bottom.
- **Manager View**:
  - Hero Total Expense Card (Grocery vs. Utility breakdown).
  - 4 Key Metric Chips: **Meal Rate (৳/meal)**, **Active Members**, **Total Meals**, **Fund Balance**.
  - Complete Member Settlement Status Table (Get Back / Pay Extra / Settled).

### 🛒 Grocery & Shared Utility Tracking
- Track item name, quantity, unit, cost in BDT (৳), buyer, date, and custom notes.
- Utility bill categories: House Rent, Electricity, Gas, Water, Waste, Transport, WiFi, and Other.
- Automatic equal utility sharing among all active members.

### 🍱 Meal Tracker & Precision Grid
- 0 to 3.0 meals per day with 0.5 step precision (0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0).
- Scrollable calendar grid of dates x members with tap-to-cycle updating for managers.

### 📦 Zero-Cost Physical Item Borrow System
- Members can send borrow requests for physical items from manager (e.g. Eggs, Oil, Salt).
- **Strict Return Policy**: Manager sets due dates on approval. No financial coupling.
- **Daily Reminders**: Automated FCM push notifications remind members daily until items are marked returned.
  > *"The manager will not accept any money, what you have borrowed needs to be returned in the given time period."*

### ⚙️ Mess Management & Invite System
- 6-character uppercase invite code generation.
- Role transfer (Manager to Member), member removal, and active mess exit support.

---

## 🖼️ Visual Tutorial & How to Use

### 1️⃣ Sign In & Mess Setup
- Sign in securely using **Google One-Tap Authentication**.
- **Create a New Mess** by choosing Mess Name, Month, and Year, or **Join an Existing Mess** using a 6-digit invite code.

```
[ Welcome Screen ] ──> [ Google Auth ] ──> [ Create / Join Mess ] ──> [ Dashboard ]
```

### 2️⃣ Daily Operations Workflow

| Action | Who | How |
| :--- | :--- | :--- |
| **Record Grocery** | Manager | Tap `+` FAB on Grocery Tab -> Enter item, cost (৳), buyer -> Save |
| **Record Utility** | Manager | Tap `+` FAB on Utility Tab -> Select Category (Rent/Wifi/etc.) -> Save |
| **Log Meals** | Manager | Tap cell in Meal Grid -> Cycle counts (0 -> 1 -> 2 -> 3 -> 0.5...) |
| **Record Cash Deposit**| Manager | Tap `+` FAB on Contributions Tab -> Select Member & Amount -> Save |
| **Borrow Item** | Member | Open Top Bar Borrow Icon -> Send Request (Item & Quantity) |
| **Approve Borrow** | Manager | Open Borrows -> Tap Accept -> Set Return Due Date |

---

## 🛠️ Architecture & Tech Stack

- **Architecture**: Clean Architecture + MVVM + Unidirectional Data Flow (StateFlow)
- **Language**: Kotlin 1.9.22
- **UI Framework**: Jetpack Compose + Material 3 (Carbon Mint OLED Theme)
- **Dependency Injection**: Hilt (Dagger)
- **Asynchronous**: Kotlin Coroutines & Flow
- **Backend & Database**: Firebase Auth, Cloud Firestore, Firebase Messaging (FCM)
- **Scheduled Tasks**: Firebase Cloud Functions (Node.js) & Pub/Sub Cron
- **CI/CD**: GitHub Actions (Auto APK Compilation & GitHub Release Publish)

---

## 💻 Local Building & Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/JeetSimanto/mess_management_system.git
   cd mess_management_system
   ```
2. **Open in Android Studio** (Hedgehog or newer).
3. **Gradle Sync**: Let Gradle download dependencies listed in `gradle/libs.versions.toml`.
4. **Firebase Configuration**: Ensure `google-services.json` is placed in `app/`.
5. **Run the App**: Connect your Android device or emulator and press **Run (Shift + F10)**.

---

## 📜 License

Distributed under the MIT License. See `LICENSE` for more information.