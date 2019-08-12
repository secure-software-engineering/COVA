package utils;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

import soot.ArrayType;
import soot.G;
import soot.Local;
import soot.Modifier;
import soot.PackManager;
import soot.RefType;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Type;
import soot.VoidType;
import soot.javaToJimple.LocalGenerator;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.options.Options;

import categories.BenchmarkTestSuite;
import cova.core.SceneTransformerFactory;
import cova.data.IConstraint;
import cova.reporter.ConstraintReporter;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForTestCase;

@Category(BenchmarkTestSuite.class)
public class ConstraintBenchTestFramework {
  @Rule
  public TestName testMethodName = new TestName();
  protected String targetTestClassName = "";
  protected TreeMap<Integer, IConstraint> results;
  
  private String userDir = System.getProperty("user.dir");
  private String covaRootDir = new File(userDir).getParent();
  private String benchdir
      = covaRootDir + File.separator + "constraintBench" + File.separator + "target" + File.separator + "classes";

  // set the unique name of configurations for comparing results
  protected final String A = "C1001";
  protected final String B = "C1002";
  protected final String C = "C1003";
  protected final String D = "C1004";
  protected final String E = "C1005";
  protected final String F = "C1006";

  protected final String FA = "C1007";
  protected final String FB = "C1008";
  protected final String FC = "C1009";

  protected final String P = "C1010";
  protected final String Q = "C1011";
  protected final String K = "C1012";
  protected final String H = "C1013";
  protected final String X = "C1014";
  protected final String onClick = "U1001";
  protected final String onScroll = "U1002";

  @Before
  public void beforeTestCaseExecution() {
    initializeSootWithEntryPoint();
    try {
      results = new TreeMap<>();
      String className = this.getClass().getName();
      Config config = new DefaultConfigForTestCase();
      SceneTransformerFactory transformerFactory = new SceneTransformerFactory(config);
      String[] names = className.split("\\.");
      ConstraintReporter reporter
          = new ConstraintReporter(names[names.length - 1] + File.separator + testMethodName.getMethodName(),
              config.isWriteJimpleOutput(), true);
      SceneTransformer transformer
          = transformerFactory.createAnalysisTransformerForTestCase(className, testMethodName.getMethodName(), reporter,
              covaRootDir);
      analyze(transformer);
      SootClass testClass = Scene.v().forceResolve(targetTestClassName, SootClass.BODIES);
      results = reporter.getResultOfLines(testClass);
      reporter.printResultOfLines(testClass);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail(e.getMessage());
    }
  }

  private void initializeSootWithEntryPoint() {
    G.v().reset();
    Options.v().set_whole_program(true);
    Options.v().set_print_tags_in_output(true);
    Options.v().setPhaseOption("cg.spark", "on");
    Options.v().set_keep_line_number(true);
    String sootClassPath
        = userDir + File.separator + "target" + File.separator + "test-classes";
    sootClassPath+= File.pathSeparator + benchdir;
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_exclude(excludedPackages());
    Options.v().set_soot_classpath(sootClassPath);
    Options.v().setPhaseOption("jb", "use-original-names:true");
    SootClass targetTestClass = Scene.v().forceResolve(this.targetTestClassName, SootClass.BODIES);
    SootMethod testMethod = null;
    testMethod = targetTestClass.getMethodByName("test");
    if (testMethod == null) {
      throw new RuntimeException(
          "The method with test was not found in " + targetTestClassName);
    }
    SootClass dummyMainClass = createDummyMainClass(testMethod);
    Scene.v().addClass(dummyMainClass);
    Scene.v().loadNecessaryClasses();
    targetTestClass.setApplicationClass();
    dummyMainClass.setApplicationClass();
    Scene.v().setMainClass(dummyMainClass);
  }

  private void analyze(SceneTransformer transformer) {
    PackManager.v().getPack("cg").apply();
    PackManager.v().getPack("wjtp").add(new Transform("wjtp.cova", transformer));
    PackManager.v().getPack("wjtp").apply();
  }

  public List<String> excludedPackages() {
    List<String> excludedPackages = new LinkedList<>();
    excludedPackages.add("sun.*");
    excludedPackages.add("javax.*");
    excludedPackages.add("com.sun.*");
    excludedPackages.add("com.ibm.*");
    excludedPackages.add("org.xml.*");
    excludedPackages.add("org.w3c.*");
    excludedPackages.add("apple.awt.*");
    excludedPackages.add("com.apple.*");
    return excludedPackages;
  }

  /**
   * This method construct a dummyMainClass with main method contains the invocation of the test method in target test class.
   * 
   * @param testMethod
   * @return
   */
  private SootClass createDummyMainClass(SootMethod testMethod) {
    SootClass dummyMainClass = new SootClass("dummyMainClass");
    Type type = ArrayType.v(RefType.v("java.lang.String"), 1);
    SootMethod dummyMainMethod
        = new SootMethod("main", Arrays.asList(new Type[] { type }), VoidType.v(), Modifier.PUBLIC | Modifier.STATIC);
    dummyMainClass.addMethod(dummyMainMethod);
    JimpleBody body = Jimple.v().newBody(dummyMainMethod);
    RefType testCaseType = RefType.v(this.targetTestClassName);
    Local allocatedTestObj = Jimple.v().newLocal("dummyObj", testCaseType);
    body.getLocals().add(allocatedTestObj);
    // create @this identityStmt for main method, otherwise boomerang will throw exception
    body.getUnits()
        .add(Jimple.v().newIdentityStmt(new LocalGenerator(body).generateLocal(type), Jimple.v().newParameterRef(type, 0)));
    body.getUnits().add(Jimple.v().newAssignStmt(allocatedTestObj, Jimple.v().newNewExpr(testCaseType)));
    body.getUnits().add(Jimple.v().newInvokeStmt(Jimple.v().newVirtualInvokeExpr(allocatedTestObj, testMethod.makeRef())));
    dummyMainMethod.setActiveBody(body);
    return dummyMainClass;
  }
}
