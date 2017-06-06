# TheBestTradingSystem
Arguably the best and fastest trading system in the world.

## Running the project

Each submodule is its own component, and you can build with the following commands:

`./gradlew :<submodule>:shadowJar` e.g. `./gradlew :trader:shadowJar` (there are three modules, trader, admin-server, and market-peer)

Each component needs to be set up separately and usually requires a number of args. Run each component after building as follows:

`java -jar <submodule>/build/libs/<submodule>-all.jar <args>` e.g. `java -jar trader/build/libs/<submodule>-all.jar`

Notice that if you don't know the args (watch out for the peer, there are a lot), there is quite a lot of help given to you by the Apache module used to help out with command line args. You can use short or long flags.

## Admin Server Backup Strategy

The admin server creates backups of the peer database every time a non-read-only operation is invoked on the database. In this way, the "copy" of the existing trading system network can be given to another copy of the admin server if the original admin server goes down for any reason.

The default offering of this backup stores copies in a local "backup" dir, so it should be changed before moving code to production.

## How to Import this into IntelliJ

Follow [directions from IntelliJ](https://www.jetbrains.com/help/idea/2017.1/importing-a-gradle-project-or-a-gradle-module.html) for Importing a Project from a Gradle Model. You should select the option that says "Use Gradle wrapper task configuration" when you get to that point in importing.

## How to Build and Run Each module

**From IntelliJ**

Same as you would any other project. Just run the main class from within IntelliJ.

**From Command Line**

1. From inside the `TheBestTradingSystem` dir, run `./gradlew :<sub-module>:shadowJar` (e.g., `./gradlew :admin-server:shadowJar`). If you are building on a Windows command line, run `gradlew :<sub-module>:shadowJar`. This builds a JAR of the project that contains all of the source and dependency JARs. It places the JAR in `./<sub-module>/build/libs/<sub-module>-all.jar`.
2. Run the sub-module with `java -jar <sub-module>/libs/<sub-module>-all.jar` (e.g., `java -jar trader/build/libs/trader-all.jar`). This makes each module pretty portable, as you can just copy the `-all.jar` around wherever you would like to run it.

## Refreshing Project in IntelliJ

If you ever update dependencies, you'll need to refresh the project in IntelliJ so IntelliJ knows about them.

Go to __View -> Tool Windows -> Gradle__ (or click the little Gradle tab on the right side of the screen) and then click the little arrows in a circle icon.

## Explanation of Base Files and Structure

Explains structure of project and what some files are used for.

### Notes on Structure

**src/main/java directory** From what I could tell, IntelliJ tends to only set users up with a `src` directory. However, this project is currently following Maven's more [standard directory layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html).

**gradle directory** The gradle directory houses components used for the Gradle wrapper. Basically, it tells the `gradlew` or `gradlew.bat` (depending on if you are building on Unix or Windows) which version of Gradle to use and where to get that version.

**admin-server, market-peer, and trader directories** These are the sub-modules within the larger project. For our purposes, you can think of each one of these as being their own individual project. See directions for how to build each one to be able to actually run them.

### Notes on Files

**logback.xml** This is a logger configuration that most of the applications I have seen use. Should never have to actually touch this xml for our project, unless we want to write log files (in which case we'd add an appender, which is very easy to do). I have provided an example of how to set up this logger in each of the main classes.

**build.gradle** This is the main script for building the runnable JARs for each of our modules. This will need to be updated when/if we: add a new JAR dependency, change the name of a module, or change the name of the Main class.

**gradlew and gradlew.bat** These are executables to run the wrapper. Never will need to update them, just run them.

**settings.gradle** This tells the main `build.gradle` where to find individual modules.
