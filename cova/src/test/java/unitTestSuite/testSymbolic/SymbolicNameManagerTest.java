package unitTestSuite.testSymbolic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import soot.ArrayType;
import soot.Local;
import soot.Modifier;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.util.Chain;

import cova.source.data.Method;
import cova.source.data.SourceMethod;
import cova.source.data.SourceType;
import cova.source.data.SourceUICallback;
import cova.source.symbolic.SymbolicNameManager;
import utils.UnitTestFramework;

public class SymbolicNameManagerTest extends UnitTestFramework {

  Unit gUnit = null;
  Unit gUnitDifferentObj = null;

  @Before
  public void initialize() {

    SootClass sClass;
    SootMethod method;

    // Resolve dependencies
    Scene.v().loadClassAndSupport("java.lang.Object");
    Scene.v().loadClassAndSupport("java.lang.System");

    // Declare 'public class HelloWorld'
    sClass = new SootClass("HelloWorld", Modifier.PUBLIC);

    // 'extends Object'
    sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
    Scene.v().addClass(sClass);

    // Create the method, public static void main(String[])
    method = new SootMethod("main",
        Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}), VoidType.v(),
        Modifier.PUBLIC | Modifier.STATIC);


    // create empty body
    JimpleBody body = Jimple.v().newBody(method);

    method.setActiveBody(body);
    Chain<Unit> units = body.getUnits();
    Local arg, tmpRef;

    // Add some locals, java.lang.String l0
    arg = Jimple.v().newLocal("l0", ArrayType.v(RefType.v("java.lang.String"), 1));
    body.getLocals().add(arg);

    // Add locals, java.io.printStream tmpRef
    tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
    body.getLocals().add(tmpRef);

    // add "l0 = @parameter0"
    units.add(Jimple.v().newIdentityStmt(arg,
        Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0)));

    gUnit = units.getLast();
    assertNotSame(null, gUnit);

    // add "l0 = @parameter0"
    units.add(Jimple.v().newIdentityStmt(arg,
        Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0)));

    gUnitDifferentObj = units.getLast();

  }


  @Test
  public void testSingleton() {

    SymbolicNameManager FirstSNM = SymbolicNameManager.getInstance();
    SymbolicNameManager SecondSNM = SymbolicNameManager.getInstance();

    assertSame(FirstSNM, SecondSNM);

  }

  @Test
  public void testReset() {

    SymbolicNameManager SNM = SymbolicNameManager.getInstance();
    SymbolicNameManager.reset();
    assertNotSame(SNM, SymbolicNameManager.getInstance());

  }



  @Test
  public void testCreateSymbolicName() {

    Method method = new Method(".+", "java.lang.Object", "getSystemService",
        Arrays.asList("java.lang.String"), Arrays.asList("\\\"alarm\\\""));
    SourceMethod source = new SourceMethod(method, SourceType.C, "ALARM", 108);

    Unit unit = gUnit;

    SymbolicNameManager.reset();
    SymbolicNameManager SNM = SymbolicNameManager.getInstance();
    String SymbolicName = SNM.createSymbolicName(unit, source);

    assertEquals("C108", SymbolicName);
    // invoke: with same unit as source but different source to check whether new inserted to
    // datastructure or returned from datastructure
    SNM.createSymbolicName(unit, new SourceMethod(method, SourceType.C, "ALARM", 123));
    assertEquals("C108", SymbolicName);

    SymbolicNameManager.reset();
    SNM = SymbolicNameManager.getInstance();

    // invoke with sourceUICallback as source
    SootMethodAndClass cb = new SootMethodAndClass("onKeyDown", "android.view.KeyEvent$Callback",
        "boolean", Arrays.asList("int", "android.view.KeyEvent"));

    SymbolicName = SNM.createSymbolicName(gUnit, new SourceUICallback(cb, 3));

    assertEquals("U3_0", SymbolicName);
    // invoke: with same unit but different source to check whether new inserted to datastructure or
    // returned from datastructure
    SymbolicName = SNM.createSymbolicName(gUnit, new SourceUICallback(cb, 4));
    assertEquals("U3_0", SymbolicName);

    // check if already in symbolicNameIndexMap
    SymbolicName = SNM.createSymbolicName(gUnitDifferentObj, new SourceUICallback(cb, 3));
    assertEquals("U3_1", SymbolicName);



  }


  @Test
  public void testGetSourceUniqueName() {

    SymbolicNameManager SNM = SymbolicNameManager.getInstance();
    assertEquals("abc", SNM.getSourceUniqueName("abc_XYZ"));
    assertEquals("XYZ123", SNM.getSourceUniqueName("XYZ123_BANANA123"));

  }

  @Test
  public void testCreateImpreciseSymbolicName() {
    Unit unit = gUnit;
    SymbolicNameManager.reset();
    SymbolicNameManager SNM = SymbolicNameManager.getInstance();
    ArrayList<String> names=new ArrayList<String>();
    names.add("C108");
    names.add("C109");
    assertEquals("im(C108+C109)_0", SNM.createImpreciseSymbolicName(unit, names));
  }

  @Test
  public void testGetSymbolicNames() {


    Method method1 = new Method(".+", "java.lang.Object", "getSystemService",
        Arrays.asList("java.lang.String"), Arrays.asList("\\\"alarm\\\""));
    SourceMethod source = new SourceMethod(method1, SourceType.C, "ALARM", 108);

    SymbolicNameManager.reset();
    SymbolicNameManager SNM = SymbolicNameManager.getInstance();
    String symbolicName = SNM.createSymbolicName(gUnit, source);
    assertEquals("ALARM", SNM.getSourceName(symbolicName));

    SymbolicNameManager.reset();
    SNM = SymbolicNameManager.getInstance();

    SootMethodAndClass cb = new SootMethodAndClass("onKeyDown", "android.view.KeyEvent$Callback",
        "boolean", Arrays.asList("int", "android.view.KeyEvent"));
    symbolicName = SNM.createSymbolicName(gUnit, new SourceUICallback(cb, 3));
    assertEquals("onKeyDown_0", SNM.getSourceName(symbolicName));

    // create again with different obj with same contents and same contents for symbolicname
    symbolicName = SNM.createSymbolicName(gUnitDifferentObj, new SourceUICallback(cb, 3));
    assertEquals("onKeyDown_1", SNM.getSourceName(symbolicName));

  }



}
