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
package utils;

import categories.BenchmarkTestSuite;
import cova.data.CombinedResult;
import cova.runner.AndroidApkAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForTestCase;
import java.io.File;
import org.junit.Before;
import org.junit.experimental.categories.Category;

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
    String benchdir =
        covaRootDir + File.separator + "constraintBench" + File.separator + "androidApps";
    testResourcePath =
        userDir + File.separator + "src" + File.separator + "test" + File.separator + "resources";
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
