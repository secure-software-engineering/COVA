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
public class ImpreciseMultiple8Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple8Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple8";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA0 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_0");
    String imFB0 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_1");
    String imFA1 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FB);
    sb.append(")_1");
    String imFB1 = sb.toString();
    // im(FA)_0
    BoolExpr termFA0 = SMTSolverZ3.getInstance().makeBoolTerm(imFA0, false);
    // im(FB)_0
    BoolExpr termFB0 = SMTSolverZ3.getInstance().makeBoolTerm(imFB0, false);
    // im(FA)_1
    BoolExpr termFA1 = SMTSolverZ3.getInstance().makeBoolTerm(imFA1, false);
    // im(FB)_1
    BoolExpr termFB1 = SMTSolverZ3.getInstance().makeBoolTerm(imFB1, false);
    // im(FA)_0 ^ im(FB)_0
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termFA0, termFB0, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // im(FA)_1 v im(FB)_1
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA1, termFB1, Operator.OR, false);
    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    Assert.assertTrue(!results.containsKey(19));
  }
}
