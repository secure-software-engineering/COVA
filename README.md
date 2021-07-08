<p align="left">
<img src="cova-logo.PNG" width="600">
</p> 


# COVA[![Build Status](https://travis-ci.com/secure-software-engineering/COVA.svg?branch=master)](https://travis-ci.com/secure-software-engineering/COVA)
COVA is a static analysis tool to compute path constraints based on user-defined APIs. COVA was created for our paper ([ASE 2019](https://2019.ase-conferences.org/)) [A Qualitative Analysis of Android Taint-Analysis Results](https://linghuiluo.github.io/ASE19Cova.pdf). 
- The Android apps we evaluated in the paper can be found on kaggle.com ([click to download](https://www.kaggle.com/covaanalyst1/cova-dataset)) 
- The directory [`cova`](cova/) contains the source code of COVA.
- The directory [`constraintBench`](constraintBench/) contains the micro-benchmark used for COVA.
- The directory [`covaIDE`](covaIDE/) contains the code for COVA IDE support.
- The lists of constraint-APIs can be found in directory [`cova/config/`](cova/config/): click to see [UICallback-related APIs](cova/config/UICallback_APIs.txt), [Configuration-related APIs](cova/config/Configuration_APIs.txt) and [IO-related APIs](cova/config/IO_APIs.txt)
- The tests for running constraintBench for COVA can be found in directory [`cova/src/test/java/constraintBenchTestSuite/`](cova/src/test/java/constraintBenchTestSuite)
- For more detailed description of COVA's underlying analysis, a long version of the paper can be found here ([click to download](https://github.com/covaanalyst/cova-root/blob/master/longversion.pdf))
## API Documentation
There is a generated [JavaDoc](https://secure-software-engineering.github.io/COVA/docs/) available.

## How to Build COVA?
COVA is implemented as a maven project. However, since some dependencies COVA uses do not have public maven repositories, to build COVA you need to follow the steps below:

## 1. Bind Z3 Library
COVA uses Z3 for STM-Solving so at first you need to bind the Z3 library on your machine to run COVA or use a [docker container](/cova/config/Dockerfile)
You can find Z3-4.8.9 in the local directory `$REPO_LOCATION/cova/localLibs/` or downloand it from [the GitHub repostiory of Z3](https://github.com/Z3Prover/z3).  

### Docker container
1. `docker build -t cova:test ./cova/config/` to build the [docker image](/cova/config/Dockerfile).
2. `docker run -it -v $(pwd):/mnt cova:test /bin/bash` to mount the container and create the container shell.
3. `git clone https://github.com/secure-software-engineering/COVA.git && cd COVA` to clone the repo into the docker container.
4. `./cova/localLibs/install_local_libs.sh` to install the required libraries.
5. `mvn install` to build the project.

### Windows
Currently, the repository only includes Z3 for Windows 64bit.
There are two choices for you to bind z3:

- **Userwide via OS**: 
Add `$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-win/bin` to the system variable `PATH` of your operating system ([How do I set or change the PATH system variable?](https://www.java.com/en/download/help/path.xml)). You may need to restart your OS. 

- **Projectwide in Eclipse**: 
After importing COVA as maven project, you can specify the environment variable: 
> Eclipse > Run > Run Configurations > Environment > New  
Name: `PATH`  
Value: `$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-win/bin`


### Linux
Currently, the repository only includes Z3 for Ubuntu and Debian-8.5 64bit.

**via OS**:  

Ubuntu based distributions:

Execute the following command in yout command line: (you can set LD_LIBRARY_PATH otherwise only in an interactive shell since Ubuntu 9.04)
    `REPO_LOCATION=$("pwd") && echo "$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-ubuntu-16.04/bin" | sudo tee /etc/ld.so.conf.d/cova.conf && sudo ldconfig`

Other distributions:

Add the **LD_LIBRARY_PATH** Variable to ~/.profile( or ~/.xprofile): cd into Repository:
`REPO_LOCATION=$("pwd") && echo "export LD_LIBRARY_PATH=\"\$LD_LIBRARY_PATH:$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-ubuntu-16.04/bin\"" >> ~/.profile;`
Load the edited file to your current environment (e.g. `source ~/.profile` or restart your user session). 


**Projectwide in Eclipse for Ubuntu64**:  
After importing COVA as maven project, you can specify the environment variable (change $REPO_LOCATION according to the location of the repository):
> Eclipse > Run > Run Configurations > Environment > New  
Name: `LD_LIBRARY_PATH`  
Value: `$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-ubuntu-16.04/bin` 

**Projectwide in IntelliJ**
Set [java.library.path] in the VM options input field in the Run/Debug Configurations dialog.
`$REPO_LOCATION/cova/localLibs/z3-4.8.9-x64-ubuntu-16.04/bin`

### OSX
You need to add Z3 to `DYLD_LIBRARY_PATH` (untested)

## 2.1 Build The Tool With Maven
- Install required local dependencies into your local maven repository with the script ``install_local_libs.*`` in `$REPO_LOCATION/cova/localLibs` (Windows using [`install_local_libs.bat`](cova/localLibs/install_local_libs.bat) or Linux using [`install_local_libs.sh`](cova/localLibs/install_local_libs.sh)). 
- Return to project root to run `mvn install` to build the tool and run all tests.
- If you want to skip tests just run `mvn -DskipTests install`

## 2.2 Build The Tool with Eclipse
- Install required local dependencies into your local maven repository with the script ``install_local_libs.*`` in `$REPO_LOCATION/cova/localLibs` (Windows using `install_local_libs.bat` or Linux using `install_local_libs.sh`). 
- Simply import the project as maven project. Maven should take care of all reqired dependences.
> Eclipse> File> Import > Maven > Existing Maven Projects > *Enter the path to your local repository*  > Finish

## 3. Running The Command-Line Tool 
- Make sure you have JAVA 8 installed and bound Z3. 
- Run the executable jar (found in [$REPO_LOCATION/cova/targets/] with JAVA: ``java -jar cova.jar`` .
- You can run COVA with the option ``-android`` to get all options for analyzing an Android application or with the option ``-java`` for normal Java application.
- Here is an example explained step by step:

(1) **Together with FlowDroid** (Default):

You can use COVA combined with FLowDroid to get the constraints under which a leak reported by FlowDroid may happen. 

Run cova with the following options (put after ``java -jar cova.jar``):

``-android -config <config files path> -p <android platform path> -apk <apk file>``

   for testing this you can find: 
   - config files:[``$REPO_LOCATION/cova/config``](cova/config)
   - android platforms (API 26-27): [``$REPO_LOCATION/cova/src/test/resources/androidPlatforms``](cova/src/test/resources/androidPlatforms)
   - an example apk:[``$REPO_LOCATION/constraintBench/androidApps/apks/Callbacks1.apk``](constraintBench/androidApps/apks/)

   The results are in JSON files located in ``$WORKING_DIRECTORY/covaOutput/jsonOutput``

(2) **Standalone**:

You can run COVA in standalone mode with the option ``s``. In this mode a constraint map will be computed. If you have the java source code of your application, you can get the constraint map printed next to each line of code in HTML sites with the option `-output_html`. 

Run cova with the following options:

``-android -config <config files path> -p <android platform path> -apk <apk file> -s "true" -output_html <source code path>``

   for testing this you can find: 
   - config files:``$REPO_LOCATION/cova/config``
   - android platforms (API 26-27): ``$REPO_LOCATION/cova/src/test/resources/androidPlatforms``
   - an example apk:``$REPO_LOCATION/constraintBench/androidApps/apks/Callbacks1.apk``
   - source code of this apk: ``$REPO_LOCATION/constraintBench/androidApps/sourceCode/Callbacks1``

   The results are in HTML files located in ``$WORKING_DIRECTORY/covaOutput/htmlOutput``
   
## 4. Run cova_automatic
1. Install the following tools:
   - Appium (http://appium.io/)
   - Android SDK with build-tools (https://developer.android.com/studio/releases/build-tools) has to be installed and the ANDROID_HOME environemnt variable has to be set.
2. Set up enviroment variables:
   - `ANDROID_HOME`: Android SDK Location 
   - `PATH` (Windows) or `LD_LIBRARY_PATH` (Linux): `$REPO_LOCATION/cova/localLibs/$Z3_FOLDER/bin`. See the section ''Bind Z3 Library'' above.
3. start Appium with default configuration 

4. run on an apk with the following command: 

`java -jar cova_automatic-$VERSION.jar -apk $APKS_PATH -config $REPO_LOCATION/cova/config -platform $ANDROID_PLATFORMS`

5. The results of automatic tests appear in `$user.home/cova_test_results`. On Linux this is `~/cova_test_results`, on Windows it is `C:\Users\%username%\cova_test_results`.

## Code Style
The submodule cova follows Google's code styleguide. We use fmt-maven-plugin to format the code.  
Execute this before making your pull request `mvn com.coveo:fmt-maven-plugin:format`
