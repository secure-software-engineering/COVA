/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package cova.setup;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlpull.v1.XmlPullParserException;

import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;
import soot.jimple.infoflow.android.config.SootConfigForAndroid;
import soot.jimple.infoflow.results.InfoflowResults;
import soot.jimple.infoflow.taintWrappers.EasyTaintWrapper;

/**
 * The Class RunFlowDroid runs FlowDroid.
 * 
 */
public class RunFlowDroid {
  private static final Logger logger = LoggerFactory.getLogger(RunFlowDroid.class);

  /** The callbacks in apk found by FlowDroid */
  private static Set<CallbackDefinition> callbacks;

  /** The info flow results. */
  private static InfoflowResults infoFlowResults;

  /**
   * Gets the callbacks found in apk.
   *
   * @return the callbacks
   */
  public static Set<CallbackDefinition> getCallbacks() {
    return callbacks;
  }

  /**
   * Gets the info flow results.
   *
   * @return the info flow results
   */
  public static InfoflowResults getInfoFlowResults() {
    return infoFlowResults;
  }

  /**
   * Run.
   *
   * @param apkFilePath
   *          the apk file path
   * @param androidJarPath
   *          the android jar path
   * @param writeJimpleOutput
   *          true, if print jimple output
   * @throws IOException
   * @throws XmlPullParserException
   * @throws Exception
   */
  public static void run(String apkFilePath, String androidJarPath, String configDir)
      throws IOException, XmlPullParserException {
    logger.info("Run FlowDroid...");
    SootConfigForAndroid sootConfigForAndroid = SootSetupForAndroid.getSootConfig();
    if (!configDir.endsWith(File.separator)) {
      configDir += File.separator;
    }
    String sourceSinksFilePath = configDir + "SourcesAndSinks.txt";
    String callbacksFilePath = configDir + "AndroidCallbacks.txt";
    String easyTaintWrapperSoucePath = configDir + "EasyTaintWrapperSource.txt";
    SetupApplication flowDroid = new SetupApplication(androidJarPath, apkFilePath);
    flowDroid.setSootConfig(sootConfigForAndroid);
    flowDroid.setCallbackFile(callbacksFilePath);
    flowDroid.setTaintWrapper(new EasyTaintWrapper(easyTaintWrapperSoucePath));
    infoFlowResults = flowDroid.runInfoflow(sourceSinksFilePath);
    callbacks = flowDroid.getCallbackMethods().values();
    flowDroid.abortAnalysis();
  }

}
