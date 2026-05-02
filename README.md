# Red Hawk Wallet

Red Hawk Wallet is a campus wallet mobile application designed for Montclair State University students and professors. The goal of this project is to provide a simple, secure, and organized way for users to manage campus-related accounts, view balances, simulate campus payments, access a digital ID card, and verify users through QR code scanning.

This project was built as a team software development project and uses Android development with Firebase services for authentication, database storage, profile photo storage, and user data management.

---

## Project Overview

Red Hawk Wallet allows users to register, log in, verify their email, and access a dashboard where they can view different campus balances such as:

- Red Hawk Dollars
- Flex
- Bonus
- Meal Swipes

The application also includes a tap-to-pay style feature using NFC simulation. When a user taps, the selected balance is updated and the transaction is saved in Firebase. The app also includes transaction history so users can view previous payments.

Another major feature of the app is the digital ID card. Users can upload a profile photo, view their Montclair-style ID card, and generate a QR code linked to their Firebase UID. The QR code can be scanned by a professor, TA, or campus staff member to verify the user’s information.

The project also includes Events and Offers, where users can view campus events, club activities, nearby business promotions, and student discounts.

This README is based on the final Red Hawk Wallet project documentation, which describes the project features, workflow, Firebase usage, team roles, and future maintenance plan. :contentReference[oaicite:0]{index=0}

---

## Repository Branch Structure

This repository uses multiple branches to keep the project organized during development and testing.

### Main Branches

| Branch | Purpose |
|---|---|
| `master` | Main branch. This is the final stable version of the project and should represent the main working code. |
| `dev` | Development and testing branch. Most new features and fixes should be tested here before being merged into `master`. |
| `staging` | First staging/testing branch used during Milestone 1. This branch was used to test the early working version of the project. |
| `staging2` | Second staging/testing branch used during Milestone 2. This branch was used for later testing, improvements, and integration before finalizing the app. |

### Feature Branches

| Branch | Purpose |
|---|---|
| `feature-auth` | Authentication, login, registration, and email verification related work. |
| `feature-db` | Database, repository structure, Firebase/Firestore related support. |
| `feature-nfc` | NFC tap-to-pay feature and transaction simulation. |
| `feature-professor-id-verification` | Professor/staff QR scanning and ID verification features. |
| `feature-qr` | QR code generation and QR-related user identification. |
| `feature-ui` | UI/UX improvements, frontend screens, layout updates, and visual changes. |

---

## Team Members and Roles

### Harshvardhan K. Nimesh — Project Lead, Final Integrator, Firebase/Firestore Lead, Android Developer

Harsh served as the project lead and final integrator for Red Hawk Wallet. He helped define the project idea, planned the main features, created and managed the GitHub repository, organized the branch structure, assigned tasks, coordinated team meetings, and helped keep the project moving toward completion.

He handled most of the direct Firebase and Firestore integration, including Firebase Authentication, Firestore Database, Firebase Storage, user data, wallet balances, transaction records, QR/ID data, and profile photo storage. He also worked on connecting the major parts of the app so the final project worked as one complete system rather than as separate, unfinished features.

Harsh also contributed to the core navigation flow, registration and email verification integration, NFC tap-to-pay structure, transaction logging, QR code generation, QR scanner support, profile photo upload, logout functionality, final debugging, merge conflict resolution, APK building, testing, and demo preparation.

### Rohaifa Yassin — Frontend Development and UI Improvements

Rohaifa worked mainly on frontend development and UI improvements. She contributed to screens such as the splash screen, login UI, registration UI, dashboard layout, and other user-facing design updates. Her work helped make the application cleaner, easier to navigate, and more presentable for the final demo.

### Skerdi Bekollori — Email Verification Restrictions, Dashboard Accounts, and Testing Support

Skerdi worked on verification restrictions and dashboard-related functionality. One of his main contributions was helping restrict payment and app access until the user’s email was verified. He also supported the dashboard account options, including Red Hawk Dollars, Flex, Bonus, and Meal Swipes, and helped with testing and feature stability.

### Danilo Shota — Android-Side Backend Structure, Repository Logic, and Testing Support

Danilo contributed to Android-side backend structure, repository logic, and code organization. He helped with repository-related files and supported the connection between UI, business logic, and data flow. He also helped with registration-related issues, navigation support, Android Studio testing, and GitHub branch work.

### Lisandra Nina Rosa — Authentication Flow, Session Handling, and UI Logic

Lisandra worked on the Android-side authentication flow, session handling, and UI logic. She supported login, registration, authentication result handling, and session state management. She also helped with registration testing and later contributed to the Events and Offers section.

### Hatice Karahalil — Dashboard UI, Transactions, Professor ID Features, and Events/Offers Components

Hatice, also known as Ati, worked on dashboard UI, transaction components, professor ID features, and the Events and Offers components. She contributed to reusable UI elements, including event cards, offer cards, and notification cards. Her work helped improve the user experience and made the app feel more complete for the final presentation.

---

## Main Features

### 1. Splash Screen

When the user first opens the app, the splash screen displays the Red Hawk Wallet branding before navigating to the login screen.

### 2. Login and Registration

Users can create an account or log in with an existing account. During registration, users enter their name, email, password, university ID, and role.

Users can register as:

- Student
- Professor

### 3. Email Verification

After registration, Firebase sends a verification email link to the user. The user must verify their email before accessing the main app features.

If the user logs in before verifying their email, the app redirects them to the email verification screen. The user can then verify their email, return to the app, and click refresh to continue.

### 4. Dashboard

After verification, the user is taken to the dashboard. The dashboard shows the user’s wallet balances and allows them to select different campus accounts.

The four main accounts are:

