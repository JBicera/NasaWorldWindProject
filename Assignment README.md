WorldWind Network Project
===================================

Problem Statement
-----------------------------------
Write a program in Java that reads a list from a JSON file containing a network graph and displays the network in NASA
WorldWind. Use the included project as a starting point. After completing **Part 1**, the globe should look something
like the following: https://www.dropbox.com/s/n07uov7uvsrzczi/World-Wind-JSON-Network-Viewer.png?dl=0. Note: the colors
of the network elements may be different for your project. For **Part 2**, augment the application to accept a live JSON
feed and display the features on the globe. As the feed is processed, the globe should contain the same data as this
site: https://earthquake.usgs.gov/earthquakes/map/

Getting Started
--------------------------------------------------
This package should include everything you need to complete the task.

* Gradle is included as the build system for the application. To build, execute `./gradlew build` or on Windows 
  `gradlew.bat build`. If using JDK 13, there are issues with the Oracle OpenJDK and Gradle 6.0.1, either downgrading 
  the JDK to 11 or using a different OpenJDK variant is required. The recommended JDK13 can be found here: https://adoptopenjdk.net
* To run the application execute `./gradlew run` or `gradlew.bat run`. You should get a window with a 3D globe if all 
  has gone well.
* To work with an IDE like Eclipse, execute `./gradlew eclipse` or `gradlew.bat eclipse`.  For IntelliJ, execute 
  `./gradlew idea` or `gradlew.bat idea`. Then use your IDE to import/open the project.
* Resources and examples for World Wind are available at: https://worldwind.arc.nasa.gov/java/
* When running the application, you may encounter the exception `SEVERE: Retrieval returned no content for ...`. This
 exception occurs when one of NASA's terrain imagery servers are down so we cannot control it. The exception means that
 some terrain may load at a lower quality but it is safe to ignore. 

Part 1 - Parse network graph from a file
--------------------------------------------------
* Use the included file open action to allow the user to load a file of their choice.
  * A sample JSON file for testing is located in the root of the project, samplenetwork.json.
* Parse the user selected JSON file.
  * Hint: World Wind has built-in GeoJSON parsing facilities.
* Create a layer in World Wind.
* Add the entries from the JSON file to the World Wind globe.

Part 2 - Parse earthquake data from a live stream
--------------------------------------------------
* Add a menu option for the user to enter the address for a live JSON feed. Please default it to the earthquake URL 
  below.
* Allow the user to configure the rate at which the live stream is queried. At a minimum, the user should be able to 
  select rates from 1 second to 5 minutes. (This will also make it easier to test your solution.)
* Parse the live feed at: https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_day.geojson. This feed
  provides a summary of all magnitude 2.5+ earthquakes in the past day and is updated every 5 minutes. More information
  about the feed is available here: https://earthquake.usgs.gov/earthquakes/feed/v1.0/geojson.php.
* Create a layer in World Wind and add the entries from the JSON feed to the World Wind globe.

Bonus (Optional)
--------------------------------------------------
* Select one aspect of the user experience that you think can be improved upon and implement the improvement. Most commonly,  
  this would target code added from or related to your solution above, but not necessarily. For example, showing the user the
  recently-imported data, adding keyboard shortcuts, etc. Be creative!

**Feel free to reach out with questions or difficulties.**

Submission
--------------------------------------------------
When you are finished with the problem, run `gradlew packageDistribution` and submit the **updated** WorldWindNetworks.zip file from
the root directory to the OneDrive URL you were provided in the email instructions for the exercise.

