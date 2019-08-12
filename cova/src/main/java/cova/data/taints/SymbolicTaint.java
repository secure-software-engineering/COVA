
package cova.data.taints;

import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.source.symbolic.SymbolicNameManager;

/**
 * The Class SymbolicTaint is an abstract Class of all symbolic taints whose values are uncertain.
 * 
 * <p>
 * Symbolic taints use {@link SymbolicTaint#symbolicName} to represent values. Different than
 * {@link ConcreteTaint} whose value can be derived from the program, statically we can not know the
 * value of a {@link SymbolicTaint}. {@link SourceTaint} and {@link ImpreciseTaint} are both
 * symbolic taints.
 * </p>
 * 
 */
public abstract class SymbolicTaint extends AbstractTaint {
  
  /** The symbolic name. */
  protected String symbolicName;

  /**
   * Instantiates a new symbolic taint.
   *
   * @param accessPath
   *          the access path
   * @param constraint
   *          the constraint
   * @param symbolicName
   *          the symbolic name created by {@link SymbolicNameManager}
   */
  public SymbolicTaint(WrappedAccessPath accessPath, IConstraint constraint, String symbolicName) {
    super(accessPath, constraint);
    this.symbolicName = symbolicName;
  }

  /**
   * Gets the symbolic name.
   *
   * @return the symbolic name
   */
  public String getSymbolicName() {
    return symbolicName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((symbolicName == null) ? 0 : symbolicName.hashCode());
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
    SymbolicTaint other = (SymbolicTaint) obj;
    if (symbolicName == null) {
      if (other.symbolicName != null) {
        return false;
      }
    } else if (!symbolicName.equals(other.symbolicName)) {
      return false;
    }
    return true;
  }

  /**
   * Return a symbolic taint whose constraint is the disjunction of the constraints of t1 and t2.
   * The access path and symbolic name of t1 and t2 must be same.
   * 
   * @param t1
   *          the first symbolic taint
   * @param t2
   *          the second symbolic taint
   * @return
   */
  public static SymbolicTaint meetConstraint(SymbolicTaint t1, SymbolicTaint t2) {
    if (t1.accessPath.equals(t2.accessPath) && t1.getSymbolicName().equals(t2.getSymbolicName())) {
      IConstraint merged = t1.constraint.or(t2.constraint, true);
      SymbolicTaint meetedTaint = (SymbolicTaint) t1.copy();
      meetedTaint.constraint = merged;
      return meetedTaint;
    } else {
      throw new RuntimeException(
          "the access pathes and symbolic names of the given two taints must be the same.");
    }
  }
}
