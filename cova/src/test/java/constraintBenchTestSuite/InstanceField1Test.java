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
public class InstanceField1Test extends ConstraintBenchTestFramework {

  public InstanceField1Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField1";
  }

  @Test
  public void test() {
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr actual = ((ConstraintZ3) results.get(20)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termB, actual);
    Assert.assertTrue(equivalent);
  }
}
