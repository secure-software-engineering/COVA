/*
 * @version 1.0
 */

package cova.rules;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import boomerang.util.AccessPath;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.data.taints.AbstractTaint;
import cova.data.taints.SourceTaint;
import cova.vasco.Context;

import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.Stmt;

/**
 * The Class SourceTaintCreationRule defines the creation rules of source taints.
 * 
 * <p>
 * Source taints can only be created at assignments.
 * </p>
 * 
 */
public class SourceTaintCreationRule {
  private RuleManager ruleManager;
  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Instantiates a new source taint propagation rule.
   *
   * @param ruleManager
   *          the rule manager
   */
  public SourceTaintCreationRule(RuleManager ruleManager) {
    this.ruleManager = ruleManager;
  }

  /**
   * This method takes a statement and returns a taint(can be null). If the statement contains a
   * source, a new taint is created and returned, otherwise return null. This taint has the type of
   * {@link data.TaintType#SourceTaint}
   *
   * @param node
   *          the current statement to be analyzed
   * @param constraint
   *          the constraint at this statement.
   * @return a new taint created at this unit
   */
  private SourceTaint searchForSource(Unit node, IConstraint constraint) {
    String symbolicName = ruleManager.getSourceAndCallbackManager()
        .searchFieldOrMethod(ruleManager.getIcfg().getMethodOf(node), node);
    if (symbolicName != null) {
      AssignStmt assignStmt = (AssignStmt) node;
      Value leftOp = assignStmt.getLeftOp();
      if (WrappedAccessPath.isSupportedType(leftOp)) {
        return new SourceTaint(new WrappedAccessPath(leftOp), constraint, symbolicName);
      }
    }
    return null;
  }

  /**
   * Create taints considering the given statement and incoming taint in.
   *
   * @param context
   *          the context
   * @param node
   *          the statement to be analyzed
   * @param method
   *          the method
   * @param in
   *          the incoming taint set.
   * @return the outgoing taint set.
   */
  public boolean flowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Abstraction in) {
    SootMethod method = context.getMethod();
    // only when the current node is an assignStmt, it can contains a source
    AssignStmt assignStmt = (AssignStmt) node;
    IConstraint constraint = in.getConstraintOfStmt();
    SourceTaint newSourceTaint = searchForSource(assignStmt, constraint);
    boolean createdSourceTaint = false;
    if (newSourceTaint != null) {
      createdSourceTaint = true;
      Value leftOp = assignStmt.getLeftOp();
      Set<AbstractTaint> taintsOfLeftOp = in.taints()
          .getTaintsWithAccessPath(new WrappedAccessPath(leftOp));
      // update the constraint of the existing taints containing leftOp
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
      // add new source taint containing leftOp
      in.taints().add(newSourceTaint);
      // compute aliases of the leftOp using boomerang
      if (cova.core.Aliasing.canBeQueried(leftOp)) {
        Set<AccessPath> results = ruleManager.getAliasing().findAliasAtStmt(leftOp, (Stmt) node,
            method);
        // create aliasing taints
        for (AccessPath accessPath : results) {
          WrappedAccessPath alias = WrappedAccessPath.convert(accessPath);
          if (alias.getFields() != null) {
            // kill existing aliasing taints
            in.taints().removeAll(in.taints().getTaintsWithAccessPath(alias));
          SourceTaint aliasTaint = new SourceTaint(alias, newSourceTaint.getConstraint(),
              newSourceTaint.getSymbolicName());
          in.taints().add(aliasTaint);
          }
        }
      }
    }
    return createdSourceTaint;
  }
}
