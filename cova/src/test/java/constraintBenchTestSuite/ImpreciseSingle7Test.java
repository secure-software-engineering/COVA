package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

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
