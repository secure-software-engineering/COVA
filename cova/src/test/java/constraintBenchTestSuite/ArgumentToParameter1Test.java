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
import soot.IntType;
import utils.ConstraintBenchTestFramework;

/** */
public class ArgumentToParameter1Test extends ConstraintBenchTestFramework {

  public ArgumentToParameter1Test() {
    targetTestClassName = "constraintBench.test.interProcedural.ArgumentToParameter1";
  }

  @Test
  public void test() {
    // D>20
    BoolExpr expected =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(D, false, "20", true, IntType.v(), Operator.GT);
    BoolExpr actual = ((ConstraintZ3) results.get(58)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    Assert.assertFalse(results.containsKey(51));
    Assert.assertFalse(results.containsKey(63));
  }
}
