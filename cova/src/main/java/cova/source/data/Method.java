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
package cova.source.data;

import java.util.List;
import soot.jimple.infoflow.data.SootMethodAndClass;

/**
 * The Class Method is a data container stores the string representation of a method call.
 *
 * <p>It extends the class {@link SootMethodAndClass} with parameter values.
 *
 * @date 05.09.2017
 */
public class Method extends SootMethodAndClass {

  private final List<String> parameterValues;

  /**
   * Instantiates a new method.
   *
   * @param className the class name
   * @param returnType the return type
   * @param methodName the method name
   * @param parameters the parameters
   * @param parameterValues the parameter values
   */
  public Method(
      String className,
      String returnType,
      String methodName,
      List<String> parameters,
      List<String> parameterValues) {
    super(methodName, className, returnType, parameters);
    this.parameterValues = parameterValues;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((parameterValues == null) ? 0 : parameterValues.hashCode());
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
    Method other = (Method) obj;
    if (parameterValues == null) {
      if (other.parameterValues != null) {
        return false;
      }
    } else if (!parameterValues.equals(other.parameterValues)) {
      return false;
    }
    return true;
  }

  @Override
  public String getSignature() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(super.getSignature());
    if (!parameterValues.isEmpty()) {
      sb.append("(");
      for (int i = 0; i < parameterValues.size(); i++) {
        if (i > 0) {
          sb.append(", ");
        }
        sb.append(parameterValues.get(i).trim());
      }
      sb.append(")");
    }
    return sb.toString();
  }
}
