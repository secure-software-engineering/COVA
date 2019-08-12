
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
