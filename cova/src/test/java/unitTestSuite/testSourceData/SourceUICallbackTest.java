/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import cova.source.data.SourceType;
import cova.source.data.SourceUICallback;
import java.util.Arrays;
import org.junit.Test;
import soot.jimple.infoflow.data.SootMethodAndClass;
import utils.UnitTestFramework;

public class SourceUICallbackTest extends UnitTestFramework {

  @Test
  public void testGetterSetter() {

    // method+callback: used in e.g. in CallbackMatcher
    SootMethodAndClass method =
        new SootMethodAndClass(
            "android.view.KeyEvent$Callback",
            "boolean",
            "onKeyDown",
            Arrays.asList("int", "android.view.KeyEvent"));

    SootMethodAndClass callback =
        new SootMethodAndClass(
            "android.view.KeyEvent$Callback",
            "boolean",
            "onKeyDown",
            Arrays.asList("int", "android.view.KeyEvent"));

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
