package cova.data;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.results.ResultSinkInfo;
import soot.jimple.infoflow.results.ResultSourceInfo;
import soot.util.MultiMap;

import cova.data.CombinedResult.LeakConstraint;
import cova.reporter.ConstraintReporter;

public class CombinedResult implements Iterable<LeakConstraint> {
  private ConstraintReporter reporter;
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

    public LeakConstraint(Unit sink, SootMethod sinkMethod, SootClass sinkClass, IConstraint sinkConstraint, Unit source,
        SootMethod sourceMethod, SootClass sourceClass, IConstraint sourceConstraint, IConstraint constraint) {
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

  public CombinedResult(MetaData apkMetaData, MultiMap<ResultSinkInfo, ResultSourceInfo> sinksSources,
      ConstraintReporter reporter) {
    this.apkMetaData = apkMetaData;
    this.reporter = reporter;
    results = new ArrayList<>();
    if (sinksSources != null) {
      for (ResultSinkInfo sink : sinksSources.keySet()) {
        Unit sinkStmt = sink.getStmt();
        SootMethod sinkMethod = this.reporter.getMethodOf(sinkStmt);
        // get the constraint map of sink method
        Map<Unit, IConstraint> constraintsOfSinkMethod = reporter.getConstraintMap(sinkMethod);
        IConstraint constraintAtSink = ConstraintZ3.getTrue();// default constraint true
        if (constraintsOfSinkMethod.containsKey(sinkStmt)) {
          // get constraint of sink stmt
          constraintAtSink = constraintsOfSinkMethod.get(sinkStmt);
        }
        for (ResultSourceInfo source : sinksSources.get(sink)) {
          Unit sourceStmt = source.getStmt();
          SootMethod sourceMethod = this.reporter.getMethodOf(sourceStmt);
          // get the constraint map of source method
          Map<Unit, IConstraint> constraintsOfSourceMethod = reporter.getConstraintMap(sourceMethod);
          IConstraint constraintAtSource = ConstraintZ3.getTrue();// default constraint true
          if (constraintsOfSourceMethod.containsKey(sourceStmt)) {
            // get constraint of source stmt
            constraintAtSource = constraintsOfSourceMethod.get(sourceStmt);
          }
          // compute merged the constraint of source and sink
          IConstraint constraint = constraintAtSource.and(constraintAtSink, true);
          results.add(new LeakConstraint(sinkStmt, sinkMethod, sinkMethod.getDeclaringClass(), constraintAtSink, sourceStmt,
              sourceMethod, sourceMethod.getDeclaringClass(), constraintAtSource, constraint));
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

  /**
   * Serialize the results.
   */
  public void serialize() {
    ObjectMapper mapper = new ObjectMapper();
    ArrayNode leaksArray = mapper.createArrayNode();
    for (LeakConstraint leakConstraint : results) {
      ObjectNode leakNode = mapper.createObjectNode();
      leakNode.put("LeakConstraint", leakConstraint.getConstraint().toReadableString());
      leakNode.put("Encoded", leakConstraint.getConstraint().toString());

      ArrayNode constraintAPIs = mapper.createArrayNode();
      ConstraintZ3 constraint = (ConstraintZ3) leakConstraint.getConstraint();
      for (String symbolicName : constraint.getSymbolicNames()) {
        if (constraint.toString().contains(symbolicName)) {
          ObjectNode apiNode = mapper.createObjectNode();
          apiNode.put(symbolicName, this.reporter.getSourceSignature(symbolicName));
          constraintAPIs.add(apiNode);
        }
      }

      leakNode.putPOJO("Constraint APIs", constraintAPIs);

      ObjectNode sourceNode = mapper.createObjectNode();
      sourceNode.put("Constraint", leakConstraint.constraintAtSource.toReadableString());
      sourceNode.put("Statement", leakConstraint.getSource().toString());
      sourceNode.put("Method", leakConstraint.getSourceMethod().toString());
      sourceNode.put("Line No.", leakConstraint.getSource().getJavaSourceStartLineNumber());
      leakNode.putPOJO("Source", sourceNode);

      ObjectNode sinkNode = mapper.createObjectNode();
      sinkNode.put("Constraint", leakConstraint.constraintAtSink.toReadableString());
      sinkNode.put("Statement", leakConstraint.getSink().toString());
      sinkNode.put("Method", leakConstraint.getSinkMethod().toString());
      sinkNode.put("Line No.", leakConstraint.getSink().getJavaSourceStartLineNumber());
      leakNode.putPOJO("Sink", sinkNode);
      leaksArray.add(leakNode);
    }
    try {
      String outputPath
          = System.getProperty("user.dir") + File.separator + "covaOutput" + File.separator + "jsonOutput" + File.separator;
      File pathFile = new File(outputPath);
      if (!pathFile.exists()) {
        pathFile.mkdirs();
      }
      mapper.writerWithDefaultPrettyPrinter().writeValue(new File(outputPath + this.apkMetaData.getApkName() + ".json"),
          leaksArray);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
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
  public IConstraint getConstraint(int sourceLine, String sourceClass, int sinkLine, String sinkClass) {
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
