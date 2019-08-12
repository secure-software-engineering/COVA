/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */

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
