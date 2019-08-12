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
 */
public class IndirectConcrete2Test extends ConstraintBenchTestFramework {

  public IndirectConcrete2Test() {
    targetTestClassName = "constraintBench.test.indirect.IndirectConcrete2";
  }

  @Test
  public void test() {
    BoolExpr termA = SMTSolverZ3.getInstance().makeBoolTerm(A, false);
    BoolExpr termB = SMTSolverZ3.getInstance().makeBoolTerm(B, false);
    BoolExpr negatedA = SMTSolverZ3.getInstance().negate(termA, false);
    BoolExpr negatedB = SMTSolverZ3.getInstance().negate(termB, false);
    // A ^ B
    BoolExpr expected1 = SMTSolverZ3.getInstance().solve(termA, termB, Operator.AND, false);
    // !A ^ !B
    BoolExpr expected2 = SMTSolverZ3.getInstance().solve(negatedA, negatedB, Operator.AND, false);
    // (A ^ B) ∨ (!A ^ !B)
    BoolExpr expected3 = SMTSolverZ3.getInstance().solve(expected1, expected2, Operator.OR, false);
    // A = B
    BoolExpr expteced4 = SMTSolverZ3.getInstance().makeNonTerminalExpr(A, false, B, false,
        BooleanType.v(), Operator.EQ);

    BoolExpr actual = ((ConstraintZ3) results.get(16)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected3, actual);
    Assert.assertTrue(equivalent);

    actual = ((ConstraintZ3) results.get(22)).getExpr();
    equivalent = SMTSolverZ3.getInstance().prove(expteced4, actual);
    Assert.assertTrue(equivalent);
  }
}
