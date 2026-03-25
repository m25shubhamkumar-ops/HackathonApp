# Firebase Credentials Extraction Guide

This document serves as a comprehensive guide to extract Firebase credentials for your application. Follow the step-by-step instructions below:

## Step 1: Create a Firebase Project
1. Go to the [Firebase Console](https://console.firebase.google.com/).
2. Click on "Add project".
3. Name your project and accept the terms.
4. Click on "Create project".
5. Once the project is created, click on "Continue".

## Step 2: Access Project Settings
1. In the Firebase Console, select your newly created project.
2. Click on the gear icon next to "Project Overview".
3. Select "Settings" from the dropdown menu.

## Step 3: Obtain Firebase Configurations
1. In the settings page, scroll down to the "Your apps" section.
2. If you haven't added any app yet, click on "Add app" and choose the relevant platform (iOS/Android/Web).
3. Follow the on-screen instructions to register your app.
4. Once registered, Firebase will provide you the configuration settings.

## Step 4: Copy the Credentials
1. You will see the Firebase SDK snippet along with the configuration details. It looks something like this:
   ```
   const firebaseConfig = {
       apiKey: "YOUR_API_KEY",
       authDomain: "YOUR_PROJECT_ID.firebaseapp.com",
       projectId: "YOUR_PROJECT_ID",
       storageBucket: "YOUR_PROJECT_ID.appspot.com",
       messagingSenderId: "YOUR_MESSAGING_SENDER_ID",
       appId: "YOUR_APP_ID"
   };
   ```
2. Copy the apiKey, authDomain, projectId, storageBucket, messagingSenderId, and appId.

## Step 5: Secure Your Credentials
1. Store these credentials securely and never hardcode them into your application.
2. Consider using environment variables or secret management services for production applications.

## Conclusion
This guide provides the essential steps to extract your Firebase credentials. Make sure to follow proper security practices to keep your credentials safe.