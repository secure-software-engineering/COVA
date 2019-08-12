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
public class ImpreciseMultiple10Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple10Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple10";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(P);
    sb.append("+");
    sb.append(Q);
    sb.append(")_0");
    String imPQ = sb.toString();
    // !im(P+Q)_0
    BoolExpr expected1 = SMTSolverZ3.getInstance().makeBoolTerm(imPQ, true);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // A
    BoolExpr expected2 = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    actual = ((ConstraintZ3) results.get(19)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
