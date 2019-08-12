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
public class IndirectConcrete1Test extends ConstraintBenchTestFramework {

  public IndirectConcrete1Test() {
    targetTestClassName = "constraintBench.test.indirect.IndirectConcrete1";
  }

  @Test
  public void test() {
    // D = 2
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "2", true,
        IntType.v(), Operator.EQ);
    BoolExpr negate1 = SMTSolverZ3.getInstance().negate(expected1, false);
    // D = 3
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "3", true,
        IntType.v(), Operator.EQ);
    BoolExpr negate2 = SMTSolverZ3.getInstance().negate(expected2, false);
    // (D = 2) v (D = 3)
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(expected2, expected1, Operator.OR, false);
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(negate1, negate2, Operator.AND, false);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(22)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(24)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(27)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    // Assert.assertTrue(!results.containsKey(45));
  }
}
