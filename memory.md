# Memory — Living Session Log & Progress Tracker
# Mess Management System — Android App

---

## Project Status

| Field | Value |
|-------|-------|
| **Current Phase** | Multi-Mess & Production Release |
| **App Version** | `1.2.0` (Version Code 15) |
| **Status** | 🟢 Production Ready — Full App Icon Audit & XML Bitmap Wrapper Architecture (`1786590497726.png`) |
| **Last Updated** | 2026-08-13T23:38:00+06:00 |
| **Started** | 2026-08-02 |

---

## All User Decisions (Confirmed)

| # | Decision | User's Answer |
|---|----------|---------------|
| 1 | Auth method | **Google Sign-In** (one-tap, persistent, production SHA fingerprint configured) |
| 2 | Cloud backend | **Firebase Firestore** (free tier) |
| 3 | Max members per mess | **Up to 20** |
| 4 | Meal count type | **Floating point** (0, 0.5, 1, 1.5, 2, 2.5, 3) |
| 5 | Meal tracker UI | **Both**: Calendar grid + Day-by-day view (switchable) |
| 6 | Member dashboard visibility | **Own data only** — personal settlement, meals, contributions. Can see grocery list, utility list, totals (shared info) |
| 7 | Language | **Bilingual**: English + Bangla with toggle |
| 8 | Grocery units | **Editable dropdown** — presets: kg, poya, pcs, liter, gram + custom input |
| 9 | Access system | **Manager** (creator) = full CRUD admin, **Member** (joiner) = read-only |
| 10 | Onboarding | Welcome screen → Create Mess / Join Mess |
| 11 | Invite system | 6-char code, shared from Settings |
| 12 | Multi-mess | MVP — user can create/join multiple messes |
| 13 | Design Theme | **Carbon Mint Dark Theme** with solid input fields and customized component styling |

---

## Context Tracker

### Key Code Files & Modules

| File / Component | Status | Description |
|------------------|--------|-------------|
| `app/build.gradle.kts` | Updated | Bumped to `v1.0.2` (versionCode 3) |
| `.github/workflows/build.yml` | Verified | GitHub Actions automated APK release on tag push |
| `AppOutlinedTextField.kt` | Created | Centralized solid `DarkSurfaceHigh` styling helper for input forms |
| `BottomNavBar.kt` | Polished | Active navigation pills, styled vector icons |
| `TopBar.kt` | Polished | Mess avatar circle, styled title layout, action icon buttons |
| `ManagerDashboardView.kt` | Polished | Hero cards, member initials avatars, settlement status chips |
| `MealCalendarGrid.kt` | Polished | Scrollable date headers, initials avatars, count cell glow states |
| `GroceryScreen.kt` | Polished | Buyer initial avatars, cost badges, category icons |
| `UtilityScreen.kt` | Polished | Rent, Electricity, Gas, Water, WiFi category icons |
| `ContributionScreen.kt` | Polished | Deposit list, member initial avatars, purpose badges |
| `BorrowScreen.kt` | Polished | Physical Return Policy notice card, borrow status tags |
| `SettingsScreen.kt` | Polished | Mess header, invite code box, member admin controls, sign out |
| `WelcomeScreen.kt` | Polished | Hero branding glow, feature chips, Google Sign-In button |

---

## Architectural Decision Record (ADR)

### ADR-001: Kotlin + Jetpack Compose ✅ Confirmed
### ADR-002: Firebase Firestore as primary database ✅ Confirmed
### ADR-003: Google Sign-In (SHA-1 & SHA-256 Registered) ✅ Confirmed
### ADR-004: Store currency as integer paisa ✅ Confirmed
### ADR-005: MVVM with Hilt DI ✅ Confirmed
### ADR-006: StateFlow over LiveData ✅ Confirmed
### ADR-007: Role-Based Access — Manager (admin) vs Member (read-only) ✅ Confirmed
### ADR-008: Invite Code System ✅ Confirmed
### ADR-009: Multi-Mess Support in MVP ✅ Confirmed
### ADR-010: Floating Point Meal Counts ✅ Confirmed
### ADR-011: Dual Meal Tracker View ✅ Confirmed
### ADR-012: Member Sees Only Own Data on Dashboard ✅ Confirmed
### ADR-013: Carbon Mint Design System ✅ Confirmed
- Solid container backgrounds (`DarkSurfaceHigh`) to prevent transparent text fields
- Initials-based avatar circles across member lists and settlement tables
### ADR-014: UI-UX Pro Max Skill Installed ✅ Confirmed
- Integrated `/home/lucifer_vtn/.agent/skills/ui-ux-pro-max/` skill database and search tool

---

## Session Log

| Timestamp | Action |
|-----------|--------|
| 2026-08-02 00:47 | Session started — user requested 7 planning files |
| 2026-08-02 01:52 | Planning complete — began implementation |
| 2026-08-09 17:15 | Fixed Google Sign-In auth credentials and SHA fingerprints |
| 2026-08-10 18:15 | Resolved input field transparency bug across form sheets |
| 2026-08-10 18:30 | Bumped app version to `1.0.2` (versionCode 3) |
| 2026-08-13 02:29 | Completed full Carbon Mint UI polish across all app screens |
| 2026-08-13 02:30 | Verified `ui-ux-pro-max` skill integration |
| 2026-08-13 08:31 | **Updated `memory.md` with current release state** |

---
