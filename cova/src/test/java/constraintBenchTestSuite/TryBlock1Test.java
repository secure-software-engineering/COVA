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
public class TryBlock1Test extends ConstraintBenchTestFramework {

  public TryBlock1Test() {
    targetTestClassName = "constraintBench.test.tryBlock.TryBlock1";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(19));
  }

}
