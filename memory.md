# Memory — Living Session Log & Progress Tracker
# Mess Management System — Android App

---

## Project Status

| Field | Value |
|-------|-------|
| **Current Phase** | Phase 0 — Initialization & Planning |
| **Status** | 🟢 All Questions Answered — Ready to code on user's command |
| **Last Updated** | 2026-08-02T01:52:00+06:00 |
| **Started** | 2026-08-02 |

---

## All User Decisions (Confirmed)

| # | Decision | User's Answer |
|---|----------|---------------|
| 1 | Auth method | **Google Sign-In** (one-tap, persistent) |
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

---

## Context Tracker

### Created Files

| File | Version | Description |
|------|---------|-------------|
| `project.txt` | v1 (pre-existing) | Raw project specification |
| `Master_File.md` | v1 (pre-existing) | Blueprint for generating planning files |
| `PRD.md` | **v3** | + member dashboard visibility rules, bilingual, float meals, editable dropdown |
| `Architecture.md` | **v3** | Google Sign-In, float mealCount, Firestore model |
| `rules.md` | **v3** | Float meal validation, Firebase deps |
| `phases.md` | v2 | Phase 1 = Auth + Onboarding |
| `design.md` | v1 | UI/UX design system (needs update for member dashboard) |
| `memory.md` | **v3** | This file — all decisions finalized |
| `Questions.md` | v2 | All questions now answered |

---

## Architectural Decision Record (ADR)

### ADR-001: Kotlin + Jetpack Compose ✅ Confirmed
### ADR-002: Firebase Firestore as primary database ✅ Confirmed
### ADR-003: Google Sign-In (not Anonymous Auth) ✅ Confirmed
- User chose persistent auth over zero-friction anonymous
### ADR-004: Store currency as integer paisa ✅ Confirmed
### ADR-005: MVVM with Hilt DI ✅ Confirmed
### ADR-006: StateFlow over LiveData ✅ Confirmed
### ADR-007: Role-Based Access — Manager (admin) vs Member (read-only) ✅ Confirmed
### ADR-008: Invite Code System ✅ Confirmed
### ADR-009: Multi-Mess Support in MVP ✅ Confirmed
### ADR-010: Floating Point Meal Counts ✅ NEW
- Meal count supports 0, 0.5, 1, 1.5, 2, 2.5, 3 (step 0.5)
- Stored as Firestore `number` (double)
### ADR-011: Dual Meal Tracker View ✅ NEW
- Calendar grid view (horizontal scroll, all days)
- Day-by-day view (select date, mark all members)
- User can toggle between views
### ADR-012: Member Sees Only Own Data on Dashboard ✅ NEW
- Members see: own settlement, own meals, own contributions, own costs
- Members also see: grocery list, utility list, total spend, meal rate (shared info)
- Members CANNOT see: other members' meals, contributions, or settlements
### ADR-013: Bilingual — English + Bangla ✅ NEW
- Language toggle in Settings
- Uses Android `strings.xml` resource system with `values/` (English) and `values-bn/` (Bangla)
### ADR-014: Editable Dropdown for Grocery Units ✅ NEW
- Preset options: kg, poya, pcs, liter, gram
- User can type custom unit in the same field

---

## Session Log

| Timestamp | Action |
|-----------|--------|
| 2026-08-02 00:47 | Session started — user requested 7 planning files |
| 2026-08-02 00:48-00:53 | Created all 7 files (v1) |
| 2026-08-02 01:20 | User added: access system, roles, onboarding, settings, invite codes |
| 2026-08-02 01:21-01:25 | Updated all docs to v2 (Firebase, roles, multi-user) |
| 2026-08-02 01:30 | User asked: what is anonymous auth? — explained |
| 2026-08-02 01:33 | User asked: does WiFi-only sync need Firebase? — explained cloud vs local |
| 2026-08-02 01:37 | User asked: Firebase cost at scale? — showed free tier math |
| 2026-08-02 01:40 | **User confirmed Firebase** |
| 2026-08-02 01:50 | **User answered ALL remaining questions** (7/7) |
| 2026-08-02 01:52 | Updated all docs to v3 with final decisions |
| 2026-08-02 01:52 | **🟢 PLANNING COMPLETE — awaiting "start the code"** |

---
