const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();

/**
 * Triggered when a manager approves a borrow request.
 * Sends immediate notification to the member.
 */
exports.onBorrowStatusChanged = functions.firestore
  .document("messes/{messId}/borrow_requests/{borrowId}")
  .onUpdate(async (change, context) => {
    const before = change.before.data();
    const after = change.after.data();

    // Check if status changed to ACCEPTED
    if (before.status !== "ACCEPTED" && after.status === "ACCEPTED") {
      const requesterUid = after.requesterUid;
      const userDoc = await admin.firestore().collection("users").doc(requesterUid).get();
      
      if (!userDoc.exists) return;
      const fcmToken = userDoc.data().fcmToken;

      if (fcmToken) {
        const payload = {
          notification: {
            title: `Borrow Approved: ${after.itemName}`,
            body: `The manager will not accept any money, what you have borrowed needs to be returned in the given time period. Due Date: ${after.dueDate || "N/A"}`
          },
          data: {
            type: "BORROW_ACCEPTED",
            borrowId: context.params.borrowId
          }
        };

        await admin.messaging().sendToDevice(fcmToken, payload);
      }
    }
  });

/**
 * Scheduled Cron Function running every day at 9:00 AM (Asia/Dhaka).
 * Sends daily reminder notifications to members with active borrowed items.
 */
exports.dailyBorrowReminder = functions.pubsub
  .schedule("0 9 * * *")
  .timeZone("Asia/Dhaka")
  .onRun(async (context) => {
    const activeBorrows = await admin.firestore()
      .collectionGroup("borrow_requests")
      .where("status", "==", "ACCEPTED")
      .get();

    const notifications = [];

    for (const doc of activeBorrows.docs) {
      const borrow = doc.data();
      const userDoc = await admin.firestore().collection("users").doc(borrow.requesterUid).get();

      if (userDoc.exists && userDoc.data().fcmToken) {
        const payload = {
          notification: {
            title: `Reminder: Return ${borrow.itemName}`,
            body: `Reminder to return ${borrow.quantity} of ${borrow.itemName}. The manager will not accept any money, what you have borrowed needs to be returned.`
          },
          data: {
            type: "DAILY_REMINDER",
            borrowId: doc.id
          }
        };

        notifications.push(admin.messaging().sendToDevice(userDoc.data().fcmToken, payload));
      }
    }

    await Promise.all(notifications);
    console.log(`Sent ${notifications.length} daily borrow reminders.`);
  });
