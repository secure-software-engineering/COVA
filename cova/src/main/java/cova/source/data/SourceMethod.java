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

package cova.source.data;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class SourceMethod represents a source API that is a method call.
 *
 * @date 05.09.2017
 */
public class SourceMethod extends Source {

  /** The method. */
  private final Method method;
  private final Pattern pattern;
  /**
   * Instantiates a new source method.
   *
   * @param method
   *          the method
   * @param type
   *          the type
   * @param name
   *          the name
   * @param id
   *          the id
   */
  public SourceMethod(Method method, SourceType type, String name, int id) {
    super(type, name, id);
    if(method==null) {
      throw new NullPointerException(
          "The method passing to the constructor of SourceMethod can not be null");
    }
    this.method = method;
    String regex = method.getSignature();
    String[] searchList = new String[] { "(", ")", "$", "[", "]" };
    String[] replaceList = new String[] { "\\(", "\\)", "\\$", "\\[", "\\]" };
    regex = StringUtils.replaceEach(regex, searchList, replaceList);
    pattern = Pattern.compile(regex);
  }

  /**
   * Gets the method.
   *
   * @return the method
   */
  public Method getMethod() {
    return method;
  }

  public Pattern getPattern() {
    return pattern;
  }

  @Override
  public String getSignature() {
    return method.getSignature();
  }
}
