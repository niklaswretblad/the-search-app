
# Get Started Guide

This project consists of "The Search", a surf spot tool application. It consists of an Android mobile application and a Python Flask backend. Follow the instructions below to download, build, and run both the Android app and the backend.

---

## Prerequisites

Ensure you have the following tools installed on your system before proceeding:

1. **Android Development**:
   - [Android Studio](https://developer.android.com/studio)
   - Java Development Kit (JDK)
   - Android SDK (API Level 29 or higher)

   **How to Check the Android SDK Version:**
   - Open Android Studio.
   - Go to **File** > **Project Structure** and check the **SDK Location** and version.
   - Alternatively, go to **Tools** > **SDK Manager** and ensure **Android 10.0 (API Level 29)** is installed under **SDK Platforms**.

2. **Flask Backend**:
   - [Python 3.10](https://www.python.org/downloads/)
   - Virtual Environment tool (`venv`)
   - Package Manager: `pip`

---

## Android App Setup

### 1. Clone the Repository
First, clone the repository to your local machine:

```bash
git clone <your-repo-url>
cd <your-repo-directory>
```

### 2. Open the Project in Android Studio

1. Open Android Studio`
2. Select "Open an Existing Project" and navigate to the android folder in the repository.
3. Let Android Studio sync the project. It will install necessary dependencies and configure the project.

### 3. Build the Project

- Make sure you have Android 10.0 (API Level 29) installed (see Prerequisites section for how to check your SDK version).
- From the toolbar, select "Build > Make Project".
- Alternatively, you can build it from the command line:
```bash
./gradlew assembleDebug
```

### 4. Run the App

- Connect an Android device or start an emulator. Make sure to use an android version with the google APIs installed.
- In Android Studio, click the Run button or use the following command to install the app on the device:
```bash
./gradlew installDebug
```

## Python Flask Backend Setup

### 1. Setup Virtual Environment

Navigate to the backend folder in the repository and create a virtual environment:

```bash
cd backend
python3.10 -m venv venv
source venv/bin/activate   # On Windows, use `venv\Scripts\activate`
```

### 2. Install Dependencies

Make sure you have all required Python packages installed. Run the following command to install dependencies from requirements.txt:

```bash
pip install -r requirements.txt
```

### 3. Configure Environment Variable`

Some environment variables may be required for the Flask app (e.g., FLASK_APP, FLASK_ENV, etc.).

Create a .env file in the backend directory and set up your environment variables like this:

```bash
FLASK_APP=app.py
FLASK_ENV=development
```

### 4. SQLite Database Setup

The app is configured to use an SQLite database locally. The database will be automatically created when you run the application for the first time. No manual setup is required for SQLite.

### 5. Run the Flask Application

Once everything is set up, you can start the Flask development server:

```bash
flask run
```

By default, this will run the Flask app at http://127.0.0.1:5000/.