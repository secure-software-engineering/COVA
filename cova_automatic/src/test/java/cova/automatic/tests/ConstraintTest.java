package cova.automatic.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import brut.androlib.AndrolibException;
import brut.directory.DirectoryException;
import cova.automatic.data.AnalysisResult;
import cova.automatic.data.TestInput;
import cova.automatic.data.TestResult;
import cova.automatic.executor.Appium;
import cova.automatic.executor.AutomaticRunner;
import cova.automatic.results.ConstraintInformation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.xmlpull.v1.XmlPullParserException;

public class ConstraintTest {

  private static AnalysisResult result;
  private static Appium appium;

  @BeforeAll
  public static void doInitalAnalysis()
      throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
          CertificateException, AndrolibException, DirectoryException, XmlPullParserException,
          InterruptedException {
    Path userDir = Paths.get(System.getProperty("user.dir"));
    Path platformDir =
        userDir
            .getParent()
            .resolve("cova")
            .resolve("src/test/resources")
            .resolve("androidPlatforms");
    Path configDir = userDir.getParent().resolve("cova").resolve("config");
    Path jarPath = platformDir.resolve("android-29/android.jar");
    Path apkFile = userDir.resolve("src/test/resources").resolve("activity_test.apk");

    Path tmpDir = Files.createTempDirectory("cova_automatic_");

    Path targetApk = tmpDir.resolve(apkFile.getFileName());
    Path signedApk = tmpDir.resolve(apkFile.getFileName() + "-signed.apk");
    Path alignedApk = tmpDir.resolve(apkFile.getFileName() + "-aligned.apk");

    result =
        AutomaticRunner.doAnalysis(
            apkFile, platformDir, jarPath, targetApk, signedApk, alignedApk, configDir, null);
    appium = Appium.setUp(null, alignedApk);
  }

  @Test
  public void testStringCombined() throws IOException {
    testString("EDIT_TEXT_COMBINED", true);
  }

  @Test
  public void testSpinnerRed() throws IOException {
    testString("SPINNER_RED", true);
  }

  @Test
  public void testStringLengthSmaller() throws IOException {
    testString("EDIT_TEXT_LENGTH_SMALLER", true);
  }

  @Test
  public void testStringLengthGreater() throws IOException {
    testString("EDIT_TEXT_LENGTH_GREATER", true);
  }

  @Test
  public void testStringLengthEquals() throws IOException {
    testString("EDIT_TEXT_LENGTH_EQUALS", true);
  }

  @Test
  public void testStringEquals() throws IOException {
    testString("EDIT_TEXT_EQUALS", true);
  }

  @Test
  public void testStringStarts() throws IOException {
    testString("EDIT_TEXT_STARTS", true);
  }

  @Test
  public void testStringEnds() throws IOException {
    testString("EDIT_TEXT_ENDS", true);
  }

  @Test
  public void testActivityChange() throws IOException {
    testString("ACTIVITY_CHANGE", true);
  }

  private void testString(String s, boolean assertType) throws IOException {

    ConstraintInformation selectedInfo = getInfo(result, s);

    TestInput input = new TestInput(result, selectedInfo);

    TestResult testResult = AutomaticRunner.testApp(input, appium, null);

    assertTrue(testResult.isReachedDestination());
  }

  private ConstraintInformation getInfo(AnalysisResult result, String name) {
    String search = "\"" + name + "\"";
    for (ConstraintInformation info : result.getConstraints()) {
      if (info.getUnit().toString().contains(search)) {
        return info;
      }
    }
    throw new RuntimeException("Output not found");
  }
}
