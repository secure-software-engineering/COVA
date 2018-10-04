package constraintBenchTestSuite.javaApp;

import com.microsoft.z3.BoolExpr;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import soot.Scene;
import soot.SootClass;

import categories.BenchmarkTestSuite;
import core.SMTSolverZ3;
import data.ConstraintZ3;
import data.IConstraint;
import utils.ConstraintBenchTestFrameworkForJavaApp;

@Category(BenchmarkTestSuite.class)
public class AnonymousClass1Test extends ConstraintBenchTestFrameworkForJavaApp {

  public AnonymousClass1Test() {
    targetTestAppName = "AnonymousClass1";
    entryPoint = "Main";
  }

  @Test
  public void test() {
    SootClass cl = Scene.v().getSootClass("Main");
    TreeMap<Integer, IConstraint> results = reporter.getResultOfLines(cl);
    Assert.assertFalse(results.isEmpty());
    Assert.assertTrue(results.containsKey(27));
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(A, false);// A
    BoolExpr actual = ((ConstraintZ3) results.get(27)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
