/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package unitTestSuite.testSourceData;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import cova.source.data.Field;
import cova.source.data.SourceField;
import cova.source.data.SourceType;
import utils.UnitTestFramework;

public class SourceFieldTest extends UnitTestFramework {

  @Test
  public void testGetterSetter() {

    Field f1 = new Field("testFlowFunctions.PrimType1Test", "int", "constraintsCount()");
    SourceField sf1 = new SourceField(f1, SourceType.C, "Coconut", 0);
    assertSame(f1, sf1.getField());

    // test getter/setter of Source
    assertEquals(0, sf1.getId());
    assertEquals(SourceType.C, sf1.getType());
    assertEquals("Coconut", sf1.getName());
    assertEquals("C0", sf1.getUniqueName());

    // SourceType I
    SourceField sf2 = new SourceField(f1, SourceType.I, "InputA", 10);
    assertEquals(10, sf2.getId());
    assertEquals(SourceType.I, sf2.getType());
    assertEquals("InputA", sf2.getName());
    assertEquals("I10", sf2.getUniqueName());

    // SourceType U, negative int
    SourceField sf3 = new SourceField(f1, SourceType.U, "UI666Banana", -123);
    assertEquals(-123, sf3.getId());
    assertEquals(SourceType.U, sf3.getType());
    assertEquals("UI666Banana", sf3.getName());
    assertEquals("U-123", sf3.getUniqueName());
  }

}
