package utils;

import java.io.File;

import org.junit.Before;
import org.junit.experimental.categories.Category;

import categories.BenchmarkTestSuite;
import cova.data.CombinedResult;
import cova.runner.AndroidApkAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForTestCase;

@Category(BenchmarkTestSuite.class)
public class ConstraintBenchTestFrameworkForAndroidApp {

  protected boolean standalone;
  protected String androidJarPath;
  protected String apksPath;
  protected String apkSourceCodePath;
  protected Config config;
  protected CombinedResult results;
  protected String testResourcePath;

  protected String targetTestAppName = "";

  public ConstraintBenchTestFrameworkForAndroidApp() {
    standalone = false;
    String userDir = System.getProperty("user.dir");
    String covaRootDir = new File(userDir).getParent();
    String benchdir = covaRootDir + File.separator + "constraintBench" + File.separator + "androidApps";
    testResourcePath = userDir + File.separator + "src" + File.separator + "test" + File.separator + "resources";
    androidJarPath = testResourcePath + File.separator + "androidPlatforms";
    apksPath = benchdir + File.separator + "apks";
    apkSourceCodePath = benchdir + File.separator + "sourceCode";
    config = new DefaultConfigForTestCase();
  }

  @Before
  public void beforeTestCaseExecution() {
    String apkFilePath = apksPath + File.separator + targetTestAppName + ".apk";
    String sourceCodePath = apkSourceCodePath + File.separator + targetTestAppName;
    AndroidApkAnalyzer.analyzeApk(androidJarPath, apkFilePath, sourceCodePath, standalone, config);
    results = AndroidApkAnalyzer.getCombinedResults();
  }
}
