# Rideable Client

## APP IS STILL UNDER CONSTRUCTION. THIS IS JUST a PROTOTYPE OF THE APP TO SHOW IT'S MAIN FEATURES##

## SERVER MUST BE RUNNING BEFORE USER CAN SIGN UP OR LOGIN ## 

This app is a rideshare app designed for users to post rideshares, and find rideshares. This is a passion project of mine, but also helped me learn how to utilize a RESTFUL api, how to encorporate a MySQL server into an app and use MySQL workbench, and how to use the Hibernate ORM. It also gave me a look into how to use location services and the GoogleMaps API and Google cloud messaging for use with push notifications.<br />

### My inpiration behind this app:

As a student from the Greater Toronto Area, studying at Carleton University in Ottawa, ON, travelling from Ottawa to Toronto, was extremely pricey and boring. The roundtrip cost of a bus ride is upwards of $150 dollars, which was really start to add up. Through classmates I heard of a service called ridesharing, where students look for drivers who are also going to a mutual destination, on social media, and Kijiji, and pay a fraction of the cost of a bus ticket to travel with the driver. Usually, they are also accompanied by other passengers, making a fun trip home. Seeing the UI on kijiji, I wanted to make a more rideshare centric app. Although the app is still underconstruction, some of its functionalities are up.<br />
<br />
How to use app:<br />
1. Start tomcat server<br />
2. Start emulator/or put app onto a physical android device (Within Android Studio project, change IP under types.java to match that of your server IP address)<br />
3. Slide app to signup or login mode. <br/>
**Sign Up**<br />
  1. To Sign up Enter First Name and Last Name <br />
  2. Add a valid email address <br />
  3. Enter a password with a minimum of 8 characters<br />
  4. Confirm the password<br />
  5. Select "Sign Up"<br />
  6. Slide tab to "Login" and enter credentials<br />
**Login**<br />
  1. To login, enter valid user credentials and select "Login"<br />
 4. When Logged in, User can post a ride, find a ride, or chat with ride you are currently accepted to:
 **Post a ride**<br />
  1. Slide navigation drawer open and select "Post Ride"<br />
  2. Enter in Departure City/Arrival City (Using google location services), departure date, departure time, number of passengers and a price <br />
  3. Select "Post Ride" to post your ride <br />
 
**Find a Ride**<br />
  1. Slide navigation drawer open and select "Find Ride"<br />
  2. Enter Departure City and Arrival City (currently none of the other search filters work) <br />
  3. View a list of rides leaving from the departure city and going to the arrival city <br />
  4. Click on the ride to view more details, and select Confirm Ride to be added to a ride<br />
 
**Chat Feature**<br/>
  1. Once you have been added to a ride, you can view the rides you are currently a part of and chat with other passengers/driver of the ride. <br />
  2. Slide the navigation drawer open and select "Chat" <br />
  3. Click through the list of current rides and select the ride you want to chat in <br />
  4. Chat with other members fo that ride <br />


