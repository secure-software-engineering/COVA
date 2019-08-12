package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 * 
 */
public class BooleanMultiple1Test extends ConstraintBenchTestFramework {

  public BooleanMultiple1Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple1";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // A ^ B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // A ^ !B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termA, negatedB, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(14)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(35));
  }

}
