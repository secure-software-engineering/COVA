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
public class ImpreciseMultiple6Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple6Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple6";
  }

  @Test
  public void test() {
    // D > 8
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "8", true,
        IntType.v(), Operator.GT);
    BoolExpr actual = ((ConstraintZ3) results.get(14)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    // (D > 8) ^ (im(FA)_0 v im(FB)_0)
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
    BoolExpr orTerm = SMTSolverZ3.getInstance().solve(termFA, termFB, Operator.OR, false);
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(expected1, orTerm, Operator.AND, false);
    actual = ((ConstraintZ3) results.get(16)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
