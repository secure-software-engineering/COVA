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
public class ImpreciseSingle1Test extends ConstraintBenchTestFramework {

  public ImpreciseSingle1Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseSingle1";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA = sb.toString();
    // im(FA)_0
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
