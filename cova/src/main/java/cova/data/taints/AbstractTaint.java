/*
 * @version 1.0
 */

package cova.data.taints;

import soot.Type;

import cova.data.IConstraint;
import cova.data.WrappedAccessPath;

/**
 * This is the abstract class of taint. A taint must have an access path and a constraint when this
 * taint is alive.
 * 
 */
public abstract class AbstractTaint {

  /** The access path of the taint and it must be immutable. */
  protected final WrappedAccessPath accessPath;

  /** The constraint of the taint when it is alive. */
  protected IConstraint constraint;

  /**
   * Constructor of a taint
   *
   * @param accessPath
   *          the access path
   * @param constraint
   *          the constraint
   */
  public AbstractTaint(WrappedAccessPath accessPath, IConstraint constraint) {
    this.accessPath = accessPath;
    this.constraint = constraint;
  }

  /**
   * Gets the access path.
   *
   * @return the access path
   */
  public WrappedAccessPath getAccessPath() {
    return accessPath;
  }

  /**
   * Gets the constraint.
   *
   * @return the constraint
   */
  public IConstraint getConstraint() {
    return constraint;
  }

  /**
   * update the constraint of this taint.
   * 
   * @param c
   *          the current constraint of this taint
   */
  public void updateConstraint(IConstraint c) {
    constraint = c;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((accessPath == null) ? 0 : accessPath.hashCode());
    result = prime * result + ((constraint == null) ? 0 : constraint.hashCode());
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
    AbstractTaint other = (AbstractTaint) obj;
    if (accessPath == null) {
      if (other.accessPath != null) {
        return false;
      }
    } else if (!accessPath.equals(other.accessPath)) {
      return false;
    }
    if (constraint == null) {
      if (other.constraint != null) {
        return false;
      }
    } else if (!constraint.equals(other.constraint)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the data type of the access path. For an acess path only contains a base, the type is just
   * the type of base. Otherwise, the type is the data type of the last field.
   *
   * @return the data type of the access path
   */
  public Type getType() {
    if (accessPath.isLocal()) {
      return accessPath.getBaseType();
    } else {
      return accessPath.getLastFieldType();
    }
  }

  /**
   * Return a copy object of this taint.
   *
   * @return the abstract taint
   */
  public abstract AbstractTaint copy();

  /**
   * Create a new taint derived from the given access path as it's access path. Except the access
   * path, all other attributes of new taint have the same value as this taint
   *
   * @param a
   *          the given access path
   * @return the abstract taint
   */
  public abstract AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a);

  /**
   * Checks if this is taint contains the return value of callee.
   *
   * @return true, if this is taint contains the return value of callee.
   */
  public boolean isReturnTaint() {
    return accessPath.isReturnAccessPath();
  }

}
