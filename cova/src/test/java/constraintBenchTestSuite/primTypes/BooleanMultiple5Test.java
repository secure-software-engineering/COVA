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
package constraintBenchTestSuite.primTypes;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class BooleanMultiple5Test extends ConstraintBenchTestFramework {

  public BooleanMultiple5Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple5";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr termC = SMTSolverZ3.getInstance().makeBoolTerm(C, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    BoolExpr negatedC = SMTSolverZ3.getInstance().negate(termC, false);
    // !A ^ !B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedA, negatedB, Operator.AND, false);
    // A v B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.OR, false);
    // ( A v B ) ^ C
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(termC, expected2, Operator.AND, false);
    // ( A v B ) ^ !C
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(negatedC, expected2, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(14)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
  }
}
