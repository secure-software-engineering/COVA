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
public class ImpreciseMultiple3Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple3Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple3";
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
    sb = new StringBuilder("im(");
    sb.append(FC);
    sb.append(")_0");
    String imFC = sb.toString();
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    BoolExpr negatedFA = SMTSolverZ3.getInstance().negate(termFA, false);
    BoolExpr termFB = SMTSolverZ3.getInstance().makeBoolTerm(imFB, false);
    BoolExpr negatedFB = SMTSolverZ3.getInstance().negate(termFB, false);
    BoolExpr termFC = SMTSolverZ3.getInstance().makeBoolTerm(imFC, false);
    BoolExpr negatedFC = SMTSolverZ3.getInstance().negate(termFC, false);
    // !im(FA) ∧ !im(FB)
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedFA, negatedFB, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // (im(FA) ∨ im(FB)) ∧ im(FC)
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.OR, false);
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(expected2, termFC, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    // (im(FA) ∨ im(FB)) ∧ !im(FC)
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(expected2, negatedFC, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
  }
}
