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
    String[] searchList = new String[] { "(", ")", "$", "[", "]" };
    String[] replaceList = new String[] { "\\(", "\\)", "\\$", "\\[", "\\]" };
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
