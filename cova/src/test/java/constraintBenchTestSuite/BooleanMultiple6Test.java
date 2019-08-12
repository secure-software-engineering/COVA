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
public class BooleanMultiple6Test extends ConstraintBenchTestFramework {

  public BooleanMultiple6Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple6";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr termC = SMTSolverZ3.getInstance().makeBoolTerm(C, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    BoolExpr negatedC = SMTSolverZ3.getInstance().negate(termC, false);
    // !A ^ C
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedA, termC, Operator.AND, false);
    // A ^ B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // !A ^ !C
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(negatedA, negatedC, Operator.AND, false);
    // A ^ !B
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(termA, negatedB, Operator.AND, false);
    // (!A ^ C) ∨ ( A ^ B)
    BoolExpr expected5 = SMTSolverZ3.getInstance().solve(expected1, expected2, Operator.OR, false);
    // (!A ^ !C) ∨ (A ^ !B)
    BoolExpr expected6 = SMTSolverZ3.getInstance().solve(expected3, expected4, Operator.OR, false);

    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected5, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected6, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(21));
  }
}
