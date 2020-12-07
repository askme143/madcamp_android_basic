# Madcamp Project 1: Health Manager

This code doesn't work on Samsung Health with version >= 6.11. See [Notice](#Notice)

## TAB1: Contacts

This tab contains contact list with retrieve function and buttons connected to dial and msg app.
When you click the contact item the hidden item shows up.

<img src="/sample-images/contact_1.jpg" width="40%">
<img src="/sample-images/contact_2.jpg" width="40%">

## TAB2: Gallery

The second tab is a gallery of images of the app's own external storage.
Click on the image in the gallery to see the enlarged picture. In this state, you can view the next image in slide motion.
You can view the selection mode by holding down the image in the gallery for a while. Select the image in Select mode and press the camera-shaped floating button to delete it. (to be changed to a proper icon in the future)
Pressing the camera-shaped floating button in the default mode instead of the select mode becomes the filming mode. Photos taken at this time are immediately reflected in the gallery.
Photos taken in this app are not affected by other apps.

<img src="/sample-images/gallery_1.jpg" width="50%"><img src="/sample-images/gallery_2.jpg" width="50%">

## TAB3: Health

This tab shows some health information(step count, sleep time, exercise history) from samsung health.
When you click the edit button, you can set the goals about step count and sleep time.
Step count box shows how many steps you walk everyday and the ratio(counts/goal). The information updated realtime when you walk.
Sleep time box shows the bar graph based on the goal time you set.
Exercise stroy box shows history of your exercise. You can add the picture of that time if you click +add button.

![health-tab-1](/sample-images/health_tab_1.jpg)![health-tab-2](/sample-images/health_tab_2.jpg)

## Notice

There was an update on Samsung Health and Samsung Health SDK. Now this code doesn't work on Samsung Health with version >= 6.11
