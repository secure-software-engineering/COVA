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

package cova.core;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import soot.Body;
import soot.MethodOrMethodContext;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;
import soot.jimple.toolkits.callgraph.ReachableMethods;
import soot.util.queue.QueueReader;

import cova.reporter.ConstraintReporter;
import cova.reporter.HtmlReportPrinter;
import cova.setup.config.Config;
import cova.source.SourceManager;

/**
 * A factory for creating soot SceneTransformer objects.
 * 
 */
public class SceneTransformerFactory {
  /** The Constant outputPath. */
  private static final String outputPath = System.getProperty("user.dir") + File.separator + "covaOutput" + File.separator;

  private Config config;

  public SceneTransformerFactory(Config configuration) {
    File file = new File(outputPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    config = configuration;
  }

  /**
   * Creates a new SceneTransformer for android apk.
   *
   * @param apkName
   *          the apk name
   * @param callbacks
   *          the callbacks
   * @param sourceCodePath
   *          the source code path of the apk
   * @param configFilePath
   *          the config file path
   * @param reporter
   *          the reporter
   * @return the scene transformer
   */
  public SceneTransformer createAnalysisTransformerForAndroidApk(String apkName, Set<CallbackDefinition> callbacks,
      String sourceCodePath, String configFilePath, ConstraintReporter reporter) {
    return new SceneTransformer() {

      /**
       * This method applies local splitter and local name standardizer to each method body. This is done before executing
       * the analysis such that each disjoint DU-UD web is associated with a single local. It is called before executing the
       * condition analysis.
       *
       * @param icfg
       *          the interprocedural control flow graph
       */
      private void applyJimpleBodyTransformer(InterproceduralCFG icfg) {
        ReachableMethods rm = Scene.v().getReachableMethods();
        QueueReader<MethodOrMethodContext> listener = rm.listener();
        while (listener.hasNext()) {
          MethodOrMethodContext next = listener.next();
          SootMethod method = next.method();
          if (method.hasActiveBody()) {
            for (Unit u : method.getActiveBody().getUnits()) {
              Collection<SootMethod> calledMethods
                  = (icfg.isCallStmt(u) ? icfg.getCalleesOfCallAt(u) : new HashSet<SootMethod>());
              for (SootMethod m : calledMethods) {
                if (m.hasActiveBody()) {
                  Body b = m.getActiveBody();
                  PackManager.v().getPack("jb").get("jb.ls").apply(b);
                  PackManager.v().getPack("jb").get("jb.lns").apply(b);
                }
              }
            }
          }
        }
      }

      @Override
      protected void internalTransform(String phaseName, Map<String, String> options) {
        InterproceduralCFG icfg = new InterproceduralCFG();
        reporter.setICFG(icfg);
        applyJimpleBodyTransformer(icfg);
        SourceManager sourceManager = new SourceManager(configFilePath, callbacks);
        RuleManager ruleManager = new RuleManager(icfg, sourceManager, config);
        ConstraintAnalysis analysis = new ConstraintAnalysis(ruleManager);
        analysis.doAnalysis();
        if (config.isWriteHtmlOutput() || config.isWriteJimpleOutput()) {
          reporter.setAnalysisResults(analysis, true);
          if (config.isWriteHtmlOutput()) {
            String htmlOutputPath = outputPath + "htmlOutput" + File.separator + apkName;
            HtmlReportPrinter reportPrinter = new HtmlReportPrinter(sourceCodePath, htmlOutputPath);
            reportPrinter.printReport(reporter.getResultsOfClasses());
          }
        } else {
          reporter.setAnalysisResults(analysis, false);
        }
      }
    };

  }

  /**
   * Creates a new SceneTransformer object for test cases.
   *
   * @param className
   *          the test class name
   * @param testMethodName
   *          the test method name
   * @param reporter
   *          the reporter
   * @param sourceCodeDir
   *          the source code dir
   * @return the scene transformer
   */
  public SceneTransformer createAnalysisTransformerForTestCase(String className, String testMethodName,
      ConstraintReporter reporter, String sourceCodeDir) {
    return new SceneTransformer() {

      @Override
      protected void internalTransform(String phaseName, Map<String, String> options) {
        InterproceduralCFG icfg = new InterproceduralCFG();
        reporter.setICFG(icfg);
        String configFilePath = "." + File.separator + "src" + File.separator + "test" + File.separator + "resources"
            + File.separator + "config";
        SourceManager sourceManager = new SourceManager(configFilePath, null);
        RuleManager ruleManager = new RuleManager(icfg, sourceManager, config);
        ConstraintAnalysis analysis = new ConstraintAnalysis(ruleManager);
        analysis.doAnalysis();
        reporter.setAnalysisResults(analysis, true);
        if (config.isWriteHtmlOutput()) {
          String htmlOutputPath = outputPath + "htmlOutput" + File.separator + className + File.separator + testMethodName;
          HtmlReportPrinter reportPrinter = new HtmlReportPrinter(sourceCodeDir, htmlOutputPath);
          reportPrinter.printReport(reporter.getResultsOfClasses());
        }
      }
    };
  }

  /**
   * Creates a new SceneTransformer object for java application.
   *
   * @param apkName
   *          the apk name
   * @param sourceCodePath
   *          the source code path
   * @param configFilePath
   *          the config file path
   * @param reporter
   *          the reporter
   * @return the scene transformer
   */
  public SceneTransformer createAnalysisTransformerForJavaApp(String apkName, String sourceCodePath, String configFilePath,
      ConstraintReporter reporter) {
    return new SceneTransformer() {

      @Override
      protected void internalTransform(String phaseName, Map<String, String> options) {
        InterproceduralCFG icfg = new InterproceduralCFG();
        reporter.setICFG(icfg);
        SourceManager sourceManager = new SourceManager(configFilePath, null);
        RuleManager ruleManager = new RuleManager(icfg, sourceManager, config);
        ConstraintAnalysis analysis = new ConstraintAnalysis(ruleManager);
        analysis.doAnalysis();
        if (config.isWriteHtmlOutput() || config.isWriteJimpleOutput()) {
          reporter.setAnalysisResults(analysis, true);
          if (config.isWriteHtmlOutput()) {
            String htmlOutputPath = outputPath + "htmlOutput" + File.separator + apkName;
            HtmlReportPrinter reportPrinter = new HtmlReportPrinter(sourceCodePath, htmlOutputPath);
            reportPrinter.printReport(reporter.getResultsOfClasses());
          }
        } else {
          reporter.setAnalysisResults(analysis, false);
        }
      }
    };
  }

}
