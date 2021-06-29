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
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

/** */
public class ImpreciseMultiple10Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple10Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple10";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(P);
    sb.append("+");
    sb.append(Q);
    sb.append(")_0");
    String imPQ = sb.toString();
    // !im(P+Q)_0
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeBoolTerm(imPQ, true);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // A
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
