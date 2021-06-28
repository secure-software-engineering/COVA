/*
 * @author Linghui Luo
 */
package covaIDE;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.ibm.wala.classLoader.Module;
import com.ibm.wala.classLoader.SourceFileModule;
import cova.data.ConstraintZ3;
import cova.data.IConstraint;
import cova.reporter.ConstraintReporter;
import cova.setup.CovaSetupForAndroid;
import cova.setup.CovaSetupForJava;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForAndroid;
import cova.setup.config.DefaultConfigForTestCase;
import cova.source.symbolic.SymbolicNameManager;
import covaIDE.TreeViewNode.TreeItemCollapsibleState;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import magpiebridge.core.IProjectService;
import magpiebridge.core.MagpieServer;
import magpiebridge.core.ServerAnalysis;
import magpiebridge.projectservice.java.AndroidProjectService;
import magpiebridge.projectservice.java.JavaProjectService;
import magpiebridge.util.SourceCodeInfo;
import magpiebridge.util.SourceCodePositionFinder;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4j.DiagnosticRelatedInformation;
import org.eclipse.lsp4j.DiagnosticSeverity;
import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.PublishDiagnosticsParams;
import org.eclipse.lsp4j.Range;
import org.xmlpull.v1.XmlPullParserException;
import soot.Scene;
import soot.SootClass;

/**
 * This class runs COVA.
 *
 * @author Linghui Luo
 */
public class CovaServerAnalysis implements ServerAnalysis {

  private ConstraintReporter reporter;
  private CovaServer server;
  private String androidJar;
  private String configDir;
  private boolean isAndroid;
  private File rootDir;

  public CovaServerAnalysis(String androidJar, String configDir) {
    this.androidJar = androidJar;
    this.configDir = configDir;
    this.isAndroid = true;
  }

  public CovaServerAnalysis(String configDir) {
    this.configDir = configDir;
    this.isAndroid = false;
  }

  @Override
  public String source() {
    return "COVA";
  }

  public void runCova(AndroidProjectService aps) {
    String apkFilePath = aps.getApkPath().get().toString();
    Config config = new DefaultConfigForAndroid();
    config.setConfigDir(configDir);
    config.setComputeConstraintMap(true);
    config.setRecordPath(true);
    CovaSetupForAndroid cova = new CovaSetupForAndroid(androidJar, apkFilePath, null, config);
    try {
      cova.run();
    } catch (IOException | XmlPullParserException e) {
      throw new RuntimeException(e);
    }
    reporter = cova.getReporter();
  }

  public void runCova(JavaProjectService ps) {
    Set<Path> classPath = ps.getClassPath();
    if (!classPath.isEmpty()) {
      String appClassPath = classPath.iterator().next().toString();
      ps.getLibraryPath();
      String libPath =
          System.getProperty("java.home") + File.separator + "lib" + File.separator + "rt.jar";
      for (Path lib : ps.getLibraryPath()) {
        libPath = File.separator + lib.toString();
      }
      Config config = new DefaultConfigForTestCase();
      config.setConfigDir(configDir);
      config.setComputeConstraintMap(true);
      config.setRecordPath(true);
      CovaSetupForJava cova =
          new CovaSetupForJava("dummy", appClassPath, null, libPath, configDir, config, "");
      cova.run();
      reporter = cova.getReporter();
    }
  }

