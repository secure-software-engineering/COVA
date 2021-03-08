package cova.automatic;

import brut.androlib.AndrolibException;
import brut.directory.DirectoryException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import cova.automatic.data.AnalysisResult;
import cova.automatic.data.TestInput;
import cova.automatic.data.TestResult;
import cova.automatic.data.gson.GsonAnalysisResult;
import cova.automatic.executor.Appium;
import cova.automatic.executor.AutomaticRunner;
import cova.automatic.results.ConstraintInformation;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map.Entry;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

public class RunAll {

  public static final String DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss";
  private static final Logger logger = LoggerFactory.getLogger(RunAll.class);

  public static void main(String[] args)
      throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
          CertificateException, AndrolibException, DirectoryException, IOException,
          XmlPullParserException, InterruptedException, ParseException {

    Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

    AnalysisResult baseResult = AutomaticRunner.doAnalysisForArgs(args);
    Path baseDir = Paths.get(System.getProperty("user.home")).resolve("cova_test_results");
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat df = new SimpleDateFormat(DATE_FORMAT);
    String currentTime = df.format(cal.getTime());

    Path targetDir = baseDir.resolve(baseResult.getApkPath().getFileName()).resolve(currentTime);
    Files.createDirectories(targetDir);

    Path targetApkPath = targetDir.resolve(baseResult.getApkPath().getFileName());
    Files.copy(baseResult.getApkPath(), targetApkPath);

    Path jsonResultPath = targetDir.resolve("result.json");
    GsonAnalysisResult gsonResult = new GsonAnalysisResult(baseResult);
    try (FileWriter writer = new FileWriter(jsonResultPath.toFile())) {
      gson.toJson(gsonResult, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }

    Path htmlPath = targetDir.resolve("result.htm");
    try (PrintStream htmlPs = new PrintStream(htmlPath.toFile())) {
      htmlPs.println("<table>");
      htmlPs.println("<tr><th>Constraint</th><th>Target</th><th>Reached</th><th>Values</th></tr>");
      Appium appium = Appium.setUp(baseResult.getAppiumURL(), baseResult.getApkPath());
      int i = 0;
      for (ConstraintInformation c : baseResult.getConstraints()) {

        Path runPath = targetDir.resolve("" + i);
        Files.createDirectories(runPath);
        Path videoPath = runPath.resolve("video.mp4");
        Path logsPath = runPath.resolve("logs.txt");
        Path resultPath = runPath.resolve("result.txt");
        Path jsonPath = runPath.resolve("information.json");

        TestInput input = new TestInput(baseResult, c);
        logger.info("[" + i + "]");
        logger.info(c.getOutput());
        logger.info(c.getConstraintMap().toString());

        TestResult result = AutomaticRunner.testApp(input, appium, videoPath);
        if (result == null) {
          logger.error("path not found");
          i++;
          continue;
        }
        if (result.isReachedDestination()) {
          Files.write(resultPath, "TARGET_REACHED".getBytes());
        } else {
          Files.write(resultPath, "TARGET_NOT_REACHED".getBytes());
        }
        try (PrintStream ps = new PrintStream(logsPath.toFile())) {
          for (String log : result.getLogs()) {
            ps.println(log);
          }
        }

        try (FileWriter writer = new FileWriter(jsonPath.toFile())) {
          gson.toJson(result, writer);
        } catch (IOException e) {
          e.printStackTrace();
        }
        logger.info("Target reached: ");
        logger.info("" + result.isReachedDestination());
        htmlPs.println("<tr>");
        htmlPs.println("<td>");
        htmlPs.println(i);
        htmlPs.println("</td><td>");
        htmlPs.println(
            input.getSelectedConstraint().getClazz()
                + ":"
                + input.getSelectedConstraint().getJavaLineNumber());
        htmlPs.println("</td><td>");
        htmlPs.println(result.isReachedDestination());
        htmlPs.println("</td><td>");
        for (Entry<String, Object> e :
            input.getSelectedConstraint().getConstraintMap().entrySet()) {
          htmlPs.print(e.getKey());
          htmlPs.print("=");
          htmlPs.print(e.getValue());
          htmlPs.print("<br/>");
        }
        htmlPs.println("</td>");
        htmlPs.println("</tr>");
        i++;
      }
      htmlPs.println("</table>");
    }
    logger.info("Finished");
    System.exit(0);
  }
}
