# Implementation Phases
# Mess Management System — Android App

---

## Phase 0: Project Setup & Environment Configuration

**Goal**: Bootstrap the Android project with all tooling, dependencies, Firebase config, and project structure.

- [ ] Create new Android project (Empty Compose Activity template)
  - Package: `com.messmanager.app`, Min SDK: API 24, Target SDK: API 35
- [ ] Configure `gradle/libs.versions.toml` version catalog
- [ ] Add all dependencies to `app/build.gradle.kts`:
  - Jetpack Compose BOM, Material 3, Navigation Compose
  - Room (runtime, ktx, compiler via KSP)
  - Hilt (android, compiler, navigation-compose)
  - **Firebase BOM, Firebase Auth, Firebase Firestore**
  - Lifecycle ViewModel Compose, Coroutines, Core KTX
  - Java 8+ desugaring
- [ ] Create Firebase project in Firebase Console
  - Enable Anonymous Authentication
  - Create Firestore database
  - Download `google-services.json` → place in `app/`
  - Add Firebase Gradle plugins
- [ ] Create base directory structure (see Architecture.md)
- [ ] Create `MessApplication.kt` with `@HiltAndroidApp`
- [ ] Set up Compose theme files from `design.md`
- [ ] Verify project builds and runs

---

## Phase 1: Authentication, Mess Creation & Join System

**Goal**: Build the onboarding flow — Welcome screen, Create Mess, Join Mess, Firebase Auth.

### 1A: Firebase Auth
- [ ] Create `AuthRepository.kt` — anonymous sign-in, get current user, sign out
- [ ] Create `AuthViewModel.kt` — manage auth state, auto sign-in on launch
- [ ] Implement anonymous auth on first app launch (no email/password needed)

### 1B: Mess Data Layer
- [ ] Create Firestore document models: `MessDocument.kt`, `UserDocument.kt`
- [ ] Create `MessRepository.kt`:
  - `createMess(name)` → generates invite code, creates Firestore doc, sets user as manager
  - `joinMess(inviteCode)` → validates code, adds user to memberIds, adds mess to user doc
  - `getUserMesses()` → list all messes for current user
  - `getMessByCode(code)` → query Firestore for mess with matching inviteCode
  - `leaveMess(messId)` → remove user from memberIds (member only)
- [ ] Create `InviteCodeGenerator.kt` — 6-char alphanumeric, uniqueness check

### 1C: Welcome & Onboarding Screens
- [ ] Create `WelcomeScreen.kt` — hero art + "Create Mess" and "Join Mess" buttons
- [ ] Create `CreateMessScreen.kt` — mess name input → creates mess → navigates to Dashboard
- [ ] Create `JoinMessScreen.kt` — invite code input → validates → shows mess name → join → Dashboard
- [ ] Create `AuthViewModel.kt` — check if user has any messes, route accordingly

### 1D: Navigation Setup
- [ ] Define `Screen` sealed class with all routes (Welcome, CreateMess, JoinMess, Main, Settings)
- [ ] Create `NavGraph.kt` — conditional start: Welcome if no messes, Main if has messes
- [ ] Create `BottomNavBar.kt` — Dashboard, Grocery, Utility, Meals, Contributions
- [ ] Add Settings icon to top app bar
- [ ] Wire into `MainActivity.kt`

### 1E: Verification
- [ ] Test: Create mess → get invite code → share code → join on different concept
- [ ] Test: User with no messes sees Welcome screen
- [ ] Test: User with messes goes directly to Dashboard

---

## Phase 2: Core Data Layer & Firestore CRUD

**Goal**: Build all Firestore data operations and domain models.

### 2A: Firestore Data Sources
- [ ] Create `GroceryDocument.kt`, `UtilityDocument.kt`, `MealDocument.kt`, `ContributionDocument.kt`
- [ ] Create `FirestoreDataSource.kt` — centralized Firestore reference helper

### 2B: Repositories
- [ ] Create `GroceryRepository.kt` — CRUD on `messes/{messId}/grocery_entries`
- [ ] Create `UtilityRepository.kt` — CRUD on `messes/{messId}/utility_entries`
- [ ] Create `MealRepository.kt` — CRUD on `messes/{messId}/meal_entries`
- [ ] Create `ContributionRepository.kt` — CRUD on `messes/{messId}/contribution_entries`
- [ ] Create `DashboardRepository.kt` — aggregate reads from all subcollections
- [ ] All repos use **real-time Firestore snapshot listeners** for live updates

### 2C: Domain Models & Calculator
- [ ] Create domain models: `User`, `Mess`, `MessRole`, `Grocery`, `Utility`, `Meal`, `Contribution`, `Member`
- [ ] Create `Settlement.kt`, `MealRate.kt`
- [ ] Create `SettlementCalculator.kt` — pure function, same logic as before

