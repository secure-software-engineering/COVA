
package cova.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.NegExpr;

import cova.core.InterproceduralCFG;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.data.taints.AbstractTaint;
import cova.data.taints.ImpreciseTaint;
import cova.data.taints.SourceTaint;
import cova.data.taints.SymbolicTaint;
import cova.source.symbolic.SymbolicNameManager;
import cova.vasco.Context;

/**
 * The Class ImpreciseTaintCreationRule defines the creation rules of imprecise taints.
 * 
 * <p>
 * Imprecise taints can be created at an assignment when a source or imprecise taint appears on the
 * right side of the assignment.
 * </p>
 * 
 */
public class ImpreciseTaintCreationRule {

  private InterproceduralCFG icfg;

  public ImpreciseTaintCreationRule(RuleManager ruleManager) {
    icfg = ruleManager.getIcfg();
  }

  /**
   * Creates the imprecise taint issued by the given source or imprecise taints.
   *
   * @param parentTaints
   *          the taints issue the creation of imprecise taints
   * @param val
   *          the value used to create access path of imprecise taints
   * @param constraint
   *          the constraint when the imprecise taints are created
   * @param stmt
   *          the statement which triggers the creation of imprecise taints.
   * @return the imprecise taints
   */
  private ImpreciseTaint createImpreciseTaint(SymbolicTaint[] parentTaints, Value val,
      IConstraint constraint, Unit stmt) {
    ArrayList<String> sourceSymbolics = new ArrayList<String>();
    for (SymbolicTaint parent : parentTaints) {
      if (parent instanceof SourceTaint) {
        sourceSymbolics.add(parent.getSymbolicName());
      } else if (parent instanceof ImpreciseTaint) {
        sourceSymbolics.addAll(((ImpreciseTaint) parent).getSourceSymbolics());
      }
    }
    String symbolicName = SymbolicNameManager.getInstance().createImpreciseSymbolicName(stmt,
        sourceSymbolics);
    ImpreciseTaint imprecise = new ImpreciseTaint(new WrappedAccessPath(val), constraint,
        parentTaints, sourceSymbolics, symbolicName);
    return imprecise;
  }

