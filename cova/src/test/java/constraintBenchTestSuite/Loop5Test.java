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
