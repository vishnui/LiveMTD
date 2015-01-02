LiveMTD
=======
An android application that presents users with a live Google Map with all the Champaign Urbana buses running at the moment.  All the current apps make you type in an intersection name or origin and destination and time but I wanted to be able to quickly see where the bus currently is.  Plus live bus ETAs are also rather hard to find; they require constant texting their web service to get.  So I built a much easier to use app: just open to see near real time color coded bus positions and tap any stop to see real time ETAs on all incoming buses in the next half hour. 

Screenshots
=======

![Screenshot](/Screenshots/screenshotGG.png?raw=true "Live 50E position and ETAs")

![Screenshot](/Screenshots/ScreenshotRTR.png?raw=true "ETAs and pull to refresh")

![Screenshot](/Screenshots/ScreenshotSILL.png?raw=true "Algolia powered search")
 
Version change log
=======
- More Bug fixes.  
- Bug fixes.  A lot of them.  This 
- Splash screen while content loads
- Pull to refresh on the eta list view
- UI Cleanup on that eta list view
- Added notification area tracker.  Click on marker window to see details and tap the bell to track a specific bus to that stop.
- Took out the server to make the app much more responsive.
- Real time search of all bus stops.
- Distributed the server.  Now one of the clients polls the server and when it disconnects, another client picks up the polling.  This might wreck havoc on data plans though :(
- Bug fixes mostly; Fixed some marker info windows disappearing/not refreshing correctly
- Applying to Algolia for a little more space in their free plan to put all stop information.
- Using Firebase as bus ETA cache
- Bus ETAs are live!
- Bus ETAs available
- Stop information available! 
- Using Firebase as bus position cache
- Switched to Firebase
- Added server backend
- Fixed bugs.  First working version
- Now updates every minute
- Added colors
- Added safe rides position as well
- Added all the buses
- MTD Network Utils