  public boolean normalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Unit succ, Abstraction in) {
    boolean createdImpreciseTaint = false;
    IConstraint constraint = in.getConstraintOfStmt();
    AssignStmt assignStmt = (AssignStmt) node;
    Value leftOp = assignStmt.getLeftOp();
    Value rightOp = assignStmt.getRightOp();
    Set<AbstractTaint> newImpreciseTaints = new HashSet<>();
    if (rightOp instanceof BinopExpr) {
      BinopExpr expr = (BinopExpr) rightOp;
      Value op1 = expr.getOp1();
      Value op2 = expr.getOp2();
      Set<SymbolicTaint> taints1 = null;
      Set<SymbolicTaint> taints2 = null;
      // check if op1 is a source taint or imprecise taint
      if (WrappedAccessPath.isSupportedType(op1)) {
        taints1 = in.taints().getSymbolicTaintsWithAccessPath(new WrappedAccessPath(op1));
      }
      // check if op2 is a source taint or imprecise taint
      if (WrappedAccessPath.isSupportedType(op2)) {
        taints2 = in.taints().getSymbolicTaintsWithAccessPath(new WrappedAccessPath(op2));
      }
      if (taints1 != null) {
        for (SymbolicTaint t1 : taints1) {
          if (taints2 != null && !taints2.isEmpty()) {
            for (SymbolicTaint t2 : taints2) {
              SymbolicTaint[] parents = { t1, t2 };
              newImpreciseTaints.add(createImpreciseTaint(parents, leftOp, constraint, node));
            }
          } else {
            SymbolicTaint[] parents = { t1 };
            newImpreciseTaints.add(createImpreciseTaint(parents, leftOp, constraint, node));
          }
        }
      } else {
        if (taints2 != null && !taints2.isEmpty()) {
          for (SymbolicTaint t2 : taints2) {
            SymbolicTaint[] parents = { t2 };
            newImpreciseTaints.add(createImpreciseTaint(parents, leftOp, constraint, node));
          }
        }
      }

    } else if (rightOp instanceof InstanceOfExpr || rightOp instanceof NegExpr
        || rightOp instanceof LengthExpr) {
      Value op = null;
      if (rightOp instanceof InstanceOfExpr) {
        op = ((InstanceOfExpr) rightOp).getOp();
      } else if (rightOp instanceof NegExpr) {
        op = ((NegExpr) rightOp).getOp();
      } else if (rightOp instanceof LengthExpr) {
        op = ((LengthExpr) rightOp).getOp();
      }
      Set<SymbolicTaint> taints = null;
      // check if op is a source taint or imprecise taint
      if (WrappedAccessPath.isSupportedType(op)) {
        taints = in.taints().getSymbolicTaintsWithAccessPath(new WrappedAccessPath(op));
      }
      if (taints != null) {
        for (SymbolicTaint t1 : taints) {
          SymbolicTaint[] parents = { t1 };
          newImpreciseTaints.add(createImpreciseTaint(parents, leftOp, constraint, node));
        }
      }
    } else if (rightOp instanceof InstanceFieldRef) {
      // TODO: consider the rightOp is a field
    }
    if (!newImpreciseTaints.isEmpty()) {
      createdImpreciseTaint = true;
      // Update the constraint of existing taints containing leftOp
      Set<AbstractTaint> taintsOfLeftOp = in.taints()
          .getTaintsWithAccessPath(new WrappedAccessPath(leftOp));
      for (AbstractTaint t : taintsOfLeftOp) {
        IConstraint negation = constraint.negate(false);
        IConstraint newConstraint = t.getConstraint().and(negation, false);
        newConstraint.simplify();
        if (newConstraint.isFalse()) {
          in.taints().remove(t);
        } else {
          t.updateConstraint(newConstraint);
        }
      }
      in.taints().addAll(newImpreciseTaints);
    }
    return createdImpreciseTaint;
  }

  public boolean callLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Unit succ, Abstraction in) {
    boolean createdImpreciseTaint = false;
    IConstraint constraint = in.getConstraintOfStmt();
    AssignStmt assignStmt = (AssignStmt) node;
    Value leftOp = assignStmt.getLeftOp();
    Value rightOp = assignStmt.getRightOp();
    Set<AbstractTaint> newImpreciseTaints = new HashSet<>();
    if (rightOp instanceof InvokeExpr) {
      InvokeExpr invoke = (InvokeExpr) rightOp;
      String methodName = invoke.getMethod().getName();
      if (methodName.equals("hashCode") && succ instanceof LookupSwitchStmt) {
        // don't create imprecse taint for hashCode before lookupswitchstmt
        return createdImpreciseTaint;
      }
      if (methodName.equals("equals") && icfg.hasPredAsLookupSwitchStmt(node)) {
        // don't create imprecise taint for equals after lookupswitchstmt
        return createdImpreciseTaint;
      } else {
        if (WrappedAccessPath.isSupportedType(leftOp)) {
          if (rightOp instanceof InstanceInvokeExpr) {
            InstanceInvokeExpr instanceInvoke = (InstanceInvokeExpr) rightOp;
            Value base = instanceInvoke.getBase();
            Set<SymbolicTaint> taintsOfBase = in.taints()
                .getSymbolicTaintsWithAccessPath(new WrappedAccessPath(base));
            // create imprecise taint when an existing taint whose access path is the base of the
            // instance invoke expression
            if (instanceInvoke.getArgCount() == 0) {
              for (SymbolicTaint taint : taintsOfBase) {
                SymbolicTaint[] parents = { taint };
                ImpreciseTaint imprecise = createImpreciseTaint(parents, leftOp, constraint, node);
                newImpreciseTaints.add(imprecise);
              }
            } else {
              List<SymbolicTaint> taintsOfArgs = new ArrayList<>();
              for (Value arg : instanceInvoke.getArgs()) {
                if (WrappedAccessPath.isSupportedType(arg)) {
                  taintsOfArgs.addAll(
                      in.taints().getSymbolicTaintsWithAccessPath(new WrappedAccessPath(arg)));
                }
              }
              for (SymbolicTaint taint : taintsOfBase) {
                SymbolicTaint[] parents = new SymbolicTaint[1 + taintsOfArgs.size()];
                parents[0] = taint;
                int i = 1;
                for (SymbolicTaint s : taintsOfArgs) {
                  parents[i] = s;
                  i++;
                }
                ImpreciseTaint imprecise = createImpreciseTaint(parents, leftOp, constraint, node);
                newImpreciseTaints.add(imprecise);
              }
            }

          }
        }
      }
      if (!newImpreciseTaints.isEmpty()) {
        createdImpreciseTaint = true;
        // Update the constraint of existing taints containing leftOp
        Set<AbstractTaint> taintsOfLeftOp = in.taints()
            .getTaintsWithAccessPath(new WrappedAccessPath(leftOp));
        for (AbstractTaint t : taintsOfLeftOp) {
          IConstraint negation = constraint.negate(false);
          IConstraint newConstraint = t.getConstraint().and(negation, false);
          newConstraint.simplify();
          if (newConstraint.isFalse()) {
            in.taints().remove(t);
          } else {
            t.updateConstraint(newConstraint);
          }
        }
        in.taints().addAll(newImpreciseTaints);
        // TODO: create aliasing taints
      }
    }
    return createdImpreciseTaint;
  }
}
