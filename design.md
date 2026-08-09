# UI/UX Design System — Pro Max Edition
# Mess Management System

---

## 1. Design DNA

**Inspired by**: Revolut × bKash × Splitwise — a premium fintech feel with South Asian warmth.

**Core Principles:**
- **Glass & Gradient** — frosted glass cards, smooth color gradients, layered depth
- **Numbers that breathe** — currency values are heroes, not afterthoughts
- **Micro-interactions everywhere** — every tap, swipe, and transition feels alive
- **Dark-first design** — OLED-optimized dark theme is the default, light is secondary
- **Bangla-ready** — UI built for both English and বাংলা from day one

---

## 2. Color System

### 2.1 Brand Colors — "Carbon Mint"

The palette uses **deep dark surfaces** with **glowing mint/cyan accents** — critical financial numbers pop against dark backgrounds. The amber secondary adds warmth.

#### Dark Theme (Default)

| Token | Hex | Usage |
|-------|-----|-------|
| `primary` | `#00E5A0` | Mint green — CTAs, active states, positive indicators |
| `primaryGlow` | `#00E5A033` | 20% opacity mint — glow behind cards, focus rings |
| `primaryMuted` | `#00B37D` | Pressed/dimmed state of primary |
| `secondary` | `#FFB547` | Warm amber — FABs, highlights, badges |
| `secondaryMuted` | `#CC9139` | Pressed amber |
| `tertiary` | `#818CF8` | Soft indigo — info badges, charts, links |
| `background` | `#0A0E14` | True dark — main background (OLED black) |
| `surfaceLowest` | `#0F1318` | Lowest elevation surface |
| `surface` | `#141B22` | Cards, bottom sheets, dialogs |
| `surfaceHigh` | `#1C252E` | Elevated cards, active items |
| `surfaceBright` | `#253240` | Highest elevation, selected states |
| `onBackground` | `#F0F2F5` | Primary text |
| `onSurface` | `#E0E3E8` | Card text |
| `onSurfaceDim` | `#7A8594` | Secondary text, labels, captions |
| `onSurfaceFaint` | `#4A5568` | Disabled text, placeholders |
| `outline` | `#2A3544` | Borders, dividers |
| `outlineVariant` | `#1E2836` | Subtle borders |

#### Light Theme

| Token | Hex | Usage |
|-------|-----|-------|
| `primary` | `#00875A` | Darker mint for light backgrounds |
| `primaryGlow` | `#00875A1A` | Subtle tinted backgrounds |
| `secondary` | `#E5982D` | Deeper amber |
| `tertiary` | `#6366F1` | Indigo |
| `background` | `#F5F7FA` | Off-white |
| `surface` | `#FFFFFF` | Cards |
| `surfaceHigh` | `#F0F2F5` | Elevated |
| `onBackground` | `#111827` | Text |
| `onSurface` | `#1F2937` | Card text |
| `onSurfaceDim` | `#6B7280` | Secondary text |
| `outline` | `#E5E7EB` | Borders |

#### Semantic Colors (Both Themes)

| Token | Dark Hex | Light Hex | Usage |
|-------|----------|-----------|-------|
| `positive` | `#34D399` | `#059669` | "GET BACK", surplus, success |
| `positiveBg` | `#34D3991A` | `#D1FAE5` | Positive background |
| `negative` | `#F87171` | `#DC2626` | "PAY EXTRA", deficit, error |
| `negativeBg` | `#F871711A` | `#FEE2E2` | Negative background |
| `info` | `#60A5FA` | `#2563EB` | "SETTLED", neutral info |
| `infoBg` | `#60A5FA1A` | `#DBEAFE` | Info background |
| `warning` | `#FBBF24` | `#D97706` | Alerts, pending states |

### 2.2 Gradient Presets

| Name | Colors | Usage |
|------|--------|-------|
| `heroGradient` | `#00E5A0 → #00B4D8` | Settlement hero card background |
| `amberGlow` | `#FFB547 → #FF8C00` | FAB gradient, accent cards |
| `surfaceGradient` | `#141B22 → #1C252E` | Card backgrounds with depth |
| `negativeGradient` | `#F87171 → #EF4444` | "PAY EXTRA" card |
| `positiveGradient` | `#34D399 → #10B981` | "GET BACK" card |

