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
public class ImpreciseMultiple1Test extends ConstraintBenchTestFramework {

  public ImpreciseMultiple1Test() {
    targetTestClassName = "constraintBench.test.imprecise.ImpreciseMultiple1";
  }

  @Test
  public void test() {
    StringBuilder sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_0");
    String imFA0 = sb.toString();
    sb = new StringBuilder("im(");
    sb.append(FA);
    sb.append(")_1");
    String imFA1 = sb.toString();
    BoolExpr termFA0 = SMTSolverZ3.getInstance().makeBoolTerm(imFA0, false);
    BoolExpr negatedFA0 = SMTSolverZ3.getInstance().negate(termFA0, false);
    BoolExpr termFA1 = SMTSolverZ3.getInstance().makeBoolTerm(imFA1, false);
    BoolExpr negatedFA1 = SMTSolverZ3.getInstance().negate(termFA1, false);
    // !im(FA)_0 ^ !im(FA)_1
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(negatedFA0, negatedFA1, Operator.AND, false);
    BoolExpr actual = ((ConstraintZ3) results.get(13)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);
    // im(FA)_0 v im(FA)_1
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(termFA0, termFA1, Operator.OR, false);
    actual = ((ConstraintZ3) results.get(15)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);
  }
}
