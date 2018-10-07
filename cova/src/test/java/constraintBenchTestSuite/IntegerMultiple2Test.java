package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;
import de.upb.swt.cova.data.Operator;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.IntType;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class IntegerMultiple2Test extends ConstraintBenchTestFramework {

  public IntegerMultiple2Test() {
    targetTestClassName = "constraintBench.test.primTypes.IntegerMultiple2";
  }

  @Test
  public void test() {
    // D < F
    BoolExpr expected = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, F, false,
        IntType.v(),
        Operator.LT);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
