package cova;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cova.runner.AndroidApkAnalyzer;
import cova.runner.JavaAppAnalyzer;

public class Main {

  public static void main(String[] args) throws ParseException, IOException {
    Options options = new Options();
    options.addOption("h", "help", false, "Print this mesage");
    options.addOption("t", "timeout", true, "COVA analysis timout duration in minutes.");

    options.addOption("android", false, "Analyze Android APK");
    options.addOption("p", "platform", true,
        "Android: The location of the android platform jars.");
    options.addOption("apk", "apkLocation", true,
        "Android:  The location of the APK file and it can an be a single Apk file or a directory.");
    options.addOption("s", "standalone", true,
        "Android: <arg> = true, if runs cova in standalone mode. <arg> = false, if runs flowdroid and cova afterwards. The default value is false");
    options.addOption("expr", false,
        "Enable experimentation mode.");
    options.addOption("output_csv", false, "Android: Print results in CSV files.");

    options.addOption("java", false, "Analyze Java application");
    options.addOption("app", true, "Java: The name of the Java application.");
    options.addOption("main", true, "Java: The main class of the Java application.");
    options.addOption("lib", true, "Java: The location of rt.jar");
    options.addOption("cp", true, "Java: The class path of the Java application.");
    options.addOption("config", true,
        "The path of config files specified for your application: at least one of Configuration_APIs.txt, IO_APIs.txt and UICallback_APIs.txt.");

    // options for analysis
    options.addOption("ITaint", "impreciseTaintCreation", true,
        "<arg> = true, if enables ImpreciseTaintCreationRule.");
    options.addOption("CTaint", "concreteTaintCreation", true,
        "<arg> = true, if enables ConcreteTaintCreationRule.");
    options.addOption("CTA", "concreteTaintAtAssignStmt", true,
        "<arg> = true, if creates concrete taint at assign statement");
    options.addOption("CTR", "concreteTaintAtReturnStmt", true,
        "<arg> = true, if creates concrete taint at return statement");
    options.addOption("CTC", "concreteTaintAtCalleeOn", true,
        "<arg> = true, if creates concrete taint for parameters passing to method");
    options.addOption("ITP", "impreciseTaintPropagation", true,
        "<arg> = true, if enables ImprecisePropagationRule.");
    options.addOption("STP", "staticFieldPropagation", true,
        "<arg> = true, if enables StaticFieldPropagationRule.");
    options.addOption("all", false,
        "Enables all propagation rules. When this is enabled, options to turn on single rule will be ignored. This is the most precise configuration of the analysis.");

    // options for output files
    options.addOption("output_html", true,
        "Print results in HTML files, this option should be followed by the java source code path of your application.");
    options.addOption("output_jimple", false, "Print results in Jimple files.");
    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    HelpFormatter helper = new HelpFormatter();
    String cmdLineSyntax
        = "\nAnalyze Android APK: -android -config <config files path> -p <android platform jar> -apk <apk file>\n"
            + "\nAnalyze Java Application: -java -config <config files path> -app <app name> -cp <class path>"
        + "\n_________________________________________________________________________\n";
    if (cmd.hasOption('h')) {
      helper.printHelp(cmdLineSyntax, options);
      return;
    }
    if (!cmd.hasOption("android") && !cmd.hasOption("java")) {
      helper.printHelp(cmdLineSyntax, options);
      return;
    } else {
      if (cmd.hasOption("android")) {
        if (cmd.hasOption("java")) {
          helper.printHelp(cmdLineSyntax, options);
          return;
        } else {
            AndroidApkAnalyzer.main(args);
        }
      }
      if (cmd.hasOption("java")) {
        if (cmd.hasOption("android")) {
          helper.printHelp(cmdLineSyntax, options);
          return;
        } else {
          JavaAppAnalyzer.main(args);
        }
      }
    }
  }

}
