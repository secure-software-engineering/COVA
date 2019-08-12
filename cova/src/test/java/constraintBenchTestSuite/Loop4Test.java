package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class Loop4Test extends ConstraintBenchTestFramework {

  public Loop4Test() {
    targetTestClassName = "constraintBench.test.loops.Loop4";
  }

  @Test
  public void test() {
    BoolExpr termD = SMTSolverZ3.getInstance().makeBoolTerm("im(" + D + ")", false);
    BoolExpr negatedD = SMTSolverZ3.getInstance().negate(termD, false);
    boolean case1 = compare(termD, negatedD);
    boolean case2 = compare(negatedD, termD);
    Assert.assertTrue(case1 || case2);

  }

  private boolean compare(BoolExpr termD, BoolExpr negatedD) {
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termD, actual);
    boolean res = equivalent;
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(negatedD, actual);
    res = res && equivalent;
    return res;
  }
}
