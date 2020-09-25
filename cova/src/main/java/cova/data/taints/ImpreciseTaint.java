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
import cova.rules.ImpreciseTaintCreationRule;
import cova.source.symbolic.SymbolicNameManager;
import java.util.List;

/**
 * The Class ImpreciseTaint contains all information of an imprecise taint.
 *
 * <p>Imprecise taints which are created by {@link ImpreciseTaintCreationRule} due to the usage of
 * symoblic taints.
 */
public class ImpreciseTaint extends SymbolicTaint {

  /** The parent taints that related to this imprecise taint. */
  private final SymbolicTaint[] parentTaints;

  private final List<String> sourceSymbolics;

  /**
   * Constructor of imprecise taint.
   *
   * @param accessPath the access path of this imprecise taint
   * @param constraint the constraint when this imprecise taint is alive
   * @param parentTaints the parent taints that causes the creation of this imprecise taint
   * @param sourceSymbolics the symbolic names of related source taints
   * @param symbolicName the symbolic name of this imprecise taint created {@link
   *     SymbolicNameManager#createImpreciseSymbolicName(soot.Unit, String)}
   */
  public ImpreciseTaint(
      WrappedAccessPath accessPath,
      IConstraint constraint,
      SymbolicTaint[] parentTaints,
      List<String> sourceSymbolics,
      String symbolicName) {
    super(accessPath, constraint, symbolicName);
    this.parentTaints = parentTaints;
    this.sourceSymbolics = sourceSymbolics;
  }

  /**
   * Gets the parent taint that issues this imprecise taint.
   *
   * @return the source taint
   */
  public SymbolicTaint[] getParentTaints() {
    return parentTaints;
  }

  public List<String> getSourceSymbolics() {
    return sourceSymbolics;
  }

  @Override
  public AbstractTaint copy() {
    return new ImpreciseTaint(accessPath, constraint, parentTaints, sourceSymbolics, symbolicName);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("I(");
    sb.append(accessPath.toString());
    sb.append(",");
    sb.append(constraint.toString());
    sb.append(",");
    sb.append(symbolicName);
    sb.append(")");
    return sb.toString();
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return new ImpreciseTaint(a, constraint, parentTaints, sourceSymbolics, symbolicName);
  }
}
