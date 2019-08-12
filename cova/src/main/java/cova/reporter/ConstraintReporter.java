/*
 * @version 1.0
 */

package cova.reporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

import soot.Body;
import soot.Printer;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;

import cova.core.ConstraintAnalysis;
import cova.core.InterproceduralCFG;
import cova.data.IConstraint;
import cova.source.data.Source;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstraintReporter is used to record the constraints computed by the analysis.
 *
 */
public class ConstraintReporter {

  /** The application name. */
  private final String appName;

  /** The results of classes. */
  private HashMap<SootClass, LineConstraints> resultsOfClasses;

  /** True. if print jimple files after the analysis. */
  private boolean writeJimpleOutput;

  /** True, if it is Junit test. */
  private boolean isTest;

  /** The timeout. */
  private boolean timeout;

  /** The reachable methods. */
  private int reachableMethods;

  /** map unit to corresponding SootClass. */
  private HashMap<Unit, SootClass> unitToClass;

  /** The icfg. */
  private InterproceduralCFG icfg = null;

  /** The analysis. */
  private ConstraintAnalysis analysis;

  private Set<Source> sources;

  /**
   * Instantiates a new reporter.
   *
   * @param appName
   *          the application name
   * @param writeJimpleOutput
   *          true, if output jimple files
   */
  public ConstraintReporter(String appName, boolean writeJimpleOutput, boolean test) {
    this.appName = appName;
    resultsOfClasses = new HashMap<SootClass, LineConstraints>();
    unitToClass = new HashMap<>();
    this.writeJimpleOutput = writeJimpleOutput;
    isTest = test;
  }

  /**
   * Sets the analysis results.
   *
   * @param analysis
   *          the new analysis results
   */
  public void setAnalysisResults(ConstraintAnalysis analysis, boolean computeForAllClasses) {
    this.analysis = analysis;
    this.sources = analysis.getSources();
    setReachableMethods(this.analysis.getReachableMethods());
    setTimeout(this.analysis.isTimeout());
    // only compute results for all classes if we want to output jimple or html files
    if (computeForAllClasses) {
      writeResultsOfClasses();
    }
  }

