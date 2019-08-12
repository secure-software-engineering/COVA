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
public class InterSingle1Test extends ConstraintBenchTestFramework {

  public InterSingle1Test() {
    targetTestClassName = "constraintBench.test.interProcedural.InterSingle1";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(17));

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(negatedA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(21));
    Assert.assertTrue(!results.containsKey(25));
  }
}
