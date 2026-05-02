#  Test Cases - RedHawk Wallet

---

##  Registration Tests

### 1. Register with Empty Full Name
- **Action:** Leave Full Name field blank and attempt to register
- **Input:** Full Name left blank, all other fields filled correctly
- **Expected Result:** Registration is not allowed — inline error shown under Full Name field
<img width="200" height="400" src="https://github.com/user-attachments/assets/b0dce9ec-c70d-455f-87be-8dd61cda6ad3" />

---

### 2. Register with Empty University ID
- **Action:** Leave University ID field blank and attempt to register
- **Input:** University ID left blank, all other fields filled correctly
- **Expected Result:** Registration is not allowed — error shown under University ID field
<img width="200" height="400" src="https://github.com/user-attachments/assets/b9ca4ba2-ea82-4963-8ab4-38e5dfb8b76f" />

---

### 3. Register with Short Password
- **Action:** Enter a password below the required minimum length
- **Input:** Password shorter than required (e.g. `123`)
- **Expected Result:** Error message indicating password is too short
<img width="200" height="400" src="https://github.com/user-attachments/assets/20dd2651-fb0d-4be9-b288-40e6eacf4340" />

---

### 4. Register with Mismatched Passwords
- **Action:** Enter different values in Password and Confirm Password fields
- **Input:** Password = `Password123`, Confirm Password = `Password456`
- **Expected Result:** Error message is displayed — "Passwords do not match"
<img width="200" height="400" src="https://github.com/user-attachments/assets/a0757f25-9418-45c3-86ae-997ba2dc56a6" />

---

### 5. Successful New Account Registration
- **Action:** Fill in all fields correctly and tap Register
- **Input:** Valid Full Name, University ID, @montclair.edu email, matching passwords
- **Expected Result:** Account created in Firebase, verification email sent, user redirected to Email Verification screen

---

##  Login Tests

### 6. Login with Invalid Email
- **Action:** Enter an email that is not registered and attempt to login
- **Input:** Invalid or non-existing email (e.g. `notauser@montclair.edu`)
- **Expected Result:** Login fails — error message displayed, user stays on Login screen
<img width="200" height="400" src="https://github.com/user-attachments/assets/f58aa035-7173-412d-a34a-015775c0ab29" />

---

### 7. Login with Empty Email and Password
- **Action:** Attempt login with both fields left empty
- **Input:** Email and Password both blank
- **Expected Result:** Login is blocked — validation errors shown under both fields
<img width="200" height="400" src="https://github.com/user-attachments/assets/3c332910-228d-4a89-8196-8481f9fecdaa" />

---

### 8. Login with Correct Credentials
- **Action:** Enter valid email and password for a verified account and tap Login
- **Input:** Registered and verified email + correct password
- **Expected Result:** User is authenticated and redirected to the Dashboard screen

---

##  Email Verification Tests

### 9. Login Before Verification Redirects to Verification Screen
- **Action:** Log in with a newly registered but unverified account
- **Input:** Valid credentials for an account whose email has not been verified yet
- **Expected Result:** User is redirected to the Email Verification screen — Dashboard is blocked

---

### 10. I Have Verified Button Grants Dashboard Access
- **Action:** After clicking the verification link in email, return to app and tap "I Have Verified"
- **Input:** User has already clicked the Firebase verification link in their inbox
- **Expected Result:** App re-checks Firebase status, confirms verified, redirects to Dashboard

---

##  Dashboard Tests

### 11. Dashboard Loads Wallet Balance
- **Action:** User logs in with a verified account
- **Input:** Valid credentials — Firestore has pre-set balance values
- **Expected Result:** Correct wallet balance is displayed for all four accounts (Red Hawk Dollars, Flex, Bonus, Meal Swipes)
<img width="200" height="400" src="https://github.com/user-attachments/assets/9407dfb3-4874-4442-a9ed-cd9f720ed8c3" />

---

### 12. Clicking a Balance Card Shows Active State
- **Action:** Tap on any balance card on the Dashboard
- **Input:** Tap Red Hawk Dollars card, then tap Flex card
- **Expected Result:** Tapped card expands showing full balance and "ACTIVE" label — tap-to-pay instructions update accordingly

---

### 13. Bonus Tab Displays Correct Balance
- **Action:** Tap the Bonus card on the Dashboard
- **Input:** Bonus card tapped
- **Expected Result:** Bonus balance is accurate and matches Firestore value
<img width="200" height="400" src="https://github.com/user-attachments/assets/968ef863-21de-400f-89db-c736c6e534df" />

---

##  NFC Payment Tests

### 14. NFC Payment Deducts $5
- **Action:** Perform NFC tap-to-pay with Red Hawk Dollars card active
- **Input:** Device tapped against NFC tag — Red Hawk Dollars active, balance ≥ $5
- **Expected Result:** $5 is deducted from Red Hawk Dollars balance and updated correctly in Firestore
<img width="200" height="400" src="https://github.com/user-attachments/assets/0d697366-c770-478b-b90e-f8ee18306bda" />

---

### 15. NFC Payment Deducts 1 Meal Swipe
- **Action:** Perform NFC tap-to-pay with Meal Swipes card active
- **Input:** Device tapped against NFC tag — Meal Swipes active, balance ≥ 1
- **Expected Result:** Meal Swipes count decreases by 1 and transaction is logged in Firestore

---

