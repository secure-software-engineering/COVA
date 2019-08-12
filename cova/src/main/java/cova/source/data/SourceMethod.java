
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
