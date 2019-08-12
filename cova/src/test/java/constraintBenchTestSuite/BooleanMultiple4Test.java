package constraintBenchTestSuite;

import com.microsoft.z3.BoolExpr;

import org.junit.Assert;
import org.junit.Test;

import soot.BooleanType;

import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import utils.ConstraintBenchTestFramework;

/**
 * 
 * 
 */
public class BooleanMultiple4Test extends ConstraintBenchTestFramework {

  public BooleanMultiple4Test() {
    targetTestClassName = "constraintBench.test.primTypes.BooleanMultiple4";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termE = SMTSolverZ3.getInstance().makeBoolTerm(E, false);
    BoolExpr negatedA= SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedE = SMTSolverZ3.getInstance().negate(termE, false);
    BoolExpr bEc = SMTSolverZ3.getInstance().makeNonTerminalExpr(B, false, C, false,
        BooleanType.v(), Operator.EQ);
    BoolExpr negatedbEc = SMTSolverZ3.getInstance().negate(bEc, false);
    BoolExpr ea = SMTSolverZ3.getInstance().solve(negatedA, negatedE, Operator.OR, false);
    // (B = C) v !A v !E
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(bEc, ea, Operator.OR, false);
    // !(B = C) v !A v !E
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(negatedbEc, ea, Operator.OR, false);

    BoolExpr actual = ((ConstraintZ3) results.get(15)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(termA, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(18)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(termE, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(21)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected1, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(23)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expected2, actual);
    Assert.assertTrue(equivalent);

    Assert.assertTrue(!results.containsKey(25));
  }
}
