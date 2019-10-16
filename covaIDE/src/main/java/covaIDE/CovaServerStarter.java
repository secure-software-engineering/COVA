/*
 * @author Linghui Luo
 */

package covaIDE;

import magpiebridge.core.IProjectService;
import magpiebridge.core.ServerConfiguration;
import magpiebridge.projectservice.java.AndroidProjectService;
import magpiebridge.projectservice.java.JavaProjectService;

public class CovaServerStarter {

  public static void main(String... args) {
    String configDir = null;
    ServerConfiguration config = new ServerConfiguration();
    CovaServer server = new CovaServer(config);
    config.doAnalysisByOpen();
    String language = "java";
    if (args.length == 2) {
      String androidJar = args[0];
      configDir = args[1];
      IProjectService androidProjectService = new AndroidProjectService();
      server.addProjectService(language, androidProjectService);
      server.addAnalysis(language, new CovaServerAnalysis(androidJar, configDir));
    } else if (args.length == 1) {
      configDir = args[0];
      IProjectService javaProjectService = new JavaProjectService();
      server.addProjectService(language, javaProjectService);
      server.addAnalysis(language, new CovaServerAnalysis(configDir));
    }
    if (configDir != null) {
      // server.launchOnStdio();
      // use server port when debugging with vs code.
      server.launchOnSocketPort(5007);
    }
  }
}