- Red Hawk Dollars
- Flex
- Bonus
- Meal Swipes

### 5. Tap-to-Pay / NFC Simulation

The app includes a tap-to-pay style feature. When the user taps an NFC card or simulates a payment, the selected account balance is updated.

The app also creates a transaction record and saves it in Firebase.

### 6. Transaction History

Users can view their previous transactions in the transaction history section. Each transaction is connected to the user’s account and stored in Firebase.

### 7. Digital ID Card

The app includes an Accounts / Digital ID screen where users can view their Montclair-style ID card.

The digital ID card displays user information such as:

- Name
- Email
- University ID
- Role
- Profile photo
- QR code

### 8. Profile Photo Upload

Users can upload a profile photo. The image is stored using Firebase Storage and displayed on the digital ID card.

### 9. QR Code Generation

The app generates a QR code linked to the user’s Firebase UID. This QR code can be used for identity verification.

### 10. QR Scanner and Verification

The app includes a Scan and Verify feature. A professor, TA, or campus staff member can scan the QR code and view the user’s verification result.

The verification result can show:

- User name
- Role
- University ID
- Email
- Verified or not verified status

### 11. Events and Offers

The app includes an Events and Offers section where users can view campus events, business promotions, student discounts, and details about offers.

This feature expands the app beyond only payments and makes it more useful for students and campus organizations.

### 12. Dark Mode

The app includes a dark mode option so users can switch the visual theme for a better viewing experience.

### 13. Logout

Users can log out of the app, which clears the active session and returns them to the login screen.

---

## Technology Used

- Kotlin
- Android Studio
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Firebase UID-based user data
- QR code generation
- QR scanner support
- NFC tap-to-pay simulation
- Git and GitHub for version control

---

## Firebase Usage

Firebase was used for the app's main backend services.

### Firebase Authentication

Used for:

- User registration
- User login
- Email verification
- Session handling

### Firestore Database

Used for storing:

- User profile information
- Wallet balances
- Transaction records
- QR/ID related information
- Events and offers data

### Firebase Storage

Used for:

- Profile photo uploads
- Displaying uploaded photos on the digital ID card

---

## Application Workflow

1. User opens the app.
2. Splash screen appears.
3. User is taken to the login screen.
4. New users can go to the registration screen.
5. User registers as a student or professor.
6. Firebase sends an email verification link.
7. User verifies email through Montclair email.
8. User returns to the app and refreshes verification status.
9. User enters the dashboard.
10. User views Red Hawk Dollars, Flex, Bonus, and Meal Swipes.
11. User uses tap-to-pay/NFC simulation.
12. App updates balance and saves transaction in Firebase.
13. User opens transaction history to view payments.
14. User opens Accounts / Digital ID screen.
15. User uploads profile photo.
16. User generates or views QR code.
17. Professor/staff scans QR code.
18. The app shows verified or unverified user results.
19. The user can view Events and Offers.
20. The user can enable dark mode or log out.

---

## Project Maintenance and Future Plans

Red Hawk Wallet will continue to be improved after the final submission. The future goal is to make the app more secure, scalable, and useful for real campus environments.

Future improvements may include:

- Better Android UI/UX improvements
- iOS version using Swift/SwiftUI
- Shared Firebase backend for Android and iOS
- Crash reporting and error tracking
- Stronger security rules
- More complete payment integration
- Stripe or Square integration
- Vendor dashboard for campus businesses
- Admin dashboard for university staff
- Improved QR verification system
- Better Events and Offers system
- Real student discount redemption
- More testing and bug fixing
- Possible white-label version for other universities

In the long term, Red Hawk Wallet could become a universal campus wallet system that other universities can use with their own branding, campus accounts, and payment options.

---

## GitHub Workflow

Recommended workflow for future development:

1. Create or use a feature branch.
2. Pull the latest code from `dev`.
3. Make changes and test locally.
4. Commit changes with a clear message.
5. Push to the feature branch.
6. Open a pull request into `dev`.
7. Test changes in `dev`.
8. Merge tested and stable code into `master`.

The `master` branch should stay stable and represent the main working version of the application.

---

## Purpose of the Project

The purpose of Red Hawk Wallet is to solve a real campus problem by combining wallet balances, student identification, QR code verification, transactions, and campus offers into a single mobile application.

This project helped the team learn real-world software development skills, including:

- Android development
- Firebase integration
- Authentication
- Firestore database structure
- File storage
- QR code systems
- NFC simulation
- UI/UX design
- GitHub collaboration
- Branch management
- Merge conflict resolution
- Testing and debugging
- Team coordination

---


## Resources and Tools Used

During the development of Red Hawk Wallet, the team used several resources and tools to support coding, debugging, writing, testing, and project organization.

### Development Tools

- Android Studio
- Kotlin
- Jetpack Compose
- Firebase Authentication
- Firebase Firestore
- Firebase Storage
- Git
- GitHub

### AI Assistance and Learning Resources

The team also used AI tools as learning and development support during the project. These tools helped with debugging errors, understanding Android/Firebase concepts, improving code structure, writing documentation, generating ideas, and polishing project explanations.

AI tools used included:

- ChatGPT
- Claude

AI tools were used as support resources, but the team was responsible for reviewing, testing, modifying, and integrating the final code into the project.
---

## Final Note

Red Hawk Wallet was challenging because it required many features to work together in one complete system. The project involved authentication, Firebase, wallet balances, NFC simulation, QR scanning, profile uploads, transactions, events, offers, and UI improvements.

Even though the team faced challenges with bugs, branches, Firebase setup, and integration, the final project became a functional campus wallet application that demonstrates the main idea clearly and can be improved further in the future.
