package cova.automatic;

import brut.common.BrutException;
import cova.automatic.apk.ApkSignHelper;
import cova.automatic.apk.ApktoolMapper;
import cova.automatic.apk.aapt.AaptHelper;
import cova.automatic.executor.Appium;
import cova.automatic.instrument.SootInstrumenter;
import cova.automatic.results.ConstraintInformation;
import cova.automatic.results.ConstraintInformationReporter;
import cova.runner.AndroidApkAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForAndroid;
import cova.source.IdManager;
import cova.source.SourceInformation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

public class AutomaticRunner {

  public static final String PRE_STRING = "COVA_CONSTRAINT_INFORMATION";

  private static Path apkFile;
  private static Path configDir;
  private static Path platformDir;
  private static Path sourceCodeDir;

  public static void main(String[] args)
      throws IOException, BrutException, UnrecoverableKeyException, KeyStoreException,
          NoSuchAlgorithmException, CertificateException, InterruptedException,
          XmlPullParserException, ParseException {

    parseArgs(args);

    Path jarPath = platformDir.resolve("android-27/android.jar");

    Path tmpDir = Files.createTempDirectory("cova_automatic_");

    Path targetApk = tmpDir.resolve(apkFile.getFileName());
    Path signedApk = tmpDir.resolve(apkFile.getFileName() + "-signed.apk");
    Path alignedApk = tmpDir.resolve(apkFile.getFileName() + "-aligned.apk");
    Path recordingFile = tmpDir.resolve(apkFile.getFileName() + ".mp4");

    ApkSignHelper apkSignHelper = new ApkSignHelper();
    SootInstrumenter instrumenter = new SootInstrumenter();

    // Instrument apk with logcat outputs
    instrumenter.instrument(apkFile, targetApk, platformDir);

    // sign instrumented apk
    apkSignHelper.sign(targetApk, signedApk, alignedApk);

    // Get main activity from manifest
    ProcessManifest manifest = new ProcessManifest(targetApk.toFile());
    String mainActivity = null;
    for (AXmlNode activity : manifest.getActivities()) {
      for (AXmlNode c : activity.getChildren()) {
        for (AXmlNode c2 : c.getChildren()) {
          if ("android.intent.action.MAIN".equals(c2.getAttribute("name").getValue())) {
            mainActivity = (String) activity.getAttribute("name").getValue();
          }
        }
      }
    }

    // get id to string mappings of apk
    Map<String, Map<Integer, String>> baseMapping = ApktoolMapper.getMapping(apkFile);
    Map<Integer, String> mapping = baseMapping.get("mapping");
    Map<Integer, String> layoutMapping = baseMapping.get("layoutMapping");
    Map<Integer, String> idMapping = baseMapping.get("idMapping");

    // insert mappings into cova
    for (Entry<Integer, String> layoutE : layoutMapping.entrySet()) {
      IdManager.getInstance().getLayouts().put(layoutE.getKey(), layoutE.getValue());
    }

    for (Entry<Integer, String> idE : idMapping.entrySet()) {
      IdManager.getInstance().getIds().put(idE.getKey(), idE.getValue());
    }

    // Parse xml events and insert into cova
    AaptHelper aapt = new AaptHelper();
    Map<Integer, List<SourceInformation>> sourceInfos = aapt.listCallbacks(apkFile, layoutMapping);
    for (Entry<Integer, List<SourceInformation>> infoE : sourceInfos.entrySet()) {
      IdManager.getInstance().getSources().put(infoE.getKey(), infoE.getValue());
    }

    // Run cova
    Config config = new DefaultConfigForAndroid();
    config.setStringTaintCreationRuleOn(true);
    config.setConfigDir(configDir.toString());
    config.setWriteJimpleOutput(true);

    // Enable dynamic ids
    IdManager.getInstance().enable();

    String sourceCodeDirStr = null;
    if (sourceCodeDir != null) {
      sourceCodeDirStr = sourceCodeDir.toString();
    }
    AndroidApkAnalyzer.analyzeApk(
        jarPath.toString(), targetApk.toString(), sourceCodeDirStr, true, config);

    List<ConstraintInformation> information = ConstraintInformationReporter.getInformation(mapping);

    for (int i = 0; i < information.size(); i++) {
      ConstraintInformation info = information.get(i);
      System.out.println("[" + i + "]");
      System.out.println(info.getClazz());
      System.out.println(info.getJavaLineNumber() + ": " + info.getUnit());
      System.out.println(info.getConstraint().toReadableString());
      System.out.println(info.getConstraintMap());
      System.out.println();
    }
    System.out.println("Choose between 0 and " + (information.size() - 1));

    Scanner in = new Scanner(System.in);
    int choosen = in.nextInt();
    in.close();
    System.out.println("Choosen: " + choosen);
    ConstraintInformation choosenInfo = information.get(choosen);
    System.out.println(choosenInfo.getOutput());
    System.out.println(choosenInfo.getConstraintMap());

    System.out.println("Main activity: " + mainActivity);

    // Get possible paths to get to target
    ActivityTraverser traverser = new ActivityTraverser(information, choosenInfo, mainActivity);
    traverser.traverse();
    List<List<ConstraintInformation>> paths = traverser.getPaths();

    // Check if one path reaches destination
    boolean reachedDestination = false;
    List<String> logs = null;
    for (List<ConstraintInformation> path : paths) {

      logs =
          Appium.executeApp(
              alignedApk,
              recordingFile,
              path,
              IdManager.getInstance().getActivityToIdMapping(),
              IdManager.getInstance().getLayouts());
      if (logs.contains(choosenInfo.getOutput())) {
        reachedDestination = true;
        break;
      }
    }
    if (reachedDestination) {
      System.out.println("Reached Target");
    } else {
      for (String log : logs) {
        System.out.println(log);
      }
      System.out.println("Did not reach target");
    }
  }

  public static void parseArgs(String[] args) throws ParseException {
    Options options = new Options();
    // standard options
    options.addOption("h", "help", false, "Print this message.");
    options.addOption("p", "platform", true, "The location of the android platform jars.");
    options.addOption(
        "apk",
        "apkLocation",
        true,
        "The location of the APK file. It can be a single Apk file or a directory.");
    options.addOption(
        "config",
        true,
        "The path of config files specified for your application: at least one of Configuration_APIs.txt, IO_APIs.txt and UICallback_APIs.txt.");
    options.addOption(
        "output_html",
        true,
        "Print results in HTML files, this option should be followed by the java source code path of your application.");

    CommandLineParser parser = new DefaultParser();
    CommandLine cmd = parser.parse(options, args);
    apkFile = Paths.get(cmd.getOptionValue("apk"));
    configDir = Paths.get(cmd.getOptionValue("config"));
    platformDir = Paths.get(cmd.getOptionValue("platform"));
    if (cmd.hasOption("output_html")) {
      sourceCodeDir = Paths.get(cmd.getOptionValue("output_html"));
    }
  }
}
