COVA - Constraint Analysis with [VASCO](https://github.com/rohanpadhye/vasco) 

## Obtaining The Tool 
You can either build COVA on your own with Maven or [download a release](...) from this repository.

## Bind Z3 Library
COVA uses Z3 for STM-Solving and you need to bind Z3 for running the tool. You can find Z3-4.5.0 in the local directory `$REPO_LOCATION/cova/src/main/resources/` or downloand it from [the GitHub repostiory of Z3](https://github.com/Z3Prover/z3).  
### - Windows
Currently, the repository only includes Z3 for Windows 64bit.
There are two choices for you to bind z3:

- **Userwide via OS**: 
Add `$REPO_LOCATION/cova/src/main/resources/z3-4.5.0-x64-win/bin` to the system variable `PATH` of your operating system ([How do I set or change the PATH system variable?](https://www.java.com/en/download/help/path.xml)).

- **Projectwide in Eclipse**: 
After importing COVA as maven project, you can specify the environment variable: 
> Eclipse > Run > Run Configurations > Environment > New  
Name: `PATH`  
Value: `$REPO_LOCATION/cova/src/main/resources/z3-4.5.0-x64-win/bin`


### - Linux
Currently, the repository only inclues Z3 for Ubuntu and Debian-8.5 64bit.

- **Userwide via OS**:  
Add the **LD_LIBRARY_PATH** Variable to .profile in your home directory and load that file to your current Environment:  
`echo "export LD_LIBRARY_PATH=\"$LD_LIBRARY_PATH:$REPO_LOCATION/cova/src/main/resources/z3-4.5.0-x64-ubuntu/bin"" >> ~/.profile
source ~/.profile`

- **Projectwide in Eclipse**:  
After importing COVA as maven project, you can specify the environment variable:
> Eclipse > Run > Run Configurations > Environment > New  
Name: `LD_LIBRARY_PATH`  
Value: `$REPO_LOCATION/cova/src/main/resources/z3-4.5.0-x64-ubuntu/bin` 

### - OSX
You need to add Z3 to `DYLD_LIBRARY_PATH` (untested)

## Build The Tool With Maven
Use 
`mvn install`
to build the tool and run all tests.

## Build The Tool with Eclipse
Simply import the project as maven project. Maven should take care of all reqired dependences.
> Eclipse> File> Import > Maven > Existing Maven Projects > *Enter the path to your local repository*  > Finish

## Running The Command-Line Tool (TODO)
### - Analyze Android Application
- **Standalone**
```
-android -jar <android platform jar> -apk <apk file>
```
- **List Of Command-Line Options(todo)**
- **Example Step By Step**
    - Make sure you have Java installed.   
    - Download a released tool from [hier](/release1.0.0.zip) and unzip it.  
    - You will find an executable .jar file `cova.jar` and a folder `config` containing a few of .txt files in the unzipped folder. 
    - Navigate in your command prompt to the unzipped folder.
    - Run the following command:
```
java -jar cova.jar -android -jar ".\androidPlatform" -apk ".\androidExample\androidExample.apk" -output_html ".\androidExample\app\src" -all
```
 - A new folder `target` is generated in the unzipped folder, navigate to  `htmlOutput\androidExample`.
    - Open `index.html` with your browser, you will see a list of classes like this. 
        ![Example1](/pics/Example1.PNG)
    - Click MainActivity.java, you will see the following computed constraint map for this class.
        ![Example2](/pics/Example2.PNG)
- **Together with FlowDroid**
```
-android -jar <android platform jar> -apk <apk file> -s false
```

### - Analyze Java Application
- **Standalone**
```
-java -app <app name> -cp <class path> -config <config files path>
```
- **List Of Command-Line Options(todo)**
- **Example Stey By Step**
    -  Make sure you have Java installed.   
    - Download a released tool from [hier](/release1.0.0.zip) and unzip it.  
    - You will find an executable .jar file `cova.jar` and a folder `config` containing a few of .txt files in the unzipped folder. 
    - Navigate in your command prompt to the unzipped folder.
    - Run the following command:
```
java -jar cova.jar -java -app javaExample -cp ".\javaExample\bin" -config ".\javaExample\config" -output_html ".\javaExample\src" -all
```
   - Navigate to `target\htmlOutput\javaExample`
   - Open `index.html` with your browser, you will see a list of classes. 
   - Click Main.java, you will see the following computed constraint map for this class.
    ![Example2](/pics/Example3.PNG)
