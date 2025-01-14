
# Get Started Guide

This project consists of "The Search", a tool for saving information about your own defined surfspots on a map! It consists of an Android mobile application and a Python Flask backend. Follow the instructions below to download, build, and run both the Android app and the backend.

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

### 2. Obtain a Google Maps API Key

The Android app uses the Google Maps API. To run the app, you need to obtain a Google Maps API key and configure it in the project.
Steps to Obtain a Google Maps API Key:

1. Go to the Google Cloud Console.
2. Create a new project (or use an existing project).
3. Navigate to the API & Services > Credentials page.
4. Click Create Credentials and select API Key.
5. Once the API Key is generated, copy it for use in the next step.
6. Enable the Google Maps Android API in the Google Cloud Console.

Configure the API Key:

1. Create a secrets.properties file in the root directory of the Android project (if it doesn't already exist).
2. Add the following line to the secrets.properties file:

```properties
MAPS_API_KEY=your-google-maps-api-key
```

Replace your-google-maps-api-key with the API key you obtained from the Google Cloud Console.

### 3. Open the Project in Android Studio

1. Open Android Studio`
2. Select "Open an Existing Project" and navigate to the android folder in the repository.
3. Let Android Studio sync the project. It will install necessary dependencies and configure the project.

### 4. Build the Project

- Make sure you have Android 10.0 (API Level 29) installed (see Prerequisites section for how to check your SDK version).
- From the toolbar, select "Build > Make Project".
- Alternatively, you can build it from the command line:
```bash
./gradlew assembleDebug
```

### 5. Run the App

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

### 3. Configure Environment Variables

The Flask app requires several environment variables. You need to generate your own SECRET_KEY, JWT_SECRET_KEY, and GOOGLE_CLIENT_ID for security purposes. These keys should be defined in a .env file in the backend directory.

#### Steps to Set Up the Environment Variables:

1. Generate a SECRET_KEY:

- In your terminal or Python shell, run the following command to generate a secure secret key:

```bash
python -c 'import os; print(os.urandom(24).hex())'
```

- Copy the generated key and save it for use in the .env file.

2. Generate a JWT_SECRET_KEY:

- Similarly, generate a JWT secret key:

```bash
python -c 'import os; print(os.urandom(24).hex())'
```

- Copy the key and save it.

3. Obtain a GOOGLE_CLIENT_ID:
        
- Follow the instructions in the [Google Developer Console](https://support.google.com/cloud/answer/6158849?hl=en) to create OAuth 2.0 credentials and obtain a GOOGLE_CLIENT_ID for the project.
- You will use this ID for Google Sign-In functionality in the Flask app.

#### Create a .env file

After generating the required keys, create a .env file in the backend folder and add the following keys:

```bash
SECRET_KEY=your-generated-secret-key
JWT_SECRET_KEY=your-generated-jwt-secret-key
GOOGLE_CLIENT_ID=your-google-client-id
```

Replace your-generated-secret-key, your-generated-jwt-secret-key, and your-google-client-id with the actual values.
- SECRET_KEY: Used by Flask to secure sessions.
- JWT_SECRET_KEY: Used for signing JWTs for user authentication.
- GOOGLE_CLIENT_ID: Client ID for Google OAuth 2.0 authentication.


### 4. SQLite Database Setup

The app is configured to use an SQLite database locally. The database will be automatically created when you run the application for the first time. No manual setup is required for SQLite.

### 5. Run the Flask Application

Once everything is set up, you can start the Flask development server:

```bash
flask run
```

By default, this will run the Flask app at http://127.0.0.1:5000/.
