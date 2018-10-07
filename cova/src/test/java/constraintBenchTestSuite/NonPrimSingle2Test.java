package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class NonPrimSingle2Test extends ConstraintBenchTestFramework {

  public NonPrimSingle2Test() {
    targetTestClassName = "constraintBench.test.nonPrimTypes.NonPrimSingle2";
  }

  @Test
  public void test() {
    BoolExpr termX = SMTSolverZ3.getInstance().makeBoolTerm("im(" + X + ")_0", false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termX, actual);
    Assert.assertTrue(equivalent);
  }
}
