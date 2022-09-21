# Revolt

## Authors
- Celia Allen, James Billows, Matthew Doonan, Michelle Hsieh, Angus Kirtlan, Harrison Tyson 

## Prerequisites
- JDK >= 17 [click here to get the latest stable OpenJDK release (as of writing this README)](https://jdk.java.net/18/)
- Maven [Download](https://maven.apache.org/download.cgi) and [Install](https://maven.apache.org/install.html)

## What's Included
This project comes with some basic examples of the following (including dependencies in the pom.xml file):
- JavaFX
- Logging (with Log4J)
- Junit 5
- Mockito (mocking unit tests)
- Cucumber (for acceptance testing)

We have also included a basic setup of the Maven project and lifecycle required for the course including:
- Required dependencies for the functionality above
- Build plugins:
    - Surefire for generating testing reports
    - Shade for packaging to UberJAR
    - JavaFX Maven plugin for working with (and packaging) JavaFX applications easily

You are expected to understand the content provided and build your application on top of it. If there is anything you
would like more information about please reach out to the tutors.

## Importing project from VCS (Using IntelliJ)
IntelliJ has built-in support for importing projects directly from Version Control Systems (VCS) like GitLab.
To download and import your project:

- Launch IntelliJ and chose `Get from VCS` from the start-up window.
- Input the url of your project e.g. `https://eng-git.canterbury.ac.nz/seng202-2022/team-X`.

**Note:** *If you run into dependency issues when running the app or the Maven pop up doesn't appear then open the Maven sidebar and click the Refresh icon labeled 'Reimport All Maven Projects'.*

## Importing Project from a folder (Using IntelliJ)
IntelliJ has built-in support for Maven (although if you turned the Maven plugin off when you were setting it up
you’ll need to re-enable it). To import your project:

- Launch IntelliJ and choose `Open` from the start-up window.
- Select the project folder.
- Select `Import project from external model` and make sure that `Maven is highlighted.

**Note:** *If you run into dependency issues when running the app or the Maven pop up doesn't appear then open the Maven sidebar and click the Refresh icon labeled 'Reimport All Maven Projects'.*

## Build Project
1. Open a command line interface inside the project directory and run `mvn clean package` to build a .jar file. The file is located at target/revolt-1.0-SNAPSHOT.jar

## Run App
- If you haven't already, Build the project.
- Open a command line interface inside the project directory and run `cd target` to change into the target directory.
- Run the command `java -jar revolt-1.0-SNAPSHOT.jar` to open the application.

## Testing
1. Open a command line interface inside the project directory and run `mvn test` to run all tests.

## Javadoc
1. Open a command line interface inside the project directory and run `mvn javadoc:jar` to generate a folder of javadoc. The folder is located at target/apidocs

## User Guide
When oppening the application you should be presented with the home screen. The home screen is made up of an interactive map on the right, a simplified table viewer on the left, and a menu controller along the top which is persistant along all screens. The map can be viewed using the drag to move around and scroll to zoom in and out. Chargers on the map can be edited by hovering over and and clicking 'Edit Charger' where details can be changed in a popout menu. Your current location can be changed by clicking on the map. This may be useful in combination with filters for nearby chargers. There is a small menu along the top which can also be used to show a very simple route between two points, add chargers, edit chargers, and delete chargers. 

On the left the simplified table viewer presents chargers in order of closest to furthest. Filters can be added by using the 'Filters' dropdown menu, selecting the desired filters, and then clicking search. Chargers can also be serached by typing their address into the "Search Charger" field and clicking searching. The advanced table can be accessed by clicking 'Show Table'.

The table viewer has its own screen where it shows all available information of the chargers in tabular form. The table has similar features to the map, where chargers can be added, deleted,and edited, and filteres applied. Columns of data can also be selected and unselected from the table viewer. Updating the table is nescessary after any filters are applied or column selections.  

The menu controller along the top of all screens can be used to return to the home screen at any time. It can also be used to access the 'My Vehicles' screen. From this screen vehicles can be added, removed, and edited.