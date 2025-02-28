get current location using Android LocationManager google
FYI

Issue : 
In an effort to reduce power consumption, Android 8.0 (API level 26) limits how frequently an app can retrieve the user's current location while the app is running in the background. Under these conditions, apps can receive location updates only a few times each hour. 


**location listener** can receive location updates only a few times per hour, when the app is running in the background.

**locationCallback**  can receive location updates when the app is running in background for automotive os.



video link app test : https://youtu.be/zfAmt5dN8N0 
 



Final version:
read current location at foreground  / background /  also when system boot up, it will auto launch the background service to read current location. 
