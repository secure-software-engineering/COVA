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
package cova.data.taints;

import cova.data.IConstraint;
import cova.data.WrappedAccessPath;
import cova.rules.SourceTaintCreationRule;
import cova.source.data.SourceField;
import cova.source.data.SourceMethod;
import cova.source.data.SourceUICallback;
import cova.source.symbolic.SymbolicNameManager;
import java.util.List;

/**
 * The Class SourceTaint contains all information of a source taint.
 *
 * <p>Source taints are created by {@link SourceTaintCreationRule} at statements that contain a
 * source of type such as {@link SourceField}, {@link SourceMethod} or {@link SourceUICallback}.
 */
public class SourceTaint extends SymbolicTaint {

  /**
   * The constructor of a taint created at a statement that contains a source.
   *
   * @param accessPath the access path represents the source taint
   * @param constraint the constraint when this source taint is alive
   * @param symbolicName the symbolic name of this source taint created by {@link
   *     SymbolicNameManager#createSymbolicName(soot.Unit, cova.source.data.Source)}
   */
  public SourceTaint(WrappedAccessPath accessPath, IConstraint constraint, String symbolicName) {
    super(accessPath, constraint, symbolicName);
  }

  public SourceTaint(
      WrappedAccessPath accessPath,
      IConstraint constraint,
      String symbolicName,
      List<String> positionInfo) {
    this(accessPath, constraint, symbolicName);
    this.info = positionInfo;
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
    return new SourceTaint(accessPath, constraint, symbolicName, info);
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return new SourceTaint(a, constraint, symbolicName, info);
  }
}
