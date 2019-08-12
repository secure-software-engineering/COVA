package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.IntType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class ImpreciseMultiple4Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple4Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple4";
  }

  @Test
  public void test() {
    // D > 8
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "8", true, IntType.v(), Operator.GT);
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append("+");
    sb.append(FB);
    sb.append(")_0");
    String imFAFB = sb.toString();
    BoolExpr negated = SMTSolverZ3.getInstance().makeBoolTerm(imFAFB, true);
    // D>8 ^ !im(FA+FB)_0
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(expected1, negated, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
