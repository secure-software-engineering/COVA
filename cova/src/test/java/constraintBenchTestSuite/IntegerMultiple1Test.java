package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.IntType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class IntegerMultiple1Test extends ConstraintBenchTestFramework {

  public IntegerMultiple1Test() {
    targetTestClassName = "constraintBench.test.primTypes.IntegerMultiple1";
  }

  @Test
  public void test() {
    // D = 5
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "5", true,
        IntType.v(), Operator.EQ);
    // F = 6
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeNonTerminalExpr(F, false, "6", true,
        IntType.v(), Operator.EQ);
    // D = 5 ^ F = 6
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(expected1, expected2, Operator.AND, false);
    BoolExpr FALSE = SMTSolverZ3.getInstance().getFalse();
    
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(FALSE, actual);
    Assert.assertTrue(equivalent);
  }
}
