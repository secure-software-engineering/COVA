package unitTestSuite.testCore;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.microsoft.z3.BoolExpr;

import soot.BooleanType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.RefType;
import soot.Type;
import soot.jimple.ConditionExpr;
import soot.jimple.Jimple;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JGeExpr;
import soot.jimple.internal.JLeExpr;
import soot.jimple.internal.JNeExpr;

import cova.core.ConstraintFactory;
import cova.core.SMTSolverZ3;
import cova.data.ConstraintZ3;
import cova.data.Operator;
import cova.data.WrappedAccessPath;
import cova.data.taints.SourceTaint;
import utils.UnitTestFramework;

public class ConstraintFactoryTest extends UnitTestFramework {
  private final BoolExpr e1 = SMTSolverZ3.getInstance().makeBoolTerm("C1", false);
  private final BoolExpr e2 = SMTSolverZ3.getInstance().makeBoolTerm("C2", false);
  private final String symbolic1 = "Sym1";
  private final String symbolic2 = "Sym2";
  private final ConstraintZ3 c1 = new ConstraintZ3(e1, "C1");
  private final ConstraintZ3 c2 = new ConstraintZ3(e2, "C2");
  @Test
  public void testCreateConstraintFromSourceTaints1() {
    // test constraint creation for two boolean source taints
    Type type=BooleanType.v();
    Local local1 = Jimple.v().newLocal("s1", type);
    Local local2 = Jimple.v().newLocal("s2", type);
    SourceTaint s1 = new SourceTaint(new WrappedAccessPath(local1), c1, symbolic1);
    SourceTaint s2 = new SourceTaint(new WrappedAccessPath(local2), c2, symbolic2);
    // Sym1 = Sym2 (goto case)
    ConditionExpr conditionExpr = new JEqExpr(local1, local2);
    ConstraintZ3 constraint = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2,
        conditionExpr, false);
    BoolExpr actual = constraint.getExpr();
    // compute expected constraint
    BoolExpr condition = SMTSolverZ3.getInstance().makeNonTerminalExpr(symbolic1, false, symbolic2,
        false, type, Operator.EQ);
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(condition);
    exprs.add(e1);
    exprs.add(e2);
    BoolExpr expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
    // !(Sym1 = Sym2) (fall through case)
    ConstraintZ3 negation = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2, conditionExpr,
        true);
    actual = negation.getExpr();
    // compute expected constraint
    BoolExpr negatedCondtion = SMTSolverZ3.getInstance().negate(condition, false);
    exprs.clear();
    exprs.add(negatedCondtion);
    exprs.add(e1);
    exprs.add(e2);
    expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
  }

  @Test
  public void testCreateConstraintFromSourceTaints2() {
    // test constraint creation for two integer source taints
    Type type = IntType.v();
    Local local1 = Jimple.v().newLocal("s1", type);
    Local local2 = Jimple.v().newLocal("s2", type);
    SourceTaint s1 = new SourceTaint(new WrappedAccessPath(local1), c1, symbolic1);
    SourceTaint s2 = new SourceTaint(new WrappedAccessPath(local2), c2, symbolic2);
    // Sym1 <= Sym2 (goto case)
    ConditionExpr conditionExpr = new JLeExpr(local1, local2);
    ConstraintZ3 constraint = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2,
        conditionExpr, false);
    BoolExpr actual = constraint.getExpr();
    // compute expected constraint
    BoolExpr condition = SMTSolverZ3.getInstance().makeNonTerminalExpr(symbolic1, false, symbolic2,
        false, type, Operator.LE);
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(condition);
    exprs.add(e1);
    exprs.add(e2);
    BoolExpr expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
    // !(Sym1 <= Sym2) (fall through case)
    ConstraintZ3 negation = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2, conditionExpr,
        true);
    actual = negation.getExpr();
    // compute expected constraint
    BoolExpr negatedCondtion = SMTSolverZ3.getInstance().negate(condition, false);
    exprs.clear();
    exprs.add(negatedCondtion);
    exprs.add(e1);
    exprs.add(e2);
    expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
  }


  @Test
  public void testCreateConstraintFromSourceTaints3() {
    // test constraint creation for two float source taints
    Type type = FloatType.v();
    Local local1 = Jimple.v().newLocal("s1", type);
    Local local2 = Jimple.v().newLocal("s2", type);
    SourceTaint s1 = new SourceTaint(new WrappedAccessPath(local1), c1, symbolic1);
    SourceTaint s2 = new SourceTaint(new WrappedAccessPath(local2), c2, symbolic2);
    // Sym1 >= Sym2 (goto case)
    ConditionExpr conditionExpr = new JGeExpr(local1, local2);
    ConstraintZ3 constraint = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2,
        conditionExpr, false);
    BoolExpr actual = constraint.getExpr();
    // compute expected constraint
    BoolExpr condition = SMTSolverZ3.getInstance().makeNonTerminalExpr(symbolic1, false, symbolic2,
        false, type, Operator.GE);
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(condition);
    exprs.add(e1);
    exprs.add(e2);
    BoolExpr expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
    // !(Sym1 >= Sym2) (fall through case)
    ConstraintZ3 negation = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2, conditionExpr,
        true);
    actual = negation.getExpr();
    // compute expected constraint
    BoolExpr negatedCondtion = SMTSolverZ3.getInstance().negate(condition, false);
    exprs.clear();
    exprs.add(negatedCondtion);
    exprs.add(e1);
    exprs.add(e2);
    expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
  }

  @Test
  public void testCreateConstraintFromSourceTaints4() {
    // test constraint creation for two string source taints
    Type type = RefType.v(String.class.getName());
    Local local1 = Jimple.v().newLocal("s1", type);
    Local local2 = Jimple.v().newLocal("s2", type);
    SourceTaint s1 = new SourceTaint(new WrappedAccessPath(local1), c1, symbolic1);
    SourceTaint s2 = new SourceTaint(new WrappedAccessPath(local2), c2, symbolic2);
    // Sym1 = Sym2 (goto case)
    ConditionExpr conditionExpr = new JEqExpr(local1, local2);
    ConstraintZ3 constraint = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2,
        conditionExpr, false);
    BoolExpr actual = constraint.getExpr();
    // compute expected constraint
    BoolExpr condition = SMTSolverZ3.getInstance().makeNonTerminalExpr(symbolic1, false, symbolic2,
        false, type, Operator.EQ);
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(condition);
    exprs.add(e1);
    exprs.add(e2);
    BoolExpr expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
    // !(Sym1 = Sym2) (fall through case)
    ConstraintZ3 negation = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2, conditionExpr,
        true);
    actual = negation.getExpr();
    // compute expected constraint
    BoolExpr negatedCondtion = SMTSolverZ3.getInstance().negate(condition, false);
    exprs.clear();
    exprs.add(negatedCondtion);
    exprs.add(e1);
    exprs.add(e2);
    expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
  }

  @Test
  public void testCreateConstraintFromSourceTaints5() {
    // test constraint creation for two refType source taints
    Type type = RefType.v(Object.class.getName());
    Local local1 = Jimple.v().newLocal("s1", type);
    Local local2 = Jimple.v().newLocal("s2", type);
    SourceTaint s1 = new SourceTaint(new WrappedAccessPath(local1), c1, symbolic1);
    SourceTaint s2 = new SourceTaint(new WrappedAccessPath(local2), c2, symbolic2);
    // Sym1 != Sym2 (goto)
    ConditionExpr conditionExpr = new JNeExpr(local1, local2);
    ConstraintZ3 constraint = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2,
        conditionExpr, false);
    BoolExpr actual = constraint.getExpr();
    // compute expected constraint
    BoolExpr condition = SMTSolverZ3.getInstance().makeNonTerminalExpr(symbolic1, false, symbolic2,
        false, type, Operator.NE);
    List<BoolExpr> exprs = new ArrayList<>();
    exprs.add(condition);
    exprs.add(e1);
    exprs.add(e2);
    BoolExpr expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    boolean equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
    // !(Sym1 != Sym2) (fall through case)
    ConstraintZ3 negation = (ConstraintZ3) ConstraintFactory.createConstraint(s1, s2, conditionExpr,
        true);
    actual = negation.getExpr();
    // compute expected constraint
    BoolExpr negatedCondtion = SMTSolverZ3.getInstance().negate(condition, false);
    exprs.clear();
    exprs.add(negatedCondtion);
    exprs.add(e1);
    exprs.add(e2);
    expected = SMTSolverZ3.getInstance().makeConjunction(exprs, false);
    // verify
    equivalent = SMTSolverZ3.getInstance().prove(expected, actual);
    if (!equivalent) {
      System.out.println("expected: " + expected);
      System.out.println("actual: " + actual);
    }
    Assert.assertTrue(equivalent);
  }

  // public void testCreateConstraintFromConcreteTaints() {
  // ConstraintFactory factory = new ConstraintFactory();
  // Local local1 = Jimple.v().newLocal("s1", IntType.v());
  // Local local2 = Jimple.v().newLocal("s2", IntType.v());
  // BoolExpr e1 = SMTSolverZ3.getInstance().makeBoolExpr("A", "B", Operator.AND);
  // BoolExpr e2 = SMTSolverZ3.getInstance().makeBoolExpr("C", "D", Operator.OR);
  // ConstraintZ3 c1 = new ConstraintZ3(e1, new ArrayList<String>());
  // ConstraintZ3 c2 = new ConstraintZ3(e2, new ArrayList<String>());
  // JEqExpr equation = new JEqExpr(local1, local2);
  // JNeExpr nequation = new JNeExpr(local1, local2);
  // JLeExpr lessEqual = new JLeExpr(local1, local2);
  // JGeExpr greaterEqual = new JGeExpr(local1, local2);
  // JLtExpr lessThan = new JLtExpr(local1, local2);
  // JGtExpr greaterThan = new JGtExpr(local1, local2);
  //
  // ConcreteTaint t1 = new ConcreteTaint(new WrappedAccessPath(local1), c1, IntConstant.v(1));
  // ConcreteTaint t2 = new ConcreteTaint(new WrappedAccessPath(local2), c2, IntConstant.v(1));
  // IConstraint ceq = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, equation, false);
  // IConstraint cneq = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, nequation, false);
  // IConstraint cle = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessEqual, false);
  // IConstraint cge = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterEqual,
  // false);
  // IConstraint clt = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessThan, false);
  // IConstraint cgt = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterThan, false);
  //
  //
  // System.out.println(ceq.toString());
  // Assert.assertEquals(ceq.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(cle.toString());
  // Assert.assertEquals(cle.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(cge.toString());
  // Assert.assertEquals(cge.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(cneq.toString());
  // Assert.assertEquals(cneq.toString(), "false");
  //
  // System.out.println(clt.toString());
  // Assert.assertEquals(clt.toString(), "false");
  //
  // System.out.println(cgt.toString());
  // Assert.assertEquals(cgt.toString(), "false");
  //
  // t2.updateCurrentValue(IntConstant.v(2));
  // IConstraint ceq2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, equation, false);
  // IConstraint cneq2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, nequation, false);
  // IConstraint cle2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessEqual, false);
  // IConstraint cge2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterEqual,
  // false);
  // IConstraint clt2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessThan, false);
  // IConstraint cgt2 = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterThan,
  // false);
  //
  // System.out.println(ceq2.toString());
  // Assert.assertEquals(ceq2.toString(), "false");
  //
  // System.out.println(cle2.toString());
  // Assert.assertEquals(cle2.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(cge2.toString());
  // Assert.assertEquals(cge2.toString(), "false");
  //
  // System.out.println(cneq2.toString());
  // Assert.assertEquals(cneq2.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(clt2.toString());
  // Assert.assertEquals(clt2.toString(), "((A∧B)∧(C∨D))");
  //
  // System.out.println(cgt2.toString());
  // Assert.assertEquals(cgt2.toString(), "false");

  public void testCreateConstraintsFromImpreciseTaints() {
    // ConstraintFactory factory = new ConstraintFactory();
    // Local s1 = Jimple.v().newLocal("s1", RefType.v("String"));
    // Local s2 = Jimple.v().newLocal("s2", RefType.v("String"));
    // Local i1 = Jimple.v().newLocal("i1", IntType.v());
    // Local i2 = Jimple.v().newLocal("i2", RefType.v());
    // BoolExpr e1 = SMTSolverZ3.getInstance().makeBoolExpr("A", "B", Operator.AND);
    // BoolExpr e2 = SMTSolverZ3.getInstance().makeBoolExpr("C", "D", Operator.OR);
    // ConstraintZ3 c1 = new ConstraintZ3(e1, new ArrayList<String>());
    // ConstraintZ3 c2 = new ConstraintZ3(e2, new ArrayList<String>());
    // SourceTaint source1 = new SourceTaint(new WrappedAccessPath(s1), ConstraintZ3.getTrue(),
    // "Sym1");
    // SourceTaint source2 = new SourceTaint(new WrappedAccessPath(s2), ConstraintZ3.getTrue(),
    // "Sym2");
    // ImpreciseTaint t1 = new ImpreciseTaint(new WrappedAccessPath(i1), c1, source1, "Im1");
    // ImpreciseTaint t2 = new ImpreciseTaint(new WrappedAccessPath(i2), c2, source2, "Im2");
    //
    // JEqExpr equation = new JEqExpr(i1, i2);
    // JNeExpr nequation = new JNeExpr(i1, i2);
    // JLeExpr lessEqual = new JLeExpr(i1, i2);
    // JGeExpr greaterEqual = new JGeExpr(i1, i2);
    // JLtExpr lessThan = new JLtExpr(i1, i2);
    // JGtExpr greaterThan = new JGtExpr(i1, i2);
    //
    // IConstraint ceq = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, equation, false);
    // IConstraint cneq = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, nequation,
    // false);
    // IConstraint cle = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessEqual, false);
    // IConstraint cge = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterEqual,
    // false);
    // IConstraint clt = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, lessThan, false);
    // IConstraint cgt = factory.createConstraint(ConstraintZ3.getTrue(), t1, t2, greaterThan,
    // false);
    //
    // System.out.println(ceq.toString());
    // Assert.assertEquals(ceq.toString(), "(((A∧B)∧(C∨D))∧(Im1=Im2))");
    //
    // System.out.println(cneq.toString());
    // Assert.assertEquals(cneq.toString(), "(((A∧B)∧(C∨D))∧!(Im1=Im2))");
    //
    // System.out.println(cle.toString());
    // Assert.assertEquals(cle.toString(), "(((A∧B)∧(C∨D))∧(Im1<=Im2))");
    //
    // System.out.println(cge.toString());
    // Assert.assertEquals(cge.toString(), "(((A∧B)∧(C∨D))∧(Im1>=Im2))");
    //
    // System.out.println(clt.toString());
    // Assert.assertEquals(clt.toString(), "(((A∧B)∧(C∨D))∧!(Im2<=Im1))");
    //
    // System.out.println(cgt.toString());
    // Assert.assertEquals(cgt.toString(), "(((A∧B)∧(C∨D))∧!(Im1<=Im2))");
  }

  public void testCreateConstraintFromSourceAndConcreteTaints() {

  }

  public void testCreateConstraintFromSourceAndImpreciseTaints() {

  }

  public void testCreateConstraintsFromImpreciseAndConcreteTaints() {

  }

  public void testCreateConstraintFromSourceTaint() {

  }

  public void testCreateConstraintFromConcreteTaint() {

  }

  public void testCreateConstraintFromImpreciseTaint() {

  }
}
