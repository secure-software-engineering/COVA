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
public class ImpreciseMultiple5Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple5Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple5";
  }

  @Test
  public void test() {
    // D > 8
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "8", true, IntType.v(), Operator.GT);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA = sb.toString();
    BoolExpr termFA = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    // D > 8 ^ im(FA)
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(expected1, termFA, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
