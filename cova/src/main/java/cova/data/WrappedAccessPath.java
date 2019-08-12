/*
 * @version 1.0
 */

package cova.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import soot.Local;
import soot.NullType;
import soot.SootField;
import soot.Type;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JimpleLocal;

import boomerang.jimple.Field;
import boomerang.jimple.StaticFieldVal;
import boomerang.jimple.Val;
import boomerang.util.AccessPath;

/**
 * The Class WrappedAccessPath represents the access path of a taint object. An access path can be a
 * base, a static field or based followed by instance fields.
 *
 */
public class WrappedAccessPath {

  /** The base. */
  private final Local base;

  /** The fields. */
  private final ArrayList<SootField> fields;

  /** The zero access path. */
  private static WrappedAccessPath zeroAccessPath;

  private static Local ret = new JimpleLocal("RET", NullType.v());
  /**
   * Instantiates a new access path with given base and fields. The value of fields can be null. The
   * base of a static field is null.
   *
   * @param base
   *          the base
   * @param fields
   *          the fields
   */
  public WrappedAccessPath(Local base, ArrayList<SootField> fields) {
    this.base = base;
    if (fields != null && fields.isEmpty()) {
      this.fields = null;
    } else {
      this.fields = fields;
    }
  }

  /**
   * Instantiates a new access path with given base and fields.
   * 
   * @param base
   * @param fields
   */
  public WrappedAccessPath(Value base, ArrayList<SootField> fields) {
    this.base = (Local) base;
    if (fields != null && fields.isEmpty()) {
      this.fields = null;
    } else {
      this.fields = fields;
    }
  }

  /**
   * Instantiates a new access path with given value. It supports the value of the type
   * {@link Local}, {@link InstanceFieldRef} and {@link StaticFieldRef}.
   *
   * @param value
   *          the value
   */
  public WrappedAccessPath(Value value) {
    if (value instanceof Local) {
      base = (Local) value;
      fields = null;
    } else if (value instanceof InstanceFieldRef) {
      final InstanceFieldRef instanceField = (InstanceFieldRef) value;
      base = (Local) instanceField.getBase();
      final ArrayList<SootField> ifields = new ArrayList<SootField>();
      ifields.add(instanceField.getField());
      fields = ifields;
    } else if (value instanceof StaticFieldRef) {
      final StaticFieldRef staticField = (StaticFieldRef) value;
      base = null;
      final ArrayList<SootField> sfields = new ArrayList<SootField>();
      sfields.add(staticField.getField());
      fields = sfields;
    } else {
      throw new RuntimeException("Unsupported reference by creating new Acesspath");
    }
  }

  /**
   * Gets the base.
   *
   * @return the base
   */
  public Local getBase() {
    return base;
  }

  /**
   * Gets the fields.
   *
   * @return the fields
   */
  public ArrayList<SootField> getFields() {
    return fields;
  }

  /**
   * Gets the zero access path.
   *
   * @return the zero access path
   */
  public static WrappedAccessPath getZeroAccessPath() {
    if (zeroAccessPath == null) {
      zeroAccessPath = new WrappedAccessPath(new JimpleLocal("ZERO", NullType.v()));
    }
    return zeroAccessPath;
  }

  /**
   * Gets the return access path.
   *
   * @return the return access path
   */
  public static WrappedAccessPath getRetAccessPath(ArrayList<SootField> fields) {
    return new WrappedAccessPath(ret, fields);
  }

  /**
   * Checks if the given value is supported type for creating an instance of access path.
   *
   * @param value
   *          the value
   * @return true, if is supported type
   */
  public static boolean isSupportedType(Value value) {
    return value instanceof Local || value instanceof InstanceFieldRef
        || value instanceof StaticFieldRef;
  }

  /**
   * Checks if this access path represents a static field.
   *
   * @return true, if it is static field.
   */
  public boolean isStaticFieldRef() {
    return base == null && fields != null;
  }

  /**
   * Checks if this access path represents an instance field.
   *
   * @return true, if it is instance field.
   */
  public boolean isInstanceFieldRef() {
    return base != null && fields != null;
  }

