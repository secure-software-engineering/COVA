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
public class ImpreciseMultiple9Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple9Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple9";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append("+");
    sb.append(FB);
    sb.append(")_0");
    String imFAFB = sb.toString();
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(imFAFB, false);
    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