---

## 3. Typography

### Font Stack
- **Display & Headings**: `Outfit` (Google Fonts) — geometric, modern, premium feel
- **Body & UI**: `Inter` (Google Fonts) — highly legible at small sizes
- **Currency & Numbers**: `Space Grotesk` (Google Fonts) — beautiful tabular numbers
- **Bangla**: `Noto Sans Bengali` (Google Fonts) — clean Bangla support
- **Fallback**: System sans-serif

### Type Scale

| Token | Font | Size | Weight | Tracking | Usage |
|-------|------|------|--------|----------|-------|
| `displayHero` | Space Grotesk | 36sp | Bold 700 | -0.02em | Settlement amount on dashboard |
| `displayLarge` | Outfit | 30sp | Bold 700 | -0.01em | Screen hero numbers |
| `displayMedium` | Outfit | 24sp | SemiBold 600 | 0 | Section totals |
| `headlineLarge` | Outfit | 22sp | SemiBold 600 | 0 | Screen titles |
| `headlineMedium` | Outfit | 18sp | SemiBold 600 | 0 | Card headers |
| `titleLarge` | Inter | 16sp | Medium 500 | 0.01em | List item primary text |
| `titleMedium` | Inter | 14sp | Medium 500 | 0.01em | Subtitles |
| `bodyLarge` | Inter | 16sp | Regular 400 | 0.02em | Body text |
| `bodyMedium` | Inter | 14sp | Regular 400 | 0.02em | Secondary body |
| `bodySmall` | Inter | 12sp | Regular 400 | 0.03em | Captions, timestamps |
| `labelLarge` | Inter | 14sp | SemiBold 600 | 0.04em | Buttons |
| `labelMedium` | Inter | 12sp | SemiBold 600 | 0.05em | Chips, tabs, badges |
| `labelSmall` | Inter | 10sp | Medium 500 | 0.06em | Overlines |
| `currency` | Space Grotesk | 20sp | Bold 700 | 0 | Inline BDT amounts |
| `currencyHero` | Space Grotesk | 32sp | Bold 700 | -0.02em | Dashboard totals |

---

## 4. Spacing & Grid

### Spacing Tokens

| Token | dp | Usage |
|-------|-----|-------|
| `2xs` | 2 | Hairline gaps |
| `xs` | 4 | Icon-to-text gap |
| `sm` | 8 | Related element spacing |
| `md` | 12 | Standard padding |
| `lg` | 16 | Card padding, screen horizontal padding |
| `xl` | 20 | Between card groups |
| `2xl` | 24 | Section separators |
| `3xl` | 32 | Major layout gaps |
| `4xl` | 48 | Hero section padding |

### Layout Rules

- Screen padding: `16dp` horizontal, `12dp` top
- Card internal padding: `16dp` (all sides), `20dp` for hero cards
- Card gap (between cards): `12dp`
- Touch target minimum: `48dp × 48dp`
- Bottom nav height: `64dp` (slightly taller for premium feel)
- Max content width: `600dp` (tablet)

---

## 5. Corner Radius & Shape

| Token | dp | Usage |
|-------|-----|-------|
| `radiusSm` | 8 | Chips, small badges |
| `radiusMd` | 12 | Buttons, input fields, small cards |
| `radiusLg` | 16 | Standard cards, dialogs |
| `radiusXl` | 20 | Hero cards, bottom sheets |
| `radiusFull` | 9999 | Pills, FAB, circular avatars |

---

## 6. Elevation & Glass Effects

### Dark Theme (Tonal Elevation — no shadows)

