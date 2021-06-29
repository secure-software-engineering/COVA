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
package constraintBenchTestSuite.mixed;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import org.junit.Assert;
import org.junit.Test;
import soot.IntType;
import utils.ConstraintBenchTestFrameworkForAndroidApp;

public class SingleIfBranchTest extends ConstraintBenchTestFrameworkForAndroidApp {

  public SingleIfBranchTest() {
    targetTestAppName = "SingleIfBranch1";
  }

  @Test
  public void testSingleIfBranch1() {
    ConstraintZ3 constraintOfLeak =
        (ConstraintZ3)
            results.getConstraint(
                22,
                "de.upb.swt.singleifbranch1.MainActivity",
                27,
                "de.upb.swt.singleifbranch1.MainActivity");
    BoolExpr model = SMTSolverZ3.getInstance().makeBoolTerm("im(C13)_0", false);
    BoolExpr sdk =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr("C24", false, "15", true, IntType.v(), Operator.GE);
    BoolExpr negatedSdk = SMTSolverZ3.getInstance().negate(sdk, false);
    BoolExpr expected = SMTSolverZ3.getInstance().solve(model, negatedSdk, Operator.AND, false);
    BoolExpr actual = constraintOfLeak.getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    constraintOfLeak =
        (ConstraintZ3)
            results.getConstraint(
                22,
                "de.upb.swt.singleifbranch1.MainActivity",
                32,
                "de.upb.swt.singleifbranch1.MainActivity");
    expected = SMTSolverZ3.getInstance().solve(model, sdk, Operator.AND, false);
    actual = constraintOfLeak.getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
