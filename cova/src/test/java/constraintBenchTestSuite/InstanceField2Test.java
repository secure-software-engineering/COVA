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
public class InstanceField2Test extends ConstraintBenchTestFramework {

  public InstanceField2Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField2";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr actual = ((ConstraintZ3) results.get(21)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);
    Assert.assertTrue(!results.containsKey(24));
    Assert.assertTrue(!results.containsKey(26));
  }
}
