# Duke project template

This is a project template for a greenfield Java project. It's named after the Java mascot _Duke_. Given below are instructions on how to use it.

## Setting up in IntelliJ

Prerequisites: JDK 21.

1. Open IntelliJ (if you are not in the welcome screen, click `File` > `Close Project` to close the existing project first)
1. Open the project into IntelliJ as follows:
   1. Click `Open`.
   1. Select the project directory, and click `OK`.
   1. If there are any further prompts, accept the defaults.
1. Configure the project to use **JDK 21** (not other versions) as explained in [here](https://www.jetbrains.com/help/idea/sdk.html#set-up-jdk).<br>
   In the same dialog, set the **Project language level** field to the `SDK default` option.
1. After that, locate the `src/main/java/Duke.java` file, right-click it, and choose `Run Duke.main()` (if the code editor is showing compile errors, try restarting the IDE). If the setup is correct, you should see something like the below as the output:
   ```
   Hello from
    ____        _        
   |  _ \ _   _| | _____ 
   | | | | | | | |/ / _ \
   | |_| | |_| |   <  __/
   |____/ \__,_|_|\_\___|
   ```

**Warning:** Keep the `src\main\java` folder as the root folder for Java files (i.e., don't rename those folders or move Java files to another folder outside of this folder path), as this is the default location some tools (e.g., Gradle) expect to find Java files.

## Running Tests

The project comes with a set of test scripts for different operating systems:

- **MacOS/Linux**: Run the shell script
  ```bash
  cd text-ui-test
  ./runtest.sh
  ```

- **Windows**: Run the batch script
  ```batch
  cd text-ui-test
  runtest.bat
  ```

These scripts will compile the source files, run the tests, and compare the output against the expected output.

## Packaging as an executable JAR

This project is configured to build a self-contained executable JAR (a “fat jar”) that can be distributed and run without any additional classpath setup.

### Build the JAR

```bash
./gradlew clean shadowJar
```

On Windows PowerShell:

```powershell
.\gradlew.bat clean shadowJar
```

The executable JAR will be created at `build/libs/spot.jar`.

### Run the JAR

1. Copy `build/libs/spot.jar` into an empty folder.
2. Open a command window in that folder.
3. Run:

```bash
java -jar "spot.jar"
```
