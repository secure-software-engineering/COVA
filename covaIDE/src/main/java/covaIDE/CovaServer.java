/*
 * @author Linghui Luo
 */
package covaIDE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.MagpieWorkspaceService;
import magpiebridge.core.ServerConfiguration;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.CodeActionKind;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.MessageParams;
import org.eclipse.lsp4j.MessageType;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.eclipse.lsp4j.jsonrpc.Launcher;
import org.eclipse.lsp4j.launch.LSPLauncher.Builder;
import org.eclipse.lsp4j.services.LanguageClient;

public class CovaServer extends MagpieServer {

  private CovaLanguageClient covaClient;

  private HashMap<String, PublishConstraintParams> constraintParams;
  private HashMap<String, PublishDiagnosticsParams> diagParams;

  public CovaServer(ServerConfiguration config) {
    super(config);
    MagpieWorkspaceService service = (MagpieWorkspaceService) this.workspaceService;
    service.addCommand(new ShowConstraintCommand());
    this.constraintParams = new HashMap<>();
    this.diagParams = new HashMap<>();
  }

  /**
   * This method generates the "show constraint" code action.
   *
   * @param serverUri
   * @param lines
   * @param rerun
   */
  public void consume(String serverUri, Set<Integer> lines, boolean rerun) {
    for (Integer line : lines) {
      try {
        String clientUri = getClientUri(serverUri);
        Range range = new Range(new Position(line - 1, 0), new Position(line, 0));
        String title = "show constraint";
        CodeAction codeAction = new CodeAction(title);
        codeAction.setKind(CodeActionKind.Source);
        List<Object> args = new ArrayList<>();
        args.add(clientUri);
        args.add(range);
        codeAction.setCommand(new Command(title, "showConstraint", args));
        this.addCodeAction(new URL(URLDecoder.decode(clientUri, "UTF-8")), range, codeAction);
      } catch (MalformedURLException e) {
        e.printStackTrace();
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }
    }
    if (rerun)
      this.covaClient.showMessage(
          new MessageParams(MessageType.Info, "COVA finished analyzing the code."));
  }

  public PublishConstraintParams getConstraintParams(String clientUri, String lineNo) {
    return this.constraintParams.get(clientUri + lineNo);
  }

  public PublishDiagnosticsParams getDiagnosticsParams(String clientUri, String lineNo) {
    return this.diagParams.get(clientUri + lineNo);
  }

  public void addDiagnosticsParams(String clientUri, int lineNo, PublishDiagnosticsParams params) {
    String key = clientUri + lineNo;
    this.diagParams.put(key, params);
  }

  public void addConstraintParams(String clientUri, int lineNo, PublishConstraintParams params) {
    String key = clientUri + lineNo;
    this.constraintParams.put(key, params);
  }

  @Override
  public void connect(LanguageClient client) {
    this.client = client;
    this.covaClient = (CovaLanguageClient) this.client;
  }

  @Override
  public void launchOnStream(InputStream in, OutputStream out) {
    Launcher<CovaLanguageClient> launcher =
        new Builder<CovaLanguageClient>()
            .setLocalService(this)
            .setRemoteInterface(CovaLanguageClient.class)
            .setInput(in)
            .setOutput(out)
            .setExecutorService(Executors.newCachedThreadPool())
            .wrapMessages(this.logger.getWrapper())
            .create();
    connect(launcher.getRemoteProxy());
    launcher.startListening();
  }

  @Override
  public void launchOnSocketPort(int port) {
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      connectionSocket = serverSocket.accept();
      Launcher<CovaLanguageClient> launcher =
          new Builder<CovaLanguageClient>()
              .setLocalService(this)
              .setRemoteInterface(CovaLanguageClient.class)
              .setInput(connectionSocket.getInputStream())
              .setOutput(connectionSocket.getOutputStream())
              .setExecutorService(Executors.newCachedThreadPool())
              .wrapMessages(this.logger.getWrapper())
              .create();
      connect(launcher.getRemoteProxy());
      launcher.startListening();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
