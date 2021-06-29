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
package constraintBenchTestSuite.loops;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class Loop4Test extends ConstraintBenchTestFramework {

  public Loop4Test() {
    targetTestClassName = "constraintBench.test.loops.Loop4";
  }

  @Test
  public void test() {
    BoolExpr termD = SMTSolverZ3.getInstance().makeBoolTerm("im(" + D + ")", false);
    BoolExpr negatedD = SMTSolverZ3.getInstance().negate(termD, false);
    boolean case1 = compare(termD, negatedD);
    boolean case2 = compare(negatedD, termD);
    Assert.assertTrue(case1 || case2);
  }

  private boolean compare(BoolExpr termD, BoolExpr negatedD) {
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termD, actual);
    boolean res = equivalent;
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(negatedD, actual);
    res = res && equivalent;
    return res;
  }
}
