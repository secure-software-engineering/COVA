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
import soot.IntType;
import utils.ConstraintBenchTestFramework;

/** */
public class ImpreciseMultiple6Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple6Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple6";
  }

  @Test
  public void test() {
    // D > 8
    BoolExpr expected1 =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(D, false, "8", true, IntType.v(), Operator.GT);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    // (D > 8) ^  (str.prefixof("http:",FA) v str.prefixof("http:",FB))
    BoolExpr termFA =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "http:", StringMethod.STARTSWITH);
    BoolExpr termFB =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FB, "http:", StringMethod.STARTSWITH);
    BoolExpr orTerm = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.OR, false);
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(expected1, orTerm, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
