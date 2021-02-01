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
import org.xmlpull.v1.XmlPullParserException;

public class RunSingle {
  public static void main(String[] args)
      throws IOException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException,
          CertificateException, AndrolibException, DirectoryException, XmlPullParserException,
          InterruptedException, ParseException {

    AnalysisResult baseResult = AutomaticRunner.doAnalysisForArgs(args);

    List<ConstraintInformation> information = baseResult.getConstraints();
    String mainActivity = baseResult.getMainActivity();
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
    int selected = in.nextInt();
    in.close();
    System.out.println("Selected: " + selected);
    ConstraintInformation selectedInfo = information.get(selected);

    TestInput input = new TestInput(baseResult, selectedInfo);
    System.out.println(selectedInfo.getOutput());
    System.out.println(selectedInfo.getConstraintMap());

    System.out.println("Main activity: " + mainActivity);
    Appium appium = Appium.setUp(baseResult.getApkPath());

    AutomaticRunner.testApp(input, appium, null);

    TestResult testResult = AutomaticRunner.testApp(input, appium, null);

    if (testResult.isReachedDestination()) {
      System.out.println("Reached Target");
    } else {
      for (String log : testResult.getLogs()) {
        System.out.println(log);
      }
      System.out.println("Did not reach target");
    }
  }
}
