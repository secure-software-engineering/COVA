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
import cova.rules.ConcreteTaintCreationRule;
import cova.rules.StringMethod;
import soot.Unit;

/**
 * The Class ConcreteTaint contains all information of a concrete taint.
 *
 * <p>It contains the concrete value of this taint. Concrete taints are created by {@link
 * ConcreteTaintCreationRule}.
 */
public class StringTaint extends AbstractTaint {

  /** The current value of this taint. */
  private SymbolicTaint source;

  private Unit stmt;

  private StringMethod stringMethod;

  private String symbolicName;
  private String constant;

  /**
   * Constructor of concrete taint.
   *
   * @param accessPath the access path of this concrete taint
   * @param constraint the constraint when this concrete taint is alive
   * @param value the current value of this concrete taint
   */
  public StringTaint(
      WrappedAccessPath accessPath,
      IConstraint constraint,
      SymbolicTaint source,
      Unit stmt,
      StringMethod stringMethod,
      String constant,
      String symbolicName) {
    super(accessPath, constraint);
    this.source = source;
    this.stmt = stmt;
    this.stringMethod = stringMethod;

    this.constant = constant;
    if (true) {
      this.symbolicName = symbolicName;
      if (symbolicName == null) {
        throw new RuntimeException();
      }
      if (symbolicName.contains("im(")) {
        throw new RuntimeException(symbolicName);
      }
    } else {
      this.symbolicName = source.getSymbolicName();
    }
  }

  public SymbolicTaint getSource() {
    return source;
  }

  public StringMethod getStringMethod() {
    return stringMethod;
  }

  public Unit getStmt() {
    return stmt;
  }

  public boolean hasStmt() {
    return stmt != null;
  }

  public boolean hasSource() {
    return source != null;
  }

  @Override
  public AbstractTaint copy() {

    return new StringTaint(
        accessPath, constraint, source, stmt, stringMethod, constant, symbolicName);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    result = prime * result + ((stmt == null) ? 0 : stmt.hashCode());
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
    StringTaint other = (StringTaint) obj;

    if (symbolicName == null) {
      if (other.symbolicName != null) {
        return false;
      }
    } else if (!symbolicName.equals(other.symbolicName)) {
      return false;
    }
    if (stmt == null) {
      if (other.stmt != null) {
        return false;
      }
    } else if (!stmt.equals(other.stmt)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("St(");
    sb.append(accessPath.toString());
    sb.append(", ");
    sb.append(constraint.toString());
    sb.append(", ");
    sb.append(symbolicName);
    if (source != null) {
      sb.append(", ");
      sb.append(source.getSymbolicName());
    }
    sb.append(")");
    return sb.toString();
  }

  @Override
  public AbstractTaint createNewTaintFromAccessPath(WrappedAccessPath a) {
    return new StringTaint(a, constraint, source, stmt, stringMethod, constant, symbolicName);
  }

  /**
   * Return a string taint whose constraint is the disjunction of the constraints of t1 and t2. The
   * access path and value of t1 and t2 must be same.
   *
   * @param t1 the first string taint
   * @param t2 the second string taint
   * @return Return a concrete taint whose constraint is the disjunction of the constraints of t1
   *     and t2.
   */
  public static StringTaint meetConstraint(StringTaint t1, StringTaint t2) {
    if (t1.accessPath.equals(t2.accessPath)) {
      IConstraint merged = t1.constraint.or(t2.constraint, true);
      StringTaint meetedTaint = (StringTaint) t1.copy();
      meetedTaint.constraint = merged;
      return meetedTaint;
    } else {
      throw new RuntimeException(
          "the access pathes and value of the given two taints must be the same.");
    }
  }

  public String getSymbolicName() {
    return symbolicName;
  }

  public String getConstant() {
    return constant;
  }
}
