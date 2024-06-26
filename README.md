# PatGPT
This project is the final assignment for my Mobile Development class. It leverages the OpenAI API to generate text or images from a given prompt. The project utilizes the OkHttp library to make API requests and integrates the GPT-3.5-turbo and DALL-E-3 models.

## Table of Contents
- [Features](#Features)
  - [Current Features](#Current-Features)
  - [Coming Soon](#Coming-Soon)
- [Setup](#Setup)
- [Emulators](#Emulators)
- [Acknowledgments](#Acknowledgments)

# Features

## Current Features
- Login Screen: Users can log in to access the app's features.
- History Page: Displays the history of generated text and images.
- Registration Page: Allows new users to create an account.
- Text and Image Generation: Generates text and images based on user prompts.
- Sharing Functionality: Users can share generated images and text.
- Navigation and Hamburger Menu: Provides easy navigation throughout the app.
- SQLite Database Functionality: Stores user data and history locally.
- Profile Settings: Users can change profile picture and other settings, which are reflected in the app, particularly in the navigation menu.
- Google Account Login: Option to log in using Google accounts
- Local Users, stayed logged in even if they close the app.
- Have your google profile image loaded in to the app.
    
> **Note**
> Some additional features have been implemented but will be available in the next release.

## Coming Soon
- History Sharing: Users will be able to share their history.
 
  
# Setup

1. Create an account and load balance on the OpenAI platform here.

2. Generate an API Key from your OpenAI account (make sure to save this key).

3. Create a resource file named environment.xml inside app/src/main/res/values.

4. The file should contain the following format:

```Env
<?xml version="1.0" encoding="utf-8"?>
<resources>
<string name="API_KEY">Put your API key here</string>
</resources>
```
5. Replace "Put your API key here" with your actual API Key.

6. For google login to work you must register a google console app, with your apps information. If you are using this app in the emulator use the SHA1 key from the debugger, in signing rport. Other words use Android studio to Generate a key(.jsk) then use this command to reveal the SHA1 key.

 ```pwoershell
keytool -list -v -keystore "keystoreandfilepath.jsk"  -alias keyAlias
```

Replacing "keystorefileamdfilepath" with your keystore .jskfile and path, and replaceing keyAlias with the keys alias. You will also need the package name and app name.

8. You can now use the built-in emulator or build your own APK.

# Emulators
The app has been tested on a real device (Note 20 Ultra) and the Pixel_3a_API_34 emulator in Android Studio. The layout, using vertical constraints as the parent layout, has been tested on these devices and found to work well..

# Acknowledgments
I have used icons from https://www.flaticon.com/free-icons/

> **Note**
> Profile page removed for google users. When using a google account only your e mail, profile image and and history are are stored locally and can be cleared. 

  
