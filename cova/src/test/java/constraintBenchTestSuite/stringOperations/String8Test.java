package constraintBenchTestSuite.stringOperations;

import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import cova.rules.StringMethod;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;
import utils.ConstraintBenchTestFramework;

public class String8Test extends ConstraintBenchTestFramework {
  public String8Test() {
    targetTestClassName = "constraintBench.test.stringOperations.String8";
  }

  @Test
  public void test() {
    BoolExpr term1 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, "@", StringMethod.CONTAINS);
    BoolExpr term2 =
        SMTSolverZ3.getInstance().makeCompareTerm(FA, 10, Operator.LE, true, StringMethod.LENGTH);
    BoolExpr term3 =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(FA, ".com", StringMethod.ENDSWITH);
    // (str.contains(FA, "@") ^ !(str.len(FA)<=10) ^ str.suffixof(".com", FA))
    BoolExpr expected =
        SMTSolverZ3.getInstance().makeConjunction(Arrays.asList(term1, term2, term3), false);
    BoolExpr actual = ((ConstraintZ3) results.get(10)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
