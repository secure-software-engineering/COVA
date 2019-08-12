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
public class BooleanMultiple3Test extends ConstraintBenchTestFramework {

  public BooleanMultiple3Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple3";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // B v !A
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termB, negatedA, Operator.OR, false);
    // !B v !A
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(negatedB, negatedA, Operator.OR, false);

    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(21));
  }
}
