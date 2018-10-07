package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;
import de.upb.swt.cova.data.Operator;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.RefType;

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
