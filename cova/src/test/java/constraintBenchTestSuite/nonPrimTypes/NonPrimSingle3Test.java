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
public class NonPrimSingle3Test extends ConstraintBenchTestFramework {

  public NonPrimSingle3Test() {
    targetTestClassName = "constraintBench.test.nonPrimTypes.NonPrimSingle3";
  }

  @Test
  public void test() {
    BoolExpr termP =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                P, false, "null", false, RefType.v("constraintBench.utils.Property"), Operator.EQ);
    BoolExpr termQ =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                Q, false, "null", false, RefType.v("constraintBench.utils.Property"), Operator.EQ);
    // P !=null
    BoolExpr expected1 = SMTSolverZ3.getInstance().negate(termP, false);
    // Q !=null
    BoolExpr expected2 = SMTSolverZ3.getInstance().negate(termQ, false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(23)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(26)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
  }
}
