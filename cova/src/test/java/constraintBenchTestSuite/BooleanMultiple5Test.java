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
public class BooleanMultiple5Test extends ConstraintBenchTestFramework {

  public BooleanMultiple5Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple5";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr termC = SMTSolverZ3.getInstance().makeBoolTerm(C, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    BoolExpr negatedC = SMTSolverZ3.getInstance().negate(termC, false);
    // !A ^ !B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedA, negatedB, Operator.AND, false);
    // A v B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.OR, false);
    // ( A v B ) ^ C
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(termC, expected2, Operator.AND, false);
    // ( A v B ) ^ !C
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(negatedC, expected2, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(14)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
  }
}
