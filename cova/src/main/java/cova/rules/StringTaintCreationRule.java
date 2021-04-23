package cova.rules;

import cova.core.InterproceduralCFG;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.data.taints.AbstractTaint;
import cova.data.taints.ImpreciseTaint;
import cova.data.taints.StringTaint;
import cova.data.taints.SymbolicTaint;
import cova.vasco.Context;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.StaticInvokeExpr;

public class StringTaintCreationRule {
  private final InterproceduralCFG icfg;
  private final Logger logger = LoggerFactory.getLogger(getClass());

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
            || methodName.equals("endsWith")
            || methodName.equals("equals")
            || methodName.equals("contentEquals")
            || methodName.equals("equalsIgnoreCase")
            || methodName.equals("isEmpty")) {
          StringMethod method = null;
          if (methodName.equals("contains")) {
            method = StringMethod.CONTAINS;
          } else if (methodName.equals("startsWith")) {
            method = StringMethod.STARTSWITH;
          } else if (methodName.equals("endsWith")) {
            method = StringMethod.ENDSWITH;
          } else if (methodName.equals("equals")
              || methodName.equals("contentEquals")
              || methodName.equals("equalsIgnoreCase")) {
            method = StringMethod.EQUALS;
          }
          String strVal = "";
          if (methodName.equals("isEmpty")) {
            method = StringMethod.EQUALS;
          } else {
            Value value = invoke.getArg(0);
            if (!(value instanceof Constant)) {
              return false;
            }
            strVal = value.toString();
            strVal = strVal.substring(1, strVal.length() - 1);
          }

          for (AbstractTaint taint : involved) {
            if (taint instanceof SymbolicTaint) {
              // create concrete taint at equals function.
              SymbolicTaint sTaint = (SymbolicTaint) taint;
              String name = sTaint.getSymbolicName();
              if (sTaint instanceof ImpreciseTaint) {
                List<String> sourceSymbolics = ((ImpreciseTaint) sTaint).getSourceSymbolics();
                if (sourceSymbolics.size() == 1) {
                  name = sourceSymbolics.get(0);
                } else {
                  throw new RuntimeException("Not implemented yet" + sourceSymbolics.toString());
                }
              }
              StringTaint stringTaint =
                  new StringTaint(
                      new WrappedAccessPath(leftOp),
                      constraint,
                      (SymbolicTaint) taint,
                      node,
                      method,
                      strVal,
                      name);

              newTaints.add(stringTaint);
            }
          }
          in.taints().addAll(newTaints);
          return true;
        } else if (methodName.equals("length")) {
          for (AbstractTaint taint : involved) {
            if (taint instanceof SymbolicTaint) {
              SymbolicTaint sTaint = (SymbolicTaint) taint;
              String name = sTaint.getSymbolicName();
              if (sTaint instanceof ImpreciseTaint) {
                List<String> sourceSymbolics = ((ImpreciseTaint) sTaint).getSourceSymbolics();
                if (sourceSymbolics.size() == 1) {
                  name = sourceSymbolics.get(0);
                } else {
                  throw new RuntimeException("Not implemented yet" + sourceSymbolics.toString());
                }
              }
              StringTaint stringTaint =
                  new StringTaint(
                      new WrappedAccessPath(leftOp),
                      constraint,
                      (SymbolicTaint) taint,
                      node,
                      StringMethod.LENGTH,
                      null,
                      name);

              newTaints.add(stringTaint);
            }
          }
          in.taints().addAll(newTaints);

          return true;
        } else {
          logger.error("Method String." + methodName + " not implemented yet");
        }
      }
    } else if (rightOp instanceof StaticInvokeExpr) {
      StaticInvokeExpr invoke = (StaticInvokeExpr) rightOp;
      String methodName = invoke.getMethod().getName();
      String declaringClass = invoke.getMethod().getDeclaringClass().toString();
      if (declaringClass.equals("java.lang.Integer")) {
        if (methodName.equals("parseInt") || methodName.equals("valueOf")) {
          Set<AbstractTaint> involved =
              in.taints().getTaintsWithAccessPath(new WrappedAccessPath(invoke.getArg(0)));
          for (AbstractTaint taint : involved) {
            if (taint instanceof SymbolicTaint) {
              SymbolicTaint sTaint = (SymbolicTaint) taint;
              String name = sTaint.getSymbolicName();
              if (sTaint instanceof ImpreciseTaint) {
                List<String> sourceSymbolics = ((ImpreciseTaint) sTaint).getSourceSymbolics();
                if (sourceSymbolics.size() == 1) {
                  name = sourceSymbolics.get(0);
                } else {
                  throw new RuntimeException("Not implemented yet" + sourceSymbolics.toString());
                }
              }
              StringTaint stringTaint =
                  new StringTaint(
                      new WrappedAccessPath(leftOp),
                      constraint,
                      (SymbolicTaint) taint,
                      node,
                      StringMethod.TO_INT,
                      null,
                      name);

              newTaints.add(stringTaint);
            }
          }
          in.taints().addAll(newTaints);
          return true;
        } else {
          logger.error("Method Integer." + methodName + " not implemented yet");
        }
      }
    }
    return false;
  }
}
