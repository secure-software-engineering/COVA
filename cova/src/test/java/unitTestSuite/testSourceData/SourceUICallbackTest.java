package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Arrays;

import org.junit.Test;

import soot.jimple.infoflow.data.SootMethodAndClass;

import cova.source.data.SourceType;
import cova.source.data.SourceUICallback;
import utils.UnitTestFramework;

public class SourceUICallbackTest extends UnitTestFramework {

  @Test
  public void testGetterSetter() {

    // method+callback: used in e.g. in CallbackMatcher
    SootMethodAndClass method = new SootMethodAndClass("android.view.KeyEvent$Callback", "boolean",
        "onKeyDown", Arrays.asList("int", "android.view.KeyEvent"));

    SootMethodAndClass callback = new SootMethodAndClass("android.view.KeyEvent$Callback",
        "boolean", "onKeyDown", Arrays.asList("int", "android.view.KeyEvent"));

    // constructor 1: sets method; forwards callback to second constructor
    SourceUICallback suic1 = new SourceUICallback(method, callback, 3);

    assertSame(suic1.getCallback(), callback);
    assertSame(suic1.getMethod(), method);

    assertEquals(suic1.getId(), 3);
    assertEquals(suic1.getType(), SourceType.U);
    assertEquals(suic1.getName(), callback.getMethodName());
    assertEquals(suic1.getUniqueName(), "U3");


    // constructor 2: sets callback
    SourceUICallback suic2 = new SourceUICallback(callback, 123);

    assertSame(suic2.getCallback(), callback);
    assertSame(suic2.getMethod(), null);

    assertEquals(suic2.getId(), 123);
    assertEquals(suic2.getType(), SourceType.U);
    assertEquals(suic2.getName(), callback.getMethodName());
    assertEquals(suic2.getUniqueName(), "U123");



  }


}
