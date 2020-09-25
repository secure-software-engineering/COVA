/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package constraintBenchTestSuite.javaApp;

import categories.BenchmarkTestSuite;
import com.microsoft.z3.BoolExpr;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.IConstraint;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.experimental.categories.Category;
import soot.Scene;
import soot.SootClass;
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
    BoolExpr expected = SMTSolverZ3.getInstance().makeBoolTerm(A, false); // A
    BoolExpr actual = ((ConstraintZ3) results.get(23)).getExpr();
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    Assert.assertTrue(equivalent);
  }
}
