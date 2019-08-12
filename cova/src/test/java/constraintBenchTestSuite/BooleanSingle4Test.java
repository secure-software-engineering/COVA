package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 * 
 */
public class BooleanSingle4Test extends ConstraintBenchTestFramework {

  public BooleanSingle4Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanSingle4";
  }

  @Test
  public void test() {
    BoolExpr FALSE = SMTSolverZ3.getInstance().getFalse();// false
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(FALSE, actual);
    Assert.assertTrue(equivalent);
  }
}
