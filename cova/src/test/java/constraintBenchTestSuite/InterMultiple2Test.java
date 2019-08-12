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
public class InterMultiple2Test extends ConstraintBenchTestFramework {

  public InterMultiple2Test() {
    targetTestClassName = "constraintBench.test.interProcedural.InterMultiple2";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr termC = SMTSolverZ3.getInstance().makeBoolTerm(C, false);
    // B ^ C
    BoolExpr expected = SMTSolverZ3.getInstance().solve(termB, termC, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(18)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(23)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(31)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termB, actual);
    Assert.assertTrue(equivalent);
  }
}
