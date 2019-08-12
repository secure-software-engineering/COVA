
package cova.data.taints;

import cova.data.ConstraintZ3;
import cova.data.WrappedAccessPath;

/**
 * The Class ZeroTaint represents the taint which is alive at all statements.
 */
public class ZeroTaint extends AbstractTaint {

  private static ZeroTaint instance;

  /**
   * Instantiates a new zero taint.
   */
  private ZeroTaint() {
    super(WrappedAccessPath.getZeroAccessPath(), ConstraintZ3.getTrue());
  }

  public static ZeroTaint getInstance() {
    if (instance == null) {
      instance = new ZeroTaint();
    }
    return instance;
  }

  @Override
  public AbstractTaint copy() {
    return instance;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("(");
    sb.append(accessPath.toString());
    sb.append(", ");
    sb.append(constraint.toString());
    sb.append(")");
    return sb.toString();
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return copy();
  }
}
