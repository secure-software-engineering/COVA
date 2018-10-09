package de.upb.swt.cova.rules;

import de.upb.swt.cova.core.ConstraintFactory;
import de.upb.swt.cova.core.RuleManager;
import de.upb.swt.cova.data.Abstraction;
import de.upb.swt.cova.data.IConstraint;
import de.upb.swt.cova.vasco.Context;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;

public class UIConstraintCreationRule implements IRule<SootMethod, Unit, Abstraction> {
  /** The rule manager. */
  private RuleManager ruleManager;

  public UIConstraintCreationRule(RuleManager ruleManager) {
    this.ruleManager = ruleManager;
  }

  @Override
  public Abstraction normalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Unit succ, Abstraction inValue) {
    return null;
  }

  @Override
  public Abstraction callEntryFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction in) {
    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    if (invokeExpr instanceof InstanceInvokeExpr) {
      String callbackName = ruleManager.getSourceAndCallbackManager()
          .searchCallback(context.getMethod(), node);
      if (callbackName != null) {
        // create constraint for callback
        IConstraint c = ConstraintFactory.createConstraint(callbackName);
        in.updateConstraintForCallback(c);
      }
      return in;
    } else {
      return null;
    }
  }

  @Override
  public Abstraction callExitFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction exitValue) {
    return null;
  }

  @Override
  public Abstraction callLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      Unit node, Unit succ, Abstraction in) {
    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    if (invokeExpr instanceof InstanceInvokeExpr) {
      String callbackName = ruleManager.getSourceAndCallbackManager()
          .searchCallback(context.getMethod(), node);
      if (callbackName != null) {
        // create constraint for callback
        IConstraint c = ConstraintFactory.createConstraint(callbackName);
        in.updateConstraintForCallback(c);
      }
      return in;
    } else {
      return null;
    }
  }
}