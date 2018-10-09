COVA - Constraint Analysis with [VASCO](https://github.com/rohanpadhye/vasco) 

## Obtaining The Tool 
You can either build COVA on your own with Maven or [download a release](...) from this repository.

## Bind Z3 Library
COVA uses Z3 for STM-Solving and you need at first to bind Z3 for running the tool or use a [docker image](https://hub.docker.com/r/linghui2016/z3maven/) (tested on Linux). 
You can find Z3-4.5.0 in the local directory `$REPO_LOCATION/cova/localLibs/` or downloand it from [the GitHub repostiory of Z3](https://github.com/Z3Prover/z3).  
### - Windows
Currently, the repository only includes Z3 for Windows 64bit.
There are two choices for you to bind z3:

- **Userwide via OS**: 
Add `$REPO_LOCATION/cova/localLibs/z3-4.5.0-x64-win/bin` to the system variable `PATH` of your operating system ([How do I set or change the PATH system variable?](https://www.java.com/en/download/help/path.xml)). You may need to retart your OS. 

- **Projectwide in Eclipse**: 
After importing COVA as maven project, you can specify the environment variable: 
> Eclipse > Run > Run Configurations > Environment > New  
Name: `PATH`  
Value: `$REPO_LOCATION/cova/localLibs/z3-4.5.0-x64-win/bin`


### - Linux
Currently, the repository only inclues Z3 for Ubuntu and Debian-8.5 64bit.

- **Userwide via OS**:  
Add the **LD_LIBRARY_PATH** Variable to .profile in your home directory and load that file to your current Environment:  
`echo "export LD_LIBRARY_PATH=\"$LD_LIBRARY_PATH:$REPO_LOCATION/cova/localLibs/z3-4.5.0-x64-ubuntu/bin"" >> ~/.profile
source ~/.profile`

- **Projectwide in Eclipse**:  
After importing COVA as maven project, you can specify the environment variable:
> Eclipse > Run > Run Configurations > Environment > New  
Name: `LD_LIBRARY_PATH`  
Value: `$REPO_LOCATION/cova/localLibs/z3-4.5.0-x64-ubuntu/bin` 

### - OSX
You need to add Z3 to `DYLD_LIBRARY_PATH` (untested)

## Build The Tool With Maven
- Install required local dependencies into your local maven repository with the script ``install_local_libs.*`` in ''$REPO_LOCATION/cova/localLibs''([Windows](https://github.com/secure-software-engineering/COVA/tree/master/cova/localLibs/install_local_libs.bat) or [Linux](https://github.com/secure-software-engineering/COVA/tree/master/cova/localLibs/install_local_libs.sh)). 
- run `mvn install` to build the tool and run all tests.

## Build The Tool with Eclipse
- Install required local dependencies into your local maven repository with the script ``install_local_libs.*`` in ''$REPO_LOCATION/cova/localLibs''([Windows](https://github.com/secure-software-engineering/COVA/tree/master/cova/localLibs/install_local_libs.bat) or [Linux](https://github.com/secure-software-engineering/COVA/tree/master/cova/localLibs/install_local_libs.sh)). 
- Simply import the project as maven project. Maven should take care of all reqired dependences.
> Eclipse> File> Import > Maven > Existing Maven Projects > *Enter the path to your local repository*  > Finish

## Running The Command-Line Tool 
- Make sure you have JAVA 8 installed. 
- Run the executabl jar with JAVA: ``java -jar cova.jar``. 
### - Analyze Android Application

You can run COVA with the option ``-android`` to get all options for analyzing an Android application.

**An Example Step By Step**
1. **Together with FlowDroid** (Default):

You can use COVA combined with FLowDroid to get the constraints under which a leak reported by FlowDroid may happen. 
Run cova with the following options:

``-android -config <config files path> -p <android platform path> -apk <apk file>``

   you can find: 
   - config files:``$REPO_LOCATION\cova\config``
   - android platforms (API 26-27): ``$REPO_LOCATION\cova\src\test\resources\androidPlatforms``
   - an example apk:``$REPO_LOCATION\constraintBench\androidApps\apks\Callbacks1.apk``

   The results are in JSON files located in ``$WORKING_DIRECTORY\covaOutput``

2. **Standalone**:

You can run COVA in standalone mode with the option ``s``. In this mode a constraint map will be computed. If you have the java source code of your application, you can get the constraint map printed next to each line of code in HTML sites with the option ``òutput_html``. Run cova with the following options:
``-android -config <config files path> -p <android platform path> -apk <apk file> -s "true" -output_html <source code path>``

   you can find: 
   - config files:``$REPO_LOCATION\cova\config``
   - android platforms (API 26-27): ``$REPO_LOCATION\cova\src\test\resources\androidPlatforms``
   - an example apk:``$REPO_LOCATION\constraintBench\androidApps\apks\Callbacks1.apk``
   - source code of this apk: ``$REPO_LOCATION\constraintBench\androidApps\sourceCode\Callbacks1``

   The results are in HTML files located in ``$WORKING_DIRECTORY\htmlOutput``
   
### - Analyze Java Application

You can run COVA with the option ``-java`` to get all options for analyzing a Java application.

**An Example Stey By Step**

Run cova with the following options:

``-java -config <config files path> -app <app name> -cp <app class path> -output_html <source code path>`` 
