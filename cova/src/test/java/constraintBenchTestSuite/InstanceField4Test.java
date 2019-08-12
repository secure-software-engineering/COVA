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
public class InstanceField4Test extends ConstraintBenchTestFramework {

  public InstanceField4Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField4";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    Assert.assertTrue(!results.containsKey(50));
    Assert.assertTrue(!results.containsKey(54));

    BoolExpr actual = ((ConstraintZ3) results.get(44)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(45)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(48));

  }
}
