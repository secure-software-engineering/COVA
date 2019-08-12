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
public class BooleanMultiple2Test extends ConstraintBenchTestFramework {

  public BooleanMultiple2Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple2";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // A v B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.OR, false);
    // !A ^ !B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(negatedA, negatedB, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    
    Assert.assertTrue(!results.containsKey(18));
  }
}
