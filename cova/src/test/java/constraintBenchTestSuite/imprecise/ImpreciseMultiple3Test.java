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
public class ImpreciseMultiple3Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple3Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple3";
  }

  @Test
  public void test() {
    BoolExpr termFA =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "FA", StringMethod.STARTSWITH);
    BoolExpr negatedFA = SMTSolverZ3.getInstance().negate(termFA, false);
    BoolExpr termFB =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FB, "B", StringMethod.STARTSWITH);
    BoolExpr negatedFB = SMTSolverZ3.getInstance().negate(termFB, false);
    BoolExpr termFC =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FC, "C", StringMethod.EQUALS);
    BoolExpr negatedFC = SMTSolverZ3.getInstance().negate(termFC, false);
    // !str.prefixof("FA", FA) ∧ !str.prefixof("B", FB)
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedFA, negatedFB, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // (str.prefixof("FA", FA) ∨ str.prefixof("B", FB)) ∧ "C" = FC
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.OR, false);
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(expected2, termFC, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    // (str.prefixof("FA", FA) ∨ str.prefixof("B", FB)) ∧ !("C" = FC)
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(expected2, negatedFC, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
  }
}
