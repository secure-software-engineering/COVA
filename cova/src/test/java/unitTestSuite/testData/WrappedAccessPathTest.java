package unitTestSuite.testData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import soot.ArrayType;
import soot.BooleanType;
import soot.Local;
import soot.LongType;
import soot.NullType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Value;
import soot.VoidType;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.StaticFieldRef;
import soot.jimple.internal.JimpleLocal;
import soot.util.Chain;

import cova.data.WrappedAccessPath;
import utils.UnitTestFramework;

public class WrappedAccessPathTest extends UnitTestFramework {

  @Before
  public void setup() {

    SootClass sClass;
    SootMethod method;

    // Resolve dependencies
    Scene.v().loadClassAndSupport("java.lang.Object");
    Scene.v().loadClassAndSupport("java.lang.System");

    Scene.v().makeSootField("java.lang.System.out", RefType.v("java.io.PrintStream"), Modifier.PUBLIC);
    Scene.v().makeSootField("java.lang.System.in", RefType.v("java.io.InputStream"), Modifier.PUBLIC);

    // Declare 'public class HelloWorld'
    sClass = new SootClass("HelloWorld", Modifier.PRIVATE);

    // 'extends Object'
    sClass.setSuperclass(Scene.v().getSootClass("java.lang.Object"));
    Scene.v().addClass(sClass);

    // Create the method, public static void main(String[])
    method = new SootMethod("main",
        Arrays.asList(new Type[] {ArrayType.v(RefType.v("java.lang.String"), 1)}), VoidType.v(),
        Modifier.PUBLIC | Modifier.STATIC);

    sClass.addMethod(method);

    // Create the method body
    {
      // create empty body
      JimpleBody body = Jimple.v().newBody(method);

      method.setActiveBody(body);
      Chain units = body.getUnits();
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

      // add "tmpRef = java.lang.System.out"
      units.add(Jimple.v().newAssignStmt(tmpRef, Jimple.v().newStaticFieldRef(
          Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())));

      // insert "return"
      units.add(Jimple.v().newReturnVoidStmt());

    }

  }


  @Test
  public void testWrappedAccessPathLocal() {

    Local local = Jimple.v().newLocal("tmp", LongType.v());
    WrappedAccessPath wap = new WrappedAccessPath(local);

    assertSame(null, wap.getFields());
    assertEquals(local, wap.getBase());

  }

  @Test
  public void testWrappedAccessPathLocalArrayListOfSootField() {

    Local local = Jimple.v().newLocal("tmp", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("instance", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("banana", LongType.v(), Modifier.PRIVATE));

    WrappedAccessPath wap = new WrappedAccessPath(local, fields);

    assertEquals(fields, wap.getFields());
    assertEquals(local, wap.getBase());

    // empty fields
    fields.clear();
    wap = new WrappedAccessPath(local, fields);
    assertEquals(null, wap.getFields());
    assertEquals(local, wap.getBase());

    wap = new WrappedAccessPath(local, null);
    assertEquals(null, wap.getFields());
    assertEquals(local, wap.getBase());


  }

  @Test
  public void testWrappedAccessPathValue() {

    // Local
    Value value = Jimple.v().newLocal("tmp", RefType.v("java.io.PrintStream"));
    WrappedAccessPath wap = new WrappedAccessPath(value);
    assertEquals(null, wap.getFields());
    assertEquals(value, wap.getBase());

    SootField field = Scene.v().getField("<java.lang.System: java.io.PrintStream out>");

    // TODO: Error: "wrong staticness"
    if (false) {
      InstanceFieldRef ifr = Jimple.v().newInstanceFieldRef(value, field.makeRef());
      wap = new WrappedAccessPath(ifr);
      ArrayList<SootField> IFRField = new ArrayList<SootField>();
      IFRField.add(ifr.getField());
      assertEquals(IFRField, wap.getFields());
      assertEquals(ifr.getBase(), wap.getBase());
    }

    StaticFieldRef sfr = Jimple.v().newStaticFieldRef(field.makeRef());
    wap = new WrappedAccessPath(sfr);
    ArrayList<SootField> SFRField = new ArrayList<SootField>();
    SFRField.add(sfr.getField());
    assertEquals("[" + field + "]", "" + wap.getFields());
    assertEquals(null, wap.getBase());


  }

  @Test
  public void testGetZeroAccessPath() {

    assertEquals("" + WrappedAccessPath.getZeroAccessPath(),
        "" + new WrappedAccessPath(new JimpleLocal("ZERO", NullType.v())));

    assertEquals("" + WrappedAccessPath.getZeroAccessPath(),
        "" + new WrappedAccessPath(new JimpleLocal("ZERO", NullType.v())));

  }

  // @Test
  public void testIsSupportedType() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    SootField field = new SootField("username", ArrayType.v(LongType.v(), 1), Modifier.PUBLIC);

    assertTrue(WrappedAccessPath.isSupportedType(Jimple.v().newLocal("tmp", LongType.v())));
    assertTrue(
        WrappedAccessPath.isSupportedType(Jimple.v().newInstanceFieldRef(value, field.makeRef())));
    assertTrue(WrappedAccessPath.isSupportedType(Jimple.v().newStaticFieldRef(field.makeRef())));

  }

