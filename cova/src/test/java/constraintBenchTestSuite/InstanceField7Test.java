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
public class InstanceField7Test extends ConstraintBenchTestFramework {

  public InstanceField7Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField7";
  }

  @Test
  public void test() {
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr actual = ((ConstraintZ3) results.get(30)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termB, actual);
    Assert.assertTrue(equivalent);
  }
}
