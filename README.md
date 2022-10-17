# Revolt

## Authors
- Celia Allen 
- James Billows
- Matthew Doonan
- Michelle Hsieh
- Angus Kirtlan
- Harrison Tyson 

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
- Open a command line interface inside the project directory and run `mvn clean package` to build a .jar file. The file is located at target/revolt-1.0-SNAPSHOT.jar
- Alternatively you can run mvn `mvn clean package -P headlessTests`. This will build the .jar file with TestFX running in the background instead of requiring you to not move your mouse while it runs

## Run App
- If you haven't already, Build the project.
- Open a command line interface inside the project directory and run `cd target` to change into the target directory.
- Run the command `java -jar revolt-1.0-SNAPSHOT.jar` to open the application.
- If this fails in Intellij go to edit configurations then the plus sign or add configuration
- Then click maven and then in command line type javafx:run and then apply
- run the configuration

## Testing
1. Open a command line interface inside the project directory and run `mvn test` to run all tests.

## Javadoc
1. Open a command line interface inside the project directory and run `mvn javadoc:jar` to generate a folder of javadoc. The folder is located at target/apidocs

## Known Bugs
- When editing a vehicle any journeys attached to the vehicle won't update
- Cursor indexing bug for car charge percentage on main screen upon overflow

## Logins
Admin:

- Username: admin

- Password: admin

Charger Owner:

- Username can be any charger owner eg: ChargeNet NZ

- Password: demo

Normal Users:

- Create own account using signup functionality

## User Guide
The user guide can be found in the `UserManual.pdf` file