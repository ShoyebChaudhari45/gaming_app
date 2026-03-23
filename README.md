🎮 Game App (Android)

A full-featured Android gaming application built using Java and Retrofit, featuring user authentication, wallet management (deposit & withdraw), bidding system, game rates, and transaction history with a clean Material UI.

🚀 Key Features
👤 User Management

User Registration & Login

Forgot / Reset Password
  
Change Password

Profile Management

Delete Account (with double confirmation)

Session handling using SharedPreferences

🎯 Game & Bidding

Game Types listing

Game Rates display

Place Bids

Bid History

Win History

💰 Wallet System

Add Points (Deposit)

Withdraw Funds (UPI / Bank / Wallet)

Wallet Balance tracking

Wallet Statement (Deposit & Withdraw history)

📊 Others

Splash Screen

Home Dashboard

Support Screen

Double-tap back to exit

Secure API calls with Bearer Token

🛠 Tech Stack
Technology	Purpose
Java	Main programming language
Android SDK	Application platform
Retrofit	REST API integration
Gson	JSON parsing
RecyclerView	Lists & history screens
Material Components	Modern UI
Glide	Image loading
SharedPreferences	Session & balance storage
📁 Project Folder Structure
📡 API Documentation
🔐 Authentication
Endpoint	Method	Description
/user/login	POST	User Login
/user	POST	Register User
/user/details	GET	Fetch User Profile
/user/change-password	POST	Change Password
/employee	DELETE	Delete (soft-delete) User Account
💰 Wallet APIs
Endpoint	Method	Description
/wallet/deposit	POST	Deposit money
/wallet/withdraw	POST	Withdraw money
/wallet/statement	GET	Wallet transaction history
🎮 Game APIs
Endpoint	Method	Description
/games	GET	Game list
/prices	GET	Game rates
/taps	GET	Game taps

🔐 All secured APIs require
Authorization: Bearer <TOKEN>

🧪 Example API Request
Deposit
{
  "price": 500
}

Withdraw
{
  "price": 200,
  "payment_mode": "upi"
}

🏗 Setup Instructions

Clone repository

git clone https://github.com/ShoyebChaudhari45/gaming_app.git


Open in Android Studio

Verify Base URL in ApiClient.java

Run on emulator or physical device

📱 Play Store–Style App Description
🎮 Game App – Play, Bid & Win

Experience an exciting gaming platform with real-time bidding, secure wallet transactions, and smooth gameplay.

✔ Easy login & registration
✔ Add & withdraw money securely
✔ Multiple game types & rates
✔ Track wallet history in real time
✔ Delete account option with safety confirmation
✔ Fast, safe & user-friendly design

Play smarter. Bid faster. Win bigger.

🔐 Security Notes

Token-based authentication

Secure session storage

API-level validation

Client-side input checks

🤝 Contribution

Pull requests are welcome.
For major changes, please open an issue first.

📄 License

This project is currently unlicensed.
You may add MIT / Apache 2.0 as needed.

👨‍💻 Author

Shoyeb Chaudhari
Android Developer
GitHub: @ShoyebChaudhari45
