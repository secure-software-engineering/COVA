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
package cova.reporter;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

/**
 * The Class ConstraintTag is used to print constraint als comments in jimple code.
 *
 * @date 05.09.2017
 */
public class ConstraintTag implements Tag {

  /** The String representation of a constraint. */
  String constraint;

  /**
   * Instantiates a new constraint tag.
   *
   * @param constraint the constraint
   */
  public ConstraintTag(String constraint) {
    this.constraint = constraint;
  }

  @Override
  public String getName() {
    return "ConstraintTag";
  }

  @Override
  public byte[] getValue() throws AttributeValueException {
    return constraint.getBytes();
  }

  @Override
  public String toString() {
    return constraint;
  }
}
