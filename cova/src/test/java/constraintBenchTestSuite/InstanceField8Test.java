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
public class InstanceField8Test extends ConstraintBenchTestFramework {

  public InstanceField8Test() {
    targetTestClassName = "constraintBench.test.instanceField.InstanceField8";
  }

  @Test
  public void test() {
    BoolExpr expected = SMTSolverZ3.getInstance().makeNonTerminalExpr(FA, false, FB, false,
        RefType.v("java.lang.String"), Operator.EQ);
    BoolExpr actual = ((ConstraintZ3) results.get(37)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
