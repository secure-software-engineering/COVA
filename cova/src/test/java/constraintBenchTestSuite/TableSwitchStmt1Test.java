package constraintBenchTestSuite;
import com.microsoft.z3.BoolExpr;

import java.util.ArrayList;
import java.util.List;

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
public class TableSwitchStmt1Test extends ConstraintBenchTestFramework {

  public TableSwitchStmt1Test() {
    targetTestClassName = "constraintBench.test.switchStmts.TableSwitchStmt1";
  }

  @Test
  public void test() {
    // D = 2
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "2", true,
        IntType.v(), Operator.EQ);
    BoolExpr negate1 = SMTSolverZ3.getInstance().negate(expected1, false);
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // D = 4
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "4", true,
        IntType.v(), Operator.EQ);
    BoolExpr negate2 = SMTSolverZ3.getInstance().negate(expected2, false);
    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    // D = 8
    BoolExpr expected3 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "8", true,
        IntType.v(), Operator.EQ);
    BoolExpr negate3 = SMTSolverZ3.getInstance().negate(expected3, false);
    actual = ((ConstraintZ3) results.get(21)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    // !(D = 2)^!(D = 4)^!(D = 8)
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(negate1);
    exprs.add(negate2);
    exprs.add(negate3);
    BoolExpr expected4 = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    actual = ((ConstraintZ3) results.get(24)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
    Assert.assertFalse(results.containsKey(27));
  }
}
