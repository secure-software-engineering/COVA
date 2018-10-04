package data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.util.MultiMap;

import data.CombinedResult.LeakConstraint;
import reporter.ConstraintReporter;

public class CombinedResult implements Iterable<LeakConstraint> {
  private ArrayList<LeakConstraint> results;
  private MetaData apkMetaData;

  public class LeakConstraint {
    private Unit sink;
    private Unit source;
    private SootClass sinkClass;
    private SootClass sourceClass;
    private SootMethod sinkMethod;
    private SootMethod sourceMethod;
    private IConstraint constraintAtSink;
    private IConstraint constraintAtSource;
    private IConstraint constraint;

    public LeakConstraint(Unit sink, SootMethod sinkMethod, SootClass sinkClass,
        IConstraint sinkConstraint, Unit source, SootMethod sourceMethod, SootClass sourceClass,
        IConstraint sourceConstraint, IConstraint constraint) {
      this.sink = sink;
      this.source = source;
      this.sinkMethod = sinkMethod;
      this.sourceMethod = sourceMethod;
      this.sinkClass = sinkClass;
      this.sourceClass = sourceClass;
      constraintAtSink = sinkConstraint;
      constraintAtSource = sourceConstraint;
      this.constraint = constraint;
    }

    public Unit getSink() {
      return sink;
    }

    public Unit getSource() {
      return source;
    }

    public SootClass getSinkClass() {
      return sinkClass;
    }

    public SootClass getSourceClass() {
      return sourceClass;
    }

    public SootMethod getSinkMethod() {
      return sinkMethod;
    }

    public SootMethod getSourceMethod() {
      return sourceMethod;
    }

    public IConstraint constraintAtSink() {
      return constraintAtSink;
    }

    public IConstraint constraintAtSource() {
      return constraintAtSource;
    }

    public IConstraint getConstraint() {
      return constraint;
    }
  }

  public CombinedResult(MetaData apkMetaData,
      MultiMap<ResultSinkInfo, ResultSourceInfo> sinksSources,
      ConstraintReporter reporter) {
    this.apkMetaData = apkMetaData;
    results = new ArrayList<>();
    if (sinksSources != null) {
      for (ResultSinkInfo sink : sinksSources.keySet()) {
        Unit sinkStmt = sink.getStmt();
        SootMethod sinkMethod = reporter.getMethodOf(sinkStmt);
        // get the constraint map of sink method
        Map<Unit, IConstraint> constraintsOfSinkMethod = reporter.getConstraintMap(sinkMethod);
        IConstraint constraintAtSink = ConstraintZ3.getTrue();// default constraint true
        if (constraintsOfSinkMethod.containsKey(sinkStmt)) {
          // get constraint of sink stmt
          constraintAtSink = constraintsOfSinkMethod.get(sinkStmt);
        }
        for (ResultSourceInfo source : sinksSources.get(sink)) {
          Unit sourceStmt = source.getStmt();
          SootMethod sourceMethod = reporter.getMethodOf(sourceStmt);
          // get the constraint map of source method
          Map<Unit, IConstraint> constraintsOfSourceMethod = reporter.getConstraintMap(sourceMethod);
          IConstraint constraintAtSource = ConstraintZ3.getTrue();// default constraint true
          if (constraintsOfSourceMethod.containsKey(sourceStmt)) {
            // get constraint of source stmt
            constraintAtSource = constraintsOfSourceMethod.get(sourceStmt);
          }
          // compute merged the constraint of source and sink
          IConstraint constraint = constraintAtSource.and(constraintAtSink, true);
          results.add(new LeakConstraint(sinkStmt, sinkMethod, sinkMethod.getDeclaringClass(),
              constraintAtSink, sourceStmt, sourceMethod, sourceMethod.getDeclaringClass(),
              constraintAtSource, constraint));
        }
      }
    }
  }

  @Override
  public Iterator<LeakConstraint> iterator() {
    return results.iterator();
  }

  public int size() {
    return results.size();
  }

  public MetaData getApkMetaData() {
    return apkMetaData;
  }

  public void print() {
    for (LeakConstraint leakConstraint : results) {
      String s = "Constraint of leak: " + leakConstraint.getConstraint().toReadableString();
      s += "\n- Source@Line." + leakConstraint.getSource().getJavaSourceStartLineNumber() + " in "
          + leakConstraint.getSourceMethod() + ": " + leakConstraint.getSource();
      s += "\n- Constraint@Source " + leakConstraint.constraintAtSource.toReadableString();
      s += "\n- Sink@Line." + leakConstraint.getSink().getJavaSourceStartLineNumber() + " in "
          + leakConstraint.getSinkMethod() + ": " + leakConstraint.getSink();
      s += "\n- Constraint@Source " + leakConstraint.constraintAtSink.toReadableString();
      s = StringUtils.replace(s, "\u2227", "^");
      s = StringUtils.replace(s, "\u2228", "v");
      System.out.println(s + "\n");
    }
  }

  /**
   * return the constraint of given leak. If it doesn't exists, return true.
   * 
   * @param sourceLine
   *          the line number of source in sourceClass
   * @param sourceClass
   *          the class contains source
   * @param sinkLine
   *          the line number of sink in sinkClass
   * @param sinkClass
   *          the class contains sink
   * @return
   */
  public IConstraint getConstraint(int sourceLine, String sourceClass, int sinkLine,
      String sinkClass) {
    for (LeakConstraint leakConstraint : results) {
      if (leakConstraint.getSource().getJavaSourceStartLineNumber() == sourceLine
          && leakConstraint.getSourceClass().toString().equals(sourceClass)) {
        if (leakConstraint.getSink().getJavaSourceStartLineNumber() == sinkLine
            && leakConstraint.getSinkClass().toString().equals(sinkClass)) {
          return leakConstraint.getConstraint();
        }
      }
    }
    return ConstraintZ3.getTrue();
  }
}
