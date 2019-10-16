/*
 * @author Linghui Luo
 */
package covaIDE;

import java.util.List;
import org.eclipse.lsp4j.Command;

/** The Class PublishConstraintParams used for extended LSP. See {@link CovaLanguageClient}. */
public class PublishConstraintParams {

  /** The id. */
  private static int id = 0;

  /** The view id. */
  private final String viewId;

  /** The constraint. */
  protected String constraint;

  /** The items in a tree. */
  protected List<TreeViewNode> items;

  /** The command. */
  protected Command command;

  /** Instantiates a new publish constraint params. */
  public PublishConstraintParams() {
    id++;
    this.viewId = "ID" + id;
  }
}
