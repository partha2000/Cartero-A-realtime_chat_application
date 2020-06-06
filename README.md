# Realtime_chat_application
A real-time chat interface where multiple users can interact with each other by sending messages. This app will be made with kotlin. This is a part of the #30daysofKotlin challenge by google dev India.

## Targeted Features
-   [X] User is asked to authenticate himself on entering the app using google sign-in, email, etc.
-   [X] User can see an `input field` where he can type a new message
-   [X] By pressing the `enter` key or by clicking on the `send` button the text will be displayed in the `chat box` alongside his username (e.g. `John Wick: Hello World!`)
-   [X] The messages will be visible to all the Users that are in the chat app (using Firebase Realtime Database)
-   [ ] When a new User joins the chat, a message is displayed to all the existing Users
-   [X] Messages are saved in a database
-   [ ] User can send images, videos and links which will be displayed properly
-   [ ] User can select and send an emoji
-   [ ] Users can chat in private
-   [ ] Users can join `channels` on specific topics

## External Libraries / Dependencies

* Primary Language   : Kotlin
* Backend            : Firebase
* Glide              : An image loading and caching library for Android focused on smooth scrolling

* Firebase `implementation 'com.google.firebase:firebase-analytics:17.2.2'`
* Firebase Realtime DB `implementation 'com.google.firebase:firebase-database-ktx:19.3.0'`
* Glide `implementation 'com.github.bumptech.glide:glide:4.11.0'`

