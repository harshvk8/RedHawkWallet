# Test Cases- Lisandra Nina Rosa 

##  Registration Tests

### 1. Register with Empty Full Name
- **Input:** Full name left blank
- **Expected Result:** Error message displayed, registration blocked
- <img width="200" height="400" alt="Screenshot_2026-04-23-08-19-32-58_76ade46a476e1b32f6086ac784584065 (1)" src="https://github.com/user-attachments/assets/b0dce9ec-c70d-455f-87be-8dd61cda6ad3" />


### 2. Register with Empty University ID
- **Input:** University ID left blank
- **Expected Result:** Error message displayed
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
