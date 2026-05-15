#Vidyarthi-Bus
Vidyarthi-Bus is a lightweight Android app for crowdsourced college bus crowd alerts. Students select a route, watch the real-time crowd meter, report whether seats are available, and call shared auto alternatives when the bus is full.

#Features
Route selector for college bus numbers.
Firebase Realtime Database listener for route-level crowd updates.
Horizontal crowd meter with green, amber, and red states.
Reports expire automatically after 15 minutes.
Location check blocks reports from students who are not near a route stop.
Shared auto contact list with one-tap dial.
Light and dark color resources tuned for a clean transit UI.

#Firebase Setup
1.Create a Firebase project and add an Android app with package com.example.vidyarthibus.
2.Create a Realtime Database.
3.Replace the placeholder values in app/src/main/res/values/strings.xml:
firebase_app_id
firebase_api_key
firebase_project_id
firebase_database_url
4.For a student demo, use these starter Realtime Database rules:
{
  "rules": {
    "routes": {
      "$routeId": {
        "reports": {
          ".read": true,
          ".write": true,
          "$reportId": {
            ".validate": "newData.hasChildren(['level', 'status', 'timestamp', 'deviceId', 'latitude', 'longitude', 'nearestStop']) && newData.child('level').isNumber() && newData.child('level').val() >= 0 && newData.child('level').val() <= 100"
          }
        }
      }
    }
  }
}
For production, add Firebase App Check and server-side validation because client-side location checks can be bypassed on modified devices.

#Run
Open the folder in Android Studio, sync Gradle, and run the app configuration on an emulator or Android device with location services enabled.# vidyarthi-bus
