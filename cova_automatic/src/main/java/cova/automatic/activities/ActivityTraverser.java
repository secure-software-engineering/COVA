package cova.automatic.activities;

import cova.automatic.results.ConstraintInformation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import soot.MethodOrMethodContext;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.toolkits.callgraph.Edge;

public class ActivityTraverser {

  private Map<String, List<ConstraintInformation>> activityConstraints;
  private ConstraintInformation selectedInfo;
  private String mainActivity;
  private List<List<ConstraintInformation>> paths;
  private List<ConstraintInformation> allInformation;
  private boolean traversed;

  public ActivityTraverser(
      List<ConstraintInformation> information,
      ConstraintInformation selectedInfo,
      String mainActivity) {
    paths = new ArrayList<>();
    activityConstraints = new HashMap<>();
    this.selectedInfo = selectedInfo;
    this.mainActivity = mainActivity;
    this.allInformation = information;
    traversed = false;
  }

  public void traverse() {
    if (!traversed) {
      parseIntents();
      traverse(new ArrayList<ConstraintInformation>(), selectedInfo);
    }
  }

  private void parseIntents() {
    for (ConstraintInformation info : allInformation) {

      Unit u = info.getUnit();
      if (u instanceof InvokeStmt) {
        InvokeStmt stmt = (InvokeStmt) u;
        String name = stmt.getInvokeExpr().getMethod().getName();
        InvokeExpr inv = stmt.getInvokeExpr();
        if (inv != null && inv.getArgCount() > 0) {
          Value v = inv.getArg(0);
          // TODO more precise detection
          if (name.equals("startActivity")) {
            Unit tmpUnit = u;
            // Look for initialisation of intent for this startActivity
            while (tmpUnit != null) {
              tmpUnit = info.getMethod().getActiveBody().getUnits().getPredOf(tmpUnit);
              if (!(tmpUnit instanceof InvokeStmt)) {
                continue;
              }
              InvokeStmt constStmt = (InvokeStmt) tmpUnit;
              InvokeExpr constExpr = constStmt.getInvokeExpr();
              // Only consider constructors
              if (!(constExpr instanceof SpecialInvokeExpr)) {
                continue;
              }
              SpecialInvokeExpr specialConstExpr = (SpecialInvokeExpr) constExpr;
              // Look only for the correct intent
              if (!specialConstExpr.getBase().equals(v)) {
                continue;
              }
              ClassConstant clValue = (ClassConstant) specialConstExpr.getArg(1);
              String cl = clValue.getValue();
              cl = cl.substring(1, cl.length() - 1).replace("/", ".");
              if (!activityConstraints.containsKey(cl)) {
                activityConstraints.put(cl, new ArrayList<>());
              }
              activityConstraints.get(cl).add(info);
            }
          }
        }
      }
    }
  }

  boolean getPreds(MethodOrMethodContext m, Set<String> activityNames) {

    if (m instanceof SootMethod) {
      SootMethod sootMethod = (SootMethod) m;
      String activityName = sootMethod.getDeclaringClass().getName();
      if (activityName.equals(mainActivity)) {
        return true;
      }
      if (activityConstraints.containsKey(activityName)) {
        activityNames.add(activityName);
      }
    }
    Iterator<Edge> edges = Scene.v().getCallGraph().edgesInto(m);
    while (edges.hasNext()) {
      Edge e = edges.next();
      boolean ret = getPreds(e.getSrc(), activityNames);
      if (ret) {
        return true;
      }
    }
    return false;
  }

  private void traverse(List<ConstraintInformation> path, ConstraintInformation info) {
    if (path.contains(info)) {
      // Found loop
      return;
    }
    path = new ArrayList<>(path);
    path.add(info);
    SootClass infoClass = info.getClazz();
    if (infoClass.isInnerClass()) {
      infoClass = infoClass.getOuterClass();
    }
    String activityName = infoClass.getName();

    if (activityName.equals(mainActivity)) {
      Collections.reverse(path);
      paths.add(path);
      return;
    }
    Set<String> activityNames = new HashSet<>();
    // Check if constraint is in activity
    if (activityConstraints.containsKey(activityName)) {
      activityNames.add(activityName);
    }

    // check for activities in constraint
    if (activityNames.isEmpty()) {
      for (SootClass cl : info.getConstraintSootClasses()) {
        String tmpActivityName = cl.getName();
        if (activityConstraints.containsKey(tmpActivityName)) {
          activityNames.add(tmpActivityName);
        }
        if (tmpActivityName.equals(mainActivity)) {
          Collections.reverse(path);
          paths.add(path);
          return;
        }
      }
    }
    // check for activities in callgraph
    if (activityNames.isEmpty()) {
      boolean ret = getPreds(info.getMethod(), activityNames);
      // Has main activity in callgraph
      if (ret) {
        Collections.reverse(path);
        paths.add(path);
        return;
      }
    }
    if (activityNames.isEmpty()) {
      return;
    }
    for (String tmpActivityName : activityNames) {
      for (ConstraintInformation candidate : activityConstraints.get(tmpActivityName)) {
        traverse(path, candidate);
      }
    }
  }

  public List<List<ConstraintInformation>> getPaths() {
    return paths;
  }
}
