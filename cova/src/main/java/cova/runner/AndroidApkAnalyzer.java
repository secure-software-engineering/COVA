package cova.runner;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xmlpull.v1.XmlPullParserException;

import soot.jimple.infoflow.results.InfoflowResults;

import cova.core.Aliasing;
import cova.core.SMTSolverZ3;
import cova.data.CombinedResult;
import cova.data.MetaData;
import cova.reporter.ConstraintReporter;
import cova.reporter.ResultPrinter;
import cova.setup.RunCova;
import cova.setup.RunFlowDroid;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForAndroid;

/**
 * Analyzer for Android application.
 * 
 *
 */
public class AndroidApkAnalyzer {
  private static String androidJarPath = "";
  private static String apkFilePath = "";
  private static boolean standalone = true;
  private static String sourceCodePath = "";
  private static int timeDuration = 0;
  private static boolean outputCSV = false;
  private static boolean isExper = false;
  private static CombinedResult results = null;
  private static ConstraintReporter reporter = null;

  enum Status {
    ANALYZED, TIMEDOUT, FAILED
  }

  public static void main(String[] args) throws IOException, ParseException {
    Config config = new DefaultConfigForAndroid();
    if (parseArgs(args, config)) {
      File apkFileFolder = new File(apkFilePath);
      boolean foundApks = false;
      if (apkFileFolder.isDirectory()) {
        // The given path is a directory
        File[] listOfApks = apkFileFolder.listFiles();
        // analyze apks one by one
        for (File apk : listOfApks) {
          if (apk.getName().endsWith("apk")) {
            foundApks = true;
            analyze(apk, config);
          }
        }
      } else {
        // The given path is a single apk file path
        if (Files.getFileExtension(apkFilePath).equals("apk")) {
          foundApks = true;
          File apk = new File(apkFilePath);
          analyze(apk, config);
        } else {
          throw new IOException(apkFilePath + " is not .apk file.");
        }
      }
      if (foundApks) {
        System.out.println("Analysis is done!");
      } else {
        throw new IOException("No APK was found in given directory " + apkFilePath);
      }
    }
  }

  public static boolean parseArgs(String[] args, Config config) throws ParseException {
    Options options = new Options();
    // standard options
    options.addOption("h", "help", false, "Print this message.");
    options.addOption("android", false, "Analyze Android APK");
    options.addOption("p", "platform", true, "The location of the android platform jars.");
    options.addOption("apk", "apkLocation", true,
        "The location of the APK file. It can be a single Apk file or a directory.");
    options.addOption("s", "standalone", true,
        "<arg> = true, to run cova in standalone mode. <arg> = false (default ), to run flowdroid and cova afterwards.");
    options.addOption("t", "timeout", true, "COVA analysis timout duration in minutes.");
    options.addOption("expr", false, "Enable experimentation mode");

    // options for analysis
    options.addOption("ITaint", "impreciseTaintCreation", true, "<arg> = true, if enables ImpreciseTaintCreationRule.");
    options.addOption("CTaint", "concreteTaintCreation", true, "<arg> = true, if enables ConcreteTaintCreationRule.");
    options.addOption("CTA", "concreteTaintAtAssignStmt", true,
        "<arg> = true, if creates concrete taint at assign statement");
    options.addOption("CTR", "concreteTaintAtReturnStmt", true,
        "<arg> = true, if creates concrete taint at return statement");
    options.addOption("CTC", "concreteTaintAtCalleeOn", true,
        "<arg> = true, if creates concrete taint for parameters passing to method");
    options.addOption("ITP", "impreciseTaintPropagation", true, "<arg> = true, if enables ImprecisePropagationRule.");
    options.addOption("STP", "staticFieldPropagation", true, "<arg> = true, if enables StaticFieldPropagationRule.");
    options.addOption("all", "all", false,
        "Enables all rules. When this is enabled, options to turn on single rule will be ignored. This is the most precise configuration of the analysis.");
    options.addOption("config", true,
        "The path of config files specified for your application: at least one of Configuration_APIs.txt, IO_APIs.txt and UICallback_APIs.txt.");

    // options for output files
    options.addOption("output_html", true,
        "Print results in HTML files, this option should be followed by the java source code path of your application.");
    options.addOption("output_jimple", false, "Print results in Jimple files.");
    options.addOption("output_csv", false, "Print results in CSV files.");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);

    HelpFormatter helper = new HelpFormatter();
    String cmdLineSyntax = "-android -config <config files path> -p <android platform jar> -apk <apk file>";
    if (cmd.hasOption("h")) {
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("p")) {
      androidJarPath = cmd.getOptionValue("p");
    } else {
      System.err.println("Option -p is required");
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("apk")) {
      apkFilePath = cmd.getOptionValue("apk");
    } else {
      System.err.println("Option -apk is required");
      helper.printHelp(cmdLineSyntax, options);
      return false;
    }
    if (cmd.hasOption("s")) {
      standalone = Boolean.parseBoolean(cmd.getOptionValue("s"));
    } else {
      standalone = false;
    }
    if (cmd.hasOption("t")) {
      timeDuration = Integer.parseInt(cmd.getOptionValue('t'));
      config.setTimeOutOn(timeDuration * 60);
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
    if (cmd.hasOption("output_csv")) {
      outputCSV = true;
    }
    if (cmd.hasOption("expr")) {
      isExper = true;
      if (!outputCSV) {
        outputCSV = true;
      }
    }
    return true;
  }

