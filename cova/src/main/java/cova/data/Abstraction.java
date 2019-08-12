/*
 * @version 1.0
 */

package cova.data;

import cova.data.taints.AbstractTaint;
import cova.data.taints.ZeroTaint;

/**
 * The Class Abstraction contains the constraint of current statement and a set of taints.
 * 
 */
public class Abstraction {

  /** The constraint of current statement. */
  private IConstraint constraintOfStmt;

  /** The wrapped taint set. */
  private WrappedTaintSet taintSet;

  private static Abstraction TOP;

  private static Abstraction BOTTOM;

  /**
   * Instantiates a new abstraction with given constraint and taint set
   */
  public Abstraction(IConstraint c, WrappedTaintSet set) {
    constraintOfStmt = c;
    taintSet = set;
  }

  /**
   * Instantiates a new abstraction as a copy of given abstraction.
   *
   * @param fact
   *          the fact
   */
  public Abstraction(Abstraction fact) {
    try {
    taintSet = new WrappedTaintSet(fact.taintSet);
    constraintOfStmt = fact.constraintOfStmt.copy();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the constraint of current statement.
   *
   * @return the constraint of current statement.
   */
  public IConstraint getConstraintOfStmt() {
    return constraintOfStmt;
  }

  public void setConstraintOfStmt(IConstraint constraint) {
    constraintOfStmt = constraint;
  }
  /**
   * Updates the constraint of current statement with given constraint and the constraint of each
   * taint by performing AND-operation to its current constraint.
   *
   * @param constraint
   *          the constraint
   */
  public void updateConstraint(IConstraint constraint) {
    constraintOfStmt = constraint;
    // update also the constraint all taints
    taintSet = taintSet.updateConstraintForTaints(constraint);
  }

  /**
   * Updates the constraint of current statement and taints by performing AND-operation to current
   * constraints. It is only used for creating constraint issued by callbacks.
   *
   * @param constraint
   *          the constraint
   */
  public void updateConstraintForCallback(IConstraint constraint) {
    constraintOfStmt = constraintOfStmt.and(constraint, false);
    // update also the constraint all taints
    taintSet = taintSet.updateConstraintForTaints(constraint);
  }

  /**
   * Return the top value whose constraint is true and only contains the zero taint.
   *
   * @return the top value
   */
  public static Abstraction topValue() {
    if (TOP == null) {
      TOP = new Abstraction(ConstraintZ3.getTrue(), new WrappedTaintSet());
      TOP.taintSet.add(ZeroTaint.getInstance());
    }
    return TOP;
  }

  /**
   * Return the bottom value represents unknown value
   * 
   * @return the bottom value
   */
  public static Abstraction bottomValue() {
    if (BOTTOM == null) {
      BOTTOM = new Abstraction(null, null);
    }
    return BOTTOM;
  }
  /**
   * Replace zero taint with given zero taint.
   *
   * @param zeroTaint
   *          the new zero taint
   */
  public void replaceZeroTaint(AbstractTaint zeroTaint) {
    AbstractTaint oldZeroTaint = taintSet.getZeroTaint();
    taintSet.remove(oldZeroTaint);
    taintSet.add(zeroTaint);
  }

  /**
   * Meet operation of two given abstractions.The constraints are merged with disjunction and the taints are merged with
   * {@link WrappedTaintSet#meet(WrappedTaintSet)}
   *
   * @param a
   *          the first abstraction
   * @param b
   *          the second abstraction
   * @return the abstraction of a and b after performing the meet operation.
   */
  public static Abstraction meet(Abstraction a, Abstraction b) {
    if (a.equals(bottomValue())) {
      return b;
    } else if (b.equals(bottomValue())) {
      return a;
    }
    Abstraction meet = new Abstraction(a);
    meet.taintSet = meet.taintSet.meet(b.taintSet);
    meet.constraintOfStmt = a.getConstraintOfStmt().or(b.getConstraintOfStmt(), true).copy();
    return meet;
  }

  /**
   * Meet operation of two given abstractions shallowly. The constraints are merged with disjunction and the taints are
   * simply merged with set union.
   * 
   *
   * @param a
   *          the first abstraction
   * @param b
   *          the second abstraction
   * @return the abstraction of a and b after performing the shallow meet operation.
   */
  public static Abstraction shallowMeet(Abstraction a, Abstraction b) {
    IConstraint conjuction = a.getConstraintOfStmt().or(b.getConstraintOfStmt(), true).copy();
    WrappedTaintSet taints = new WrappedTaintSet();
    taints.addAll(a.taints());
    taints.addAll(b.taints());
    return new Abstraction(conjuction, taints);
  }
  /**
   * Merge local abstraction with return abstraction at call site.
   *
   * @param local
   *          the local abstraction of caller
   * @param ret
   *          the return abstraction of callee
   * @return the merged abstraction at call site
   */
  public static Abstraction merge(Abstraction local, Abstraction ret) {
    Abstraction merged = new Abstraction(local);
    merged.taintSet = merged.taintSet.merge(ret.taintSet);
    return merged;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((constraintOfStmt == null) ? 0 : constraintOfStmt.hashCode());
    result = prime * result + ((taintSet == null) ? 0 : taintSet.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Abstraction other = (Abstraction) obj;
    if (constraintOfStmt == null) {
      if (other.constraintOfStmt != null) {
        return false;
      }
    } else if (!constraintOfStmt.equals(other.constraintOfStmt)) {
      return false;
    }
    if (taintSet == null) {
      if (other.taintSet != null) {
        return false;
      }
    } else if (!taintSet.equals(other.taintSet)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    if (equals(BOTTOM)) {
      return "BOT";
    }
    StringBuilder sb = new StringBuilder(constraintOfStmt.toString());
    if (!taintSet.isEmpty()) {
      sb.append(" -- ");
      sb.append(taintSet.toString());
    }
    return sb.toString();
  }

  /**
   * Gets the taint set.
   *
   * @return the taint set
   */
  public WrappedTaintSet taints() {
    return taintSet;
  }

  /**
   * Updates taint set.
   *
   * @param newSet
   *          the newtaint set
   */
  public void updateTaintSet(WrappedTaintSet newSet) {
    taintSet = newSet;
  }

  public boolean isBottomValue()
  {
    return equals(BOTTOM);
  }

  public void clearTaintSet() {
    if (taintSet != null) {
      taintSet.clear();
    }
  }
}
