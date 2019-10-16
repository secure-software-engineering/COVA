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

/**
 * The Class Field is a data container stores the string representation of a field.
 *
 * @date 05.09.2017
 */
public class Field {

  /** The class name. */
  private final String className;

  /** The field type. */
  private final String fieldType;

  /** The field name. */
  private final String fieldName;

  /**
   * Instantiates a new field.
   *
   * @param className the class name
   * @param fieldType the field type
   * @param fieldName the field name
   */
  public Field(String className, String fieldType, String fieldName) {
    this.className = className;
    this.fieldType = fieldType;
    this.fieldName = fieldName;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((className == null) ? 0 : className.hashCode());
    result = prime * result + ((fieldName == null) ? 0 : fieldName.hashCode());
    result = prime * result + ((fieldType == null) ? 0 : fieldType.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Field other = (Field) obj;
    if (className == null) {
      if (other.className != null) {
        return false;
      }
    } else if (!className.equals(other.className)) {
      return false;
    }
    if (fieldName == null) {
      if (other.fieldName != null) {
        return false;
      }
    } else if (!fieldName.equals(other.fieldName)) {
      return false;
    }
    if (fieldType == null) {
      if (other.fieldType != null) {
        return false;
      }
    } else if (!fieldType.equals(other.fieldType)) {
      return false;
    }
    return true;
  }

  /**
   * Gets the class name.
   *
   * @return the class name
   */
  public String getClassName() {
    return className;
  }

  /**
   * Gets the field type.
   *
   * @return the field type
   */
  public String getFieldType() {
    return fieldType;
  }

  /**
   * Gets the field name.
   *
   * @return the field name
   */
  public String getFieldName() {
    return fieldName;
  }

  /**
   * Gets the signature.
   *
   * @return the signature
   */
  public String getSignature() {
    return toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<");
    sb.append(className);
    sb.append(": ");
    sb.append(fieldType);
    sb.append(" ");
    sb.append(fieldName);
    sb.append(">");
    return sb.toString();
  }
}
