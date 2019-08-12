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
public class IndirectConcrete4Test extends ConstraintBenchTestFramework {

  public IndirectConcrete4Test() {
    targetTestClassName = "constraintBench.test.indirect.IndirectConcrete4";
  }

  @Test
  public void test() {
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(A, false);// A

    BoolExpr actual1 = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual1);
    Assert.assertTrue(equivalent);

    BoolExpr actual2 = ((ConstraintZ3) results.get(22)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual2);
    Assert.assertTrue(equivalent);
  }
}
