package cova.automatic;

import brut.androlib.AndrolibException;
import brut.directory.DirectoryException;
import cova.automatic.data.AnalysisResult;
import cova.automatic.data.TestInput;
import cova.automatic.data.TestResult;
import cova.automatic.executor.Appium;
import cova.automatic.executor.AutomaticRunner;
import cova.automatic.results.ConstraintInformation;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.Scanner;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

public class RunSingle {

  private static final Logger logger = LoggerFactory.getLogger(RunAll.class);

  public static void main(String[] args)
      throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
          CertificateException, AndrolibException, DirectoryException, XmlPullParserException,
          InterruptedException, ParseException {

    AnalysisResult baseResult = AutomaticRunner.doAnalysisForArgs(args);

    List<ConstraintInformation> information = baseResult.getConstraints();
    String mainActivity = baseResult.getMainActivity();
    for (int i = 0; i < information.size(); i++) {
      ConstraintInformation info = information.get(i);
      logger.info("[" + i + "]");
      logger.info(info.getClazz().toString());
      logger.info(info.getJavaLineNumber() + ": " + info.getUnit());
      logger.info(info.getConstraint().toReadableString());
      logger.info(info.getConstraintMap().toString());
    }
    logger.info("Choose between 0 and " + (information.size() - 1));

    Scanner in = new Scanner(System.in);
    int selected = in.nextInt();
    in.close();
    logger.info("Selected: " + selected);
    ConstraintInformation selectedInfo = information.get(selected);

    TestInput input = new TestInput(baseResult, selectedInfo);
    logger.info(selectedInfo.getOutput());
    logger.info(selectedInfo.getConstraintMap().toString());

    logger.info("Main activity: " + mainActivity);
    Appium appium = Appium.setUp(baseResult.getAppiumURL(), baseResult.getApkPath());

    AutomaticRunner.testApp(input, appium, null);

    TestResult testResult = AutomaticRunner.testApp(input, appium, null);

    if (testResult.isReachedDestination()) {
      logger.info("Reached Target");
    } else {
      for (String log : testResult.getLogs()) {
        logger.info(log);
      }
      logger.error("Did not reach target");
    }
  }
}
