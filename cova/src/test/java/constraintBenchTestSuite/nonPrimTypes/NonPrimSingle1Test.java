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
package constraintBenchTestSuite.nonPrimTypes;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import soot.RefType;
import utils.ConstraintBenchTestFramework;

/** */
public class NonPrimSingle1Test extends ConstraintBenchTestFramework {

  public NonPrimSingle1Test() {
    targetTestClassName = "constraintBench.test.nonPrimTypes.NonPrimSingle1";
  }

  @Test
  public void test() {
    // FA = null
    BoolExpr expected1 =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                FA, false, "null", false, RefType.v("java.lang.String"), Operator.EQ);
    // FA !=null
    BoolExpr expected2 = SMTSolverZ3.getInstance().negate(expected1, false);

    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(18));
  }
}