### 2D: Role-Based Access Helper
- [ ] Create `MessRole.kt` enum: `MANAGER`, `MEMBER`
- [ ] Create helper: `isManager(messId, userId): Boolean`
- [ ] All ViewModels expose `isManager` state to hide/show write UI elements

### 2E: Utilities
- [ ] Create `CurrencyFormatter.kt`, `DateUtils.kt`, `Constants.kt`

### 2F: Verification
- [ ] Write unit tests for `SettlementCalculator`
- [ ] Test Firestore CRUD manually — create, read, update, delete entries
- [ ] Confirm real-time sync: manager writes → member sees update

---

## Phase 3: All Screens & ViewModels

**Goal**: Build all 6 main screens with full functionality and role-based UI.

### 3A: Dashboard Screen
- [ ] Create `DashboardViewModel.kt` — aggregate data, calculate settlements
- [ ] Create `DashboardScreen.kt`:
  - Expense overview cards (grocery, utility, combined, contributions, surplus/deficit)
  - Meal rate display
  - Member-wise cost breakdown table
  - Settlement status per member
- [ ] Both Manager and Member see the same dashboard (read-only for both)

### 3B: Grocery Screen
- [ ] Create `GroceryViewModel.kt` — CRUD (manager), read (member)
- [ ] Create `GroceryScreen.kt` — list + total; FAB visible only for manager
- [ ] Create `GroceryFormDialog.kt` — add/edit form (manager only)

### 3C: Utility Screen
- [ ] Create `UtilityViewModel.kt` — CRUD (manager), read (member)
- [ ] Create `UtilityScreen.kt` — categorized list + total; FAB for manager only
- [ ] Create `UtilityFormDialog.kt` — add/edit form (manager only)

### 3D: Meal Tracker Screen
- [ ] Create `MealViewModel.kt` — edit meals (manager), view (member)
- [ ] Create `MealTrackerScreen.kt` — grid; tap-to-edit for manager, view-only for member
- [ ] Create `MealCalendarGrid.kt` — calendar component

### 3E: Contribution Screen
- [ ] Create `ContributionViewModel.kt` — CRUD (manager), read (member)
- [ ] Create `ContributionScreen.kt` — list + totals; FAB for manager only
- [ ] Create `ContributionFormDialog.kt` — add/edit (manager only)

### 3F: Settings Screen
- [ ] Create `SettingsViewModel.kt`:
  - List all user's messes (created + joined)
  - Switch active mess
  - Create new mess / Join new mess
  - For manager: show invite code with share/copy action
  - For manager: manage members (view list, remove)
  - For member: leave mess option
- [ ] Create `SettingsScreen.kt` — full settings UI

### 3G: Verification
- [ ] End-to-end test: Manager creates mess → adds grocery → member sees it
- [ ] Test role enforcement: member cannot see FABs or edit actions
- [ ] Test multi-mess switching

---

## Phase 4: UI Polish & UX Refinements

**Goal**: Elevate from functional to polished.

### 4A: Reusable Components
- [ ] `SummaryCard.kt`, `EntryListItem.kt`, `EmptyStateView.kt`, `MonthYearPicker.kt`

### 4B: Visual Polish
- [ ] Apply Material 3 color scheme from `design.md`
- [ ] Typography, card elevation, rounded corners, consistent spacing
- [ ] Screen transition animations, list animations
- [ ] Haptic feedback on meal tap

### 4C: UX Improvements
- [ ] Confirmation dialogs for delete and leave actions
- [ ] Month navigation arrows
- [ ] Loading states and skeleton screens
- [ ] Error states with retry

### 4D: Edge Cases
- [ ] Division by zero (no meals, no members)
- [ ] Offline handling — show cached data, queue writes
- [ ] Empty mess (no data yet) — friendly empty states

---

## Phase 5: Hardening, Security & Deployment

**Goal**: Production-ready release.

### 5A: Firestore Security
- [ ] Deploy Firestore security rules (from Architecture.md)
- [ ] Test: member cannot write to subcollections
- [ ] Test: non-member cannot read mess data

### 5B: Performance
- [ ] Pagination for large lists (Firestore cursors)
- [ ] Minimize Firestore reads (use snapshot listeners, not repeated fetches)
- [ ] Profile on low-end device

### 5C: App Polish
- [ ] App icon, splash screen
- [ ] About screen with version info
- [ ] Proper error messages (network errors, invalid codes, etc.)

### 5D: Testing
- [ ] Unit tests: calculator, repositories
- [ ] Manual test all flows on physical device
- [ ] Test month boundaries, large datasets

### 5E: Release
- [ ] R8/ProGuard, signing config
- [ ] Generate signed APK/AAB
- [ ] Play Store listing
- [ ] Submit

---
