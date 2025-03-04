get current location using Google Android Location
FYI

Issue : 
In an effort to reduce power consumption, Android 8.0 (API level 26) limits how frequently an app can retrieve the user's current location while the app is running in the background. Under these conditions, apps can receive location updates only a few times each hour. 


**location listener** can receive location updates only a few times per hour, when the app is running in the background.

**locationCallback**  can receive location updates when the app is running in background for automotive os.



video link app test : https://youtu.be/zfAmt5dN8N0 
 



Final version:
read current location at foreground  / background /  also when system boot up, it will auto launch the background service to read current location. 



<img width="1076" alt="Screenshot 2025-03-04 at 1 13 45 PM" src="https://github.com/user-attachments/assets/f27a5288-96b2-4a3f-bc47-af572cfca365" />

case 1: button "Get Location on Screen"
It will display current location info to the screen.

case 2: button "Start Background Service at Front"
This button will trigger: print out current location (log) every one seconds 

case 3: button "Start Service At Back"
When this is clicked, it will auto minimize your app, and prints out your current location (log) every 15 seconds

case 4: button "Stop"
Stops Both foreground and background service. (stop printing log of current location)

case 5:
If notification switch turned on
When you reboot your system, you will see log print current location every 15 seconds when system complete boot up.
If swich is turned off, you won't see log of current location when system complete boot up.