| Level | Surface Color | Usage |
|-------|--------------|-------|
| Level 0 | `background` (#0A0E14) | Page background |
| Level 1 | `surfaceLowest` (#0F1318) | Inactive cards |
| Level 2 | `surface` (#141B22) | Default cards, sheets |
| Level 3 | `surfaceHigh` (#1C252E) | Active/focused cards |
| Level 4 | `surfaceBright` (#253240) | Modals, selected items |

### Glassmorphism (Used sparingly on hero elements)

```
Background:    surface with 60% opacity
Backdrop blur:  16dp
Border:         1dp outline at 30% opacity
```

Used on: Settlement hero card, welcome screen cards, bottom sheet header.

### Light Theme (Shadow-based elevation)

| Level | Shadow | Usage |
|-------|--------|-------|
| Level 1 | `0dp 1dp 3dp rgba(0,0,0,0.08)` | Cards at rest |
| Level 2 | `0dp 4dp 12dp rgba(0,0,0,0.12)` | FAB, active cards |
| Level 3 | `0dp 8dp 24dp rgba(0,0,0,0.16)` | Dialogs, modals |

---

## 7. Screen Designs

### 7.1 Welcome Screen (First Launch)

```
┌──────────────────────────────────┐
│                                  │
│        [App Logo/Icon]           │
│                                  │
│     Mess Management System       │  ← Outfit, displayMedium
│     মেস ম্যানেজমেন্ট সিস্টেম       │  ← Noto Sans Bengali, bodyLarge
│                                  │
│  ┌────────────────────────────┐  │
│  │  🏠  Create Mess            │  │  ← Primary gradient button, full width
│  │     মেস তৈরি করুন           │  │
│  └────────────────────────────┘  │
│                                  │
│  ┌────────────────────────────┐  │
│  │  🔗  Join Mess              │  │  ← Outlined button, primary border
│  │     মেসে যোগ দিন            │  │
│  └────────────────────────────┘  │
│                                  │
│     Already have an account?     │
│     Sign in with Google →        │  ← Text link, primary color
│                                  │
└──────────────────────────────────┘
```

### 7.2 Manager Dashboard

```
┌──────────────────────────────────┐
│  ☰  Mirpur Mess · Aug 2026  ⚙️  │  ← TopBar: mess name + month + settings
├──────────────────────────────────┤
│                                  │
│  ╔══════════════════════════════╗ │
│  ║  TOTAL MESS EXPENSE          ║ │  ← Hero card, glass effect
│  ║  ৳ 24,850.00                 ║ │  ← currencyHero, primary glow
│  ║  Grocery ৳18,200 · Util ৳6,650║│  ← bodySmall, onSurfaceDim
│  ╚══════════════════════════════╝ │
│                                  │
│  ┌──────────┐  ┌──────────┐      │  ← 2-column stat cards
│  │ Meal Rate │  │ Members  │      │
│  │ ৳ 72.40  │  │    6     │      │
│  │ per meal  │  │  active  │      │
│  └──────────┘  └──────────┘      │
│                                  │
│  ┌──────────┐  ┌──────────┐      │
│  │ Total    │  │ Fund     │      │
│  │ Meals    │  │ Collected│      │
│  │   251    │  │ ৳26,000  │      │
│  └──────────┘  └──────────┘      │
│                                  │
│  ── Member Settlements ────────  │
│                                  │
│  ┌────────────────────────────┐  │
│  │ 🟢 Jeet          GET BACK │  │
│  │    42 meals     ৳ 1,240   │  │  ← positive color, positive bg
│  ├────────────────────────────┤  │
│  │ 🔴 Sagnik       PAY EXTRA │  │
│  │    38 meals     ৳ 820     │  │  ← negative color, negative bg
│  ├────────────────────────────┤  │
│  │ 🔵 Antu           SETTLED │  │
│  │    44 meals     ৳ 0       │  │  ← info color, info bg
│  └────────────────────────────┘  │
│                                  │
├──────────────────────────────────┤
│ 🏠  🛒  📋  🍽️  💰             │  ← Bottom nav (5 items)
└──────────────────────────────────┘
```

### 7.3 Member Dashboard (Personal View — Clean & Focused)

```
┌──────────────────────────────────┐
│  ☰  Mirpur Mess · Aug 2026  ⚙️  │
├──────────────────────────────────┤
│                                  │
│     ┌─────────┐  ┌─────────┐    │
│     │  ╭───╮  │  │  ╭───╮  │    │
│     │  │42 │  │  │  │৳5k│  │    │  ← Two circular stat widgets
│     │  ╰───╯  │  │  ╰───╯  │    │
│     │My Meals │  │My Contri│    │  ← "Contribution" label
│     │         │  │ -bution │    │
│     └─────────┘  └─────────┘    │
│                                  │
│  ╔══════════════════════════════╗ │
│  ║  💰 Money Remains            ║ │  ← Glass card
│  ║                              ║ │
│  ║     ৳ 1,150.00              ║ │  ← Total fund manager holds
│  ║     (Total Contributions     ║ │     = totalContributions
│  ║      - Total Expenses)       ║ │       - totalExpenses
│  ╚══════════════════════════════╝ │
│                                  │
│  ── Your Breakdown ────────────  │
│                                  │
│  Meal Rate        ৳ 72.40       │
│  My Grocery Cost  ৳ 3,040.80    │
│  My Utility Share ৳ 1,108.33    │
│  My Total Cost    ৳ 4,149.13    │
│  Settlement       GET BACK ৳1,240│ ← positive/negative
│                                  │
│  ── Recent Grocery ────────────  │
│                                  │
│  🛒 Rice (5 kg)       ৳ 350     │  ← Latest 3-5 grocery entries
│     Aug 8 · Kacha bazar          │
│  🛒 Oil (2 liter)     ৳ 280     │
│     Aug 8 · Shwapno              │
│  🛒 Potato (3 kg)     ৳ 120     │
│     Aug 7                        │
│                                  │
│       View All Grocery →         │  ← Link to full grocery list
│                                  │
├──────────────────────────────────┤
│ 🏠  🛒  📋  🍽️  💰             │
└──────────────────────────────────┘
```

**Circle Widget Spec:**
- Size: `80dp × 80dp`
- Ring: `4dp` stroke, `primary` color (mint)
- Progress: ring fills based on proportion (e.g., meals out of max possible)
- Value inside: `displayMedium`, bold, centered
- Label below: `labelMedium`, `onSurfaceDim`

**Money Remains Card:**
- Shows: `Total Contributions - Total Expenses` = how much cash the manager has
- Positive value: `primary` (mint) — fund has surplus
- Negative value: `negative` (red) — fund is in deficit

### 7.4 Grocery List Screen

```
┌──────────────────────────────────┐
│  ←  Grocery        ৳ 18,200.00  │  ← Total in topbar, currency font
├──────────────────────────────────┤
│                                  │
│  Aug 2, 2026                     │  ← Date header, sticky
│  ┌────────────────────────────┐  │
│  │  Rice (5 kg)    ৳ 350.00  │  │
│  │  Kacha bazar               │  │  ← bodySmall, dim
│  ├────────────────────────────┤  │
│  │  Oil (2 liter)  ৳ 280.00  │  │
│  │  Shwapno                   │  │
│  └────────────────────────────┘  │
│                                  │
│  Aug 1, 2026                     │
│  ┌────────────────────────────┐  │
│  │  Potato (3 kg)  ৳ 120.00  │  │
│  └────────────────────────────┘  │
│                                  │
│                        [+ FAB]   │  ← Manager only, amber gradient
├──────────────────────────────────┤
│ 🏠  🛒  📋  🍽️  💰             │
└──────────────────────────────────┘
```

### 7.5 Meal Tracker — Day-by-Day View

```
┌──────────────────────────────────┐
│  ←  Meal Tracker    [Grid|Day]  │  ← Toggle between views
├──────────────────────────────────┤
│                                  │
│     ◀  August 2, 2026  ▶        │  ← Date picker, swipe to change
│                                  │
│  ┌────────────────────────────┐  │
│  │  Jeet          [  -  2  +  ] │ │  ← Stepper: 0.5 increments
│  ├────────────────────────────┤  │
│  │  Sagnik        [  -  3  +  ] │ │
│  ├────────────────────────────┤  │
│  │  Antu          [  - 1.5 +  ] │ │  ← Float support
│  ├────────────────────────────┤  │
│  │  Noyon         [  -  0  +  ] │ │  ← Dim when 0
│  └────────────────────────────┘  │
│                                  │
│  Today's Total:  6.5 meals       │  ← Bold, primary color
│                                  │
├──────────────────────────────────┤
│ 🏠  🛒  📋  🍽️  💰             │
└──────────────────────────────────┘
```

### 7.6 Settings Screen

```
┌──────────────────────────────────┐
│  ←  Settings                     │
├──────────────────────────────────┤
│                                  │
│  MY MESSES                       │  ← Overline label
│  ┌────────────────────────────┐  │
│  │ ★ Mirpur Mess · Aug 2026  │  │  ← Active mess, highlighted
│  │   Manager · 6 members      │  │
│  ├────────────────────────────┤  │
│  │   Hall 5 Mess · Aug 2026  │  │
│  │   Member · 8 members       │  │
│  └────────────────────────────┘  │
│                                  │
│  [+ Create New Mess]             │  ← Outlined button
│  [🔗 Join Mess]                  │  ← Outlined button
│                                  │
│  ── Current Mess ──────────────  │
│                                  │
│  Invite Code        X7K9M2 📋   │  ← Tap to copy, share button
│  Share Code         [Share →]    │
│  Transfer Manager   [→]         │  ← Manager only
│  Manage Members     [→]         │  ← Manager only
│  Leave Mess         [→]         │  ← Member only, red
│                                  │
│  ── App ───────────────────────  │
│                                  │
│  Language     [English | বাংলা]  │  ← Toggle
│  About                [→]       │
│                                  │
└──────────────────────────────────┘
```

---

## 8. Component Specs

### Hero Card (Settlement / Expense Overview)
- Corner radius: `20dp`
- Padding: `20dp`
- Background: gradient (`heroGradient` or `surfaceGradient`)
- Glass blur overlay: `16dp` blur, `60%` surface opacity
- Border: `1dp` `outline` at `30%` opacity
- Currency text: `displayHero` (36sp, Space Grotesk, Bold)
- Min height: `120dp`

### Stat Card (2×2 Grid)
- Corner radius: `16dp`
- Padding: `16dp`
- Background: `surface`
- Value: `displayMedium` (24sp, Outfit, SemiBold)
- Label: `bodySmall` (12sp, `onSurfaceDim`)
- Size: fill half width, `100dp` height
- Subtle glow on the value's left edge matching the icon color

### List Item (Grocery / Utility / Contribution)
- Corner radius: `12dp`
- Padding: `16dp` vertical, `16dp` horizontal
- Height: auto (min `64dp`)
- Primary text: `titleLarge` (16sp)
- Secondary text: `bodySmall` (12sp, `onSurfaceDim`)
- Amount: `currency` (20sp, Space Grotesk, right-aligned)
- Swipe left to delete (red bg), swipe right to edit (primary bg)
- Manager only: swipe actions visible
- Member: no swipe, no actions

### FAB (Manager Only)
- Shape: `radiusFull` (pill)
- Size: `56dp` height, auto width (if extended)
- Background: `amberGlow` gradient
- Icon + Text: `onSecondary` (#1A1200)
- Elevation: `8dp` shadow (light), glow effect (dark)
- Position: bottom-end, `16dp` inset
- Hidden for members

### Bottom Navigation
- Height: `56dp`
- Background: `surfaceLowest` (dark), `surface` (light)
- Top border: `1dp` `outlineVariant`
- **No icons — text labels only**
- Active label: `labelLarge`, `primary` color, bold
- Inactive label: `labelMedium`, `onSurfaceDim` color
- Items: `Home` · `Grocery` · `Utility` · `Meals` · `Deposits`
- Active indicator: pill-shaped `primaryGlow` background behind active text

### Settlement Badge
- Positive: `positive` text on `positiveBg`, left border `3dp positive`
- Negative: `negative` text on `negativeBg`, left border `3dp negative`
- Neutral: `info` text on `infoBg`, left border `3dp info`
- Corner radius: `8dp`
- Padding: `12dp` horizontal, `8dp` vertical
- Typography: `labelLarge`, bold

### Meal Stepper (Day-by-Day View)
- Container: `surfaceHigh` background, `radiusMd` corners
- Buttons (-/+): `40dp × 40dp`, `primary` color, circular
- Value display: `currency` font (20sp), centered, `48dp` wide
- Step: `0.5`
- Range: `0` to `3`
- Haptic feedback on each step
- Value `0`: dim (`onSurfaceFaint`), Values `> 0`: bright (`onSurface`)

### Form Bottom Sheet
- Corner radius: `20dp` top corners
- Background: `surface`
- Handle: `4dp × 40dp` centered, `outlineVariant` color, `radiusFull`
- Header: `headlineMedium` + close (X) icon
- Input fields: Material 3 `OutlinedTextField`, `radiusMd`
- Focused border: `primary`
- Error text: `negative` color, `bodySmall`, below field
- Save button: full width, `primary` filled, `radiusMd`
- Cancel: text button, `onSurfaceDim`

---

## 9. Iconography

- **Icon Set**: Material Symbols Rounded (weight 400, grade 0, size 24)
- **Why Rounded**: Softer, friendlier feel vs sharp outlines

| Element | Icon Name | Active Style |
|---------|-----------|-------------|
| Dashboard | `home` | Filled |
| Grocery | `shopping_cart` | Filled |
| Utility | `receipt_long` | Filled |
| Meals | `restaurant` | Filled |
| Contributions | `account_balance_wallet` | Filled |
| Settings | `settings` | Outlined always |
| Add | `add` | — |
| Edit | `edit` | — |
| Delete | `delete` | — |
| Share | `share` | — |
| Copy | `content_copy` | — |
| Transfer | `swap_horiz` | — |
| Leave | `logout` | — |
| Calendar | `calendar_month` | — |
| Person | `person` | — |
| Group | `group` | — |
| Grid View | `grid_view` | — |
| Day View | `view_day` | — |

---

## 10. Motion & Animation

### Easing Curves
- **Enter**: `EmphasizedDecelerate` (M3 standard) — elements arriving
- **Exit**: `EmphasizedAccelerate` — elements leaving
- **Standard**: `Emphasized` — continuous motion (resizing, repositioning)

### Animation Specs

| Interaction | Type | Duration | Easing |
|-------------|------|----------|--------|
| Screen transition | Shared axis (horizontal) | 350ms | Emphasized |
| Card appear (stagger) | Fade up + scale 0.96→1.0 | 250ms | Decelerate, 50ms stagger |
| Hero number change | Counter roll (digit-by-digit) | 400ms | Emphasized |
| FAB press | Scale 1.0→0.92→1.0 | 120ms | Standard |
| Meal stepper tap | Value scale pulse 1.0→1.15→1.0 | 150ms | Standard |
| List item add | Slide from right + fade | 250ms | Decelerate |
| List item swipe delete | Slide out + shrink height | 200ms | Accelerate |
| Bottom sheet open | Slide up + scrim fade | 300ms | Decelerate |
| Bottom nav switch | Crossfade + indicator slide | 200ms | Standard |
| Settlement badge | Subtle shimmer on first reveal | 600ms | Linear |
| Skeleton loading | Shimmer left→right | 1200ms | Linear, infinite loop |

### Haptics
- Meal stepper +/- tap: `HapticFeedbackType.LightImpact`
- FAB press: `HapticFeedbackType.MediumImpact`
- Swipe delete complete: `HapticFeedbackType.HeavyImpact`
- Form submit success: `HapticFeedbackType.Success`

---

## 11. Loading & Empty States

### Skeleton Loading
- Shape matches actual content layout
- Background: `outlineVariant`
- Shimmer highlight: `surfaceBright` sweeping left→right
- Duration: `1200ms`, infinite until data loads

### Empty States
Each empty screen has:
- A simple vector illustration (monochrome, `onSurfaceDim` color)
- Title: `headlineMedium`
- Subtitle: `bodyMedium`, `onSurfaceDim`
- CTA button (if applicable): `primary` outlined

| Screen | Title | Subtitle |
|--------|-------|----------|
| Grocery (empty) | "No groceries yet" | "Tap + to add your first purchase" |
| Utility (empty) | "No bills recorded" | "Add rent, water, or other expenses" |
| Meals (empty) | "No meals tracked" | "Start marking today's meals" |
| Contributions (empty) | "No deposits yet" | "Record member contributions" |
| Members (empty) | "Share your invite code" | "Members will appear here once they join" |

---
