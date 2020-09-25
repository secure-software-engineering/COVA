/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package cova.rules;

import cova.core.ConstraintFactory;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.vasco.Context;
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
  public Abstraction normalFlowFunction(
      Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction inValue) {
    return null;
  }

  @Override
  public Abstraction callEntryFlowFunction(
      Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee,
      Unit node,
      Unit succ,
      Abstraction in) {
    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    if (invokeExpr instanceof InstanceInvokeExpr) {
      String callbackName =
          ruleManager.getSourceAndCallbackManager().searchCallback(context.getMethod(), node);
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
  public Abstraction callExitFlowFunction(
      Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee,
      Unit node,
      Unit succ,
      Abstraction exitValue) {
    return null;
  }

  @Override
  public Abstraction callLocalFlowFunction(
      Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    if (invokeExpr instanceof InstanceInvokeExpr) {
      String callbackName =
          ruleManager.getSourceAndCallbackManager().searchCallback(context.getMethod(), node);
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
