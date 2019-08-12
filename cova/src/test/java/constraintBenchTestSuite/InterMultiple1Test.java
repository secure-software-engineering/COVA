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
public class InterMultiple1Test extends ConstraintBenchTestFramework {

  public InterMultiple1Test() {
    targetTestClassName = "constraintBench.test.interProcedural.InterMultiple1";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    // A ^ B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // !(A ^ B)
    BoolExpr expected2 = SMTSolverZ3.getInstance().negate(expected1, false);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(17)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(24)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(26)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(negatedA, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(19));
  }
}
