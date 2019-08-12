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
 */
public class StaticField3Test extends ConstraintBenchTestFramework {

  public StaticField3Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField3";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr termC = SMTSolverZ3.getInstance().makeBoolTerm(C, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // A ^ B
    BoolExpr and = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // (A ^ B) v !B)
    BoolExpr expected = SMTSolverZ3.getInstance().solve(and, negatedB, Operator.OR, false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(20)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termC, actual);
    Assert.assertTrue(equivalent);
  }
}
