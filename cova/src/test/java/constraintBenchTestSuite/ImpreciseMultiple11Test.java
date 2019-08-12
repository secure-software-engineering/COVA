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
public class ImpreciseMultiple11Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple11Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple11";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_0");
    String imFB = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FC);
    sb.append(")_0");
    String imFC = sb.toString();
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    // im(FB)_0
    BoolExpr termFB = SMTSolverZ3.getInstance().makeBoolTerm(imFB, false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termFB, actual);
    Assert.assertTrue(equivalent);

    BoolExpr negatedFB = SMTSolverZ3.getInstance().negate(termFB, false);
    BoolExpr termFC = SMTSolverZ3.getInstance().makeBoolTerm(imFC, false);
    // (im(FB)_0 ^ im(FC)_0)
    BoolExpr andTerm1 = SMTSolverZ3.getInstance().solve(termFB, termFC, Operator.AND, false);
    // (!im(FB)_0 ^ im(FA)_0)
    BoolExpr andTerm2 = SMTSolverZ3.getInstance().solve(negatedFB, termFA, Operator.AND, false);
    // (im(FB)_0 ^ im(FC)_0) v (!im(FB)_0 ^ im(FA)_0)
    BoolExpr expected = SMTSolverZ3.getInstance().solve(andTerm1, andTerm2, Operator.OR, false);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
