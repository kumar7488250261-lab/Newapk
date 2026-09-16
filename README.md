# Kharsia Lobby (SECR Bilaspur Division)
### संयुक्त चालक एवं परिचालक लॉबी, खरसिया — दक्षिण पूर्व मध्य रेलवे

Official digital management system and operational staff portal for the **Combined Crew & Train Manager Lobby, Kharsia (KHS)** under South East Central Railway (Bilaspur Division).

This repository serves as a single unified source maintaining both:
1. **Android Application**: 100% native Jetpack Compose, Material 3, offline-first app.
2. **Responsive Web Application & PWA**: Mobile-first, desktop-ready, installable Progressive Web App published automatically to **GitHub Pages**.

---

## 📁 Project Architecture

```text
├── app/                              # Android Native Application
│   ├── src/main/java/com/example/    # Kotlin Jetpack Compose Code
│   │   ├── data/                     # Data Models & Repository (Staff Directory)
│   │   ├── ui/screens/               # Compose Screens (Landing, Login, Menu, Directory, Detail)
│   │   └── ui/theme/                 # M3 Color Schemes & Typography
│   ├── src/main/res/                 # Drawables, Logos & Vector Resources
│   └── src/main/assets/              # Local staff_directory.json (14 lobbies, 1,200+ staff)
│
├── web/                              # Responsive Web Application (PWA)
│   ├── index.html                    # Mobile-first Web HTML (Hindi + English)
│   ├── style.css                     # Railway Dark Theme & Responsive Stylesheet
│   ├── app.js                        # Client-side Logic, Search, Actions & Local Auth
│   ├── manifest.json                 # PWA Manifest (Add to Home Screen)
│   ├── service-worker.js             # Offline Caching & Stale-while-revalidate Engine
│   └── assets/                       # Self-contained Assets (Logo, Images, JSON data)
│       ├── ic_kharsia_logo.png
│       ├── img_kharsia_lobby_main.png
│       ├── staff_directory.json
│       ├── icon-192.png
│       ├── icon-512.png
│       └── favicon.png
│
├── .github/
│   └── workflows/
│       ├── deploy-web.yml            # Automated GitHub Pages Deployment Workflow
│       └── build-apk.yml             # Automated Debug APK Build & Release Workflow
│
└── README.md                         # Documentation & Setup Guide
```

---

## 🌐 Web Version & GitHub Pages Setup

The `/web/` folder is designed to run statically without server-side dependencies and works on **both desktop and mobile browsers** (Android Chrome, iOS Safari, etc.).

### 1. Enable GitHub Pages in your Repository
To publish the web version to GitHub Pages:
1. Open your repository on GitHub.
2. Click on **Settings** (top menu).
3. In the left sidebar, click **Pages** (under "Code and automation").
4. Under **Build and deployment** -> **Source**, choose **GitHub Actions** (NOT "Deploy from a branch").
5. Save changes.

### 2. Expected Website URL
Once deployed, your application will be live at:
```
https://<USERNAME>.github.io/<REPOSITORY>/
```
*Example*: If your username is `railway-admin` and repo name is `kharsia-lobby`, the URL is:
```
https://railway-admin.github.io/kharsia-lobby/
```
> **Note**: All URLs, stylesheets, scripts, images, and service worker scopes in `/web/` use relative paths (`./`), ensuring seamless loading from any repository subpath without broken links.

### 3. Progressive Web App (PWA) Features
- **Add to Home Screen**: When opened in Android Chrome, users can tap *"Install app"* or *"Add to Home screen"*.
- **Offline Support**: The application caches static resources and staff contacts, allowing browsing even with low or no connectivity.
- **Fast Loading**: Instant launch via client-side cache and responsive layout.

---

## 📱 Android APK Generation (GitHub Actions)

You do **not** need Android Studio installed locally to build the APK. Every push to the `main` branch builds a clean debug APK.

### Steps to Download the APK:
1. Go to the **Actions** tab in your GitHub repository.
2. Select the workflow **"Build and Publish Debug APK"** from the left list.
3. Click on the latest workflow run.
4. Scroll down to the **Artifacts** section at the bottom of the page.
5. Click **`app-debug-apk`** to download the ZIP file containing `app-debug.apk`.
6. Alternatively, check the **Releases** section on the right side of the repository main page for downloadable APK releases.

---

## 🚀 How to Trigger Deployments

Both workflows are triggered automatically on commit to `main`:
- Push changes to `web/**` → Triggers `deploy-web.yml` (GitHub Pages).
- Push changes to `app/**` or root → Triggers `build-apk.yml` (Android APK).

You can also trigger them manually at any time:
1. Go to **Actions** tab on GitHub.
2. Click **Deploy Web App to GitHub Pages** or **Build & Publish Debug APK**.
3. Click **Run workflow** -> Select `main` branch -> Click **Run workflow**.

---

## 🔐 Credentials & Authentication

- **User ID**: Starts with `KHS` followed by exactly 4 digits (e.g., `KHS1234`, `KHS2026`).
- **Default Password**: `1234`
- **Session Management**: Both Android and Web apps maintain local session state for authenticated railway staff.
