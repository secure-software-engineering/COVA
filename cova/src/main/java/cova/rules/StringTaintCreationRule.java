package cova.rules;

import cova.core.InterproceduralCFG;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.data.taints.AbstractTaint;
import cova.data.taints.StringTaint;
import cova.data.taints.SymbolicTaint;
import cova.vasco.Context;
import java.util.HashSet;
import java.util.Set;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.InstanceInvokeExpr;

public class StringTaintCreationRule {
  private final InterproceduralCFG icfg;

  public StringTaintCreationRule(RuleManager ruleManager) {
    icfg = ruleManager.getIcfg();
  }

  public boolean normalFlowFunction(
      Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    return false;
  }

  public boolean callEntryFlowFunction(
      Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee,
      Unit node,
      Unit succ,
      Abstraction in) {

    return false;
  }

  public boolean callLocalFlowFunction(
      Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    IConstraint constraint = in.getConstraintOfStmt();
    AssignStmt assignStmt = (AssignStmt) node;
    Value leftOp = assignStmt.getLeftOp();
    Value rightOp = assignStmt.getRightOp();
    Set<AbstractTaint> newTaints = new HashSet<>();
    if (rightOp instanceof InstanceInvokeExpr) {
      InstanceInvokeExpr invoke = (InstanceInvokeExpr) rightOp;
      String methodName = invoke.getMethod().getName();
      String declaringClass = invoke.getMethod().getDeclaringClass().toString();
      if (declaringClass.equals("java.lang.String")) {
        Value base = invoke.getBase();

        Set<AbstractTaint> involved =
            in.taints().getTaintsWithAccessPath(new WrappedAccessPath(base));

        if (methodName.equals("contains")
            || methodName.equals("startsWith")
            || methodName.equals("endsWith")) {
          StringMethod method = null;
          if (methodName.equals("contains")) {
            method = StringMethod.CONTAINS;
          } else if (methodName.equals("startsWith")) {
            method = StringMethod.STARTSWITH;
          } else if (methodName.equals("endsWith")) {
            method = StringMethod.ENDSWITH;
          }

          Value value = invoke.getArg(0);
          if (!(value instanceof Constant)) {
            return false;
          }
          String strVal = value.toString();
          strVal = strVal.substring(1, strVal.length() - 1);

          for (AbstractTaint taint : involved) {
            if (taint instanceof SymbolicTaint) {
              // create concrete taint at equals function.
              SymbolicTaint sTaint = (SymbolicTaint) taint;
              String name = sTaint.getSymbolicName();

              StringTaint imprecise =
                  new StringTaint(
                      new WrappedAccessPath(leftOp),
                      constraint,
                      (SymbolicTaint) taint,
                      node,
                      method,
                      name,
                      strVal);

              newTaints.add(imprecise);
            }
          }
          in.taints().addAll(newTaints);
          return true;
        } else if (methodName.equals("length")) {
          for (AbstractTaint taint : involved) {
            if (taint instanceof SymbolicTaint) {
              StringTaint imprecise =
                  new StringTaint(
                      new WrappedAccessPath(leftOp),
                      constraint,
                      (SymbolicTaint) taint,
                      node,
                      StringMethod.LENGTH);

              newTaints.add(imprecise);
            }
          }
          in.taints().addAll(newTaints);

          return true;
        } else {
          System.err.println("Method " + methodName + " not implemented yet");
        }
      }
    }
    return false;
  }
}
