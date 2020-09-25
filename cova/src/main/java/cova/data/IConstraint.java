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
   * @param other the other
   * @return the i constraint
   */
  public IConstraint and(IConstraint other, boolean simplify);

  /**
   * Or Operation.
   *
   * @param other the other
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
   * @param other the other constraint to be compared
   * @return
   */
  public boolean isMoreConstrained(IConstraint other);

  public void simplify();

  public String toReadableString();
}
