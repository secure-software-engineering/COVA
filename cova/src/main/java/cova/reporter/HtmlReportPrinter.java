
package cova.reporter;

import static j2html.TagCreator.a;
import static j2html.TagCreator.attrs;
import static j2html.TagCreator.body;
import static j2html.TagCreator.div;
import static j2html.TagCreator.each;
import static j2html.TagCreator.h2;
import static j2html.TagCreator.head;
import static j2html.TagCreator.html;
import static j2html.TagCreator.iff;
import static j2html.TagCreator.li;
import static j2html.TagCreator.link;
import static j2html.TagCreator.main;
import static j2html.TagCreator.pre;
import static j2html.TagCreator.script;
import static j2html.TagCreator.span;
import static j2html.TagCreator.table;
import static j2html.TagCreator.td;
import static j2html.TagCreator.title;
import static j2html.TagCreator.tr;
import static j2html.TagCreator.ul;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.io.Files;

import j2html.Config;
import j2html.tags.ContainerTag;
import j2html.tags.DomContent;
import j2html.tags.MyLi;

import soot.SootClass;

import cova.data.IConstraint;

/**
 * The Class HtmlReportPrinter is used to print the constraints in source code as HTML files.
 * 
 */
public class HtmlReportPrinter {

  static {
    /** don't escape text (escaping breaks java source indentation). */
    Config.textEscaper = text -> text;
  }

  /** The source code path. */
  private String sourcePath = ".";

  /** The HTML output path. */
  private String outputPath;

  /** The report. */
  private Map<SootClass, LineConstraints> report;

  /**
   * Instantiates a new html report printer.
   *
   * @param outputPath
   *          the output path
   */
  public HtmlReportPrinter(String outputPath) {
    this.outputPath = outputPath;
  }

  /**
   * Instantiates a new HTML report printer.
   *
   * @param sourcePath
   *          the source path
   * @param outputPath
   *          the output path
   */
  public HtmlReportPrinter(String sourcePath, String outputPath) {
    this(outputPath);
    this.sourcePath = sourcePath;
  }

  /**
   * Merge.
   *
   * @param lineResults1
   *          the line results 1
   * @param lineResults2
   *          the line results 2
   * @return the line results
   */
  private LineConstraints merge(LineConstraints lineResults1, LineConstraints lineResults2) {
    lineResults1.getLineNumberConstraintMap().putAll(lineResults2.getLineNumberConstraintMap());
    return lineResults1;
  }

  /**
   * Merge inner classes into one HMTL file.
   *
   * @param report
   *          the report
   */
  private void mergeInnerClasses() {
    // create list of inner classes only
    List<SootClass> innerClasses = report.keySet().stream().filter(sc -> sc.getName().contains("$"))
        .collect(Collectors.toList());
    // merge inner class LineResultss into outer classes
    innerClasses.forEach(inner -> {
      String innerName = inner.getName();
      String outerName = innerName.substring(0, innerName.indexOf("$"));
      Optional<Entry<SootClass, LineConstraints>> outerEntry = report.entrySet().stream()
          .filter(outer -> outer.getKey().getName().equals(outerName)).findFirst();
      if (outerEntry.isPresent()) {
        // merge into LineResultss for outer class and remove inner class from LineResults
        merge(outerEntry.get().getValue(), report.remove(inner));
      } else {
        // outer class not in LineResultsSet: use inner class as outer class
        inner.setName(outerName);
      }
    });
  }

  /**
   * The Class Package represents a pacakge of the source code.
   */
  private class Package {

    /**
     * Instantiates a new package.
     *
     * @param parent
     *          the parent package
     * @param name
     *          the name
     */
    public Package(Package parent, String name) {
      this.parent = parent;
      this.name = name;
    }

    /** The parent. */
    Package parent = null;

    /** The name. */
    String name = "";

    /** The no of constraints. */
    int noOfConstraints = 0;

    /** The sub packages. */
    List<Package> subpackages = new ArrayList<>();

    /** The classes. */
    List<SootClass> classes = new ArrayList<>();

    /**
     * Gets the full package name.
     *
     * @return the full package name
     */
    String getFullName() {
      if (parent != null && !parent.name.isEmpty()) {
        return parent.getFullName() + "." + name;
      }
      return name;
    }
  }

