# Questions & Risks

---

## Must Fix in Code

| Issue | Problem | Fix |
|-------|---------|-----|
| No meals recorded | `totalGrocery / 0` = crash | Show "No meals yet" if totalMeals = 0 |
| No members | `totalUtility / 0` = crash | Show "Add members first" if members = 0 |

---

## Known Risks (Accepted for MVP)

| Risk | Impact | Plan |
|------|--------|------|
| Manager loses Google account | Loses control of mess | Manager transfer is in MVP |
| Invite code shared publicly | Strangers join mess | Manager can remove members from Settings |
| No edit history | Can't verify who changed what | Add timestamps now, full audit trail post-MVP |
| Offline stale data | Member sees old data | Show "Last updated: X min ago" |

---

## Answered

| # | Question | Answer |
|---|----------|--------|
| 1 | Mid-month join — full or prorated utility? | ✅ **Full share** |
| 2 | Guest meals? | ✅ **Shared equally** among all members |
| 3 | One mess = one month? | ✅ **Yes**, new month = create new mess |
| 4 | Manager transfer? | ✅ **MVP**, manager can transfer to any member |
| 5 | Distribution? | ✅ **APK** |

---
