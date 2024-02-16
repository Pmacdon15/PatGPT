# PatGPT
This is the final project for my Mobile Dev class. It uses the OpenAi Api to Generate text or images from a prompt. I used a OkHttp to make the api request. I am using the Gpt-3.5-torubo and DALL-E-3 modles.

## Table of Contents
- [Features](#Features)
- [Setup](#Setup)
- [Defaults](#Defaults)
- [Emulators](#Emulators)
- [Mentions](#Mentions)

# Features
## Current Features
- FeaturesCurrently text and image generation are working as expected.
- Currently you are able to share the link for the image generated.
- Login screen works at the most basic level
- Navagation and hamburger menu working as expected
- SQL Lite database functionality
## Coming Soon
- Ability to share photo instead of link and also ability to save
- Ability to share generated text(maybe as a file of some kind)
- Added prompt above response
- Added history of responses(potentially while navagating as well
- Ability to change profile picture and other settings and have it reflected in the app specifically the navigation menu


# Setup

1. Make an account and load a balance on the account @ https://platform.openai.com/docs/overview

2. Make an API Key on your account(save this key some where)

3. Make a resource flie inside of  app/src/main/res/values callend environment.xml

4. The contents be formatted in this way:
```Env
<?xml version="1.0" encoding="utf-8"?>
<resources>
<string name="API_KEY">Put your api key here</string>
</resources>
```
5. Replace "Put your api key here" with your API Key

6. At this point you are free to use the built in emulator or build your own APK

# Defaults
Until I build a backend or integrate SQL Light the default credentials are:
- User Name
```
admin
```
- Password
```
Admin
```

# Emulators
So far I have tested the app on my Note 20 Ultra on a real phone and the Pixel_3a_API_34 on android studio. I use a vertical constraint as my parent layout just to simplify everything. This seems to work well on atleast two devices.

# Mentions
I have used icons from https://www.flaticon.com/free-icons/


  
