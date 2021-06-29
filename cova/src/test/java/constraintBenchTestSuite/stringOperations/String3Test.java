package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import cova.rules.StringMethod;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String3Test extends ConstraintBenchTestFramework {
  public String3Test() {
    targetTestClassName = "constraintBench.test.stringOperations.String3";
  }

  @Test
  public void test() {
    // !(str.len(FA)>=8)
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeCompareTerm(FA, 8, Operator.GE, true, StringMethod.LENGTH);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    // (str.len(FA)>=8)
    expected =
        SMTSolverZ3.getInstance().makeCompareTerm(FA, 8, Operator.GE, false, StringMethod.LENGTH);
    actual = ((ConstraintZ3) results.get(12)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
