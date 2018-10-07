package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;
import de.upb.swt.cova.data.Operator;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class StaticField4Test extends ConstraintBenchTestFramework {

  public StaticField4Test() {
    targetTestClassName = "constraintBench.test.staticField.StaticField4";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    // A ^ B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(25)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
  }
}
