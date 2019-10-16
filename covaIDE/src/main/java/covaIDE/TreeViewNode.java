/*
 * @author Linghui Luo
 */
package covaIDE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Range;

/**
 * The Class TreeViewNode is used to store information which will be display in a tree view. This is
 * created for an extended LSP in VS code. see {@link CovaLanguageClient} and {@link
 * PublishConstraintParams}. See https://code.visualstudio.com/api/extension-guides/tree-view
 *
 * @author Linghui Luo
 */
public class TreeViewNode {

  /** The resource uri. */
  public String resourceUri;

  /** The range. */
  public Range range;

  /** The children. */
  public List<TreeViewNode> children;

  /** The label. */
  public String label;

  /** The collapsible state. */
  public TreeItemCollapsibleState collapsibleState;

  /** The command. */
  public Command command;

  /** The Enum TreeItemCollapsibleState. */
  enum TreeItemCollapsibleState {

    /** The None. */
    None(0),

    /** The Collapsed. */
    Collapsed(1),

    /** The Expanded. */
    Expanded(2);

    /** The num. */
    private int num;

    /**
     * Instantiates a new tree item collapsible state.
     *
     * @param num the num
     */
    TreeItemCollapsibleState(int num) {
      this.num = num;
    }
  }

  /**
   * Instantiates a new tree view node.
   *
   * @param resourceUri the resource uri
   * @param range the range
   * @param children the children
   * @param label the label
   * @param state the state
   */
  public TreeViewNode(
      String resourceUri,
      Range range,
      List<TreeViewNode> children,
      String label,
      TreeItemCollapsibleState state) {
    this.resourceUri = resourceUri;
    this.range = range;
    this.children = children;
    this.label = label;
    this.collapsibleState = state;
    this.command =
        new Command(
            "goto", "covaIDE.goto", Collections.singletonList(new Location(resourceUri, range)));
  }

  /**
   * Instantiates a new tree view node.
   *
   * @param resourceUri the resource uri
   * @param range the range
   * @param label the label
   */
  public TreeViewNode(String resourceUri, Range range, String label) {
    this.resourceUri = resourceUri;
    this.range = range;
    this.children = new ArrayList<TreeViewNode>();
    this.label = label;
    this.collapsibleState = TreeItemCollapsibleState.None;
    this.command =
        new Command(
            "goto", "covaIDE.goto", Collections.singletonList(new Location(resourceUri, range)));
  }
}
