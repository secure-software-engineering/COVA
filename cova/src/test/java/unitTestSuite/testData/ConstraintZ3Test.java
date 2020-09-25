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
package unitTestSuite.testData;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import utils.UnitTestFramework;

public class ConstraintZ3Test extends UnitTestFramework {

  @Test
  public void test01() throws Exception {
    BoolExpr e1 = SMTSolverZ3.getInstance().makeBoolTerm("A", false);
    BoolExpr e2 = SMTSolverZ3.getInstance().makeBoolTerm("A", true);
    BoolExpr e3 = SMTSolverZ3.getInstance().solve(e1, e2, Operator.OR, false);
    Assert.assertTrue(SMTSolverZ3.getInstance().prove(e3, SMTSolverZ3.getInstance().getTrue()));
  }
}
