package constraintBenchTestSuite.javaApp;

import com.microsoft.z3.BoolExpr;

import java.util.TreeMap;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;

import soot.Scene;
import soot.SootClass;

import categories.BenchmarkTestSuite;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.IConstraint;
import utils.ConstraintBenchTestFrameworkForJavaApp;

@Category(BenchmarkTestSuite.class)
public class InnerClass1Test extends ConstraintBenchTestFrameworkForJavaApp {

  public InnerClass1Test() {
    targetTestAppName = "InnerClass1";
    entryPoint = "Outer";
  }

  @Ignore
  public void test() {
    SootClass cl = Scene.v().getSootClass("Outer");
    TreeMap<Integer, IConstraint> results = reporter.getResultOfLines(cl);
    Assert.assertFalse(results.isEmpty());
    Assert.assertTrue(results.containsKey(23));
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(A, false);// A
    BoolExpr actual = ((ConstraintZ3) results.get(23)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }

}
