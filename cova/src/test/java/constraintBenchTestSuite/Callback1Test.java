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
public class Callback1Test extends ConstraintBenchTestFramework {

  public Callback1Test() {
    targetTestClassName = "constraintBench.test.callbacks.Callback1";
  }

  @Test
  public void test() {
    BoolExpr termOnClick = SMTSolverZ3.getInstance().makeBoolTerm(onClick + "_0", false);
    BoolExpr termnOnScroll = SMTSolverZ3.getInstance().makeBoolTerm(onScroll + "_0", false);
    BoolExpr expected = SMTSolverZ3.getInstance().solve(termOnClick, termnOnScroll, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termnOnScroll, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(28)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termnOnScroll, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(29)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

  }
}
