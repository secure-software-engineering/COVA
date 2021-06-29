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
public class ImpreciseMultiple11Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple11Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple11";
  }

  @Test
  public void test() {
    BoolExpr termFA =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "a", StringMethod.STARTSWITH);
    // str.prefixof("b", FB)
    BoolExpr termFB =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FB, "b", StringMethod.STARTSWITH);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termFB, actual);
    Assert.assertTrue(equivalent);

    BoolExpr termFC =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FC, "c", StringMethod.EQUALS);
    BoolExpr andTerm1 = SMTSolverZ3.getInstance().solve(termFB, termFC, Operator.AND, false);
    // (str.prefixof("a", FA) v (str.prefixof("b", FB) ^ ("c" = FC)))
    BoolExpr expected = SMTSolverZ3.getInstance().solve(termFA, andTerm1, Operator.OR, false);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