  /**
   * Creates the path for a jimple class.
   *
   * @param jimpleOutPutPath
   *          the jimple output path
   * @param declaringClass
   *          the declaring class
   * @return the string
   */
  private String createPathForJimpleClass(String jimpleOutPutPath, String declaringClass) {
    String[] dirs = declaringClass.split("\\.");
    String path = jimpleOutPutPath + File.separator;
    for (int i = 0; i < dirs.length - 1; i++) {
      path += dirs[i] + File.separator;
    }
    File dir = new File(path);
    if (!dir.exists()) {
      try {
        Files.createDirectories(dir.toPath());
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return path;
  }

  /**
   * This method is only used for test case!.
   *
   * @param sc
   *          the sc
   * @return the result of lines
   */
  public TreeMap<Integer, IConstraint> getResultOfLines(SootClass sc) {
    TreeMap<Integer, IConstraint> ret = new TreeMap<Integer, IConstraint>();
    for (SootClass klass : resultsOfClasses.keySet()) {
      if (klass.getName().equals(sc.getName()) || klass.getName().startsWith(sc.getName() + "$")) {
        ret.putAll(resultsOfClasses.get(klass).getLineNumberConstraintMap());
      }
    }
    return ret;
  }

  /**
   * Gets the results of classes.
   *
   * @return the results of classes
   */
  public HashMap<SootClass, LineConstraints> getResultsOfClasses() {
    return resultsOfClasses;
  }

  /**
   * Gets the constraint map of given method.
   *
   * @return the constraint map of given method
   */
  public Map<Unit, IConstraint> getConstraintMap(SootMethod method) {
    return analysis.getConstraintMap(method);
  }

  /**
   * Use the constraint map to write results of classes.
   */
  private void writeResultsOfClasses() {
    Map<Unit, IConstraint> constraintMap = analysis.getConstraintMap();
    String jimpleOutputPath = System.getProperty("user.dir") + File.separator + "covaOutput" + File.separator
        + "jimpleOutput"
        + File.separator + appName + File.separator;
    Printer printer = Printer.v();
    Iterator<SootClass> reachableClasses = Scene.v().getClasses().snapshotIterator();
    while (reachableClasses.hasNext()) {
      SootClass cl = reachableClasses.next();
      if (!cl.isPhantomClass() && (isTest || cl.isApplicationClass())) {
        LineConstraints resultOfClass = new LineConstraints(cl);
        cl.getTags().clear();
        for (SootField field : cl.getFields()) {
          field.getTags().clear();
        }
        for (SootMethod method : cl.getMethods()) {
          method.getTags().clear();
          if (method.hasActiveBody()) {
            Body body = method.getActiveBody();
            body.getTags().clear();
            for (Unit unit : body.getUnits()) {
              unitToClass.put(unit, cl);
              int javaLineNumber = unit.getJavaSourceStartLineNumber();
              unit.getTags().clear();
              // get the constraint before this line of code is executed
              IConstraint constraintOfStmt = constraintMap.get(unit);
              if (constraintOfStmt != null) {
                resultOfClass.addLineResult(javaLineNumber, constraintOfStmt);
                // write constraints to jimple files
                if (writeJimpleOutput) {
                  unit.addTag(
                      new ConstraintTag("\n *\t\t" + javaLineNumber + "." + "\n" + constraintOfStmt.toString() + "\n"));
                }
              }
            }
          }
        }
        if (!resultOfClass.isEmpty()) {
          resultsOfClasses.put(cl, resultOfClass);
        }
        if (writeJimpleOutput) {
          String declaringClass = cl.getName();
          String path = createPathForJimpleClass(jimpleOutputPath, declaringClass);
          String filePath = path + declaringClass + ".jimple";
          try {
            PrintWriter out = new PrintWriter(new File(filePath));
            printer.printTo(cl, out);
            out.close();
          } catch (FileNotFoundException e) {
            e.printStackTrace();
          }
        }
      }
    }
  }

  /**
   * Prints the result of classes in console.
   */
  public void printResultOfClasses() {
    if (!resultsOfClasses.isEmpty()) {
      StringBuilder sb = new StringBuilder("\n");
      int size = 0;
      for (LineConstraints result : resultsOfClasses.values()) {
        sb.append(result.toReadableString());
        size = result.getLineNumberConstraintMap().size();
      }
      sb.append("#constraints: ");
      sb.append(size);
      String s = sb.toString();
      s = StringUtils.replace(s, "\u2227", "^");
      s = StringUtils.replace(s, "\u2228", "v");
      System.out.println(s + "\n");
    }
  }

  /**
   * Prints the result of lines.
   *
   * @param sc
   *          the sc
   */
  public void printResultOfLines(SootClass sc) {
    for (SootClass klass : resultsOfClasses.keySet()) {
      if (klass.getName().equals(sc.getName()) || klass.getName().startsWith(sc.getName())) {
        StringBuilder sb = new StringBuilder("\n");
        sb.append(resultsOfClasses.get(klass).toReadableString());
        sb.append("#constraints: ");
        sb.append(resultsOfClasses.get(klass).size());
        String s = sb.toString();
        s = StringUtils.replace(s, "\u2227", "^");
        s = StringUtils.replace(s, "\u2228", "v");
        System.out.println(s + "\n");
      }
    }
  }

  /**
   * Sets the timeout.
   *
   * @param timeout
   *          the new timeout
   */
  private void setTimeout(boolean timeout) {
    this.timeout = timeout;
  }

  /**
   * Sets the icfg.
   *
   * @param icfg
   *          the new icfg
   */
  public void setICFG(InterproceduralCFG icfg) {
    this.icfg = icfg;
  }

  /**
   * Checks if is timeout.
   *
   * @return true, if is timeout
   */
  public boolean isTimeout() {
    return timeout;
  }

  /**
   * Sets the reachable methods.
   *
   * @param n
   *          the new reachable methods
   */
  private void setReachableMethods(int n) {
    reachableMethods = n;
  }

  /**
   * Gets the number of reachable methods.
   *
   * @return the reachable methods
   */
  public int getNumberOfReachableMethods() {
    return reachableMethods;
  }

  /**
   * Gets the method contains the unit.
   *
   * @param unit
   *          the unit
   * @return the method of
   */
  public SootMethod getMethodOf(Unit unit) {
    return icfg.getMethodOf(unit);
  }

  /**
   * Return the signature of the source API.
   * 
   * @param symbolicName
   * @return
   */
  public String getSourceSignature(String symbolicName) {
    String s = "unknown";
    for (Source source : sources) {
      String uniqueName = symbolicName.split("_")[0];
      if (uniqueName.equals(source.getUniqueName())) {
        s = source.getSignature();
        break;
      }
    }
    return s;
  }

}