  @Override
  public void analyze(Collection<Module> files, MagpieServer server, boolean rerun) {
    if (this.server == null) this.server = (CovaServer) server;
    Optional<IProjectService> ps = this.server.getProjectService("java");
    if (ps.isPresent()) {
      if (rerun) {
        if (isAndroid) {
          AndroidProjectService aps = (AndroidProjectService) ps.get();
          if (rootDir == null) rootDir = aps.getRootPath().get().toFile();
          runCova(aps);
        } else {
          JavaProjectService p = (JavaProjectService) ps.get();
          if (rootDir == null) rootDir = p.getRootPath().get().toFile();
          runCova(p);
        }
      }
      for (Module file : files) {
        SourceFileModule source = (SourceFileModule) file;
        Optional<PackageDeclaration> pkg = getPackageName(new File(source.getAbsolutePath()));
        String className = source.getClassName();
        String klassSignature = "";
        if (pkg.isPresent()) klassSignature = pkg.get().getName().toString() + "." + className;
        else klassSignature = className;
        SootClass klass = Scene.v().forceResolve(klassSignature, SootClass.BODIES);
        TreeMap<Integer, IConstraint> results = reporter.getResultOfLines(klass, true);
        String serverUri = source.getURL().toString();

        for (Integer line : results.keySet()) {
          if (line > 0) {
            PublishConstraintParams params = new PublishConstraintParams();
            String uri = this.server.getClientUri(serverUri);
            File currentFile = SourceCodePositionFinder.find(rootDir, className);
            SourceCodeInfo info = SourceCodePositionFinder.findCode(currentFile, line);
            Range range = info.range;
            // Construct constraint information to publish to client
            Diagnostic d = new Diagnostic();
            d.setSeverity(DiagnosticSeverity.Information);
            d.setSource(source());
            List<DiagnosticRelatedInformation> relatedInfo = new ArrayList<>();
            ConstraintZ3 constraint = (ConstraintZ3) results.get(line);
            List<String> sNames = constraint.getSymbolicNames();
            List<String> positions = constraint.getPath().getPositions();

            List<TreeViewNode> apis = new ArrayList<>();
            for (String symbolic : sNames) {
              String sourceName = SymbolicNameManager.getInstance().getSourceName(symbolic);
              String signature = reporter.getSourceSignature(symbolic);
              apis.add(
                  new TreeViewNode(
                      uri,
                      range,
                      new ArrayList<>(),
                      sourceName + ": " + signature,
                      TreeItemCollapsibleState.None));
            }
            List<TreeViewNode> path = new ArrayList<>();
            for (String position : positions) {
              String[] splits = position.split("@");
              String type = splits[0];
              String classSignature = splits[1];
              int lineNo = Integer.parseInt(splits[2]);
              String[] strs = classSignature.split("\\.");
              String classFileName = strs[strs.length - 1];
              if (classFileName.contains("$")) classFileName = classFileName.split("\\$")[0];

              File javaFile = SourceCodePositionFinder.find(rootDir, classFileName);
              if (javaFile != null && lineNo >= 0) {
                String javaUri = javaFile.toURI().toString();
                SourceCodeInfo codeInfo = SourceCodePositionFinder.findCode(javaFile, lineNo);
                if (codeInfo.code.length() > 1) {
                  path.add(
                      new TreeViewNode(
                          javaUri,
                          codeInfo.range,
                          new ArrayList<>(),
                          codeInfo.code,
                          TreeItemCollapsibleState.None));
                  DiagnosticRelatedInformation related =
                      new DiagnosticRelatedInformation(
                          new Location(javaUri, codeInfo.range), codeInfo.code);
                  if (!relatedInfo.contains(related)) relatedInfo.add(related);
                }
              }
            }

            TreeViewNode apiNode =
                new TreeViewNode(
                    uri, range, apis, "Constraint APIs", TreeItemCollapsibleState.Expanded);
            TreeViewNode pathNode =
                new TreeViewNode(
                    uri, range, path, "Witness Path", TreeItemCollapsibleState.Expanded);

            List<TreeViewNode> items = new ArrayList<>();

            items.add(apiNode);
            items.add(pathNode);

            params.constraint = constraint.toReadableString();
            params.items = items;
            params.command =
                new Command(
                    "goto", "covaIDE.goto", Collections.singletonList(new Location(uri, range)));
            this.server.addConstraintParams(uri, range.getStart().getLine(), params);

            // set up PublishDiagnosticsParams
            PublishDiagnosticsParams diag = new PublishDiagnosticsParams();
            diag.setUri(uri);

            d.setMessage(constraint.toReadableString());
            d.setRange(range);
            d.setRelatedInformation(relatedInfo);

            diag.setDiagnostics(Collections.singletonList(d));
            this.server.addDiagnosticsParams(uri, range.getStart().getLine(), diag);
          }
        }
        this.server.consume(serverUri, results.keySet(), rerun);
      }
    }
  }

  protected Optional<PackageDeclaration> getPackageName(File javaFile) {
    JavaParser parser = new JavaParser();
    Optional<CompilationUnit> result;
    try {
      result = parser.parse(javaFile).getResult();
      if (result.isPresent()) {
        CompilationUnit cu = result.get();
        return cu.getPackageDeclaration();
      }
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
    return Optional.empty();
  }
}
