
package cova.setup;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.xmlpull.v1.XmlPullParserException;

import soot.PackManager;
import soot.SceneTransformer;
import soot.Transform;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;

import cova.core.SceneTransformerFactory;
import cova.reporter.ConstraintReporter;
import cova.setup.config.Config;

/**
 * The Class CovaSetupForAndroid setups COVA for analyzing Android apks.
 *
 * @date 05.09.2017
 */
public class CovaSetupForAndroid extends SetupApplication {

  /** The source code path. */
  private final String sourceCodePath;

  /** The apk name. */
  private final String apkName;

  /** The reporter. */
  private final ConstraintReporter reporter;

  private Config config;


  /**
   * Instantiates a new cova setup.
   *
   * @param androidJarPath
   *          the android jar path
   * @param apkFilePath
   *          the apk file path
   * @param sourceCodePath
   *          the source code path
   * @param writeJimpleOutput
   *          true, if print jimple output
   * @param writeHtmlOutput
   *          true, if print thtml output
   * @param verbose
   *          true, verbose mode
   * @param standalone
   *          true, standalone mode
   */
  public CovaSetupForAndroid(String androidJarPath, String apkFilePath, String sourceCodePath,
      Config configuration) {
    super(androidJarPath, apkFilePath);
    this.sourceCodePath = sourceCodePath;
    config = configuration;
    apkName = Files.getNameWithoutExtension(apkFilePath);
    reporter = new ConstraintReporter(apkName, config.isWriteJimpleOutput(), false);
  }

  /**
   * Gets the reporter.
   *
   * @return the reporter
   */
  public ConstraintReporter getReporter() {
    return reporter;
  }

  /**
   * This method use the SetupApplication class from FlowDroid to create the dummyMainMethod and
   * sets it as entry point.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  private void createDummyMainClass() throws IOException {
    // add aditional soot options
    setSootConfig(SootSetupForAndroid.getSootConfig());
    // add additional configurations
    setCallbackFile(config.getConfigDir() + File.separator + "AndroidCallbacks.txt");
    // construct call graph
    constructCallgraph();
    // absort infoflow, since we just use flowdroid to generate the call graph
    abortAnalysis();
  }

  /**
   * Starts the analysis.
   *
   * @param transformer
   *          the transformer
   */
  private void analyze(SceneTransformer transformer) {
    PackManager.v().getPack("wjtp").add(new Transform("wjtp.cova", transformer));
    PackManager.v().getPack("wjtp").apply();
  }

  /**
   * * Run COVA as a standalone tool.
   *
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   * @throws XmlPullParserException
   *           the xml pull parser exception
   */
  public void run() throws IOException, XmlPullParserException {
    createDummyMainClass();
    Set<CallbackDefinition> callbacks = getCallbackMethods().values();
    run(callbacks);
  }

  /**
   * Run COVA after running flowDroid.
   *
   * @param callbacks
   *          the callbacks
   */
  public void run(Set<CallbackDefinition> callbacks) {
    SceneTransformerFactory transformerFactory = new SceneTransformerFactory(config);
    SceneTransformer transformer = transformerFactory.createAnalysisTransformerForAndroidApk(
        apkName, callbacks, sourceCodePath, config.getConfigDir(), reporter);
    analyze(transformer);
  }
}
