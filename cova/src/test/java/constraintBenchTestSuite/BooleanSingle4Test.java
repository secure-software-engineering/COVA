package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

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
