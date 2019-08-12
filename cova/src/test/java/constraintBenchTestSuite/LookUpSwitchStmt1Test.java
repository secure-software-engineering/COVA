package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.RefType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class LookUpSwitchStmt1Test extends ConstraintBenchTestFramework {

  public LookUpSwitchStmt1Test() {
    targetTestClassName = "constraintBench.test.switchStmts.LookUpSwitchStmt1";
  }

  @Test
  public void test() {
    // FA = A
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(FA, false, "A", true,
        RefType.v(String.class.getName()), Operator.EQ);
    BoolExpr negate1 = SMTSolverZ3.getInstance().negate(expected1, false);
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // FA = B
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeNonTerminalExpr(FA, false, "B", true,
        RefType.v(String.class.getName()), Operator.EQ);
    BoolExpr negate2 = SMTSolverZ3.getInstance().negate(expected2, false);
    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    // !(FA = A) ^ !(FA = B)
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(negate1, negate2, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(21)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    Assert.assertFalse(results.containsKey(24));
  }
}
