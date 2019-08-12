
package cova.setup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Transform;
import soot.options.Options;

import cova.core.SceneTransformerFactory;
import cova.reporter.ConstraintReporter;
import cova.setup.config.Config;

/**
 * The Class CovaSetupForJava setups COVA for analyzing java application.
 */
public class CovaSetupForJava {

  /** The application name. */
  private final String appName;

  /** The application class path. */
  private final String appClassPath;

  /** The source code path. */
  private final String sourceCodePath;

  /** The java run time class path. */
  private final String libPath;

  /** The configuration file path. */
  private final String configFilePath;

  private final String mainClass;

  /** The reporter. */
  private final ConstraintReporter reporter;

  private Config config;
  /**
   * Instantiates a new cova setup for java application.
   *
   * @param appName
   *          the app name
   * @param appClassPath
   *          the app class path
   * @param sourceCodePath
   *          the source code path
   * @param libPath
   *          the lib path
   * @param writeJimpleOutput
   *          the write jimple output
   * @param writeHtmlOutput
   *          the write html output
   * @param verbose
   *          the verbose
   */
  public CovaSetupForJava(String appName, String appClassPath, String sourceCodePath,
      String libPath, String configFilePath, Config configuration, String mainClass) {
    this.appName = appName;
    this.appClassPath = appClassPath;
    this.sourceCodePath = sourceCodePath;
    this.libPath = libPath;
    config = configuration;
    this.configFilePath = configFilePath;
    this.mainClass = mainClass;
    reporter = new ConstraintReporter(appName, config.isWriteJimpleOutput(), false);
  }

  /**
   * Initialize soot.
   */
  private void initializeSoot() {
    G.reset();
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_soot_classpath(libPath);
    Options.v().set_src_prec(Options.src_prec_class);
    List<String> processDirs = new ArrayList<String>();
    processDirs.add(appClassPath);
    Options.v().set_process_dir(processDirs);
    Options.v().set_keep_line_number(true);
    if (config.isWriteJimpleOutput()) {
      Options.v().set_print_tags_in_output(true);
    }
    // set spark options for construct call graphs
    Options.v().setPhaseOption("cg.spark", "on");
    Options.v().setPhaseOption("cg.spark", "string-constants:true");

    Options.v().set_whole_program(true);
    Options.v().set_main_class(mainClass);

    Scene.v().loadNecessaryClasses();

    SootMethod mainMethod = Scene.v().getMainMethod();
    Scene.v().setEntryPoints(Collections.singletonList(mainMethod));
  }

  /**
   * Starts the analysis.
   *
   * @param transformer
   *          the transformer
   */
  private void analyze(SceneTransformer transformer) {
    PackManager.v().getPack("cg").apply();
    PackManager.v().getPack("wjtp").add(new Transform("wjtp.cova", transformer));
    PackManager.v().getPack("wjtp").apply();
  }

  
  /**
   * Run COVA for java application.
   */
  public void run()
  {
    initializeSoot();
    SceneTransformerFactory transformerFactory = new SceneTransformerFactory(config);
    SceneTransformer transformer = transformerFactory.createAnalysisTransformerForJavaApp(appName,
        sourceCodePath, configFilePath, reporter);
    analyze(transformer);
  }

  /**
   * Gets the reporter.
   *
   * @return the reporter
   */
  public ConstraintReporter getReporter() {
    return reporter;
  }

}
