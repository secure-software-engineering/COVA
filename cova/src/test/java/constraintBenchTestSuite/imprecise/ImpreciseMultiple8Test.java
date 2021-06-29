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
package constraintBenchTestSuite.imprecise;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class ImpreciseMultiple8Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple8Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple8";
  }

  @Test
  public void test() {
    // str.prefixof("FA", FA)
    BoolExpr termFA0 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "FA", StringMethod.STARTSWITH);
    // str.suffixof("FB", FB)
    BoolExpr termFB0 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FB, "FB", StringMethod.ENDSWITH);
    // str.suffixof("FA", FA)
    BoolExpr termFA1 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "FA", StringMethod.ENDSWITH);
    // str.prefixof("FB", FB)
    BoolExpr termFB1 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FB, "FB", StringMethod.STARTSWITH);
    // str.prefixof("FA", FA) ^ str.suffixof("FB", FB)
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termFA0, termFB0, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // str.suffixof("FA", FA) v str.prefixof("FB", FB)
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA1, termFB1, Operator.OR, false);
    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    Assert.assertTrue(!results.containsKey(19));
  }
}
