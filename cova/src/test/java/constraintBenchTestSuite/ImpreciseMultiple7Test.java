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
public class ImpreciseMultiple7Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple7Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple7";
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
    // im(FA)_0
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    // im(FB)_0
    BoolExpr termFB = SMTSolverZ3.getInstance().makeBoolTerm(imFB, false);
    // im(FA)_0 ^ im(FB)_0
    BoolExpr andTerm = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termFA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(14)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termFA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(andTerm, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termFA, actual);
    Assert.assertTrue(equivalent);
  }
}
