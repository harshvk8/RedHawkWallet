#  Test Cases - RedHawk Wallet

---

##  Registration Tests

### 1. Register with Empty Full Name
- **Input:** Full name left blank  
- **Expected Result:** Registration is not allowed  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/b0dce9ec-c70d-455f-87be-8dd61cda6ad3" />

### 2. Register with Empty University ID
- **Input:** University ID left blank  
- **Expected Result:** Registration is not allowed  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/b9ca4ba2-ea82-4963-8ab4-38e5dfb8b76f" />

### 3. Register with Short Password
- **Input:** Password below required length  
- **Expected Result:** Error message indicating password is too short  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/20dd2651-fb0d-4be9-b288-40e6eacf4340" />

### 4. Register with Mismatched Passwords
- **Input:** Password and confirm password do not match  
- **Expected Result:** Error message is displayed  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/a0757f25-9418-45c3-86ae-997ba2dc56a6" />

---

##  Login Tests

### 5. Login with Invalid Email
- **Input:** Invalid or non-existing email  
- **Expected Result:** Login fails with an error message  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/f58aa035-7173-412d-a34a-015775c0ab29" />

### 6. Login Requires Email and Password
- **Action:** Attempt login with empty email and password  
- **Expected Result:** Login is blocked and error message is displayed  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/3c332910-228d-4a89-8196-8481f9fecdaa" />

---

##  Dashboard Tests

### 7. Dashboard Loads Wallet Balance
- **Action:** User logs in  
- **Expected Result:** Correct wallet balance is displayed  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/9407dfb3-4874-4442-a9ed-cd9f720ed8c3" />

### 8. Bonus Tab Displays Correct Balance
- **Action:** Navigate to Bonus tab  
- **Expected Result:** Bonus balance is accurate  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/968ef863-21de-400f-89db-c736c6e534df" />

---

##  NFC Payment Tests

### 9. NFC Payment Deducts $5
- **Action:** Perform NFC payment  
- **Expected Result:** $5 is deducted from Red Hawk Dollars balance and updated correctly  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/0d697366-c770-478b-b90e-f8ee18306bda" />

---

##  Account & QR Tests

### 10. User Photo Loads Correctly
- **Action:** Open profile screen after login  
- **Expected Result:** Profile photo displays correctly  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/f73dc124-e10e-44c3-9315-a16c1404e531" />

### 11. Uploaded Photo is Saved to Firebase Storage
- **Action:** Upload a profile photo  
- **Expected Result:** Photo is successfully saved in Firebase Storage  

### 12. Show Account QR Code Opens QR View
- **Action:** Tap "Show Account QR Code"  
- **Expected Result:** QR code screen opens and displays correctly  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/9e1d479c-777a-4fa9-9c17-20b383719ea0" />

### 13. Dark Mode Toggle Changes Theme
- **Action:** Toggle dark mode  
- **Expected Result:** App switches between light and dark mode  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/c64cf4c5-6dfb-4e3c-9300-6b458689f5a0" />

### 14. Logout Button Returns User to Login Screen
- **Action:** Tap logout  
- **Expected Result:** User is logged out and redirected to login  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/db3de3df-7a8f-4515-8094-c2755b626616" />

### 15. Scanner Verifies Professor Account Correctly
- **Action:** Scan professor QR code  
- **Expected Result:** Professor account is verified  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/4d64391c-b9c5-439c-bfcb-1edd7b008611" />

### 16. Events and Offers Button Opens Correct Screen
- **Action:** Tap "Events and Offers"  
- **Expected Result:** Events and Offers screen opens correctly  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/6af27dff-43cd-4a24-8050-8e4f527309bb" />

### 17. Scanner Verifies Student Account Correctly
- **Action:** Scan student QR code  
- **Expected Result:** Student account is verified  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/c0e6878d-ae81-4e65-a85f-6167f886cab2" />

### 18. Scanner Does Not Verify Professor Account
- **Action:** Scan professor QR code  
- **Expected Result:** System denies verification and blocks access  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/c2d246a6-1888-4732-8ea7-afb8ba7cf670" />

### 19. Zero Meal Swipes Left
- **Action:** Attempt to use meal swipe with 0 balance  
- **Expected Result:** Transaction is denied with error message  
- <img width="200" height="400" src="https://github.com/user-attachments/assets/0d19be49-6e47-433b-844e-bf405825eb03" />

---
