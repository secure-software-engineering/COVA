package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cova.source.data.Field;
import utils.UnitTestFramework;

public class FieldTest extends UnitTestFramework {

  @Test
  public void testGetterSetter() {

    Field f1 = new Field("testFlowFunctions.PrimType1Test", "int", "constraintsCount");
    assertEquals("testFlowFunctions.PrimType1Test", f1.getClassName());
    assertEquals("int", f1.getFieldType());
    assertEquals("constraintsCount", f1.getFieldName());

    // getSignature() calls toString; toString is tested in testHashcodeAndToString
    assertEquals(f1.getSignature(), f1.toString());

  }

  @Test
  public void testEquals() {

    Field f1 = new Field("testFlowFunctions.PrimType1Test", "int", "constraintsCount");
    Field f1Equal = new Field("testFlowFunctions.PrimType1Test", "int", "constraintsCount");

    // test same object
    assertTrue(f1.equals(f1));
    // test with null
    assertFalse(f1.equals(null));
    // test different object
    assertFalse(f1.equals(new Integer(123)));

    // own classname is null but other is set
    assertFalse(new Field(null, "int", "constraintsCount").equals(f1));
    // classname differs
    assertFalse(f1.equals(new Field("differentClassName", "int", "constraintsCount")));

    // own fieldNameis null but other is set
    assertFalse(new Field("testFlowFunctions.PrimType1Test", "int", null).equals(f1));
    // fieldName differs
    assertFalse(f1.equals(new Field("differentClassName", "int", "differentCount()")));

    // own fieldtype is null but other is set
    assertFalse(new Field("testFlowFunctions.PrimType1Test", null, "constraintsCount").equals(f1));
    // fieldtype differs
    assertFalse(
        f1.equals(new Field("testFlowFunctions.PrimType1Test", "void", "constraintsCount")));

    // same object different instance
    assertEquals(f1, f1Equal);

  }


  @Test
  public void testHashcodeAndToString() {

    // test empty input
    Field f1 = new Field("", "", "");
    Field f2 = new Field("", "", "");
    assertEquals(f1.hashCode(), f2.hashCode());
    assertEquals("<:  >", f1.toString());

    // check calcuation with null
    Field f1_1 = new Field(null, "", "");
    Field f2_1 = new Field("", null, "");
    Field f3_1 = new Field("", "", null);
    assertEquals(f1_1.hashCode(), f2_1.hashCode());
    assertEquals(f1_1.hashCode(), f3_1.hashCode());
    assertEquals("<null:  >", f1_1.toString());
    assertEquals("<: null >", f2_1.toString());
    assertEquals("<:  null>", f3_1.toString());

    // empty Strings
    Field f3 = new Field("asdf", "", "");
    Field f4 = new Field("asdf", "", "");
    assertEquals(f3.hashCode(), f4.hashCode());

    // test numbers and chars
    Field f5 = new Field("a", "1", "1");
    Field f6 = new Field("b", "1", "1");
    assertNotEquals(f5.hashCode(), f6.hashCode());
    assertEquals("<a: 1 1>", f5.toString());
    assertEquals("<b: 1 1>", f6.toString());

    // upper/lowercase chars
    Field f7 = new Field("A", "a", "B");
    Field f8 = new Field("A", "a", "B");
    assertEquals(f7.hashCode(), f8.hashCode());
    assertEquals("<A: a B>", f7.toString());
    assertEquals("<A: a B>", f8.toString());

    // mixed number/chars
    Field f9 = new Field("1", "1", "a");
    Field f10 = new Field("1", "1", "a");
    assertEquals(f9.hashCode(), f10.hashCode());
    assertEquals("<1: 1 a>", f9.toString());
    assertEquals("<1: 1 a>", f10.toString());


  }



}
