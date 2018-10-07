package constraintBenchTestSuite;

import de.upb.swt.cova.core.SMTSolverZ3;
import de.upb.swt.cova.data.ConstraintZ3;
import de.upb.swt.cova.data.Operator;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Ignore;

import soot.IntType;

import utils.ConstraintBenchTestFramework;

/**
 *
 */
public class Array1Test extends ConstraintBenchTestFramework {

  public Array1Test() {
    targetTestClassName = "constraintBench.test.array.Array1";
  }

  @Ignore
  public void test() {
    BoolExpr termD = SMTSolverZ3.getInstance().makeNonTerminalExpr(D, false, "0", true,
        IntType.v(), Operator.LE);
    BoolExpr expected = SMTSolverZ3.getInstance().negate(termD, false);
    BoolExpr actual = ((ConstraintZ3) results.get(17)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
