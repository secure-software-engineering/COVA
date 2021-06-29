package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String2Test extends ConstraintBenchTestFramework {
  public String2Test() {
    targetTestClassName = "constraintBench.test.stringOperations.String2";
  }

  @Test
  public void test() {
    if (!failImpreciseTests) return;
    // FA = FB
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeVarStrTermWithTwoVaraibles(FA, FB, StringMethod.EQUALS);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
