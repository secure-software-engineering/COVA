/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package cova.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cova.core.ConstraintFactory;
import cova.core.InterproceduralCFG;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.ConstraintZ3;
import cova.data.IConstraint;
import cova.data.NodeType;
import cova.data.WrappedAccessPath;
import cova.data.WrappedTaintSet;
import cova.data.taints.AbstractTaint;
import cova.data.taints.ConcreteTaint;
import cova.vasco.Context;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.TableSwitchStmt;

public class TaintConstraintCreationRule implements IRule<SootMethod, Unit, Abstraction> {
  /** The interprocedural control flow graph. */
  private InterproceduralCFG icfg;
  
  private RuleManager ruleManager;

  public TaintConstraintCreationRule(RuleManager ruleManager) {
    icfg = ruleManager.getIcfg();
    this.ruleManager=ruleManager;
  }

  /**
   * Creates the constraint for if-statement.
   *
   * @param context 
   * 		 the context
   * @param node
   *          the current if-statement
   * @param succ
   *          the successor statement
   * @param in
   *          the abstraction before analyzing the current if-statement
   * @return the abstraction after analyzing the current if-statement
   */
  private Abstraction createConstraintForIfStmt(Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    IConstraint constraintOfStmt = in.getConstraintOfStmt();
    IfStmt ifStmt = (IfStmt) node;
    ConditionExpr conditionExpr = (ConditionExpr) ifStmt.getCondition();
    Value op1 = conditionExpr.getOp1();
    Value op2 = conditionExpr.getOp2();
    Set<AbstractTaint> involved1 = new HashSet<AbstractTaint>();
    if (WrappedAccessPath.isSupportedType(op1)) {
      involved1 = in.taints().getTaintsWithAccessPath(new WrappedAccessPath(op1));
    }
    Set<AbstractTaint> involved2 = new HashSet<AbstractTaint>();
    if (WrappedAccessPath.isSupportedType(op2)) {
      involved2 = in.taints().getTaintsWithAccessPath(new WrappedAccessPath(op2));
    }
    IConstraint constraint = null;
    boolean isFallThroughEdge = icfg.isFallThroughSuccessor(node, succ);
    if (!involved1.isEmpty() && !involved2.isEmpty()) {
      constraint = ConstraintZ3.getFalse();
      for (AbstractTaint taint1 : involved1) {
        for (AbstractTaint taint2 : involved2) {
          IConstraint c = ConstraintFactory.createConstraint(ruleManager.getConfig().recordPath(),taint1, taint2, conditionExpr,
              isFallThroughEdge);
          constraint = constraint.or(c, false);
        }
      }
      constraint.simplify();
      IConstraint negation1 = WrappedTaintSet.getMissingConstraint(involved1, constraintOfStmt);
      IConstraint negation2 = WrappedTaintSet.getMissingConstraint(involved2, constraintOfStmt);
      if (!negation1.isFalse()) {
        constraint = constraint.or(negation1, true);
      }
      if (!negation2.isFalse()) {
        constraint = constraint.or(negation2, true);
      }
    }
    if (!involved1.isEmpty() && involved2.isEmpty()) {
      constraint = ConstraintZ3.getFalse();
      boolean allConcrete = true;
      for (AbstractTaint taint : involved1) {
        IConstraint c = ConstraintFactory.createConstraint(ruleManager.getConfig().recordPath(),taint, conditionExpr, false,
            isFallThroughEdge);
        if(ruleManager.getConfig().recordPath())
        	c.getPath().add(NodeType.control,context.getMethod().getDeclaringClass().getName(), node.getJavaSourceStartLineNumber());
        constraint = constraint.or(c, false);
        if (!(taint instanceof ConcreteTaint)) {
          allConcrete = false;
        }
      }
      constraint.simplify();
      IConstraint negation = WrappedTaintSet.getMissingConstraint(involved1, constraintOfStmt);
      if (!allConcrete) {
        constraint = constraint.or(negation, false);
      } else {
        if (constraint.isFalse()) {
          constraint = negation;
        }
      }
    }
    if (involved1.isEmpty() && !involved2.isEmpty()) {
      constraint = ConstraintZ3.getFalse();
      boolean allConcrete = true;
      for (AbstractTaint taint : involved2) {
        IConstraint c = ConstraintFactory.createConstraint(ruleManager.getConfig().recordPath(),taint, conditionExpr, true,
            isFallThroughEdge);
        constraint = constraint.or(c, false);
        if (!(taint instanceof ConcreteTaint)) {
          allConcrete = false;
        }
      }
      constraint.simplify();
      IConstraint negation = WrappedTaintSet.getMissingConstraint(involved2, constraintOfStmt);
      if (!allConcrete) {
        constraint = constraint.or(negation, false);
      } else {
        if (constraint.isFalse()) {
          constraint = negation;
        }
      }
    }
    if (constraint != null) {
      constraint.simplify();
      in.updateConstraint(constraint);
    }
    return in;
  }

