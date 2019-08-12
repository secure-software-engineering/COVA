package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;

import cova.source.data.Method;
import utils.UnitTestFramework;


public class MethodTest extends UnitTestFramework {



  @Test
  public void testGetterSetter() {

    Method m1 = new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("-10"));

    // getSignature() calls toString; toString is tested in testToString
    assertEquals(m1.getSignature(), m1.toString());

  }

  @Test
  public void testEquals() {
    Method m1 = new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("-666"));
    Method m1Equal = new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("-666"));
    // same object
    assertTrue(m1.equals(m1));
    // with null
    assertFalse(m1.equals(null));
    // different object
    assertFalse(m1.equals(new Integer(666)));
    // own parametervalues is null but other is set
    assertFalse(new Method("Math", "int", "abs", Arrays.asList("a"), null).equals(m1));
    // parametervalues differs
    assertFalse(m1
        .equals(new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("123", "321"))));
    // same object different instance
    assertEquals(m1, m1Equal);

  }

  @Test
  public void testToString() {

    // test multiple parameter
    Method m1 =
        new Method("Math", "double", "pow", Arrays.asList("a", "b"), Arrays.asList("2", "8"));
    assertEquals("<Math: double pow(a,b)>(2, 8)", m1.toString());

    // single parameter
    Method m2 = new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("-10"));
    assertEquals("<Math: int abs(a)>(-10)", m2.toString());

    // untrimmed parametervalue
    Method m3 =
        new Method("Integer", "Integer", "parseInt", Arrays.asList("s"), Arrays.asList("     1  "));
    assertEquals("<Integer: Integer parseInt(s)>(1)", m3.toString());

    // parametervalue is null
    Method m4 = new Method("Integer", "Integer", "parseInt", Arrays.asList("s"),
        Arrays.asList("null"));
    assertEquals("<Integer: Integer parseInt(s)>(null)", m4.toString());

  }


}
