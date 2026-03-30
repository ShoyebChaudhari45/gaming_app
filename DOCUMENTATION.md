# 🎮 Game App - Complete Technical Documentation

**Version:** 1.0  
**Document Date:** March 25, 2026  
**Platform:** Android (Java)  
**Author:** Shoyeb Chaudhari  

This document provides a comprehensive, deep-dive technical reference for the Game App project. Built primarily using Android/Java, it features a complete set of features including User Authentication, Real-Time Wallet Management, Bidding/Lottery Systems, and a custom Security & Session overlay.

---

## 📑 Table of Contents

1. [Project Overview & Statistics](#1-project-overview--statistics)
2. [Technology Stack & Dependencies](#2-technology-stack--dependencies)
3. [Project Architecture & Folder Structure](#3-project-architecture--folder-structure)
4. [Android Manifest & Permissions](#4-android-manifest--permissions)
5. [Application Entry Point & Splash Screen](#5-application-entry-point--splash-screen)
6. [Authentication Module](#6-authentication-module)
7. [Home Dashboard & Navigation Drawer](#7-home-dashboard--navigation-drawer)
8. [Game & Bidding System](#8-game--bidding-system)
9. [Wallet System](#9-wallet-system-deposit-withdraw-statement)
10. [Profile Management](#10-profile-management)
11. [Support Module](#11-support-module)
12. [Session Management (SharedPreferences)](#12-session-management)
13. [API Layer (Retrofit + OkHttp)](#13-api-layer-retrofit--okhttp)
14. [Complete API Endpoint Reference](#14-complete-api-endpoint-reference)
15. [Data Models (Request & Response)](#15-data-models)
16. [RecyclerView Adapters](#16-recyclerview-adapters)
17. [UI/UX & XML Layouts](#17-uiux--xml-layouts)
18. [Security Implementation](#18-security-implementation)
19. [Role-Based Access](#19-role-based-access)
20. [Build Configuration & Setup](#20-build-configuration--setup)

---

## 1. Project Overview & Statistics

Game App is a full-featured Android gaming platform built in **Java** with **Retrofit** for REST API integration. It provides user authentication, real-time wallet management (deposit & withdrawal), a bidding/lottery system with multiple game types, and complete transaction history—all wrapped in a clean, Material Design-based UI.

### Key Stats:
- **19 Activity Screens**
- **9 RecyclerView Adapters**
- **22 API Endpoints**
- **30 XML Layouts**
- **10 Request Models & 22 Response Models**

---

## 2. Technology Stack & Dependencies

| Technology | Version | Purpose | Why We Used It |
| :--- | :--- | :--- | :--- |
| **Java** | 11 | Primary language | Industry standard for Android, widespread ecosystem support |
| **Android SDK** | API 23-35 | Platform | Connects ~98% of users (minSdk 23) to latest APIs (SDK 35) |
| **Retrofit 2** | 2.9.0 | HTTP Client | Type-safe REST API calls with highly readable interface configuration |
| **Gson** | 2.9.0 | JSON Parser | Seamless and fast auto-serialization between JSON and Java POJOs |
| **OkHttp** | 4.12.0 | HTTP Engine | Robust networking, connection pooling, custom timeouts (15s read/write) |
| **Glide** | 4.16.0 | Image Loader | Highly efficient caching and loading for assets like QR Codes and Games |
| **Material Components** | 1.11.0 | UI Framework | Standard elements like MaterialCardView, TextInputLayout provide modern UX |
| **SwipeRefreshLayout** | 1.1.0 | Pull-to-refresh | Standard UI pattern to trigger manual reload of Game Taps & User Details |
| **Lottie** | 6.4.0 | Animations | Beautiful vector-based animations (e.g., Success tick upon bidding) |
| **ConstraintLayout** | 2.1.4 | Layout Engine | Allows for completely flat view hierarchies to improve heavy render times |

---

## 3. Project Architecture & Folder Structure

The project employs a robust **package-by-feature/type** structure:

```text
com.example.gameapp/
├── GameApp.java                  // Application Class - Defaulting Night Mode to NO
├── activities/                   // All 19 Android Activities
│   ├── BaseActivity.java         // Role-based navigation setup
│   ├── SplashActivity.java       // Entry Point - Role routing based on Session
│   ├── LoginActivity.java        // User Login - Incorporates user details fetch with retries
│   ├── HomeActivity.java         // Main Customer interaction dashboard
│   ├── BidActivity.java          // Handles formatted bidding system input
│   ... (14 other activities)
├── api/                          
│   ├── ApiClient.java            // Retrofit singleton and OkHttp configurations
│   └── ApiService.java           // Declarative endpoints interface
├── models/
│   ├── request/                  // 10 POJOs exclusively for formatting HTTP Request Bodies
│   └── response/                 // 22 POJOs mapping exactly to backend JSON formats
├── Adapters/                     // 9 specific RecyclerView mapping logic handlers
├── session/
│   └── SessionManager.java       // Wrapper over Android SharedPreferences
└── utils/
    └── FileUtils.java            // Converts Uri to File for Multipart Body requests
```

---

## 4. Android Manifest & Permissions

### Permissions Requested
- `INTERNET`: Essential for API requests to the Durwankur backend.
- `READ_EXTERNAL_STORAGE` / `WRITE_EXTERNAL_STORAGE`: Used by `FileUtils.java` to access caching files when creating Proof uploads for Deposit/Withdraw functions.
- `VIBRATE`: Enhances UX by haptic buzzing on errors (like selecting closed game taps).

### Manifest Components
- `forceDarkAllowed="false"` - Bypasses OEM dark modes to keep layouts from breaking.
- `windowSoftInputMode="adjustPan"` (BidActivity) - Required so numerical input forms are not obscured by softer keyboards.

---

## 5. Application Entry Point & Splash Screen

**`GameApp.java` (Application Configuration):**
Fires immediately on process creation. Forces `AppCompatDelegate.MODE_NIGHT_NO` preventing fragmented visual representations depending on system settings.

**`SplashActivity.java` (App Entry):**
Implements a strict 2-second timeout followed by conditional routing:
- If `SessionManager.isLoggedIn()` is `true`: Checks user-role.
  - Customer -> Redirects to `HomeActivity`
  - Employee -> Redirects to `EmployeeHomeActivity`
- Else: Redirects to `LoginActivity`

---

## 6. Authentication Module

**`LoginActivity.java`:**
Accepts 10-digit mobile and password string. Upon 200 OK from `POST /user/login`, saves JWT Bearer. **Crucial Next Step:** Issues a follow up `GET /user/details`. If user-details fetch fails, a 3-sequence exponential backoff retry mechanism (500ms, 15s, 3s jumps) attempts fetch again. Hides exact invalidity reasons for security.

**`RegisterActivity.java`:**
Accepts Name, Mobile, Email, Pass, optional referral code, and importantly: User Type Radio checks. Connects to `POST /user`.

**`ForgotPasswordActivity.java` & `ResetPasswordActivity.java`:**
Takes an email check using `Patterns.EMAIL_ADDRESS`. Hits `POST /user/forgot-password` allowing the server to issue an OTP array sequence.

---

## 7. Home Dashboard & Navigation Drawer

**`HomeActivity.java`:**
- **Navigation Drawer Implementation:** Houses quick routing to 12 areas of the app including "Panel Access", "Share", and standard functional screens.
- **Delete Account Routine:** Implements a double-confirmation `AlertDialog`.
- **Game Tap List:** Hits `GET /taps`. Reorders array internally putting `running`/`open` top, `upcoming` middle, `closed` bottom.
- **Background Balances:** Runs `Runnable balanceRefreshRunnable` taking active polls of `GET /user/details` every 30 seconds to maintain high integrity of wallet UI synchronization.

---

## 8. Game & Bidding System

**`GameTypesActivity.java`:**
Parses `GET /games` mapped to `GameTypeAdapter` showing custom branded Game Images.

**`BidActivity.java` (Core Mechanism):**
- **Dynamic Session Targeting:** Lets users choose Open / Close phases based on statuses passed through Intent parameters.
- **Advanced TextWatchers:** Input masks. `Jodi/Cycle` formatting pairs inputs to 2 blocks; `Patte` forms 3 blocks, joined internally by an `=` character separator.
- **Duplication Tracking:** Prevents users from inputting duplicate blocks via `LinkedHashSet` tracking arrays.
- **Success Mapping:** On successful API POST, `LottieAnimationView` executes a checkmark animation with a concurrent system UI sound.

**`BidHistoryActivity.java`:**
Exposes a UI for `DatePickerDialog` connecting dynamic `start_date` and `end_date` parameters into the Retrofit Query.

---

## 9. Wallet System (Deposit, Withdraw, Statement)

**`AddPointsActivity.java` (Deposit):**
1. Uses `Glide` to load the Administrative QR Code dynamically provided during earlier startup handshakes.
2. Contacts `GET /prices` populating quick-select chip grids mapping straight to the manual amount entry form.
3. Submits `MultipartBody.Part` of images wrapped with the manual Amount value via `POST /wallet/deposit`.

**`WithdrawActivity.java`:**
Hard verification: amount >= ₹100; amount <= current_balance. Uses same file uploading approach for bank proof verification as Deposit.

**`WalletStatementActivity.java`:**
Translates `GET /wallet/statement` list structure into visual Recycler adapters mapping out Approved, Rejected and Pending color badges relative to individual row statuses.

---

## 10. Profile Management

Handles rendering and updates in `ProfileActivity.java`. Fields are locked statically until the "Edit Mode" toggle releases edit-states for Name/Email columns mapping a custom UI change reflecting an action requirement. Upon validation, pushes the model by `POST /profile`.

---

## 11. Support Module

The `SupportActivity.java` screen dynamically generates communication channels. A Call, WhatsApp, Actionable URI intent format exists for each potential platform derived directly from boolean checks provided by a specific `GET /support` call. If parameters for one service omit, the UI view group for that service sets to `GONE`, preventing broken intents from occurring.

---

## 12. Session Management

Implemented through `SessionManager.java`. Acts as a global Singleton static modifier accessing standard Android `SharedPreferences`. Stores states such as:
- Auth JWT Token (`token`)
- Account Balances (`balance` ~ Uses string encapsulation guarding against generic primitive errors)
- Customer vs Employee booleans
- Quick access info like Name, Mobile, Emails ensuring screens don't make hyper-redundant fetch routines.

---

## 13. API Layer (Retrofit + OkHttp)

**`ApiClient.java` Configuration:**
Configures `new OkHttpClient.Builder()` with standardized 15-second connectivity definitions mapping to a central base URL `https://lottery.durwankurgroup.com/api/`. `GsonConverterFactory` hooks are enabled allowing direct POJO JSON translation.

**`ApiService.java` Structure:**
Every request contains the `@Header("Authorization") String token` architecture binding local authentication securely straight onto endpoint routes avoiding interceptors configuration bulk.

---

## 14. Complete API Endpoint Reference

| HTTP Call | Endpoint | Body Requirements | Security | Action |
| --- | --- | --- | --- | --- |
| `POST` | `/user` | `RegisterRequest` | Open | Create Account |
| `POST` | `/user/login` | `LoginRequest` | Open | JWT Generation |
| `GET` | `/user/details` | N/A | Bearer | Fetch details & Balance |
| `POST` | `/user/change-password` | `ChangePasswordRequest`| Bearer | Settings Change |
| `POST` | `/user/forgot-password` | `ForgotPasswordRequest`| Open | Initialize OTP |
| `POST` | `/user/reset-password` | `ResetPasswordRequest` | Open | Complete password change|
| `GET` | `/taps` | N/A | Bearer | List valid games phases|
| `GET` | `/games` | N/A | Bearer | Core Game Categories |
| `POST` | `/lottery` | `LotteryRateRequest` | Bearer | Place Bidding Call |
| `GET` | `/lottery/list` | Date Queries | Bearer | Detailed Ticket History |
| `POST` | `/wallet/deposit` | Multipart File + Type | Bearer | Submit Transfer Proofs|
| `POST` | `/wallet/withdraw` | Multipart File + Type | Bearer | Request Return Balances |
| `DELETE` | `/employee` | N/A | Bearer | Fully removes users |

*(Note: There are 22 total endpoints. The above is a summary of the core transaction flows).*

---

## 15. Data Models

To ensure strict adherence to JSON parameters and memory-safety, explicit Data Models were created:

- **10 Request Models:** Objects constructed before Retrofit fires a `.enqueue()`. Designed carefully matching backend object keys.
- **22 Response Models:** Automatically mapped and populated by Gson Factory. Examples like `WalletStatementResponse` provide list data directly available to Map structures.

---

## 16. RecyclerView Adapters

**9 Discrete Adapters** act as delegates rendering internal Array data onto XML lists:
- `GameTapAdapter`: Connects game phase models handling intricate visual logic denoting open and closed game states relative to time.
- `BidHistoryAdapter`: Provides visual tracking lines defining tickets.
- `WalletStatementAdapter`: Structures deposit logic arrays.
- `StarlineRateAdapter`: Dedicated structure matching unique JSON architectures differing from core games.

---

## 17. UI/UX & XML Layouts

The application runs using purely native XML structures using heavy `MaterialComponent` logic:
- Designed natively using XML providing flat hierarchal benefits (ConstraintLayout usage).
- `95+ XML Custom Drawables` providing buttons, stroke arrays, generic badges styling natively rendering extremely fast vector-images.
- Complete responsive padding elements matching devices ranging up to tablet format without stretching artifacts.

---

## 18. Security Implementation

- **JSON Web Tokens:** 100% of data points require an encrypted sequence payload in headers preventing snooping or illegal manual backend alterations without verification.
- **Input Strictness:** Prevents any incorrect input array sizes or irregular character encoding matching strictly to specific Regex parameters during string generation.
- **Application Routing Strictness:** Overrides default activity backstack queues when altering sensitive data sequences, ensuring Users cannot press "Back" accessing forms after the transaction completes or clearing a layout.

---

## 19. Role-Based Access

Operates exclusively through `BaseActivity` methods `checkUserTypeAccess()` and `enforceUserTypeAccess()`.
- Customer Access opens access explicitly to `HomeActivity` endpoints.
- Employee Access changes the core intent routing structure pointing to `EmployeeHomeActivity` while modifying specific sub-routines and adapter arrays rendered.

---

## 20. Build Configuration & Setup

### Requirements:
- Android Studio Arctic Fox (or later)
- JDK 11 environment mappings attached.

### Source Control Build Configurations:
```gradle
android {
    namespace "com.example.gameapp"
    compileSdk 35
    defaultConfig {
        minSdk 23
        targetSdk 34
    }
}
```

### ProGuard Specifications:
The build defaults to `minifyEnabled false`. It uses `proguard-rules.pro` mappings. Ensure standard Retrofit/Gson exemptions apply to `proguard-rules` if obfuscation gets explicitly turned back to True in production variants.
