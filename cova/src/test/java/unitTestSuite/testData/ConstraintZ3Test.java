package unitTestSuite.testData;

import org.junit.Assert;
import org.junit.Test;

import cova.core.SMTSolverZ3;
import cova.data.Operator;

import com.microsoft.z3.BoolExpr;

import utils.UnitTestFramework;

public class ConstraintZ3Test extends UnitTestFramework {

  @Test
  public void test01() throws Exception {
    BoolExpr e1 = SMTSolverZ3.getInstance().makeBoolTerm("A", false);
    BoolExpr e2 = SMTSolverZ3.getInstance().makeBoolTerm("A", true);
    BoolExpr e3 = SMTSolverZ3.getInstance().solve(e1, e2, Operator.OR, false);
    Assert.assertTrue(SMTSolverZ3.getInstance().prove(e3, SMTSolverZ3.getInstance().getTrue()));
  }
}