  /**
   * Builds the navigation tree.
   *
   * @param parent
   *          the parent
   * @param classes
   *          the classes
   */
  private void buildNavigationTree(Package parent, Collection<SootClass> classes) {
    Map<String, List<SootClass>> subPackages = new HashMap<>();
    for (SootClass clazz : classes) {
      String item = clazz.getName();
      if (!parent.name.isEmpty()) {
        item = item.replace(parent.getFullName() + ".", "");
      }

      if (item.contains(".")) {
        // item is package
        item = item.substring(0, item.indexOf("."));
        if (!subPackages.containsKey(item)) {
          subPackages.put(item, new ArrayList<>());
        }
        subPackages.get(item).add(clazz);
      } else {
        parent.classes.add(clazz);
      }
    }
    subPackages.forEach((packName, subClasses) -> {
      Package pack = new Package(parent, packName);
      for (SootClass subClass : subClasses) {
        pack.noOfConstraints += report.get(subClass).getLineNumberConstraintMap().size();
      }
      parent.subpackages.add(pack);
      buildNavigationTree(pack, subClasses);
    });
  }

  /**
   * Prints the report.
   *
   * @param report
   *          the report
   */
  public void printReport(Map<SootClass, LineConstraints> report) {
    this.report = report;
    Map<SootClass, String> outputPaths = new HashMap<>();
    Map<SootClass, File> sourceFiles = new HashMap<>();
    mergeInnerClasses();
    // create output files for each class
    report.keySet().forEach(sootClass -> {
      String classname = sootClass.getName();
      outputPaths.put(sootClass, "classes/" + classname + ".html");
      try {
		sourceFiles.put(sootClass, findSourceFile(sootClass.getName()));
	} catch (FileNotFoundException e) {
		//ignore
	}
    });

    Package root = new Package(null, "");
    buildNavigationTree(root, report.keySet());

    // print index page
    ContainerTag document = html(
        head(getHeader("COVA Report Index", true)),
        body(main(div(attrs(".navigation"), h2("Analyzed Classes:"),
            createNavigation(root, outputPaths,sourceFiles, true, null)))));

    File outputFile = new File(outputPath + "/index.html");
    try {
      File css = new File(outputPath + "/css/report.css");
      File js = new File(outputPath + "/js/report.js");

      Files.createParentDirs(css);
      Files.createParentDirs(js);

      File cssResource = new File("." + File.separator + "config" + File.separator + "report.css");
      File jsResource = new File("." + File.separator + "config" + File.separator + "report.js");

      Files.copy(Paths.get(cssResource.toURI()).toFile(), css);
      Files.copy(Paths.get(jsResource.toURI()).toFile(), js);
      Files.createParentDirs(outputFile);
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    try (BufferedWriter writer = Files.newWriter(outputFile, StandardCharsets.UTF_8)) {
      writer.write(document.renderFormatted());
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // create output files
    report.forEach((sootClass, LineResults) -> {
      String content;
      try {
        content = html(
            head(getHeader(sootClass.getName(), false)),
            body(main(
                div(attrs(".split.split-horizontal.navigation"),
                	a("Back to Index").withHref("../index.html"),
                    createNavigation(root, outputPaths,sourceFiles, false, sootClass)),
                div(attrs("#code.split.split-horizontal"),
                iff(!report.get(sootClass).getLineNumberConstraintMap().isEmpty(),
                    a("Go to first").withHref("#constr0")),
                createOutputContent(LineResults))))).renderFormatted();

        File classOutputFile = new File(outputPath + "/" + outputPaths.get(sootClass));

        Files.createParentDirs(classOutputFile);

        try (BufferedWriter writer = Files.newWriter(classOutputFile, StandardCharsets.UTF_8);) {
          writer.write(content);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

    });

  }
  
  private DomContent[] getHeader(String title, boolean isIndex) {
	  return new DomContent[]{
			  title(title),
          	link().withRel("stylesheet").withHref("https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/themes/default/style.min.css"),
          	link().withRel("stylesheet").withHref("https://use.fontawesome.com/releases/v5.0.13/css/all.css"),
          	link().withRel("stylesheet").withHref(isIndex?"css/report.css":"../css/report.css"),
          	script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/jquery/1.12.1/jquery.min.js"),
          	script().withSrc("https://cdnjs.cloudflare.com/ajax/libs/jstree/3.2.1/jstree.min.js"),
          	script().withSrc("https://unpkg.com/split.js/split.min.js"),
          	script().withSrc(isIndex?"js/report.js":"../js/report.js")
	  };
  }

  /**
   * Creates the navigation.
   *
   * @param root
   *          the root
   * @param outputPaths
   *          the output paths
 * @param sourceFiles 
   * @param isIndex
   *          the is index
   * @return the container tag
   */
  private ContainerTag createNavigation(Package root, Map<SootClass, String> outputPaths,
      Map<SootClass, File> sourceFiles, boolean isIndex, SootClass current) {
    return div(attrs("#tree"),
    	each(root.subpackages, sub -> createPackageSubtree(current, sub, outputPaths,sourceFiles, isIndex)),
        each(root.classes, clazz -> createClassLink(clazz == current, clazz, outputPaths, isIndex, sourceFiles.containsKey(clazz))));
  }

  /**
   * Creates the package subtree.
   *
   * @param indentation
   *          the indentation
   * @param pack
   *          the pack
   * @param outputPaths
   *          the output paths
 * @param sourceFiles 
   * @param isIndex
   *          the is index
   * @return the dom content
   */
  private DomContent createPackageSubtree(SootClass current, Package pack,
      Map<SootClass, String> outputPaths, Map<SootClass, File> sourceFiles, boolean isIndex) {
	  boolean isonpath = false;
	  if (current != null) {
		  isonpath = current.getPackageName().startsWith(pack.getFullName());
	  }
    return ul(
        li(	a(pack.name /*+ " (" + pack.noOfConstraints + ")"*/).withHref("#"),
        		each(pack.subpackages, sub -> createPackageSubtree(current, sub, outputPaths,sourceFiles, isIndex)),
        		each(pack.classes, clazz -> createClassLink(clazz==current, clazz, outputPaths, isIndex, sourceFiles.containsKey(clazz))))
        .withCondClass(isonpath||isIndex, "jstree-open"));
  }

  /**
   * Creates the class link.
   *
   * @param isCurrent
   *          the indentation
   * @param clazz
   *          the clazz
   * @param outputPaths
   *          the output paths
   * @param isIndex
   *          the is index
 * @param hasSource 
   * @return the container tag
   */
  private ContainerTag createClassLink(boolean isCurrent, SootClass clazz,
      Map<SootClass, String> outputPaths, boolean isIndex, boolean hasSource) {
	  String type =hasSource?"file":"noSource";
    return ul(new MyLi(a(clazz.getShortName() + ".java (" + report.get(clazz).getLineNumberConstraintMap().size() + ")")
            .withHref((isIndex ? "" : "../") + outputPaths.get(clazz)))
    		.withData("jstree","{\"type\":\""+type+"\",\"selected\":"+isCurrent+"}")); 
    }
  

  /**
   * Find source file.
   *
   * @param className
   *          the class name
   * @return the file
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private File findSourceFile(String className) throws FileNotFoundException {
    String relativePath = className.replaceAll("\\.", "/") + ".java";
    // search in root
    File fileInRoot = new File(sourcePath + "/" + relativePath);
    if (fileInRoot.exists()) {
      return fileInRoot;
    }
    // search in src
    File fileInSrc = new File(sourcePath + "/src/" + relativePath);
    if (fileInSrc.exists()) {
      return fileInSrc;
    }
    // search in main
    File fileInMain = new File(sourcePath + "/src/main/java/" + relativePath);
    if (fileInMain.exists()) {
      return fileInMain;
    }
    // search in test
    File fileInTest = new File(sourcePath + "/src/test/java/" + relativePath);
    if (fileInTest.exists()) {
      return fileInTest;
    }

    // search all java files in sourcepath
    File f = findSourceFile(new File(sourcePath), relativePath);
    if (f != null) {
      return f;
    }
    throw new FileNotFoundException(relativePath);
  }

  /**
   * Find source file.
   *
   * @param startFile
   *          the start file
   * @param targetFileName
   *          the target file name
   * @return the file
   * @throws FileNotFoundException
   */
  private File findSourceFile(File startFile, String targetFileName) throws FileNotFoundException {
    if (startFile.isDirectory()) {
      File fileInFolder = new File(startFile, targetFileName);
      if (fileInFolder.exists()) {
        return fileInFolder;
      } else {
        for (File file : startFile.listFiles()) {
          File f = findSourceFile(file, targetFileName);
          if (f != null) {
            return f;
          }
        }
      }
    }
    return null;
  }

  /**
   * Creates the output content.
   *
   * @param LineResults
   *          the line results
   * @return the container tag
   * @throws FileNotFoundException
   *           the file not found exception
   */
  private ContainerTag createOutputContent(LineConstraints LineResults) {
    ContainerTag output = table().withClass("content");

    // load source class

    try (InputStream stream = new FileInputStream(
        findSourceFile(LineResults.getSootClass().getName()))) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

      int lineNumber = 1;
      int constraintNo = 0;
      while (reader.ready()) {
        ContainerTag tr = tr();
        output.with(tr);
        tr.with(td(attrs(".lineNo"), lineNumber + ""));
        String line = reader.readLine();
        ContainerTag lineContainer = printLine(line);
        if (isblockCommentLine) {
          lineContainer.withClass("comment");
          isblockCommentLine = false;
        }
        tr.with(lineContainer);
        IConstraint constr = LineResults.getLineNumberConstraintMap().get(lineNumber);
        if (constr != null) {
          tr.with(td(attrs("#constr" + constraintNo++ + ".constraint"), constr.toReadableString()));
          tr.withClass("constraint");
        }
        lineNumber++;
      }
    } catch (IOException e) {
      //source file not found:
    	output.with(h2("No source found"));
    }
    return output;
  }

  /**
   * Prints the line.
   *
   * @param line
   *          the line
   * @return the container tag
   */
  private ContainerTag printLine(String line) {
    if (line.isEmpty()) {
      return td();
    }
    String[] linePartStrs = line.split("((?= )|(?<= )|(?=//)|(?<=//)|(?=;)|(?<=;)|(?=\")|(?<=\"))");
    highlightKeywords(linePartStrs);

    return td(attrs(".srcLine"), pre(String.join("", linePartStrs)).render());
  }

  /** The jva keywords. */
  private static List<String> keywords = new ArrayList<>();

  static {
    keywords.add("public");
    keywords.add("private");
    keywords.add("protected");
    keywords.add("package");
    keywords.add("static");
    keywords.add("class");
    keywords.add("return");
    keywords.add("if");
    keywords.add("while");
    keywords.add("do");
    keywords.add("catch");
    keywords.add("throw");
    keywords.add("try");
    keywords.add("new");
    keywords.add("null");
    keywords.add("import");
    keywords.add("void");
    keywords.add("int");
    keywords.add("float");
    keywords.add("double");
    keywords.add("else");
    keywords.add("true");
    keywords.add("false");
  };

  /** The block comment open. */
  private boolean blockCommentOpen = false;

  /** The isblock comment line. */
  private boolean isblockCommentLine = false;

  /**
   * Highlight the keywords in HTML.
   *
   * @param strParts
   *          the str parts
   */
  private void highlightKeywords(String[] strParts) {
    boolean lineComment = false;
    boolean isString = false;
    for (int i = 0; i < strParts.length; i++) {
      if (strParts[i].contains("/*")) {
        blockCommentOpen = true;
      }
      if (blockCommentOpen) {
        isblockCommentLine = true;
      }
      if (strParts[i].contains("*/")) {
        blockCommentOpen = false;
      }
      if (strParts[i].contains("//") && !lineComment) {
        lineComment = true;
        strParts[i] = "<span class='comment'>" + strParts[i];
      }
      if (strParts[i].contains("\"")) {
        isString = !isString;
        if (isString) {
          strParts[i] = "<span class='string'>" + strParts[i];
        } else {
          strParts[i] = strParts[i] + "</span>";

        }
      }
      if (!isString && !isblockCommentLine && !lineComment
          && keywords.contains(strParts[i].replaceAll(" ", ""))) {
        strParts[i] = span(attrs(".keyword"), strParts[i]).render();
      } else if (strParts[i].replaceAll(" ", "").startsWith("@")) {
        strParts[i] = span(attrs(".annotation"), strParts[i]).render();
      } else {
        strParts[i] = strParts[i];
      }
    }
    if (lineComment) {
      strParts[strParts.length - 1] = strParts[strParts.length - 1] + "</span>";
    }
  }

}
