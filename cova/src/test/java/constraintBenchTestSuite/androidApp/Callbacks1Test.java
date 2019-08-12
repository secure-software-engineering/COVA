package constraintBenchTestSuite.androidApp;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.IntType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFrameworkForAndroidApp;

public class Callbacks1Test extends ConstraintBenchTestFrameworkForAndroidApp {

  public Callbacks1Test() {
    targetTestAppName = "Callbacks1";
  }

  @Test
  public void test() {
    ConstraintZ3 constraintOfLeak = (ConstraintZ3) results.getConstraint(28, "de.upb.swt.callbacks1.MainActivity$1", 41,
        "de.upb.swt.callbacks1.MainActivity");
    BoolExpr onClick = SMTSolverZ3.getInstance().makeBoolTerm("U8_0", false);
    BoolExpr model = SMTSolverZ3.getInstance().makeBoolTerm("im(C13)_0", false);
    BoolExpr sdk = SMTSolverZ3.getInstance().makeNonTerminalExpr("C24", false, "15", true, IntType.v(), Operator.GE);
    BoolExpr negatedSdk = SMTSolverZ3.getInstance().negate(sdk, false);
    BoolExpr temp = SMTSolverZ3.getInstance().solve(onClick, model, Operator.AND, false);
    BoolExpr expected = SMTSolverZ3.getInstance().solve(temp, negatedSdk, Operator.AND, false);
    BoolExpr actual = constraintOfLeak.getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
