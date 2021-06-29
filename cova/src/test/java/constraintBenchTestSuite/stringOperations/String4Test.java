package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String4Test extends ConstraintBenchTestFramework {
  public String4Test() {
    targetTestClassName = "constraintBench.test.stringOperations.String4";
  }

  @Test
  public void test() {
    // str.prefixof("abc", FA)
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "abc", StringMethod.STARTSWITH);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    // !str.prefixof("abc", FA)
    expected = SMTSolverZ3.getInstance().negate(expected, false);
    actual = ((ConstraintZ3) results.get(12)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
