package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class ImpreciseMultiple2Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple2Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple2";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_0");
    String imFB = sb.toString();
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    BoolExpr termFB = SMTSolverZ3.getInstance().makeBoolTerm(imFB, false);
    // im(FA)_0 ^ im(FB)_0
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
  }
}
