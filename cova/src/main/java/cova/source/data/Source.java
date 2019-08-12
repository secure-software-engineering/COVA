package cova.source.data;

/**
 * The abstract class Source.
 * 
 * <p>
 * A source is an API which can produce some constraint. There are three kinds of sources: A field
 * source {@link SourceField}, A method source {@link SourceMethod} and an UI callback source
 * {@link SourceUICallback}.
 * </p>
 * 
 * @date 05.09.2017
 */
public abstract class Source {

  /** The type of source. */
  private final SourceType type;

  /** The user-defined id reads from .txt files. It should be unique in each .txt file. */
  private final int id;

  /** The user-defined name, not necessary to be unique. */
  private final String name;

  /**
   * The unique name of the source, it is a concatenation of type and id. e.g. C100 stands for
   * configuration-related source with id 100.
   */
  private final String uniqueName;

  /**
   * Instantiates a new source.
   *
   * @param type
   *          the type
   * @param name
   *          the name
   * @param id
   *          the id
   */
  public Source(SourceType type, String name, int id) {
    this.type = type;
    this.name = name;
    this.id = id;
    StringBuilder sb = new StringBuilder(type.name());
    sb.append(id);
    uniqueName = sb.toString();
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  public SourceType getType() {
    return type;
  }

  /**
   * Gets the name.
   *
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the id.
   *
   * @return the id
   */
  public int getId() {
    return id;
  }

  /**
   * Gets the unique name.
   *
   * @return the unique name
   */
  public String getUniqueName() {
    return uniqueName;
  }

  public abstract String getSignature();
}
