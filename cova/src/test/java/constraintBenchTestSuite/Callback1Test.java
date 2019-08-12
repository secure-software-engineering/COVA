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
 * 
 */
public class Callback1Test extends ConstraintBenchTestFramework {

  public Callback1Test() {
    targetTestClassName = "constraintBench.test.callbacks.Callback1";
  }

  @Test
  public void test() {
    BoolExpr termOnClick = SMTSolverZ3.getInstance().makeBoolTerm(onClick + "_0", false);
    BoolExpr termnOnScroll = SMTSolverZ3.getInstance().makeBoolTerm(onScroll + "_0", false);
    BoolExpr expected = SMTSolverZ3.getInstance().solve(termOnClick, termnOnScroll, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termnOnScroll, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(28)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termnOnScroll, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(29)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

  }
}
