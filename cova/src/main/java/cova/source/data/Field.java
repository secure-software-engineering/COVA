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
   * @param className
   *          the class name
   * @param fieldType
   *          the field type
   * @param fieldName
   *          the field name
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
