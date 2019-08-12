package constraintBenchTestSuite.androidApp;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.IntType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFrameworkForAndroidApp;

public class SingleIfBranchTest extends ConstraintBenchTestFrameworkForAndroidApp {

  public SingleIfBranchTest() {
    targetTestAppName = "SingleIfBranch1";
  }

  @Test
  public void testSingleIfBranch1() {
    ConstraintZ3 constraintOfLeak = (ConstraintZ3) results.getConstraint(22,
        "de.upb.swt.singleifbranch1.MainActivity", 27, "de.upb.swt.singleifbranch1.MainActivity");
    BoolExpr model = SMTSolverZ3.getInstance().makeBoolTerm("im(C13)_0", false);
    BoolExpr sdk = SMTSolverZ3.getInstance().makeNonTerminalExpr("C24", false, "15", true,
        IntType.v(), Operator.GE);
    BoolExpr negatedSdk = SMTSolverZ3.getInstance().negate(sdk, false);
    BoolExpr expected = SMTSolverZ3.getInstance().solve(model, negatedSdk, Operator.AND, false);
    BoolExpr actual = constraintOfLeak.getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);

    constraintOfLeak = (ConstraintZ3) results.getConstraint(22,
        "de.upb.swt.singleifbranch1.MainActivity", 32, "de.upb.swt.singleifbranch1.MainActivity");
    expected = SMTSolverZ3.getInstance().solve(model, sdk, Operator.AND, false);
    actual = constraintOfLeak.getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
