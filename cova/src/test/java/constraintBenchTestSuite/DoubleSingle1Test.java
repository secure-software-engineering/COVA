package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 * 
 */
public class DoubleSingle1Test extends ConstraintBenchTestFramework {

  public DoubleSingle1Test() {
    targetTestClassName = "constraintBench.test.primTypes.DoubleSingle1";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(K);
    sb.append(")_0");
    String imK = sb.toString();
    // D = im(K)_0
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(imK, true);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }

}
