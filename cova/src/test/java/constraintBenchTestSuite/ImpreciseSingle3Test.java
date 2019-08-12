package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.RefType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class ImpreciseSingle3Test extends ConstraintBenchTestFramework {

  public ImpreciseSingle3Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseSingle3";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA = sb.toString();
    // FA = null
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(FA, false, "null", false,
        RefType.v("java.lang.String"), Operator.EQ);
    // !(FA = null)
    BoolExpr negation1 = SMTSolverZ3.getInstance().negate(expected1, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(negation1, actual);
    Assert.assertTrue(equivalent);
    // im(FA)_0
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    BoolExpr negatedFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, true);
    // !(FA=null) ^ im(FA)_0
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(negation1, termFA, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(14)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
    // !(FA=null) ^ !im(FA)_0
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(negation1, negatedFA, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);
    // (FA=null) V im(FA)_0
    BoolExpr expected4 = SMTSolverZ3.getInstance().solve(expected1, termFA, Operator.OR, false);
    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected4, actual);
    Assert.assertTrue(equivalent);
  }
}
