/*
 * @author Linghui Luo
 */
package covaIDE;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.util.List;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.WorkspaceCommand;
import org.eclipse.lsp4j.ExecuteCommandParams;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.services.LanguageClient;

/**
 * The implementation of ShowConstraintCommand for "show constraint" code action.
 *
 * @author Linghui Luo
 */
public class ShowConstraintCommand implements WorkspaceCommand {

  @Override
  public String getName() {
    return "showConstraint";
  }

  @Override
  public void execute(ExecuteCommandParams params, MagpieServer server, LanguageClient client) {
    List<Object> args = params.getArguments();
    JsonPrimitive juri = (JsonPrimitive) args.get(0);
    String uri = juri.getAsString();
    JsonObject jrange = (JsonObject) args.get(1);
    JsonObject jstart = jrange.get("start").getAsJsonObject();
    String lineNo = jstart.get("line").getAsString();
    CovaLanguageClient covaClient = (CovaLanguageClient) client;
    CovaServer covaServer = (CovaServer) server;
    PublishConstraintParams p = covaServer.getConstraintParams(uri, lineNo);
    covaClient.publishConstraintParams(p);
    PublishDiagnosticsParams d = covaServer.getDiagnosticsParams(uri, lineNo);
    covaClient.publishDiagnostics(d);
  }
}
