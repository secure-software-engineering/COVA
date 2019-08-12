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
 *
 */
public class ArgumentToParameter1Test extends ConstraintBenchTestFramework {

  public ArgumentToParameter1Test() {
    targetTestClassName = "constraintBench.test.interProcedural.ArgumentToParameter1";
  }

  @Test
  public void test() {
    // D>20
    BoolExpr expected = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "20", true, IntType.v(), Operator.GT);
    BoolExpr actual = ((ConstraintZ3) results.get(58)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    Assert.assertFalse(results.containsKey(51));
    Assert.assertFalse(results.containsKey(63));
  }
}
