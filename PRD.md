# Product Requirements Document (PRD)
# Mess Management System — Android App

---

## 1. Executive Summary

### Problem Statement
Members of shared housing setups (bachelor messes, hostels, communal living groups) in Bangladesh and similar regions face recurring friction around:
- **Untracked grocery spending** — no single source of truth for daily food purchases.
- **Unfair meal cost allocation** — members who eat fewer meals subsidize those who eat more.
- **Opaque utility splitting** — rent, water, and household bills are divided informally, causing disputes.
- **Manual end-of-month reconciliation** — error-prone arithmetic to figure out who owes whom.
- **No centralized access** — members have no way to see live updates of expenses and their settlement status.

### Product Vision
A **native Android app** that serves as the single financial operating system for any shared mess. The **Mess Manager** (creator) handles all data entry — groceries, utilities, meals, contributions — while **Members** get real-time read-only access by joining via a shared invite code. It automates rate calculation and final settlement, eliminating manual bookkeeping and disputes entirely.

### Success Metrics (KPIs)
| # | KPI | Target |
|---|-----|--------|
| 1 | Monthly Active Mess Groups | 500+ within 6 months of launch |
| 2 | End-of-Month Settlement Accuracy | 100% (zero manual math errors) |
| 3 | Average Time to Complete Monthly Reconciliation | < 2 minutes (vs. 30+ min manual) |
| 4 | User Retention (Month-over-Month) | ≥ 70% |

---

## 2. Target Personas

### Primary Persona: Mess Manager (Admin)
- **Who**: The person who creates the mess in the app. Has **full write access**.
- **Pain Points**: Manually recording every grocery purchase in notebooks, calculating meal rates by hand, chasing members for payments, disputes about who ate how many meals.
- **Goals**: One-tap grocery entry, automatic meal rate calculation, instant settlement report at month end. Share a code so members can join and see updates.
- **Permissions**: Add/edit/delete grocery, utility, meals, contributions. Add/remove members. Access settings. Share invite code.

### Secondary Persona: Mess Member (Read-Only)
- **Who**: Any person who joins a mess via an invite code. Has **read-only access**.
- **Pain Points**: Lack of transparency — doesn't know how money is being spent, unclear how much they owe or are owed. Has to ask the manager for updates.
- **Goals**: View all expenses, see personal meal count, check contribution history, know exact settlement amount — all in real-time without asking the manager.
- **Permissions**: View dashboard, grocery list, utility list, meal tracker, contribution log. **Cannot** add, edit, or delete any data.

---

## 3. Access System & Onboarding Flow

### 3.1 First Launch — Welcome Screen
When a user installs and opens the app for the first time, they see a **Welcome Screen** with two actions:

| Action | Description |
|--------|-------------|
| **🏠 Create Mess** | Creates a new mess group. The user becomes the **Mess Manager** (admin). |
| **🔗 Join Mess** | Enter an invite code to join an existing mess as a **read-only Member**. |

### 3.2 Create Mess Flow
1. User taps **"Create Mess"**
2. Enters mess name (e.g., "Mirpur Mess", "Hall 5 Room 302")
3. Selects **which month & year** this mess is for (e.g., August 2026)
4. System creates the mess and generates a **unique 6-character invite code** (e.g., `X7K9M2`)
5. User is assigned as **Mess Manager** (admin role) for that month
6. User lands on the **Dashboard** of the newly created mess
7. Manager can share the invite code from **Settings → Share Code**

> **Note**: One mess = one month. For the next month, create a new mess. The same group can join via a new code, and a different member can be the manager.

### 3.3 Join Mess Flow
1. User taps **"Join Mess"**
2. Enters the **6-character invite code** shared by the manager
3. System validates the code and fetches the mess details
4. User enters their **display name** (so the manager can identify them)
5. User joins the mess as a **read-only Member**
6. User lands on the **Dashboard** (read-only view — no FABs, no edit buttons)

### 3.4 Multi-Mess Support
- A single user can **create multiple messes** (they are manager of each)
- A single user can **join multiple messes** (they are member of each)
- A single user can be **manager of some messes AND member of others**
- **Settings screen** provides:
  - List of all messes (created + joined)
  - Option to **Create New Mess** or **Join Another Mess**
  - Ability to **switch between messes** (active mess selection)
  - For created messes: **Share Invite Code**, **Mess Settings**

