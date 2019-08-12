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
public class InterSingle7Test extends ConstraintBenchTestFramework {

  public InterSingle7Test() {
    targetTestClassName = "constraintBench.test.interProcedural.InterSingle7";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_1");
    String imFA = sb.toString();
    // im(FA)
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(imFA, false);
    BoolExpr actual = ((ConstraintZ3) results.get(20)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
