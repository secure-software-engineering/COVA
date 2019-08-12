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
public class StaticField1Test extends ConstraintBenchTestFramework {

  public StaticField1Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField1";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);

    BoolExpr actual = ((ConstraintZ3) results.get(18)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(20));
    Assert.assertTrue(!results.containsKey(21));

    actual = ((ConstraintZ3) results.get(22)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termB, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(27));
  }

}
