/**
 * Copyright (C) 2019 Linghui Luo 
 * 
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as 
 * published by the Free Software Foundation, either version 2.1 of the 
 * License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class Loop5Test extends ConstraintBenchTestFramework {

  public Loop5Test() {
    targetTestClassName = "constraintBench.test.loops.Loop5";
  }

  @Test
  public void test() {
    BoolExpr termD = SMTSolverZ3.getInstance().makeBoolTerm("im(" + D + ")", false);
    BoolExpr negatedD = SMTSolverZ3.getInstance().negate(termD, false);
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    boolean case1 = compare(termD, negatedD, termA);
    boolean case2 = compare(negatedD, termD, termA);
    Assert.assertTrue(case1 || case2);
  }

  private boolean compare(BoolExpr termD, BoolExpr negatedD, BoolExpr termA) {
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termD, actual);
    boolean res = equivalent;

    BoolExpr expected = SMTSolverZ3.getInstance().solve(termD, termA, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    res = res && equivalent;

    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(negatedD, actual);
    res = res && equivalent;
    return res;
  }
}