  /**
   * Checks if this access path represents a local.
   *
   * @return true, if is local
   */
  public boolean isLocal() {
    return !equals(zeroAccessPath) && base != null && fields == null;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((base == null) ? 0 : base.hashCode());
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
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
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    final WrappedAccessPath other = (WrappedAccessPath) obj;
    if (base == null) {
      if (other.base != null) {
        return false;
      }
    } else if (!base.equals(other.base)) {
      return false;
    }
    if (fields == null) {
      if (other.fields != null) {
        return false;
      }
    } else {
      if (other.fields == null) {
        return false;
      }
      if (fields.size() != other.fields.size()) {
        return false;
      } else {
        for (int i = fields.size() - 1; i >= 0; i--) {
          if (!fields.get(i).equals(other.fields.get(i))) {
            return false;
          }
        }
      }
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    if (base == null) {
      final SootField staticField = fields.get(0);
      sb.append(staticField.getDeclaringClass().getName());
      sb.append(".");
      sb.append(staticField.getName());
      sb.append(": Static");
      if (isPublic()) {
        sb.append("_Public");
      }
      return sb.toString();
    } else {
      sb.append(base.getName());
      if (fields != null) {
        final int length = fields.size();
        for (int i = 0; i < length; i++) {
          sb.append(".");
          sb.append(fields.get(i).getName());
        }
      }
      return sb.toString();
    }
  }

  /**
   * Copy this access path
   *
   * @return the access path
   */
  public WrappedAccessPath copy() {
    return new WrappedAccessPath(base, fields);
  }

  /**
   * Checks if the given access path is a prefix of this access path.
   *
   * @param other
   *          the given access path
   * @return true, if the given access path is a prefix of this access path.
   */
  public boolean hasPrefix(WrappedAccessPath other) {
    if ((base != null && other.base != null) || (base == null && other.base == null)) {
      if (base != null) {
        // compare base
        if (!base.toString().equals(other.base.toString())) {
          return false;
        }
      }
      // compare fields
      if (fields == null) {
        if (other.fields != null) {
          return false;
        } else {
          return true;
        }
      } else if (other.fields == null) {
        return true;
      } else if (fields.size() < other.fields.size()) {
        return false;
      } else {
        final int length = other.fields.size();
        for (int i = 0; i < length; i++) {
          if (!fields.get(i).toString().equals(other.fields.get(i).toString())) {
            return false;
          }
        }
        return true;
      }
    } else {
      return false;
    }
  }

  /**
   * Gets the base type.
   *
   * @return the base type
   */
  public Type getBaseType() {
    return base.getType();
  }

  /**
   * Gets the last field type.
   *
   * @return the last field type
   */
  public Type getLastFieldType() {
    if (fields != null && fields.size() != 0) {
      return fields.get(fields.size() - 1).getType();
    } else {
      throw new RuntimeException("there is no field in this access path");
    }
  }

  public boolean isPublic() {
    if (fields != null) {
      return fields.get(fields.size() - 1).isPublic();
    } else {
      throw new NullPointerException();
    }
  }

  public boolean isPrivate() {
    if (fields != null) {
      return fields.get(fields.size() - 1).isPrivate();
    } else {
      throw new NullPointerException();
    }
  }

  public boolean isProtected() {
    if (fields != null) {
      return fields.get(fields.size() - 1).isProtected();
    } else {
      throw new NullPointerException();
    }
  }

  /**
   * Gets the data type of this access path.
   *
   * @return the type
   */
  public Type getType() {
    if (fields == null || fields.size() == 0) {
      return getBaseType();
    } else {
      return getLastFieldType();
    }
  }

  /**
   * This method is used to map the taints as arguments at caller site taints to taints as parameter
   * at callee site. eg. the access path at the caller is a.b.c.field, the parameter is d. If d and
   * b have the same data type, then the access path at the callee is d.c.field.
   *
   * @param param
   *          parameter of callee method, it can only be local.
   * @return a new access path at the callee
   * @see rules.GeneralPropagationRule
   */
  public WrappedAccessPath copyFields(Local param) {
    if (param.getType().equals(getBaseType())) {
      return new WrappedAccessPath(param, fields);
    } else {
      final ArrayList<SootField> newFields = new ArrayList<SootField>();
      if (fields != null) {
        Iterator<SootField> it = fields.iterator();
        SootField field = null;
        while (it.hasNext()) {
          field = it.next();
          if (field.getType().equals(param.getType())) {
            break;
          }
        }
        while (it.hasNext()) {
          field = it.next();
          newFields.add(field);
        }
      }
      return new WrappedAccessPath(param, newFields);
    }
  }

  /**
   * This method converts the access path of type {@link boomerang.util.AccessPath} to
   * {@link WrappedAccessPath}.
   * 
   * @param accessPath
   *          the access path of type {@link boomerang.util.AccessPath}
   * @return the access path of type {@link WrappedAccessPath}
   */
  public static WrappedAccessPath convert(AccessPath accessPath) {
    final Val val = accessPath.getBase();
    if (val.isStatic()) {
      // convert static field
      final StaticFieldVal staticField = (StaticFieldVal) val;
      final SootField field = staticField.field();
      final ArrayList<SootField> newFields = new ArrayList<SootField>();
      newFields.add(field);
      return new WrappedAccessPath(null, newFields);
    } else {
      // convert local or instance field
      final Local base = (Local) val.value();
      final Collection<Field> fields = accessPath.getFields();
      if (!fields.isEmpty()) {
        final ArrayList<SootField> newFields = new ArrayList<SootField>();
        for (final Field field : fields) {
          newFields.add(field.getSootField());
        }
        return new WrappedAccessPath(base, newFields);
      } else {
        return new WrappedAccessPath(base);
      }
    }
  }

  /**
   * The method extends the given access path with given field by appending it at the tail of the
   * access path.
   * 
   * @param accessPath
   *          the access path of type {@link boomerang.util.AccessPath}
   * @param f
   *          the given field
   * @return the extended access path
   */
  public static WrappedAccessPath deriveExtendedAccessPath(AccessPath accessPath, SootField f) {
    final Val val = accessPath.getBase();
    if (val.isStatic()) {
      // handle static field
      final StaticFieldVal staticField = (StaticFieldVal) val;
      final SootField field = staticField.field();
      final ArrayList<SootField> newFields = new ArrayList<SootField>();
      newFields.add(field);
      newFields.add(f);
      return new WrappedAccessPath(null, newFields);
    } else {
      // handle instance field
      final Local base = (Local) accessPath.getBase().value();
      final Collection<Field> fields = accessPath.getFields();
      final ArrayList<SootField> newFields = new ArrayList<SootField>();
      for (final Field field : fields) {
        newFields.add(field.getSootField());
      }
      newFields.add(f);
      return new WrappedAccessPath(base, newFields);
    }
  }

  /**
   * This method replace the prefix of this access path with given access path e.g. Assume this
   * access path is a.b.c, prefixLength is 2, given access path is d.e, then the return access path
   * is d.e.c.
   * 
   * @param accessPath
   *          the acessPath which replaces the prefix
   * @param prefixLength
   *          the length of the prefix to be replaced
   * @return access path whose prefix with given length is replaced by the given value.
   */
  public WrappedAccessPath replacePrefix(WrappedAccessPath accessPath, int prefixLength) {
    WrappedAccessPath ret = null;
    if (prefixLength > getDepth()) {
      throw new RuntimeException("PrefixLength must be smaller than the depth of the access path");
    } else {
      switch (prefixLength) {
      case 0:
        ret = this;
        break;
      case 1:
        if (!isStaticFieldRef()) {
          if (accessPath.isLocal() && isLocal()) {
            ret = accessPath;
          } else {
            final ArrayList<SootField> newFields = new ArrayList<SootField>();
            if (accessPath.fields != null) {
              newFields.addAll(accessPath.fields);
            }
            if (fields != null) {
              newFields.addAll(fields);
            }
            ret = new WrappedAccessPath(accessPath.base, newFields);
          }
        } else {
          final ArrayList<SootField> newFields = new ArrayList<SootField>();
          if (accessPath.fields != null) {
            newFields.addAll(accessPath.fields);
          }
          final int length = fields.size();
          for (int i = 1; i < length; i++) {
            newFields.add(fields.get(i));
          }
          ret = new WrappedAccessPath(accessPath.base, newFields);
        }
        break;
      default:
        if (prefixLength == getDepth()) {
          ret = accessPath;
        } else {
          int startIndex = 0;
          if (!isStaticFieldRef()) {
            startIndex = prefixLength - 1;
          } else {
            startIndex = prefixLength;
          }
          final ArrayList<SootField> newFields = new ArrayList<SootField>();
          if (accessPath.fields != null) {
            newFields.addAll(accessPath.fields);
          }
          final int length = fields.size();
          for (int i = startIndex; i < length; i++) {
            newFields.add(fields.get(i));
          }
          ret = new WrappedAccessPath(accessPath.base, newFields);
        }
        break;
      }
    }
    return ret;
  }

  /**
   * Get the depth of the access path.
   * 
   * @return depth of the access path.
   */
  public int getDepth() {
    int depth = 0;
    if (base != null) {
      depth++;
    }
    if (fields != null) {
      depth += fields.size();
    }
    return depth;
  }

  /**
   * Checks if the given access paths have same suffix of length k.
   *
   * @param a1
   *          the first access path
   * @param a2
   *          the second access path
   * @return true, if they have same suffix
   */
  public static boolean hasSameSuffix(WrappedAccessPath a1, WrappedAccessPath a2, int k) {
    if (a1.getDepth() >= k && a2.getDepth() >= k) {
      if (a1.equals(a2)) {
        return true;
      } else {
        if (a1.isStaticFieldRef()) {
          if (!a2.isStaticFieldRef()) {
            return false;
          } else {
            ArrayList<SootField> fields1 = a1.getFields();
            ArrayList<SootField> fields2 = a2.getFields();
            int i = fields1.size() - 1;
            int j = fields2.size() - 1;
            int count = 0;
            while (i >= 0) {
              SootField field1 = fields1.get(i);
              SootField field2 = fields2.get(j);
              if (field1.equals(field2)) {
                count++;
                i--;
                j--;
              } else {
                return false;
              }
              if (k == count) {
                return true;
              }
            }
          }
        } else if (a1.isInstanceFieldRef()) {
          if (!a2.isInstanceFieldRef()) {
            return false;
          } else {
            ArrayList<SootField> fields1 = a1.getFields();
            ArrayList<SootField> fields2 = a2.getFields();
            int i = fields1.size() - 1;
            int j = fields2.size() - 1;
            int count = 0;
            while (i >= 0) {
              SootField field1 = fields1.get(i);
              SootField field2 = fields2.get(j);
              if (field1.equals(field2)) {
                count++;
                i--;
                j--;
              } else {
                return false;
              }
              if (k == count) {
                return true;
              }
            }
          }
        }
      }
    }
    return false;
  }

  public boolean isReturnAccessPath() {
    if (base != null) {
      return base.equals(ret);
    } else {
      return false;
    }
  }
}