  private static void analyze(File apk, Config config) throws IOException {
    try {
      boolean timedout = analyzeApk(androidJarPath, apk.getCanonicalPath(), sourceCodePath, standalone, config);
      System.gc();
      Status status = Status.ANALYZED;
      if (timedout) {
        status = Status.TIMEDOUT;
      }
      if (isExper) {
        moveApkAfterAnalysis(apk.getParentFile(), apk, status);
      }
    } catch (RuntimeException e) {
      e.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (apk.exists() & isExper) {
      System.gc();
      if (isExper) {
        moveApkAfterAnalysis(apk.getParentFile(), apk, Status.FAILED);
      }
    }
  }

  private static void moveApkAfterAnalysis(File apkFileFolder, File apk, Status status) throws IOException {
    String failedApkPath = apkFileFolder + File.separator + "failed";
    File failedDir = new File(failedApkPath);
    if (!failedDir.exists()) {
      failedDir.mkdirs();
    }
    String analyzedApkPath = apkFileFolder + File.separator + "analyzed";
    File analyzedDir = new File(analyzedApkPath);
    if (!analyzedDir.exists()) {
      analyzedDir.mkdirs();
    }
    String timeoutApkPath = apkFileFolder + File.separator + "timeout";
    File timeoutDir = new File(timeoutApkPath);
    if (!timeoutDir.exists()) {
      timeoutDir.mkdirs();
    }
    File target = new File(analyzedApkPath + File.separator + apk.getName());
    if (status.equals(Status.TIMEDOUT)) {
      target = new File(timeoutApkPath + File.separator + apk.getName());
    }
    if (status.equals(Status.FAILED)) {
      target = new File(failedApkPath + File.separator + apk.getName());
    }
    Files.move(apk, target);
  }

  public static boolean analyzeApk(String androidJarPath, String apkFilePath, String sourceCodePath, boolean standalone,
      Config config) {
    try {
      // validate if the given path exists
      androidJarPath = new File(androidJarPath).getCanonicalPath();
      apkFilePath = new File(apkFilePath).getCanonicalPath();
      if (config.isWriteHtmlOutput()) {
        sourceCodePath = new File(sourceCodePath).getCanonicalPath();
      }
    } catch (IOException e1) {
      e1.printStackTrace();
    }
    boolean timeout = false;
    try {
      if (Files.getFileExtension(apkFilePath).equals("apk")) {
        if (!standalone) {
          if (!outputCSV) {
            // run flowdroid
            RunFlowDroid.run(apkFilePath, androidJarPath, config.getConfigDir());
            InfoflowResults infoFlowResults = RunFlowDroid.getInfoFlowResults();
            // run cova
            reporter = RunCova.runForAndroid(apkFilePath, androidJarPath, sourceCodePath, standalone,
                RunFlowDroid.getCallbacks(), config);
            if (infoFlowResults.getResults() != null) {
              CombinedResult combinedResults = new CombinedResult(new MetaData(Files.getNameWithoutExtension(apkFilePath)),
                  infoFlowResults.getResults(), reporter);
              results = combinedResults;
              combinedResults.serialize();
            } else {
              System.out.print("No leak was found by FlowDroid.");
            }
          } else {
            // run flowdroid
            long startTime = System.currentTimeMillis();
            RunFlowDroid.run(apkFilePath, androidJarPath, config.getConfigDir());
            long endTime = System.currentTimeMillis();
            double ftime = (double) (endTime - startTime) / 1000;
            InfoflowResults infoFlowResults = RunFlowDroid.getInfoFlowResults();
            // run cova
            startTime = System.currentTimeMillis();
            ConstraintReporter reporter = RunCova.runForAndroid(apkFilePath, androidJarPath, sourceCodePath, standalone,
                RunFlowDroid.getCallbacks(), config);
            endTime = System.currentTimeMillis();
            double covaTime = (double) (endTime - startTime) / 1000;
            String apkName = Files.getNameWithoutExtension(apkFilePath);
            // compute various data
            // compute dexFile size
            ZipFile apkFile = new ZipFile(apkFilePath);
            Enumeration<? extends ZipEntry> entries = apkFile.entries();
            long dexSizeTotal = 0;
            // sum the size of all .dex files in apk.
            while (entries.hasMoreElements()) {
              ZipEntry entry = entries.nextElement();
              if (entry.getName().endsWith(".dex")) {
                dexSizeTotal += entry.getSize();
              }
            }
            long size = dexSizeTotal / 1024;// size in KB
            int reachableMethods = reporter.getNumberOfReachableMethods();
            apkFile.close();
            timeout = reporter.isTimeout();
            double failedAliasing = Aliasing.failedAliasing();
            int z3Queries = SMTSolverZ3.getInstance().getCount();
            double z3Time = SMTSolverZ3.getInstance().getUsedTimeInSeconds();
            MetaData meta = new cova.data.MetaData(apkName, size, reachableMethods, ftime, timeout, covaTime,
                z3Time, failedAliasing, z3Queries);
            // print results in .txt
            if (infoFlowResults.getResults() != null) {
              CombinedResult combinedResults = new CombinedResult(meta, infoFlowResults.getResults(), reporter);
              results = combinedResults;
              ResultPrinter printer = new ResultPrinter(combinedResults);
              printer.print(timeout);
            }
          }
        } else {
          reporter = RunCova.runForAndroid(apkFilePath, androidJarPath, sourceCodePath, standalone,
              RunFlowDroid.getCallbacks(), config);
          timeout = reporter.isTimeout();
          reporter.printResultOfClasses();
        }
      }
    } catch (IOException | XmlPullParserException e) {
      e.printStackTrace();
    }
    return timeout;
  }

  public static CombinedResult getCombinedResults() {
    return results;
  }

  public static ConstraintReporter getReporter() {
    return reporter;
  }
}
