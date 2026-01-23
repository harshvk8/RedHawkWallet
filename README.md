


Do passwords in ONE place only: the Android app
Website: Will work on it when we complete the App
1) register interest/email + role
2) download APK
3) ❌ no passwords

App:
1) signup/login (Firebase Auth)
2) wallet + NFC simulation
3) The password needs to be saved in Hashed version


First “hard part” checklist (do this in order)

Create Firebase project + add Android app + google-services.json

Enable Firestore

Create collections:

users

transactions

Build screens (UI first with fake data)

Connect UI → Firestore (load wallet + show transactions)

Add NFC tap simulation → create transaction in Firestore

Build APK → test on 2 phones → demo ready
