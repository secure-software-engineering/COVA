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
public class ImpreciseSingle2Test extends ConstraintBenchTestFramework {

  public ImpreciseSingle2Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseSingle2";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(F);
    sb.append(")_0");
    String imF = sb.toString();
    // D = im(F)_0
    BoolExpr expected = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, imF, false,
        IntType.v(), Operator.EQ);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
