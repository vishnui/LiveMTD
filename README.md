LiveMTD
=======
An android application that presents users with a live Google Map with all the Champaign Urbana buses running at the moment.  All the current apps make you type in an intersection name or origin and destination and time but I just want to quickly see where the bus is right now.  Plus live bus ETAs are also rather hard to find.  They require constant texting their web service to get.  So I built a much easier to use app; just open to see near real time color coded bus positions and tap any stop to see ETAs on all incoming buses in the next half hour. 

Latest Screenshots
=======

![Screenshot](/LiveMTDScreenshot.png?raw=true "Live 22N position and ETAs")

That little purple bus in the bottom left corner is the 220N and the silver bus next to the stop is the 130N. For all you unfamiliar with its route, the 220 is about 30 seconds away from the selected stop.  The system seems to be accurate to about one minute give or take.  Not too bad but I'm working on making it even better. :)

This should be on the play store pretty soon.
 
Version change log
=======
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
