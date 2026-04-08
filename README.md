<h1 align="center">🎮 Game App (Android)</h1>

<p align="center">
  <strong>A full-featured Android gaming platform built with Java, Retrofit, and Material Design.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Language-Java-007396?style=flat-square&logo=java" alt="Language: Java" />
  <img src="https://img.shields.io/badge/API-Retrofit-1266B3?style=flat-square" alt="API: Retrofit" />
  <img src="https://img.shields.io/badge/Architecture-MVC-FF9800?style=flat-square" alt="Architecture: MVC" />
  <img src="https://img.shields.io/badge/Status-Production_Ready-success?style=flat-square" alt="Status" />
</p>

---

## 📖 Complete Documentation

A fully comprehensive technical reference covering endpoints, XML hierarchies, classes, and logic gates has been generated for this repository.
* **Developer Markdown:** [DOCUMENTATION.md](DOCUMENTATION.md)
* **Design/PDF Exportable HTML:** `documentation.html` (Open in any modern browser)

---

## 🎯 Project Overview

**Game App** is a fast, responsive, tightly integrated Android application built specifically to interface with a remote backend environment (`lottery.durwankurgroup.com`).

The application facilitates a multi-role user environment (Customer and Employee) through rigorous JWT Authentication mechanisms natively managed by a robust local `SessionManager`. 

### ✨ Core Features
- **Role-Based Access Control System (RBAC):** Automatic routing and UI adjustments distinguishing Customers from internal Employees.
- **Deep Authentication Suite:** Includes Login, Register, Forgot Password (OTP Verification), Reset Password, Account Update, and Soft Deletion.
- **Wallet & Economy Module:**
  - Automated Balance Fetches via 30-Second API background polling.
  - Wallet Deposits using dynamic Administrative QR loading and Multi-Part `File` proofs.
  - Withdrawals supported natively with transaction history tracing arrays.
- **Advanced Dynamic Bidding Flow:** Input formatting systems prevent double-entry and block specific patterns relative to generic (Jodi, Patte, Open, Close) phase logics natively before issuing POST intents.
- **Security Checksums:** Enforces strictly locked API handshakes, un-obfuscated error handling (anti-brute force), and restricted layout-reentry.
- **Universal Support Aggregation:** Provides an integrated gateway passing intent triggers to WhatsApp, Email, Telegram, Phone dialers, and external Browsers dynamically based on valid server-side inputs.

---

## 🛠 Tech Stack

* **Language:** Java 11
* **Minimum SDK:** API 23 (Android 6.0 Marshmallow)
* **Target SDK:** API 34 (Android 14)
* **Networking:** Retrofit 2 + Gson + OkHttp3
* **Image Loading:** Glide
* **Animations:** Lottie Vectors
* **UI Components:** AndroidX, Material Components 1.11, SwipeRefreshLayout

---

## ⚙️ Installation & Setup

1. **Clone the repository:**
   ```bash
   git clone https://github.com/ShoyebChaudhari45/gaming_app.git
   ```
2. **Open the project** in Android Studio (Arctic Fox or newer recommended).
3. Let Gradle sync naturally to resolve `implementation` packages.
4. Verify your remote proxy route inside `ApiClient.java`:
   ```java
   public static final String BASE_URL = "https://lottery.durwankurgroup.com/api/";
   ```
5. Build and Run on any emulator/device running **API 23+**.

---

<p align="center">
  <i>Developed and Maintained by Shoyeb Chaudhari</i>
</p>


developer  Shoyeb Chaudhari.
