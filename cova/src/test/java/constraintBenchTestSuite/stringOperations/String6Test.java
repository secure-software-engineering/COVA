package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String6Test extends ConstraintBenchTestFramework {
  public String6Test() {
    config.setStringTaintCreationRuleOn(true);
    targetTestClassName = "constraintBench.test.stringOperations.String6";
  }

  @Test
  public void test() {
    // !(str.to_int(FA)<=5)
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeCompareTerm(FA, 5, Operator.LE, true, StringMethod.TO_INT);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    // (str.to_int(FA)<=5)
    expected = SMTSolverZ3.getInstance().negate(expected, false);
    actual = ((ConstraintZ3) results.get(12)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
