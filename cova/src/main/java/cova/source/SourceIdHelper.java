package cova.source;

import java.util.Map;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;

public class SourceIdHelper {

  public static Integer getActivityId(SootClass declClass) {
    Map<String, Integer> mapping = IdManager.getInstance().getActivityToIdMapping();
    if (declClass.hasOuterClass()) {
      declClass = declClass.getOuterClass();
    }
    String clazz = declClass.getName();

    if (mapping.containsKey(clazz)) {
      return mapping.get(clazz);
    }
    for (SootMethod otherM : declClass.getMethods()) {
      if (otherM.getName().equals("onCreate")) {
        for (Unit u : otherM.getActiveBody().getUnits()) {
          if (u instanceof InvokeStmt) {
            InvokeStmt invStmt = (InvokeStmt) u;
            InvokeExpr invoke = invStmt.getInvokeExpr();

            SootMethod m = invoke.getMethod();
            if ("setContentView".equals(m.getName())) {
              int id = Integer.parseInt(invoke.getArgs().get(0).toString());
              mapping.put(clazz, id);
              return id;
            }
          }
        }
      }
    }
    return 0;
  }
}
