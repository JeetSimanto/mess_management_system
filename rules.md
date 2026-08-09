# Coding Rules & AI Guardrails
# Mess Management System — Android App

---

## 1. Code Style & Standards

### Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| **Packages** | Lowercase, dot-separated | `com.messmanager.app.ui.dashboard` |
| **Classes/Objects** | PascalCase | `GroceryViewModel`, `MealEntity` |
| **Functions** | camelCase | `calculateMealRate()`, `getTotalGrocery()` |
| **Variables/Properties** | camelCase | `mealCount`, `totalCostBdt` |
| **Constants** | SCREAMING_SNAKE_CASE | `MAX_MEAL_PER_DAY`, `DEFAULT_CURRENCY` |
| **Composable Functions** | PascalCase (noun) | `DashboardScreen()`, `SummaryCard()` |
| **State variables** | camelCase with `_uiState` suffix | `_dashboardUiState` |
| **Firestore Documents** | PascalCase + `Document` suffix | `GroceryDocument`, `MessDocument` |
| **Room Entities** | PascalCase + `Entity` suffix (local cache) | `GroceryEntity` |
| **DAOs** | PascalCase + `Dao` suffix | `GroceryDao` |
| **Repositories** | PascalCase + `Repository` suffix | `GroceryRepository` |
| **ViewModels** | PascalCase + `ViewModel` suffix | `GroceryViewModel` |
| **Firestore collections** | snake_case plural | `grocery_entries`, `meal_entries` |
| **Firestore fields** | camelCase | `costBdt`, `mealCount`, `memberIds` |
| **Files** | Match class name | `GroceryViewModel.kt` |

### Code Organization Rules

1. **One class/interface per file** — except sealed classes and their subtypes.
2. **Max function length**: 40 lines. Extract helper functions if longer.
3. **Max file length**: 300 lines. Split into multiple files if longer.
4. **Max Composable function**: 80 lines. Break into smaller composables.
5. **Import ordering**: Android/Kotlin stdlib → Third-party → Project (alphabetical within groups).
6. **No wildcard imports** (`import com.example.*` is forbidden).

### Kotlin Specific

- Use `val` over `var` wherever possible (immutability first).
- Use `data class` for all models and entities.
- Use `sealed class` or `sealed interface` for UI state and navigation routes.
- Use Kotlin coroutines for all async operations — **no callbacks**.
- Use `Flow` from Room DAOs, never `LiveData`.
- Use `StateFlow` in ViewModels for UI state.
- All public functions and classes must have KDoc comments.

---

## 2. Allowed vs. Forbidden Dependencies

### ✅ Allowed (Core Stack)

| Package | Purpose |
|---------|---------|
| `androidx.compose.*` | Jetpack Compose UI |
| `androidx.compose.material3` | Material 3 Design components |
| `androidx.navigation:navigation-compose` | Screen navigation |
| `androidx.room:room-runtime`, `room-ktx`, `room-compiler` | Local cache database |
| `com.google.dagger:hilt-android`, `hilt-compiler` | Dependency injection |
| `androidx.hilt:hilt-navigation-compose` | Hilt + Navigation integration |
| `androidx.lifecycle:lifecycle-viewmodel-compose` | ViewModel in Compose |
| `org.jetbrains.kotlinx:kotlinx-coroutines-android` | Coroutines |
| `androidx.core:core-ktx` | Kotlin extensions |
| `com.google.android.material:material` | Material components (if needed) |
| **`com.google.firebase:firebase-bom`** | **Firebase BOM for version management** |
| **`com.google.firebase:firebase-auth-ktx`** | **Anonymous authentication** |
| **`com.google.firebase:firebase-firestore-ktx`** | **Cloud database — real-time sync** |

### ❌ Forbidden

| Package | Reason |
|---------|--------|
| `retrofit`, `okhttp`, `ktor` | Use Firestore SDK directly, no custom HTTP |
| `rxjava`, `rxandroid` | Use Kotlin coroutines/Flow instead |
| `butterknife`, `databinding` | Obsolete with Jetpack Compose |
| `gson` | Use `kotlinx.serialization` if needed |
| `android.support.*` | Use `androidx.*` only |
| `LiveData` | Use `StateFlow` / `Flow` exclusively |
| Any image loading library (Glide, Coil, Picasso) | No remote images in MVP |

---

## 3. Error Handling

### General Rules

1. **Never swallow exceptions silently** — always log or surface errors.
2. Use `Result<T>` or sealed classes for operation outcomes:

```kotlin
sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : OperationResult<Nothing>()
}
```

3. **Firestore operations**: Wrap all Firestore calls in `try-catch` within repositories. Use `await()` with coroutines. ViewModels receive `OperationResult`.
4. **Firestore errors**: Handle `FirebaseFirestoreException` — show "No internet" for `UNAVAILABLE`, "Permission denied" for `PERMISSION_DENIED`, generic retry for others.
5. **UI error display**: Use Snackbar for transient errors, Dialog for critical errors.
5. **Validation errors**: Validate all form inputs before database writes. Show inline error messages on form fields.

### Input Validation Rules

| Field | Validation |
|-------|-----------|
| Member name | Non-empty, max 50 chars, no special chars except spaces |
| Cost/Amount | > 0, max 10,000,000 BDT (paisa integer) |
| Quantity | > 0, max 9999 |
| Meal count | Float: 0, 0.5, 1, 1.5, 2, 2.5, 3 (min 0, max 3, step 0.5) |
| Date | Must be within the selected month/year |
| Item name | Non-empty, max 100 chars |
| Notes/Purpose | Max 200 chars |
| Mess name | Non-empty, max 50 chars |
| Invite code | Exactly 6 chars, alphanumeric uppercase |

---

## 4. Currency Handling

- **Store all monetary values as integers (paisa)** — 1 BDT = 100 paisa.
- **Display**: Format as `৳ X.XX` using a centralized `CurrencyFormatter` utility.
- **Calculations**: Use `Long` arithmetic for sums. Use `Double` only for meal rate division, then round to nearest paisa.
- **Never use `Float`** for currency.

---

## 5. AI Safety Guardrails

### 🚫 Destructive Operations (Require Explicit Permission)

1. **Firestore security rules changes** — Must be reviewed before deployment.
2. **Firestore collection/document schema changes** — Requires migration strategy for existing data.
3. **Database schema migrations** — Never auto-generate Room migrations.
4. **Modifying `AndroidManifest.xml` permissions** — Must be explicitly requested.
5. **Changing `build.gradle.kts` dependencies** — Adding/removing deps requires user approval.
6. **Deleting files** — No file deletion without explicit instruction.
7. **Changing package names** — Requires full project awareness.
8. **Modifying Firebase project config** — `google-services.json` changes need review.

### ✅ Safe Operations (Can Be Done Freely)

1. Adding new Composable screens/components.
2. Adding new entity fields (with proper migration).
3. Refactoring within existing files.
4. Adding/modifying unit tests.
5. Updating string resources.
6. Creating new utility functions.

---

## 6. Testing Standards

- **Unit tests required** for: `SettlementCalculator`, all Repository methods, all ViewModel state transitions.
- **Test naming**: `should_expectedResult_when_condition()` pattern.
- **No UI tests in MVP** — focus on business logic correctness.
- **Test data**: Use deterministic test fixtures, never random data.

---
