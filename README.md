LiveMTD
=======
An android application that presents users with a live Google Map with all the Champaign Urbana buses running at the moment.  All the current apps make you type in an intersection name or origin and destination and time but I just want to quickly see where the bus is right now.  Plus live bus ETAs are also rather hard to find.  They require constant texting their web service to get.  So I built a much easier to use app; just open to see near real time color coded bus positions and tap any stop to see ETAs on all incoming buses in the next half hour.
 
Version change log
=======
- PLANS - Next major version will include full schedule information and real time search.  Need to stay away from any navigation like functionality though; avoid GMaps API TOS Violations.  Algolia will be very cool to have.
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
