package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import utils.ConstraintBenchTestFramework;

/**
 * 
 */
public class StringLiteral1Test extends ConstraintBenchTestFramework {

  public StringLiteral1Test() {
    targetTestClassName = "constraintBench.test.primTypes.StringLiteral1";
  }

  @Test
  public void test() {
    BoolExpr termX = SMTSolverZ3.getInstance().makeBoolTerm("im(" + FA + ")_0", false);
    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termX, actual);
    Assert.assertTrue(equivalent);
  }
}
