package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String5Test extends ConstraintBenchTestFramework {
  public String5Test() {
    targetTestClassName = "constraintBench.test.stringOperations.String5";
  }

  @Test
  public void test() {
    // str.suffixof("abc", FA)
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "abc", StringMethod.ENDSWITH);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    // !str.suffixof("abc", FA)
    expected = SMTSolverZ3.getInstance().negate(expected, false);
    actual = ((ConstraintZ3) results.get(12)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
