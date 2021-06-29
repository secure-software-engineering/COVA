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
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class BooleanSingle3Test extends ConstraintBenchTestFramework {

  public BooleanSingle3Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanSingle3";
  }

  @Test
  public void test() {
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeBoolTerm(A, false); // A
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeBoolTerm(A, true); // !A

    BoolExpr actual1 = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual1);
    Assert.assertTrue(equivalent);

    BoolExpr actual2 = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual2);
    Assert.assertTrue(equivalent);
  }
}
