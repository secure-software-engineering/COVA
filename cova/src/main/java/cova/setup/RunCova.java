/*
 * @version 1.0
 */

package cova.setup;

import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import soot.jimple.infoflow.android.callbacks.CallbackDefinition;

import cova.reporter.ConstraintReporter;
import cova.setup.config.Config;

/**
 * The Class RunCova runs COVA.
 *
 * @date 05.09.2017
 */
public class RunCova {
  private static final Logger logger = LoggerFactory.getLogger(RunCova.class);

  /**
   * Run COVA for analyzing Android apks.
   *
   * @param apkFilePath
   *          the apk file path
   * @param androidJarPath
   *          the android jar path
   * @param sourceCodePath
   *          the source code path
   * @param standalone
   *          true, if use COVA as a standalone tool
   * @param callbacks
   *          the callbacks found in apk
   * @param configuration
   *          the configuration
   * @return the reporter
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws XmlPullParserException
   *           the xml pull parser exception
   */
  public static ConstraintReporter runForAndroid(String apkFilePath,
      String androidJarPath, String sourceCodePath, boolean standalone,
      Set<CallbackDefinition> callbacks, Config configuration)
      throws IOException, XmlPullParserException {
    logger.info("Analyzing " + apkFilePath);
    CovaSetupForAndroid cova = new CovaSetupForAndroid(androidJarPath, apkFilePath, sourceCodePath,
        configuration);
    if (standalone) {
      cova.run();
    } else {
      cova.run(callbacks);
    }
    return cova.getReporter();
  }

  /**
   * Run COVA for analyzing java application.
   *
   * @param appName
   *          the app name
   * @param appClassPath
   *          the app class path
   * @param sourceCodePath
   *          the source code path
   * @param libPath
   *          the lib path
   * @param config
   *          the config
   * @return the reporter
   */
  public static ConstraintReporter runForJava(String appName, String appClassPath,
      String sourceCodePath, String libPath, String configFilePath, Config config,
      String mainClass) {
    logger.info("Analyzing classes in " + appClassPath);
    CovaSetupForJava cova = new CovaSetupForJava(appName, appClassPath, sourceCodePath, libPath,
        configFilePath, config, mainClass);
    cova.run();
    return cova.getReporter();
  }
}
