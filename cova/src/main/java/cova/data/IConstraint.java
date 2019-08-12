
package cova.data;

/**
 * The Interface Constraint declare all operations a constraint class should support.
 *
 * @date 07.08.2017
 */
public interface IConstraint {

  /**
   * And Operation.
   *
   * @param other
   *          the other
   * @return the i constraint
   */
  public IConstraint and(IConstraint other, boolean simplify);

  /**
   * Or Operation.
   *
   * @param other
   *          the other
   * @return the i constraint
   */
  public IConstraint or(IConstraint other, boolean simplify);

  /**
   * Negate this constraint.
   *
   * @return the i constraint
   */
  public IConstraint negate(boolean simplify);

  /**
   * Copy this constraint.
   *
   * @return the i constraint
   */
  public IConstraint copy();

  /**
   * Checks if this constraint is true.
   *
   * @return true, if this constraint is true
   */
  public boolean isTrue();

  /**
   * Checks if this constraint is false.
   *
   * @return true, if this constraint is false
   */
  public boolean isFalse();

  /**
   * Checks if this constraint is more constrained than other.
   * 
   * @param other
   *          the other constraint to be compared
   * @return
   */
  public boolean isMoreConstrained(IConstraint other);

  public void simplify();

  public String toReadableString();
}