### 16. Zero Meal Swipes Left — Transaction Denied
- **Action:** Attempt to use meal swipe with 0 balance
- **Input:** Meal Swipes card active, balance = 0, device tapped against NFC tag
- **Expected Result:** Transaction is denied — error message displayed, balance stays at 0
<img width="200" height="400" src="https://github.com/user-attachments/assets/0d19be49-6e47-433b-844e-bf405825eb03" />

---

### 17. Transaction List Loads Correctly
- **Action:** Open the transaction history screen
- **Input:** Tap the Transactions arrow on Dashboard — account has prior transactions
- **Expected Result:** Transaction list displays correctly with all recent transactions shown (amount, status, token ID)
<img width="200" height="400" src="https://github.com/user-attachments/assets/41b85e02-05ce-43bb-a2ce-9c8b8304438b" />

---

##  Account & QR Tests

### 18. User Photo Loads Correctly
- **Action:** Open Account Services screen after login
- **Input:** User has a previously uploaded profile photo stored in Firebase Storage
- **Expected Result:** Profile photo displays correctly on the digital ID card
<img width="200" height="400" src="https://github.com/user-attachments/assets/f73dc124-e10e-44c3-9315-a16c1404e531" />

---

### 19. Uploaded Photo is Saved to Firebase Storage
- **Action:** Upload a new profile photo from the device gallery
- **Input:** Tap "Upload Photo" → select image from gallery → confirm
- **Expected Result:** Photo is successfully saved in Firebase Storage under `profile_photos/` and appears on the ID card

---

### 20. Show Account QR Code Opens QR View
- **Action:** Tap "Show Account QR Code" button
- **Input:** Button tapped on Account Services screen
- **Expected Result:** QR code screen opens — scannable QR linked to Firebase UID displayed — Refresh QR and Back buttons visible
<img width="200" height="400" src="https://github.com/user-attachments/assets/9e1d479c-777a-4fa9-9c17-20b383719ea0" />

---

### 21. Dark Mode Toggle Changes Theme
- **Action:** Toggle the Dark Mode switch on Account Services screen
- **Input:** Toggle switched ON, then OFF
- **Expected Result:** App switches between light and dark mode — preference persists across the session
<img width="200" height="400" src="https://github.com/user-attachments/assets/c64cf4c5-6dfb-4e3c-9300-6b458689f5a0" />

---

### 22. Logout Button Returns User to Login Screen
- **Action:** Tap the Logout button on Account Services screen
- **Input:** Logout button tapped while user is authenticated
- **Expected Result:** Active Firebase session is cleared — user redirected to Login screen — Back button does not return to the app
<img width="200" height="400" src="https://github.com/user-attachments/assets/db3de3df-7a8f-4515-8094-c2755b626616" />

---

### 23. Scan and Verify Button Opens Scanner
- **Action:** Tap the "Scan and Verify" button
- **Input:** Button tapped on Account Services screen
- **Expected Result:** Scanner screen opens successfully — camera activates — red scan frame visible — ready to scan QR codes
<img width="200" height="400" src="https://github.com/user-attachments/assets/c656977a-0807-4d22-b0a5-a51b16dd475c" />

---

### 24. Scanner Verifies Student Account Correctly
- **Action:** Scan a registered student's QR code
- **Input:** Valid student QR code displayed on another device
- **Expected Result:** Green "Verified User" result shown — Name, Role: Student, University ID, "QR belongs to a registered university user"
<img width="200" height="400" src="https://github.com/user-attachments/assets/c0e6878d-ae81-4e65-a85f-6167f886cab2" />

---

### 25. Scanner Verifies Professor Account Correctly
- **Action:** Scan a registered professor's QR code
- **Input:** Valid professor QR code displayed on another device
- **Expected Result:** Green "Verified User" result shown — Name, Role: Professor, University ID
<img width="200" height="400" src="https://github.com/user-attachments/assets/4d64391c-b9c5-439c-bfcb-1edd7b008611" />

---

### 26. Scanner Does Not Verify Invalid QR Code
- **Action:** Scan a QR code that is not from the Red Hawk Wallet system
- **Input:** Random or non-Montclair QR code (e.g. a website QR)
- **Expected Result:** Red "Invalid QR" shown — "This is not a valid Montclair QR code" — no user data displayed
<img width="200" height="400" src="https://github.com/user-attachments/assets/c2d246a6-1888-4732-8ea7-afb8ba7cf670" />

---

##  Events & Offers Tests

### 27. Events and Offers Button Opens Correct Screen
- **Action:** Tap "Events and Offers" button on Account Services screen
- **Input:** Button tapped while on Account Services
- **Expected Result:** Events and Offers screen opens — Events and Offers tabs both visible — content loads
<img width="200" height="400" src="https://github.com/user-attachments/assets/6af27dff-43cd-4a24-8050-8e4f527309bb" />

---

### 28. Offer Redemption Dialog Appears on Offer Tap
- **Action:** Tap on an available offer card inside the Offers tab
- **Input:** Offers tab selected — tap on an offer (e.g. The Halal Shack)
- **Expected Result:** "Confirm Redemption" dialog appears showing item name, venue, price, and "Confirm & Pay" button — Cancel dismisses without charging
<img width="916" height="2048" alt="WhatsApp Image 2026-05-02 at 00 17 04 (9)" src="https://github.com/user-attachments/assets/1584d617-4197-4729-9246-f8e82f5d5425" />

---
