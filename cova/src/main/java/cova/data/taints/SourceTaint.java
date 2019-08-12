
package cova.data.taints;

import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.rules.SourceTaintCreationRule;
import cova.source.data.SourceField;
import cova.source.data.SourceMethod;
import cova.source.data.SourceUICallback;
import cova.source.symbolic.SymbolicNameManager;

/**
 * The Class SourceTaint contains all information of a source taint.
 * 
 * <p>
 * Source taints are created by {@link SourceTaintCreationRule} at statements that contain a
 * source of type such as {@link SourceField}, {@link SourceMethod} or {@link SourceUICallback}.
 * </p>
 * 
 *
 */
public class SourceTaint extends SymbolicTaint {

  /**
   * The constructor of a taint created at a statement that contains a source.
   * 
   * @param accessPath
   *          the access path represents the source taint
   * @param constraint
   *          the constraint when this source taint is alive
   * @param symbolicName
   *          the symbolic name of this source taint created by
   *          {@link SymbolicNameManager#createSymbolicName(soot.Unit, cova.source.data.Source)}
   */
  public SourceTaint(WrappedAccessPath accessPath, IConstraint constraint, String symbolicName) {
    super(accessPath, constraint, symbolicName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("S(");
    sb.append(accessPath.toString());
    sb.append(", ");
    sb.append(constraint.toString());
    sb.append(", ");
    sb.append(symbolicName);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public AbstractTaint copy() {
    return new SourceTaint(accessPath, constraint, symbolicName);
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return new SourceTaint(a, constraint, symbolicName);
  }

}