### 3.5 Role-Based Permissions Matrix

| Feature | Mess Manager | Mess Member |
|---------|:------------:|:-----------:|
| **Dashboard — Full Overview (all members)** | ✅ | ❌ |
| **Dashboard — Own Settlement Only** | ✅ | ✅ |
| View Grocery List (shopping list) | ✅ | ✅ |
| View Total Grocery Spend | ✅ | ✅ |
| View Utility List & Totals | ✅ | ✅ |
| View Own Meal Count | ✅ | ✅ |
| View All Members' Meal Counts | ✅ | ❌ |
| View Own Contributions | ✅ | ✅ |
| View All Members' Contributions | ✅ | ❌ |
| View Own Settlement (GET BACK / PAY EXTRA) | ✅ | ✅ |
| View All Members' Settlements | ✅ | ❌ |
| **Add/Edit/Delete Grocery** | ✅ | ❌ |
| **Add/Edit/Delete Utility** | ✅ | ❌ |
| **Mark/Edit Meals** | ✅ | ❌ |
| **Add/Edit/Delete Contributions** | ✅ | ❌ |
| **Add/Remove Members** | ✅ | ❌ |
| **Share Invite Code** | ✅ | ❌ |
| **Mess Settings** | ✅ | ❌ |
| Switch Mess / Join New | ✅ | ✅ |
| Create New Mess | ✅ | ✅ |
| Leave Mess | ❌ (owner) | ✅ |

### 3.6 Member Dashboard — What They See

The member's dashboard is **clean and personal**:

| Section | What Member Sees |
|---------|------------------|
| **My Settlement** | "GET BACK ৳X" or "PAY EXTRA ৳Y" — large, prominent |
| **My Meals This Month** | Total meal count for current month |
| **My Contributions** | Total amount contributed |
| **My Grocery Cost** | Their share based on meals × meal rate |
| **My Utility Share** | Equal split of utility expenses |
| **Meal Rate** | Current ৳/meal rate (shared info) |
| **Total Grocery Spend** | How much was spent on food total (shared info) |
| **Total Utility Spend** | How much was spent on utilities (shared info) |
| **Grocery List** | Full shopping list — what was bought (shared info) |
| **Utility List** | What bills were paid (shared info) |

---

## 4. Scope & Feature Hierarchy

### MVP (Phase 1 — Critical)

#### Module 0: Onboarding & Access System
| User Story | Priority |
|------------|----------|
| As a **new user**, I want to see a welcome screen with "Create Mess" and "Join Mess" options so that I can get started. | P0 |
| As a **new user**, I want to create a mess and become its manager so that I can start tracking expenses. | P0 |
| As a **new user**, I want to join an existing mess using an invite code so that I can see the mess's financial data. | P0 |
| As a **manager**, I want to share my mess's invite code from Settings so that members can join. | P0 |

#### Module 1: Grocery Purchases Tracking
| User Story | Priority |
|------------|----------|
| As a **Mess Manager**, I want to log each grocery purchase with date, item name, unit, quantity, and cost so that all food expenses are recorded. | P0 |
| As a **Mess Manager**, I want to add optional notes to a grocery entry. | P1 |
| As a **Mess Member**, I want to see the grocery list and running total (read-only) so that I know how much has been spent on food. | P0 |

#### Module 2: Utility & Household Expenses
| User Story | Priority |
|------------|----------|
| As a **Mess Manager**, I want to log utility bills with category, amount, and notes. | P0 |
| As a **Mess Member**, I want to see the utility expenses and my equal share (read-only). | P0 |

#### Module 3: Daily Meal Tracker
| User Story | Priority |
|------------|----------|
| As a **Mess Manager**, I want to mark meals as float values (0, 0.5, 1, 1.5, 2, 2.5, 3) per member per day to support half-meal tracking. | P0 |
| As a **Mess Manager**, I want to switch between a **calendar grid view** and a **day-by-day view** for marking meals. | P0 |
| As a **Mess Member**, I want to see my meal count and the full grid (read-only). | P0 |

#### Module 4: Member Contributions & Deposit Log
| User Story | Priority |
|------------|----------|
| As a **Mess Manager**, I want to record contributions with member, amount, date, and purpose. | P0 |
| As a **Mess Member**, I want to see all contributions (read-only). | P0 |

