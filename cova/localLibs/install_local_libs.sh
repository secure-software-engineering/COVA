#!/bin/bash
mvn install:install-file -Dfile=com.microsoft.z3.jar -DgroupId=z3Prover -DartifactId=z3 -Dversion=4.5.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=boomerang.jar -DgroupId=de.fraunhofer.iem -DartifactId=boomerangPDS -Dversion=1.0.0 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=soot-infoflow.jar -DgroupId=de.tud.sse -DartifactId=soot-infoflow -Dversion=2.5.1 -Dpackaging=jar -DgeneratePom=true
mvn install:install-file -Dfile=soot-infoflow-android.jar -DgroupId=de.tud.sse -DartifactId=soot-infoflow-android -Dversion=2.5.1 -Dpackaging=jar -DgeneratePom=true