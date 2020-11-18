package cova.automatic.results;

import cova.automatic.AutomaticRunner;
import cova.data.IConstraint;
import cova.runner.AndroidApkAnalyzer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

public class ConstraintInformationReporter {
  public static List<ConstraintInformation> getInformation(Map<Integer, String> mapping) {
    List<ConstraintInformation> information = new ArrayList<>();
    Map<Unit, IConstraint> constraintMap = AndroidApkAnalyzer.getReporter().getConstraintMap();

    Iterator<SootClass> reachableClasses = Scene.v().getClasses().snapshotIterator();
    while (reachableClasses.hasNext()) {
      SootClass cl = reachableClasses.next();
      if (!cl.isPhantomClass() && (cl.isApplicationClass())) {
        cl.getTags().clear();
        for (SootField field : cl.getFields()) {
          field.getTags().clear();
        }
        for (SootMethod method : cl.getMethods()) {
          method.getTags().clear();
          if (method.hasActiveBody()) {
            Body body = method.getActiveBody();
            body.getTags().clear();
            List<Unit> units = body.getUnits().stream().collect(Collectors.toList());
            for (int i = 0; i < units.size(); i++) {
              Unit unit = units.get(i);
              Unit nextUnit = null;
              if (i + 1 < units.size()) {
                nextUnit = units.get(i + 1);
              }
              int javaLineNumber = unit.getJavaSourceStartLineNumber();
              unit.getTags().clear();
              // get the constraint before this line of code is executed
              IConstraint constraintOfStmt = constraintMap.get(unit);
              if (constraintOfStmt != null) {
                if (unit instanceof InvokeStmt) {
                  InvokeStmt stmt = (InvokeStmt) unit;
                  InvokeExpr expr = stmt.getInvokeExpr();
                  String className = expr.getMethod().getDeclaringClass().getName();
                  String methodName = expr.getMethod().getName();

                  if ((className.equals("android.util.Log") && methodName.equals("e"))
                      || (className.equals("java.io.PrintStream")
                          && methodName.equals("println"))) {
                    String output = expr.getArgs().get(0).toString();
                    output = output.substring(1, output.length() - 1);
                    if (output.startsWith(AutomaticRunner.PRE_STRING)) {

                      ConstraintInformation c =
                          new ConstraintInformation(
                              cl,
                              method,
                              javaLineNumber,
                              nextUnit,
                              constraintOfStmt,
                              output,
                              mapping);
                      information.add(c);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return information;
  }
}
