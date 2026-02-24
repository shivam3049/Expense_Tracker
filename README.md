# ExpenseTracker - Smart Finance Manager 

**ExpenseTracker** is a robust Android application designed to help users manage their personal finances effortlessly. The standout feature is its **Automated SMS Parsing**, which eliminates manual entry by intelligently capturing transaction data from bank messages.

## Key Features

* **Smart SMS Parsing:** Utilizes a `BroadcastReceiver` to automatically extract transaction details (amount, category) from incoming SMS alerts.
* **Offline-First Architecture:** Built with `Room Database` to ensure lightning-fast performance and full accessibility without an internet connection.
* **Interactive Data Visualization:** Features `MPAndroidChart` to provide users with visual insights into their weekly and monthly spending trends.
* **Secure Cloud Sync:** Integrated with `Firebase Authentication` (Google & Email) and `Firestore` for secure multi-device data backup.
* **Modern UI/UX:** Developed using `Material Design` principles, `ViewBinding`, and smooth animations for a premium user experience.

## Tech Stack

* **Language:** Kotlin
* **Database:** Room (Local Persistence), Firebase Firestore (Cloud Sync)
* **Threading:** Kotlin Coroutines (Asynchronous Tasks)
* **UI Components:** ViewBinding, RecyclerView, Material Components, Glide
* **External Libraries:** * `MPAndroidChart` for Analytics
    * `Firebase Auth` for Security

## Installation & Setup

1.  Clone the repository:  
    `git clone https://github.com/yourusername/ExpenseTracker.git`
2.  Open the project in **Android Studio**.
3.  Add your `google-services.json` file from Firebase into the `app/` directory.
4.  Build and Run the app on your device/emulator.
