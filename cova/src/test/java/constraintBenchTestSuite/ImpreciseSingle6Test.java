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
public class ImpreciseSingle6Test extends ConstraintBenchTestFramework {

  public ImpreciseSingle6Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseSingle6";
  }

  @Test
  public void test() {
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm("im(" + D + ")_0", true);
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
