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
public class PrimitiveFieldRef2Test extends ConstraintBenchTestFramework {

  public PrimitiveFieldRef2Test() {
    targetTestClassName = "constraintBench.test.instanceField.PrimitiveFieldRef2";
  }

  @Test
  public void test() {
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(A, false);// A
    BoolExpr actual = ((ConstraintZ3) results.get(28)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(32)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    Assert.assertFalse(results.containsKey(36));

    actual = ((ConstraintZ3) results.get(39)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

  }
}
