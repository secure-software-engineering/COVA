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
package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import soot.RefType;
import utils.ConstraintBenchTestFramework;

/** */
public class LookUpSwitchStmt1Test extends ConstraintBenchTestFramework {

  public LookUpSwitchStmt1Test() {
    targetTestClassName = "constraintBench.test.switchStmts.LookUpSwitchStmt1";
  }

  @Test
  public void test() {
    // FA = A
    BoolExpr expected1 =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                FA, false, "A", true, RefType.v(String.class.getName()), Operator.EQ);
    BoolExpr negate1 = SMTSolverZ3.getInstance().negate(expected1, false);
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // FA = B
    BoolExpr expected2 =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                FA, false, "B", true, RefType.v(String.class.getName()), Operator.EQ);
    BoolExpr negate2 = SMTSolverZ3.getInstance().negate(expected2, false);
    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    // !(FA = A) ^ !(FA = B)
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(negate1, negate2, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(21)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    Assert.assertFalse(results.containsKey(24));
  }
}
