package utils;

import java.io.File;

import org.junit.Before;
import org.junit.experimental.categories.Category;

import categories.BenchmarkTestSuite;
import cova.reporter.ConstraintReporter;
import cova.runner.JavaAppAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForTestCase;

@Category(BenchmarkTestSuite.class)
public class ConstraintBenchTestFrameworkForJavaApp {
  protected Config config;
  protected String libPath;
  protected String javaAppSourceCodePath;
  protected ConstraintReporter reporter;
  protected String targetTestAppName = "";
  protected String entryPoint = "";

  // set the unique name of configurations
  protected final String A = "C1001";
  protected final String B = "C1002";
  protected final String C = "C1003";
  protected final String D = "C1004";
  protected final String E = "C1005";
  protected final String F = "C1006";

  protected final String FA = "C1007";
  protected final String FB = "C1008";
  protected final String FC = "C1009";

  protected final String P = "C1010";
  protected final String Q = "C1011";
  protected final String K = "C1012";
  protected final String H = "C1013";
  protected final String X = "C1014";
  protected final String onClick = "U1001";
  protected final String onScroll = "U1002";

  public ConstraintBenchTestFrameworkForJavaApp() {
    libPath = System.getProperty("java.home") + File.separator + "lib" + File.separator
        + "rt.jar";
    String userDir = System.getProperty("user.dir");
    String covaRootDir = new File(userDir).getParent();
    String benchdir = covaRootDir + File.separator + "constraintBench";
    javaAppSourceCodePath = benchdir + File.separator + "javaApps";
    config = new DefaultConfigForTestCase();
  }

  @Before
  public void beforeTestCaseExecution() {
    String appPath = javaAppSourceCodePath + File.separator + targetTestAppName + File.separator;
    String appClassPath = appPath + "bin";
    String sourceCodePath = appPath + "src";
    String configFilePath = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources"
        + File.separator + "config" + File.separator;
    reporter = JavaAppAnalyzer.analyzeApp(targetTestAppName, appClassPath, sourceCodePath, libPath, configFilePath,
        entryPoint,
        config);
  }
}
