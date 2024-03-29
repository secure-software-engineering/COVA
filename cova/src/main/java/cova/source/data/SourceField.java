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

import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

/**
 * The Class SourceField represents a source API that is a field.
 *
 * @date 05.09.2017
 */
public class SourceField extends Source {

  /** The field. */
  private Field field;

  private final Pattern pattern;

  /**
   * Instantiates a new source field.
   *
   * @param field the field
   * @param type the type
   * @param name the name
   * @param id the id
   */
  public SourceField(Field field, SourceType type, String name, int id) {
    super(type, name, id);
    if (field == null) {
      throw new NullPointerException(
          "The field passing to the constructor of SourceField can not be null");
    }
    this.field = field;
    String regex = ".*" + field.getSignature();
    String[] searchList = new String[] {"(", ")", "$", "[", "]"};
    String[] replaceList = new String[] {"\\(", "\\)", "\\$", "\\[", "\\]"};
    regex = StringUtils.replaceEach(regex, searchList, replaceList);
    pattern = Pattern.compile(regex);
  }

  /**
   * Gets the field.
   *
   * @return the field
   */
  public Field getField() {
    return field;
  }

  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public String getSignature() {
    return field.getSignature();
  }
}
