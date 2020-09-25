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
import utils.ConstraintBenchTestFramework;

/** */
public class ImpreciseMultiple8Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple8Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple8";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA0 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_0");
    String imFB0 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_1");
    String imFA1 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_1");
    String imFB1 = sb.toString();
    // im(FA)_0
    BoolExpr termFA0 = SMTSolverZ3.getInstance().makeBoolTerm(imFA0, false);
    // im(FB)_0
    BoolExpr termFB0 = SMTSolverZ3.getInstance().makeBoolTerm(imFB0, false);
    // im(FA)_1
    BoolExpr termFA1 = SMTSolverZ3.getInstance().makeBoolTerm(imFA1, false);
    // im(FB)_1
    BoolExpr termFB1 = SMTSolverZ3.getInstance().makeBoolTerm(imFB1, false);
    // im(FA)_0 ^ im(FB)_0
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termFA0, termFB0, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // im(FA)_1 v im(FB)_1
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA1, termFB1, Operator.OR, false);
    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    Assert.assertTrue(!results.containsKey(19));
  }
}
