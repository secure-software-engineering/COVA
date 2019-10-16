/*
 * @author Linghui Luo
 */
package covaIDE;

import org.eclipse.lsp4j.jsonrpc.services.JsonNotification;
import org.eclipse.lsp4j.services.LanguageClient;

/** The extended Cova LanguageClient. */
public interface CovaLanguageClient extends LanguageClient {

  /**
   * Publish constraint params. This is an extension for the LSP protocol. Information contained in
   * params will be displayed in a tree view at the left side of VS code. See more about
   * https://code.visualstudio.com/api/extension-guides/tree-view
   *
   * @param params the PublishConstraintParams
   */
  @JsonNotification("covaIDE/constraintInfo")
  void publishConstraintParams(PublishConstraintParams params);
}
