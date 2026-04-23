# Test Cases- RedHawk Wallet 

##  Registration Tests

### 1. Register with Empty Full Name
- **Input:** Full name left blank
- **Expected Result:** Doesn't allow to register 
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-19-32-58_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/b0dce9ec-c70d-455f-87be-8dd61cda6ad3" />


### 2. Register with Empty University ID
- **Input:** University ID left blank
- **Expected Result:** Doesn't allow to register 
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-19-08-93_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/b9ca4ba2-ea82-4963-8ab4-38e5dfb8b76f" />


### 3. Register with Short Password
- **Input:** Password less than required length
- **Expected Result:** Error message indicating password is too short
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-18-27-52_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/20dd2651-fb0d-4be9-b288-40e6eacf4340" />


### 4. Register with Mismatched Passwords
- **Input:** Password and confirm password do not match
- **Expected Result:** Error message displayed
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-20-33-50_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/a0757f25-9418-45c3-86ae-997ba2dc56a6" />

---

##  Login Tests

### 5. Login with Invalid Email
- **Input:** Incorrect email format or non-existing email
- **Expected Result:** Login fails with error message
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-19-58-03_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/f58aa035-7173-412d-a34a-015775c0ab29" />

---

## Dashboard Tests

### 7. Dashboard Loads Wallet Balance
- **Action:** User logs in successfully
- **Expected Result:** Correct wallet balance is displayed
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-26-14-20_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/9407dfb3-4874-4442-a9ed-cd9f720ed8c3" />


### 8. Bonus Tab Displays Correct Balance
- **Action:** Navigate to Bonus tab
- **Expected Result:** Bonus balance is accurate
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-28-57-01_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/968ef863-21de-400f-89db-c736c6e534df" />

---

## NFC Payment Test

### 9. NFC Payment Deducts $5
- **Action:** Perform NFC payment
- **Expected Result:** $5 is deducted from Red Hawk Dollars balance
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-30-10-03_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/0d697366-c770-478b-b90e-f8ee18306bda" />

---

## Account/ QR 

### 10. User Photo Loads Correctly
- **Action:** Open the profile/account screen after logging in  
- **Expected Result:** The user's profile photo is displayed correctly on the screen
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-41-32-52_76ade46a476e1b32f6086ac784584065" src="https://github.com/user-attachments/assets/f73dc124-e10e-44c3-9315-a16c1404e531" />

### 11. Show Account QR Code Button Opens QR View
- **Action:** Tap the "Show Account QR Code" button  
- **Expected Result:** The QR code view opens and displays the user's account QR code  
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-34-08-34_76ade46a476e1b32f6086ac784584065" src="https://github.com/user-attachments/assets/9e1d479c-777a-4fa9-9c17-20b383719ea0" />


### 12. Dark Mode Toggle Changes Theme
- **Action:** Toggle dark mode on or off  
- **Expected Result:** The app theme changes accordingly between light mode and dark mode
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-32-28-02_76ade46a476e1b32f6086ac784584065" src="https://github.com/user-attachments/assets/c64cf4c5-6dfb-4e3c-9300-6b458689f5a0" />
### 14. Logout Button Returns User to Login Screen
- **Action:** Tap the logout button  
- **Expected Result:** The user is logged out and returned to the login screen
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-32-01-25_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/db3de3df-7a8f-4515-8094-c2755b626616" />

### 15. Scanner Verifies Professor Account Correctly
- **Action:** Scan a professor’s account QR code using the app scanner  
- **Expected Result:** The system correctly recognizes and verifies the professor’s account
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-43-27-51_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/4d64391c-b9c5-439c-bfcb-1edd7b008611" />

### 16. Events and Offers Button Opens Correct Screen
- **Action:** Tap the "Events and Offers" button  
- **Expected Result:** The Events and Offers screen opens correctly and displays relevant content  
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-42-20-10_76ade46a476e1b32f6086ac784584065" src="https://github.com/user-attachments/assets/6af27dff-43cd-4a24-8050-8e4f527309bb" />