  // @Test
  public void testIsStaticFieldRef() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    SootField field = new SootField("username", ArrayType.v(LongType.v(), 1), Modifier.PUBLIC);

    assertFalse(new WrappedAccessPath(Jimple.v().newLocal("tmp", LongType.v())).isStaticFieldRef());
    assertFalse(new WrappedAccessPath(Jimple.v().newInstanceFieldRef(value, field.makeRef()))
        .isStaticFieldRef());
    assertTrue(
        new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef())).isStaticFieldRef());

  }

  // @Test
  public void testIsInstanceFieldRef() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    SootField field = new SootField("username", ArrayType.v(LongType.v(), 1), Modifier.PUBLIC);

    assertFalse(
        new WrappedAccessPath(Jimple.v().newLocal("tmp", LongType.v())).isInstanceFieldRef());
    assertTrue(new WrappedAccessPath(Jimple.v().newInstanceFieldRef(value, field.makeRef()))
        .isInstanceFieldRef());
    assertFalse(
        new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef())).isInstanceFieldRef());

  }

  // @Test
  public void testIsLocal() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    SootField field = new SootField("username", ArrayType.v(LongType.v(), 1), Modifier.PUBLIC);

    assertTrue(new WrappedAccessPath(Jimple.v().newLocal("tmp", LongType.v())).isLocal());
    assertFalse(
        new WrappedAccessPath(Jimple.v().newInstanceFieldRef(value, field.makeRef())).isLocal());
    assertFalse(new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef())).isLocal());

  }

  @Test
  public void testEqualsObject() {

    Local local = Jimple.v().newLocal("tmp", LongType.v());

    WrappedAccessPath wap = new WrappedAccessPath(local);
    WrappedAccessPath wapEqual = new WrappedAccessPath(local);
    WrappedAccessPath wapDifferent =
        new WrappedAccessPath(Jimple.v().newLocal("differentname", ShortType.v()));

    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("fruit", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("banana", LongType.v(), Modifier.PUBLIC));
    WrappedAccessPath wapFields = new WrappedAccessPath(local, fields);

    fields = new ArrayList<SootField>();
    fields.add(new SootField("animals", LongType.v(), Modifier.PUBLIC));
    WrappedAccessPath wapFieldsDifferent = new WrappedAccessPath(local, fields);

    assertTrue(wap.equals(wap));
    assertFalse(wap.equals(null));
    assertFalse(wap.equals(new Integer(123)));

    // base is null
    SootField field = Scene.v().getField("<java.lang.System: java.io.PrintStream out>");
    assertFalse(new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef())).equals(wap));
    assertTrue(new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef()))
        .equals(new WrappedAccessPath(Jimple.v().newStaticFieldRef(field.makeRef()))));

    assertFalse(wap.equals(wapDifferent));

    assertFalse(wap.equals(wapFields));

    assertFalse(wapFields.equals(wap));
    assertFalse(wapFields.equals(wapFieldsDifferent));

    fields = new ArrayList<SootField>();
    fields.add(new SootField("fruit", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("otherbanana", ShortType.v(), Modifier.PUBLIC));
    assertFalse(wapFieldsDifferent.equals(new WrappedAccessPath(local, fields)));

    fields = new ArrayList<SootField>();
    fields.add(new SootField("potato", ShortType.v(), Modifier.PUBLIC));
    assertFalse(wapFieldsDifferent.equals(new WrappedAccessPath(local, fields)));

    fields.add(new SootField("slice", ShortType.v(), Modifier.PUBLIC));
    fields.add(new SootField("atom", ShortType.v(), Modifier.PUBLIC));
    assertTrue(new WrappedAccessPath(local, fields).equals(new WrappedAccessPath(local, fields)));

    ArrayList<SootField> fields2 = new ArrayList<SootField>(fields);
    fields2.add(new SootField("quarks", ShortType.v(), Modifier.PUBLIC));
    fields.add(new SootField("electron", ShortType.v(), Modifier.PUBLIC));
    assertFalse(new WrappedAccessPath(local, fields2).equals(new WrappedAccessPath(local, fields)));


    assertTrue(wap.equals(wapEqual));

  }

  @Test
  public void testCopy() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    WrappedAccessPath wap = new WrappedAccessPath(value);
    assertEquals(wap, wap.copy());

  }

  @Ignore
  public void testHasPrefix() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    Value valueEqual = Jimple.v().newLocal("tmp", LongType.v());
    SootField field = Scene.v().getField("<java.lang.System: java.io.PrintStream out>");

    StaticFieldRef sfr = Jimple.v().newStaticFieldRef(field.makeRef());
    StaticFieldRef sfrEqual = Jimple.v().newStaticFieldRef(field.makeRef());

    field = Scene.v().getField("<java.lang.System: java.io.InputStream in>");
    StaticFieldRef sfrDifferent = Jimple.v().newStaticFieldRef(field.makeRef());

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("wood", LongType.v(), Modifier.PRIVATE));

    ArrayList<SootField> fieldsDifferent = new ArrayList<SootField>();
    fields.add(new SootField("stone", LongType.v(), Modifier.PRIVATE));

    // base1 == base2 == null || base1 == !null && base2 == !null
    assertFalse(new WrappedAccessPath(value).hasPrefix(new WrappedAccessPath(local, fields)));
    assertTrue(new WrappedAccessPath(local, fields)
        .hasPrefix(new WrappedAccessPath(local, fieldsDifferent)));

    assertTrue(new WrappedAccessPath(value).hasPrefix(new WrappedAccessPath(valueEqual)));

    // TODO: MORE



  }

  @Test
  public void testGetBaseType() {

    Value value = Jimple.v().newLocal("tmp", LongType.v());
    WrappedAccessPath wap = new WrappedAccessPath(value);
    assertEquals(LongType.v(), wap.getBaseType());

  }

  @Test
  public void testGetLastFieldType() {

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("stem", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("leaf", LongType.v(), Modifier.PRIVATE));

    assertEquals(LongType.v(), new WrappedAccessPath(local, fields).getLastFieldType());

    fields.clear();
    try {
      new WrappedAccessPath(local, fields).getLastFieldType();
      fail("there is no such a field - no Exception thrown");
    } catch (RuntimeException e) {
    }

    fields.add(new SootField("leaf", ShortType.v(), Modifier.PRIVATE));
    assertEquals(ShortType.v(), new WrappedAccessPath(local, fields).getLastFieldType());

  }

  @Test
  public void testIsPublic() {

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root0", ShortType.v(), Modifier.PUBLIC));
    assertTrue(new WrappedAccessPath(local, fields).isPublic());

    fields.add(new SootField("root1", ShortType.v(), Modifier.PRIVATE));
    assertFalse(new WrappedAccessPath(local, fields).isPublic());

    fields.add(new SootField("root2", ShortType.v(), Modifier.PROTECTED));
    assertFalse(new WrappedAccessPath(local, fields).isPublic());

  }

  @Test
  public void testIsPrivate() {

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root0", ShortType.v(), Modifier.PUBLIC));
    assertFalse(new WrappedAccessPath(local, fields).isPrivate());

    fields.add(new SootField("root1", ShortType.v(), Modifier.PRIVATE));
    assertTrue(new WrappedAccessPath(local, fields).isPrivate());

    fields.add(new SootField("root2", ShortType.v(), Modifier.PROTECTED));
    assertFalse(new WrappedAccessPath(local, fields).isPrivate());

  }

  @Test
  public void testIsProtected() {

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root0", ShortType.v(), Modifier.PUBLIC));
    assertFalse(new WrappedAccessPath(local, fields).isProtected());

    fields.add(new SootField("root1", ShortType.v(), Modifier.PRIVATE));
    assertFalse(new WrappedAccessPath(local, fields).isProtected());

    fields.add(new SootField("root2", ShortType.v(), Modifier.PROTECTED));
    assertTrue(new WrappedAccessPath(local, fields).isProtected());

  }

  @Test
  public void testGetType() {

    Local local = Jimple.v().newLocal("Thing", LongType.v());
    assertEquals(LongType.v(), new WrappedAccessPath(local).getType());

    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root", ShortType.v(), Modifier.PROTECTED));
    assertEquals(ShortType.v(), new WrappedAccessPath(local, fields).getType());

  }

  @Test
  public void testCopyFields() {

    Local local = Jimple.v().newLocal("Dog", LongType.v());
    Local localOther = Jimple.v().newLocal("Elephant", LongType.v());

    // fields is null
    assertEquals(new WrappedAccessPath(localOther),
        new WrappedAccessPath(local).copyFields(localOther));

    assertEquals(new WrappedAccessPath(local), new WrappedAccessPath(localOther).copyFields(local));


    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("Fruit", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("Banana", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("color", ShortType.v(), Modifier.PRIVATE));

    ArrayList<SootField> fieldsOther = new ArrayList<SootField>();
    fieldsOther.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Fruit", LongType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Pear", ShortType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Color", ShortType.v(), Modifier.PRIVATE));

    ArrayList<SootField> expectedFields = new ArrayList<SootField>();
    expectedFields.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("Fruit", ShortType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("Banana", ShortType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("color", ShortType.v(), Modifier.PRIVATE));

    assertEquals("" + new WrappedAccessPath(localOther, expectedFields),
        "" + new WrappedAccessPath(local, fields).copyFields(localOther));


    ArrayList<SootField> fields2 = new ArrayList<SootField>();
    fields.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("Fruit", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("Banana", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("color", ShortType.v(), Modifier.PRIVATE));

    ArrayList<SootField> fieldsOther2 = new ArrayList<SootField>();
    fieldsOther.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Fruit", LongType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Pear", ShortType.v(), Modifier.PRIVATE));
    fieldsOther.add(new SootField("Color", BooleanType.v(), Modifier.PRIVATE));

    ArrayList<SootField> expectedFields2 = new ArrayList<SootField>();
    expectedFields.add(new SootField("Things", LongType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("Fruit", ShortType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("Banana", ShortType.v(), Modifier.PRIVATE));
    expectedFields.add(new SootField("color", BooleanType.v(), Modifier.PRIVATE));

    assertEquals("" + new WrappedAccessPath(localOther, expectedFields),
        "" + new WrappedAccessPath(local, fields).copyFields(localOther));



  }

  @Test
  public void testConvert() {
    // TODO:
    // AccessPath bap = new AccessPath();
  }

  // boomerang stuff @Test
  public void testDeriveExtendedAccessPath() {
    // TODO: static example

    // TODO: instance field example
    Local local = Jimple.v().newLocal("Things", LongType.v());

    // assertEquals(new WrappedAccessPath(local, expectFields),
    // WrappedAccessPath.deriveExtendedAccessPath(new AccessPath(), new SootField("leaf",
    // IntType.v(), Modifier.PRIVATE)));


  }

  // @Test
  public void testReplacePrefix() {

    Local local = Jimple.v().newLocal("Things", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("root", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("stem", ShortType.v(), Modifier.PRIVATE));
    fields.add(new SootField("leaf", LongType.v(), Modifier.PRIVATE));

    ArrayList<SootField> prefixFields = new ArrayList<SootField>();
    prefixFields.add(new SootField("Banana", LongType.v(), Modifier.PRIVATE));

    try {
      new WrappedAccessPath(local, fields).replacePrefix(new WrappedAccessPath(local, prefixFields),
          5);
      fail("should throw an exception");
    } catch (Exception e) {
    }

    // prefixlentgh:0
    WrappedAccessPath wap = new WrappedAccessPath(local, fields);
    assertSame(wap, wap.replacePrefix(new WrappedAccessPath(local, fields), 0));

    // prefixlentgh:1
    prefixFields = new ArrayList<SootField>();
    prefixFields.add(new SootField("animal", LongType.v(), Modifier.PRIVATE));
    prefixFields.add(new SootField("monkey", ShortType.v(), Modifier.PRIVATE));
    prefixFields.add(new SootField("body", LongType.v(), Modifier.PRIVATE));

    assertSame(wap, wap.replacePrefix(new WrappedAccessPath(local, prefixFields), 1));



  }

  @Test
  public void testGetDepth() {

    Local local = Jimple.v().newLocal("tmp", LongType.v());
    ArrayList<SootField> fields = new ArrayList<SootField>();
    fields.add(new SootField("fruit", LongType.v(), Modifier.PRIVATE));
    fields.add(new SootField("banana", LongType.v(), Modifier.PUBLIC));
    assertEquals(3, new WrappedAccessPath(local, fields).getDepth());

    fields.clear();
    fields.add(new SootField("fruit", LongType.v(), Modifier.PRIVATE));
    assertEquals(1, new WrappedAccessPath(null, fields).getDepth());

  }


}
