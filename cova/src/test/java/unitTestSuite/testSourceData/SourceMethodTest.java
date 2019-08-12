package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

import cova.source.data.Method;
import cova.source.data.SourceMethod;
import cova.source.data.SourceType;
import utils.UnitTestFramework;

public class SourceMethodTest extends UnitTestFramework {

  @Test
  public void testGetterSetter() {

    Method m1 = new Method("Math", "int", "abs", Arrays.asList("a"), Arrays.asList("-10"));

    SourceMethod sm1 = new SourceMethod(m1, SourceType.C, "Coconut", 0);
    assertSame(m1, sm1.getMethod());

    // test getter/setter of Source
    assertEquals(0, sm1.getId());
    assertEquals(SourceType.C, sm1.getType());
    assertEquals("Coconut", sm1.getName());
    assertEquals("C0", sm1.getUniqueName());

    // SourceType I
    SourceMethod sm2 = new SourceMethod(m1, SourceType.I, "InputA", 10);
    assertEquals(sm2.getId(), 10);
    assertEquals(SourceType.I, sm2.getType());
    assertEquals("InputA", sm2.getName());
    assertEquals("I10", sm2.getUniqueName());

    // SourceType U, negative int
    SourceMethod sm3 = new SourceMethod(m1, SourceType.U, "UI666Banana", -123);
    assertEquals(-123, sm3.getId());
    assertEquals(SourceType.U, sm3.getType());
    assertEquals("UI666Banana", sm3.getName());
    assertEquals("U-123", sm3.getUniqueName());

  }

}
