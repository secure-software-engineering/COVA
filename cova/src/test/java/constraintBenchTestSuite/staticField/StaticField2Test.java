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
package constraintBenchTestSuite.staticField;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class StaticField2Test extends ConstraintBenchTestFramework {

  public StaticField2Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField2";
  }

  @Test
  public void test() {
    if (!failImpreciseTests) return;
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // A ^ B
    BoolExpr and = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // (A ^ B) v !B)
    BoolExpr expected = SMTSolverZ3.getInstance().solve(and, negatedB, Operator.OR, false);
    BoolExpr actual = ((ConstraintZ3) results.get(21)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
