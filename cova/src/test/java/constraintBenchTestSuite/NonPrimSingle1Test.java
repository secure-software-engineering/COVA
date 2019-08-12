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
public class NonPrimSingle1Test extends ConstraintBenchTestFramework {

  public NonPrimSingle1Test() {
    targetTestClassName = "constraintBench.test.nonPrimTypes.NonPrimSingle1";
  }

  @Test
  public void test() {
    // FA = null
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(FA, false, "null", false,
        RefType.v("java.lang.String"), Operator.EQ);
    // FA !=null
    BoolExpr expected2 = SMTSolverZ3.getInstance().negate(expected1, false);

    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(18));

  }
}
