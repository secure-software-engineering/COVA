/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package cova.setup;

import cova.reporter.ConstraintReporter;
import cova.setup.config.Config;
import java.io.IOException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;

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
   * @param apkFilePath the apk file path
   * @param androidJarPath the android jar path
   * @param sourceCodePath the source code path
   * @param standalone true, if use COVA as a standalone tool
   * @param callbacks the callbacks found in apk
   * @param configuration the configuration
   * @return the reporter
   * @throws IOException Signals that an I/O exception has occurred.
   * @throws XmlPullParserException the xml pull parser exception
   */
  public static ConstraintReporter runForAndroid(
      String apkFilePath,
      String androidJarPath,
      String sourceCodePath,
      boolean standalone,
      Set<CallbackDefinition> callbacks,
      Config configuration)
      throws IOException, XmlPullParserException {
    logger.info("Analyzing " + apkFilePath);
    CovaSetupForAndroid cova =
        new CovaSetupForAndroid(androidJarPath, apkFilePath, sourceCodePath, configuration);
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
   * @param appName the app name
   * @param appClassPath the app class path
   * @param sourceCodePath the source code path
   * @param libPath the lib path
   * @param config the config
   * @return the reporter
   */
  public static ConstraintReporter runForJava(
      String appName,
      String appClassPath,
      String sourceCodePath,
      String libPath,
      String configFilePath,
      Config config,
      String mainClass) {
    logger.info("Analyzing classes in " + appClassPath);
    CovaSetupForJava cova =
        new CovaSetupForJava(
            appName, appClassPath, sourceCodePath, libPath, configFilePath, config, mainClass);
    cova.run();
    return cova.getReporter();
  }
}
