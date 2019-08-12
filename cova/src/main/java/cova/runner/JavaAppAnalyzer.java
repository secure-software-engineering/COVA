package cova.runner;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cova.reporter.ConstraintReporter;
import cova.setup.RunCova;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForJava;

/**
 * Analyzer for Java application.
 * 
 *
 */
public class JavaAppAnalyzer {
  private static String javaLibPath = "";
  private static String appName = "";
  private static String appClassPath = "";
  private static String sourceCodePath = "";
  private static String configFilePath = "";
  private static String mainClass = "";

  public static void main(String[] args) throws ParseException {
    Config config = new DefaultConfigForJava();
    if (parseArgs(args, config)) {
      analyzeApp(appName, appClassPath, sourceCodePath, javaLibPath, configFilePath, mainClass,
          config);
    }
  }

  public static boolean parseArgs(String[] args, Config config) throws ParseException {
    Options options = new Options();
    options.addOption("h", "help", false, "Print this mesage");
    options.addOption("java", false, "Analyze Java application");
    options.addOption("app", true, "The name of the Java application.");
    options.addOption("main", true, "The main class of the Java application.");
    options.addOption("lib", true, "The location of rt.jar");
    options.addOption("cp", true, "The class path of the Java application.");
    options.addOption("config", true,
        "The path of config files specified for your application: at least one of Configuration_APIs.txt, IO_APIs.txt and UICallback_APIs.txt.");
    options.addOption("t", "timeout", true, "COVA analysis timout duration in minutes.");

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
        "Enables all rules. When this is enabled, options to turn on single rule will be ignored. This is the most precise configuration of the analysis.");

    // options for output files
    options.addOption("output_html", true,
        "Print results in HTML files, this option should be followed by the java source code path of your application.");
    options.addOption("output_jimple", false, "Print results in Jimple files.");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    HelpFormatter helper = new HelpFormatter();
    String cmdLineSyntax = "-java -config <config files path> -app <app name> -cp <class path>";
    if (cmd.hasOption('h')) {
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("app")) {
      appName = cmd.getOptionValue("app");
    } else {
      System.err.println("Option -app is required");
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("main")) {
      mainClass = cmd.getOptionValue("main");
    }
    if (cmd.hasOption("lib")) {
      javaLibPath = cmd.getOptionValue("lib");
    } else {
      javaLibPath = System.getProperty("java.home") + File.separator + "lib" + File.separator
          + "rt.jar";
    }
    if (cmd.hasOption("cp")) {
      appClassPath = cmd.getOptionValue("cp");
    } else {
      System.err.println("Option -cp is required");
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("config")) {
      configFilePath = cmd.getOptionValue("config");
    } else {
      System.err.println("Option -config is required");
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("t")) {
      int timeout = Integer.parseInt(cmd.getOptionValue('t'));
      config.setTimeOutOn(timeout * 60);
    }
    if (cmd.hasOption("all")) {
      config.turnOnAllRules();
    } else {
      if (cmd.hasOption("ITaint")) {
        boolean value = Boolean.parseBoolean(cmd.getOptionValue("ITaint"));
        config.setImpreciseTaintCreationRuleOn(value);
      }
      if (cmd.hasOption("CTaint")) {
        boolean value = Boolean.parseBoolean(cmd.getOptionValue("CTaint"));
        config.setConcreteTaintCreationRuleOn(value);
      }
      if (config.isConcreteTaintCreationRuleOn()) {
        boolean cta = true;
        if (cmd.hasOption("CTA")) {
          cta = Boolean.parseBoolean(cmd.getOptionValue("CTA"));
        }
        boolean ctr = true;
        if (cmd.hasOption("CTR")) {
          ctr = Boolean.parseBoolean(cmd.getOptionValue("CTR"));
        }
        boolean ctc = true;
        if (cmd.hasOption("CTC")) {
          ctc = Boolean.parseBoolean(cmd.getOptionValue("CTC"));
        }
        config.setConcreteTaintCreationRuleOn(true, cta, ctr, ctc);
      } else {
        config.setConcreteTaintCreationRuleOn(false);
      }
      if (cmd.hasOption("STP")) {
        boolean value = Boolean.parseBoolean(cmd.getOptionValue("STP"));
        config.setStaticFieldPropagationRuleOn(value);
      }
      if (cmd.hasOption("ITP")) {
        boolean value = Boolean.parseBoolean(cmd.getOptionValue("ITP"));
        config.setImprecisePropagationRuleOn(value);
      }
    }
    if (cmd.hasOption("config")) {
      config.setConfigDir(cmd.getOptionValue("config"));
    }
    if (cmd.hasOption("output_html")) {
      config.setWriteHtmlOutput(true);
      sourceCodePath = cmd.getOptionValue("output_html");
    }
    if (cmd.hasOption("output_jimple")) {
      config.setWriteJimpleOutput(true);
    }
    return true;
  }

  public static ConstraintReporter analyzeApp(String appName, String appClassPath,
      String sourceCodePath,
      String libPath, String configFilePath, String mainClass, Config config) {
    try {
      // validate if the given path exists
      appClassPath = new File(appClassPath).getCanonicalPath();
      libPath = new File(libPath).getCanonicalPath();
      if (config.isWriteHtmlOutput()) {
        sourceCodePath = new File(sourceCodePath).getCanonicalPath();
      }
      File configFile = new File(configFilePath);
      configFilePath = configFile.getCanonicalPath();
      if (!configFile.isDirectory()) {
        throw new IOException("The given path " + configFilePath + " is not a directory!");
      } else {
        if (configFile.listFiles().length == 0) {
          throw new IOException("No config file exists in the given path. " + configFilePath);
        }
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    // run cova
    ConstraintReporter reporter
        = RunCova.runForJava(appName, appClassPath,
        sourceCodePath, libPath, configFilePath, config, mainClass);
    reporter.printResultOfClasses();
    return reporter;
  }
}
