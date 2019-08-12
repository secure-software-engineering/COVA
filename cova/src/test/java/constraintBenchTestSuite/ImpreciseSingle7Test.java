package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class ImpreciseSingle7Test extends ConstraintBenchTestFramework {

  public ImpreciseSingle7Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseSingle7";
  }

  @Test
  public void test() {
    // im(FA)
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm("im(" + FA + ")_1", false);
    BoolExpr actual = ((ConstraintZ3) results.get(17)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
