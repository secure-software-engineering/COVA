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
package constraintBenchTestSuite.array;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Ignore;
import soot.IntType;
import utils.ConstraintBenchTestFramework;

/** */
public class Array1Test extends ConstraintBenchTestFramework {

  public Array1Test() {
    targetTestClassName = "constraintBench.test.array.Array1";
  }

  @Ignore
  public void test() {
    BoolExpr termD =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(D, false, "0", true, IntType.v(), Operator.LE);
    BoolExpr expected = SMTSolverZ3.getInstance().negate(termD, false);
    BoolExpr actual = ((ConstraintZ3) results.get(17)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