#### Module 5: Dashboard (Homepage)
| User Story | Priority |
|------------|----------|
| As **any user**, I want to see the expense overview, meal rate, member-wise breakdown, and settlement status. | P0 |
| As a **Mess Member**, I want to see "GET BACK BDT X" or "PAY EXTRA BDT Y" for my settlement. | P0 |

#### Module 6: Settings & Mess Management
| User Story | Priority |
|------------|----------|
| As a **user**, I want a Settings screen to manage my messes (list, switch, create, join). | P0 |
| As a **manager**, I want to see and share my mess's invite code from Settings. | P0 |
| As a **manager**, I want to manage mess members (view, remove) from Settings. | P0 |
| As a **manager**, I want to **transfer managership** to another member of the mess. | P0 |
| As a **member**, I want to leave a mess from Settings. | P1 |

#### Module 7: Navigation
| User Story | Priority |
|------------|----------|
| As a **user**, I want bottom navigation to switch between Dashboard, Grocery, Utility, Meals, and Contributions. | P0 |
| As a **user**, I want a Settings icon in the top bar to access mess management. | P0 |

#### Module 8: Personalized Push Notifications
| User Story | Priority |
|------------|----------|
| As a **member**, when the manager updates my meal count, I want a notification like "Your meals for Aug 5: 2.5" — only MY data, not others'. | P0 |
| As a **member**, when the manager records my contribution, I want a notification like "Your deposit of ৳2,000 recorded". | P0 |
| As a **member**, when a new grocery entry is added, I want a notification like "New grocery: Rice 5kg ৳350" (shared info, everyone gets this). | P1 |
| As a **member**, when a utility bill is added, I want a notification like "New bill: Rent ৳6,000" (shared info). | P1 |

#### Module 9: Auto-Update via GitHub
| User Story | Priority |
|------------|----------|
| As a **developer**, when I push code to GitHub, a new APK should be built automatically by GitHub Actions. | P0 |
| As a **user**, when a new version is available on GitHub Releases, the app should show an "Update Available" notification on launch. | P0 |
| As a **user**, I want to tap the update notification to download and install the new APK. | P0 |

#### Module 10: Borrow System (Item Return, NOT Financial)
| User Story | Priority |
|------------|----------|
| As a **member**, I want to send a borrow request (item name, quantity) when I take something from the mess pantry. | P0 |
| As a **manager**, I want to see pending borrow requests and **accept or reject** each one. | P0 |
| As a **manager**, when I accept, I want to set a **due date** for when the item must be returned. | P0 |
| As a **member**, after approval I get a notification: "Return [item] by [date]". | P0 |
| As a **member**, I get a **daily reminder notification** until I return the item. | P0 |
| As a **manager**, I want to mark a borrow as **"returned"** to stop the reminders. | P0 |
| As a **member**, I want to see my active borrows and their due dates. | P1 |

> **No settlement impact** — borrow is physical item return only. No money involved.

---

### Post-MVP (Future Expansion)

| Feature | Description | Priority |
|---------|-------------|----------|
| **Export to PDF/Excel** | Generate downloadable monthly reports. | P2 |
| **Bkash/Payment Integration** | Direct payment links or QR codes for contributions. | P3 |
| **Historical Analytics** | Month-over-month trends, average meal rate history. | P3 |
| **Dark Mode** | Alternate theme for low-light usage. | P3 |

---

## 5. Non-Functional Requirements

| Category | Requirement |
|----------|-------------|
| **Performance** | App launch to interactive dashboard in < 2 seconds. Calculations < 500ms. |
| **Real-Time Sync** | Manager's data changes must be visible to members within 5 seconds. |
| **Offline Resilience** | Manager can add data offline; syncs when connectivity returns. Members see cached data. |
| **Data Integrity** | Financial calculations deterministic. Integer paisa storage. No float rounding. |
| **Security** | Google Sign-In for user identity. Firestore security rules enforce role permissions. |
| **Accessibility** | Min 48dp touch targets. **Bilingual: English + Bangla** with language toggle. |
| **Compatibility** | Android 7.0+ (API 24). Target SDK API 35. |
| **Scalability** | Up to 20 members/mess, 12 months history, unlimited messes per user. `[Inferred Default]` |
| **Currency** | All monetary values in BDT (Bangladeshi Taka). |

---
