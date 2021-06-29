package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String7Test extends ConstraintBenchTestFramework {
  public String7Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String7";
  }

  @Test
  public void test() {
    // str.contains(FA, "abc")
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "abc", StringMethod.CONTAINS);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    // !str.contains(FA, "abc")
    expected = SMTSolverZ3.getInstance().negate(expected, false);
    actual = ((ConstraintZ3) results.get(12)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
