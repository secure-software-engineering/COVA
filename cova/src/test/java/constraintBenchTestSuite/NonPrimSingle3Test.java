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
public class NonPrimSingle3Test extends ConstraintBenchTestFramework {

  public NonPrimSingle3Test() {
    targetTestClassName = "constraintBench.test.nonPrimTypes.NonPrimSingle3";
  }

  @Test
  public void test() {
    BoolExpr termP = SMTSolverZ3.getInstance().makeNonTerminalExpr(P, false, "null", false,
        RefType.v("constraintBench.utils.Property"), Operator.EQ);
    BoolExpr termQ = SMTSolverZ3.getInstance().makeNonTerminalExpr(Q, false, "null", false,
        RefType.v("constraintBench.utils.Property"), Operator.EQ);
    // P !=null
    BoolExpr expected1 = SMTSolverZ3.getInstance().negate(termP, false);
    // Q !=null
    BoolExpr expected2 = SMTSolverZ3.getInstance().negate(termQ, false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(23)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(26)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
  }
}
