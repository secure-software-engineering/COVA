package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 *
 */
public class BooleanSingle8Test extends ConstraintBenchTestFramework {

  public BooleanSingle8Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanSingle8";
  }

  @Test
  public void test() {
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeBoolTerm(A, false);// A
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeBoolTerm(A, true);// !A

    BoolExpr actual1 = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual1);
    Assert.assertTrue(equivalent);

    BoolExpr actual2 = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual2);
    Assert.assertTrue(equivalent);
  }
}
