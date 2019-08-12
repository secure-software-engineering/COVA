
package cova.data.taints;

import soot.Unit;
import soot.Value;

import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.rules.ConcreteTaintCreationRule;

/**
 * The Class ConcreteTaint contains all information of a concrete taint.
 * 
 * <p>
 * It contains the concrete value of this taint. Concrete taints are created by
 * {@link ConcreteTaintCreationRule}.
 * </p>
 * 
 */
public class ConcreteTaint extends AbstractTaint {

  /** The current value of this taint. */
  private Value currentValue;
  private SymbolicTaint source;
  private Unit stmt;

  /**
   * Constructor of concrete taint.
   * 
   * @param accessPath
   *          the access path of this concrete taint
   * @param constraint
   *          the constraint when this concrete taint is alive
   * @param value
   *          the current value of this concrete taint
   */
  public ConcreteTaint(WrappedAccessPath accessPath, IConstraint constraint, Value value) {
    super(accessPath, constraint);
    currentValue = value;
    source = null;
    stmt = null;
  }

  public ConcreteTaint(WrappedAccessPath accessPath, IConstraint constraint, Value value,
      SymbolicTaint source, Unit stmt) {
    this(accessPath, constraint, value);
    this.source = source;
    this.stmt = stmt;
  }

  /**
   * Gets the current value.
   *
   * @return the current value
   */
  public Value getCurrentValue() {
    return currentValue;
  }

  public SymbolicTaint getSource() {
    return source;
  }

  public Unit getStmt() {
    return stmt;
  }

  public boolean hasStmt() {
    return stmt != null;
  }

  public boolean hasSource() {
    return source != null;
  }

  /**
   * Update current value.
   *
   * @param value
   *          the value
   */
  public void updateCurrentValue(Value value) {
    currentValue = value;
  }

  @Override
  public AbstractTaint copy() {
    return new ConcreteTaint(accessPath, constraint, currentValue, source, stmt);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((currentValue == null) ? 0 : currentValue.hashCode());
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConcreteTaint other = (ConcreteTaint) obj;
    if (currentValue == null) {
      if (other.currentValue != null) {
        return false;
      }
    } else if (!currentValue.equals(other.currentValue)) {
      return false;
    }
    if (source == null) {
      if (other.source != null) {
        return false;
      }
    } else if (!source.equals(other.source)) {
      return false;
    }
    if (stmt == null) {
      if (other.stmt != null) {
        return false;
      }
    } else if (!stmt.equals(other.stmt)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("C(");
    sb.append(accessPath.toString());
    sb.append(", ");
    sb.append(constraint.toString());
    sb.append(", ");
    sb.append(currentValue.toString());
    if (source != null) {
      sb.append(", ");
      sb.append(source.getSymbolicName());
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return new ConcreteTaint(a, constraint, currentValue);
  }

  /**
   * Return a concrete taint whose constraint is the disjunction of the constraints of t1 and t2.
   * The access path and value of t1 and t2 must be same.
   * 
   * @param t1
   *          the first concrete taint
   * @param t2
   *          the second concrete taint
   * @return Return a concrete taint whose constraint is the disjunction of the constraints of t1
   *         and t2.
   */
  public static ConcreteTaint meetConstraint(ConcreteTaint t1, ConcreteTaint t2) {
    if (t1.accessPath.equals(t2.accessPath) && t1.currentValue.equals(t2.currentValue)) {
      IConstraint merged = t1.constraint.or(t2.constraint, true);
      ConcreteTaint meetedTaint = (ConcreteTaint) t1.copy();
      meetedTaint.constraint = merged;
      return meetedTaint;
    } else {
      throw new RuntimeException(
          "the access pathes and value of the given two taints must be the same.");
    }
  }

}