  /**
   * Creates the constraint for table switch statement.
   *
   * @param context 
   * 		 the context
   * @param node
   *          the current table switch statement
   * @param succ
   *          the successor statement
   * @param in
   *          the abstraction before analyzing the current table switch statement
   * @return the abstraction after analyzing the current table switch statement
   */
  private Abstraction createConstraintForTableSwitchStmt(Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    IConstraint constraint = null;
    if (node instanceof TableSwitchStmt) {
      TableSwitchStmt switchStmt = (TableSwitchStmt) node;
      Value key = switchStmt.getKey();
      if (WrappedAccessPath.isSupportedType(key)) {
        Set<AbstractTaint> involved = in.taints()
            .getTaintsWithAccessPath(new WrappedAccessPath(key));
        for (AbstractTaint taint : involved) {
          constraint = ConstraintZ3.getFalse();
          Unit defaultTarget = switchStmt.getDefaultTarget();
          boolean createConstraint = true;
          if (taint instanceof ConcreteTaint) {
            // for concrete taint we don't create constraint yet
            createConstraint = false;
          }
          if (defaultTarget.equals(succ)) {
            // default case
            if (createConstraint) {
              List<IConstraint> constraints = new ArrayList<>();
              for (int i = 0; i < switchStmt.getTargets().size(); i++) {
                if (!defaultTarget.equals(switchStmt.getTarget(i))) {
                  int value = i + switchStmt.getLowIndex();
                  EqExpr eq = Jimple.v().newEqExpr(key, IntConstant.v(value));
                  IConstraint c = ConstraintFactory.createConstraint(ruleManager.getConfig().recordPath(), taint, eq, false, true);
                  if(ruleManager.getConfig().recordPath())
                  { 
                	  c.getPath().add(NodeType.control,context.getMethod().getDeclaringClass().getName(), node.getJavaSourceStartLineNumber());              
                  }
                	  constraints.add(c);
                }
              }
              IConstraint defaultConstraint = ConstraintZ3.getTrue();
              for (IConstraint c : constraints) {
                defaultConstraint = defaultConstraint.and(c, false);
              }
              constraint = constraint.or(defaultConstraint, false);
            } else {
              constraint = ConstraintZ3.getTrue();
            }
          } else {
            // switch case
            Unit target = null;
            int value = 0;
            for (int i = 0; i < switchStmt.getTargets().size(); i++) {
              target = switchStmt.getTarget(i);
              if (target.equals(succ)) {
                value = i + switchStmt.getLowIndex();
                break;
              }
            }
            if (createConstraint) {
              EqExpr eqExpr = Jimple.v().newEqExpr(key, IntConstant.v(value));
              IConstraint c = ConstraintFactory.createConstraint(ruleManager.getConfig().recordPath(), taint, eqExpr, false, false);
              constraint = constraint.or(c, false);
            } else {
              constraint = ConstraintZ3.getTrue();
            }
          }
        }
      }
    }
    if (constraint != null) {
      constraint.simplify();
      in.updateConstraint(constraint);
    }
    return in;
  }

  /**
   * Creates the constraint for the default case by lookup switch statement.
   *
   * @param node
   *          the node
   * @param succ
   *          the succ
   * @param in
   *          the in
   * @return the abstraction
   */
  private Abstraction createConstraintForLookupSwitchStmt(Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ, Abstraction in) {
    Unit pred = icfg.getPredAsLookupSwitchStmt(node);
    if (pred != null) {
      // create constraint for the default case by lookupswitchstmt
      LookupSwitchStmt switchStmt = (LookupSwitchStmt) pred;
      Unit defaultTarget = switchStmt.getDefaultTarget();
      if (defaultTarget.equals(node)) {
        IConstraint constraint = ConstraintZ3.getTrue();
        List<AbstractTaint> remove = new ArrayList<>();
        for (AbstractTaint taint : in.taints().getAllConcreteTaints()) {
          ConcreteTaint concrete = (ConcreteTaint) taint;
          if (concrete.hasStmt() && concrete.getStmt().equals(switchStmt)) {
            constraint = constraint.and(concrete.getConstraint(), false);
            if(ruleManager.getConfig().recordPath())
            	constraint.getPath().add(NodeType.control, context.getMethod().getDeclaringClass().getName(),node.getJavaSourceStartLineNumber());
            remove.add(concrete);
          }
        }
        in.taints().removeAll(remove);
        if (!constraint.isTrue()) {
          constraint.simplify();
          in.updateConstraint(constraint);
        }
      }
    }
    return in;
  }

  @Override
  public Abstraction normalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Unit succ, Abstraction in) {
    in = createConstraintForLookupSwitchStmt(context, node, succ, in);
    if (node instanceof IfStmt) {
      return createConstraintForIfStmt(context,node, succ, in);
    } else if (node instanceof TableSwitchStmt) {
      return createConstraintForTableSwitchStmt(context, node, succ, in);
    } else {
      return null;
    }
  }

  @Override
  public Abstraction callEntryFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction in) {
    return null;
  }

  @Override
  public Abstraction callExitFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction exitValue) {
    return null;
  }

  @Override
  public Abstraction callLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      Unit node, Unit succ, Abstraction in) {
    return createConstraintForLookupSwitchStmt(context, node, succ, in);
  }
}
