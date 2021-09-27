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
package cova.core;

import com.microsoft.z3.BoolExpr;
import cova.data.ConstraintZ3;
import cova.data.IConstraint;
import cova.data.Operator;
import cova.data.WitnessPath;
import cova.data.taints.AbstractTaint;
import cova.data.taints.ConcreteTaint;
import cova.data.taints.ImpreciseTaint;
import cova.data.taints.SourceTaint;
import cova.data.taints.StringTaint;
import cova.rules.StringMethod;
import java.util.ArrayList;
import soot.BooleanType;
import soot.IntType;
import soot.Type;
import soot.Value;
import soot.jimple.ArithmeticConstant;
import soot.jimple.ConditionExpr;
import soot.jimple.Constant;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.IntConstant;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;
import soot.jimple.StringConstant;

/** A factory for creating constraint. */
public class ConstraintFactory {

  /**
   * Creates a new constraint when two source taints appear in the same condition expression of the
   * if-statement.
   *
   * @param source1 the first source taint
   * @param source2 the second source taint
   * @param conditionExpr the condition expression of the if-statement
   * @param isFallThroughEdge true, if the current edge is a fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromSourceTaints(
      SourceTaint source1,
      SourceTaint source2,
      ConditionExpr conditionExpr,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = source1.getConstraint();
    IConstraint constraint2 = source2.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    String nameWithoutIndex1 = source1.getSymbolicName();
    String nameWithoutIndex2 = source2.getSymbolicName();
    BoolExpr expr =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                nameWithoutIndex1,
                false,
                nameWithoutIndex2,
                false,
                source1.getType(),
                SMTSolverZ3.getInstance().translate(conditionExpr));
    ArrayList<String> symbolicNames = new ArrayList<String>();
    symbolicNames.add(source1.getSymbolicName());
    symbolicNames.add(source2.getSymbolicName());
    IConstraint newConstraint =
        new ConstraintZ3(
            expr, symbolicNames, WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
    if (isFallThroughEdge) {
      newConstraint = newConstraint.negate(true);
    }
    constraint = constraint.and(newConstraint, false);
    return constraint;
  }

  /**
   * Creates a new constraint when two concrete taints appear in the same condition expression.
   *
   * @param concrete1 the first concrete taint
   * @param concrete2 the second concrete taint
   * @param conditionExpr the condition expression of the if-statement
   * @param isFallThroughEdge true, if the current edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromConcreteTaints(
      ConcreteTaint concrete1,
      ConcreteTaint concrete2,
      ConditionExpr conditionExpr,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = concrete1.getConstraint();
    IConstraint constraint2 = concrete2.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    Value value1 = concrete1.getCurrentValue();
    Value value2 = concrete2.getCurrentValue();
    if (value1 instanceof ArithmeticConstant && value2 instanceof ArithmeticConstant) {
      BoolExpr expr =
          SMTSolverZ3.getInstance().makeNonTerminalExpr(value1, true, value2, true, conditionExpr);
      IConstraint newConstraint =
          new ConstraintZ3(
              expr,
              new ArrayList<String>(),
              WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
      if (isFallThroughEdge) {
        newConstraint = newConstraint.negate(true);
      }
      constraint = constraint.and(newConstraint, false);
    }
    return constraint;
  }

  /**
   * Creates a new constraint when two imprecise taints appear in the same condition expression.
   *
   * @param imprecise1 the first imprecise taint
   * @param imprecise2 the second imprecise taint
   * @param conditionExpr the condition expression of the if-statement
   * @param isFallThroughEdge true, if the current edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromImpreciseTaints(
      ImpreciseTaint imprecise1,
      ImpreciseTaint imprecise2,
      ConditionExpr conditionExpr,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = imprecise1.getConstraint();
    IConstraint constraint2 = imprecise2.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    String name1 = imprecise1.getSymbolicName();
    String name2 = imprecise2.getSymbolicName();
    BoolExpr expr =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                name1,
                false,
                name2,
                false,
                imprecise1.getType(),
                SMTSolverZ3.getInstance().translate(conditionExpr));
    ArrayList<String> symbolicNames = new ArrayList<String>();
    symbolicNames.addAll(imprecise1.getSourceSymbolics());
    symbolicNames.addAll(imprecise2.getSourceSymbolics());
    IConstraint newConstraint =
        new ConstraintZ3(
            expr, symbolicNames, WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
    if (isFallThroughEdge) {
      newConstraint = newConstraint.negate(true);
    }
    constraint = constraint.and(newConstraint, false);
    return constraint;
  }

  /**
   * Creates a new constraint when a source taint and a concrete taint appear in the same condition
   * expression.
   *
   * @param source the source taint
   * @param concrete the concrete taint
   * @param conditionExpr the condition expression of the if-statement
   * @param sourceOnLeft true, if the source taint is on left side of the condition expression
   * @param isFallThroughEdge true, if the current edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromSourceAndConcreteTaints(
      SourceTaint source,
      ConcreteTaint concrete,
      ConditionExpr conditionExpr,
      boolean sourceOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = source.getConstraint();
    IConstraint constraint2 = concrete.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    Value cval = concrete.getCurrentValue();
    if (cval instanceof ArithmeticConstant) {
      String name = source.getSymbolicName();
      BoolExpr expr = null;
      if (source.getType() instanceof BooleanType) {
        int v = ((IntConstant) cval).value;
        boolean equal = true;
        if (conditionExpr instanceof NeExpr) {
          equal = false;
        }
        expr = SMTSolverZ3.getInstance().makeBoolTerm(name, v, equal, !sourceOnLeft);
      } else {
        if (sourceOnLeft) {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      name,
                      false,
                      cval.toString(),
                      true,
                      source.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        } else {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      cval.toString(),
                      true,
                      name,
                      false,
                      source.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        }
      }
      IConstraint newConstraint =
          new ConstraintZ3(
              expr,
              source.getSymbolicName(),
              WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
      if (isFallThroughEdge) {
        newConstraint = newConstraint.negate(true);
      }
      constraint = constraint.and(newConstraint, false);
    }
    return constraint;
  }

  /**
   * Creates a new constraint when an imprecise taint and a concrete taint appear in the same
   * condition expression.
   *
   * @param imprecise the imprecise taint
   * @param concrete the concrete taint
   * @param conditionExpr the condition expression
   * @param impreciseOnLeft true, if the imprecise taint is on left side of the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromImpreciseAndConcreteTaints(
      ImpreciseTaint imprecise,
      ConcreteTaint concrete,
      ConditionExpr conditionExpr,
      boolean impreciseOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = imprecise.getConstraint();
    IConstraint constraint2 = concrete.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    Value val = concrete.getCurrentValue();
    if (val instanceof ArithmeticConstant) {
      String name = imprecise.getSymbolicName();
      BoolExpr expr = null;
      if (imprecise.getType() instanceof BooleanType) {
        int v = ((IntConstant) val).value;
        boolean equal = true;
        if (conditionExpr instanceof NeExpr) {
          equal = false;
        }
        expr = SMTSolverZ3.getInstance().makeBoolTerm(name, v, equal, !impreciseOnLeft);
      } else {
        if (impreciseOnLeft) {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      name,
                      false,
                      val.toString(),
                      true,
                      imprecise.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        } else {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      val.toString(),
                      true,
                      name,
                      false,
                      imprecise.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        }
      }
      IConstraint newConstraint =
          new ConstraintZ3(
              expr,
              imprecise.getSourceSymbolics(),
              WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
      if (isFallThroughEdge) {
        newConstraint = newConstraint.negate(true);
      }
      constraint = constraint.and(newConstraint, false);
    }
    return constraint;
  }

  /**
   * Creates a new constraint when a source taint and an imprecise taint appear in the same
   * condition expression.
   *
   * @param source the source taint
   * @param imprecise the imprecise taint
   * @param conditionExpr the condition expression
   * @param sourceOnLeft true, if the source taint is on the left side of the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the i constraint
   */
  private static IConstraint createConstraintFromSourceAndImpreciseTaint(
      SourceTaint source,
      ImpreciseTaint imprecise,
      ConditionExpr conditionExpr,
      boolean sourceOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint1 = source.getConstraint();
    IConstraint constraint2 = imprecise.getConstraint();
    IConstraint constraint = constraint1.and(constraint2, false);
    String sname = source.getSymbolicName();
    String iname = imprecise.getSymbolicName();
    BoolExpr expr =
        SMTSolverZ3.getInstance()
            .makeNonTerminalExpr(
                sname,
                false,
                iname,
                false,
                source.getType(),
                SMTSolverZ3.getInstance().translate(conditionExpr));
    ArrayList<String> symbolicNames = new ArrayList<String>();
    symbolicNames.add(source.getSymbolicName());
    symbolicNames.addAll(imprecise.getSourceSymbolics());
    IConstraint newConstraint =
        new ConstraintZ3(
            expr, symbolicNames, WitnessPath.merge(constraint1.getPath(), constraint2.getPath()));
    if (isFallThroughEdge) {
      newConstraint = newConstraint.negate(true);
    }
    constraint = constraint.and(newConstraint, false);
    return constraint;
  }

  /**
   * Creates a new constraint when the condition expression contains only one concrete taint.
   *
   * @param concrete the concrete taint
   * @param conditionExpr the condition expression of the if-statement
   * @param taintOnLeft true, if the concrete taint is on the left side of the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromConcreteTaint(
      ConcreteTaint concrete,
      ConditionExpr conditionExpr,
      boolean taintOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint = concrete.getConstraint();
    Value cval = concrete.getCurrentValue();
    Value val = null;
    if (taintOnLeft) {
      val = conditionExpr.getOp2();
    } else {
      val = conditionExpr.getOp1();
    }

    if (cval instanceof ArithmeticConstant && val instanceof ArithmeticConstant) {
      BoolExpr expr =
          SMTSolverZ3.getInstance()
              .makeNonTerminalExpr(concrete.getCurrentValue(), true, val, true, conditionExpr);
      if (isFallThroughEdge) {
        expr = SMTSolverZ3.getInstance().negate(expr, false);
      }
      if (!expr.isFalse()) {
        IConstraint newConstraint =
            new ConstraintZ3(expr, new ArrayList<String>(), WitnessPath.copy(constraint.getPath()));
        constraint = constraint.and(newConstraint, false);
      } else {
        constraint = ConstraintZ3.getFalse();
      }
    } else if (!(cval instanceof ArithmeticConstant) && val instanceof ArithmeticConstant) {
      if (concrete.hasSource()) {
        Operator op = Operator.EQ;
        String name = concrete.getSource().getSymbolicName();
        if (conditionExpr instanceof EqExpr && val.equals(IntConstant.v(0))) {
          op = Operator.NE;
        }
        if (conditionExpr instanceof NeExpr && val.equals(IntConstant.v(1))) {
          op = Operator.NE;
        }
        BoolExpr expr =
            SMTSolverZ3.getInstance()
                .makeNonTerminalExpr(name, false, cval.toString(), true, cval.getType(), op);
        IConstraint newConstraint =
            new ConstraintZ3(
                expr,
                concrete.getSource().getSymbolicName(),
                WitnessPath.copy(constraint.getPath()));
        if (isFallThroughEdge) {
          newConstraint = newConstraint.negate(false);
        }
        constraint = constraint.and(newConstraint, false);
      }
    } else {
      if (cval instanceof StringConstant && val instanceof StringConstant) {
        String symbol = conditionExpr.getSymbol();
        BoolExpr expr = null;
        if (symbol.indexOf("==") > -1) {
          expr =
              SMTSolverZ3.getInstance()
                  .makeStrTerm(
                      ((StringConstant) cval).value,
                      ((StringConstant) val).value,
                      StringMethod.EQUALS);
          expr.simplify();
        } else if (symbol.indexOf("!=") > -1) {
          expr =
              SMTSolverZ3.getInstance()
                  .makeStrTerm(
                      ((StringConstant) cval).value,
                      ((StringConstant) val).value,
                      StringMethod.EQUALS);
          expr = SMTSolverZ3.getInstance().negate(expr, true);
        }
        if (expr != null) {
          IConstraint newConstraint =
              new ConstraintZ3(
                  expr, new ArrayList<String>(), WitnessPath.copy(constraint.getPath()));
          if (isFallThroughEdge) {
            newConstraint = newConstraint.negate(false);
          }
          constraint = constraint.and(newConstraint, false);
        }
      }
    }
    return constraint;
  }

  /**
   * Creates a new constraint when the condition expression contains only one source taint.
   *
   * @param source the source taint
   * @param conditionExpr the condition expression of the if-statement
   * @param taintOnLeft true, if the source taint is on the left side of the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromSourceTaint(
      SourceTaint source,
      ConditionExpr conditionExpr,
      boolean taintOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint = source.getConstraint();
    Value val = null;
    if (taintOnLeft) {
      val = conditionExpr.getOp2();
    } else {
      val = conditionExpr.getOp1();
    }
    BoolExpr expr = null;
    String name = source.getSymbolicName();
    boolean equal = true;
    if (conditionExpr instanceof NeExpr) {
      equal = false;
    }
    if (val instanceof Constant) {
      if (source.getType() instanceof BooleanType) {
        int v = ((IntConstant) val).value;
        expr = SMTSolverZ3.getInstance().makeBoolTerm(name, v, equal, !taintOnLeft);
      } else {
        if (taintOnLeft) {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      name,
                      false,
                      val.toString(),
                      true,
                      source.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        } else {
          expr =
              SMTSolverZ3.getInstance()
                  .makeNonTerminalExpr(
                      val.toString(),
                      true,
                      name,
                      false,
                      source.getType(),
                      SMTSolverZ3.getInstance().translate(conditionExpr));
        }
      }
    } else {
      expr = SMTSolverZ3.getInstance().makeBoolTerm("im(" + name + ")", false);
    }
    if (expr != null) {
      if (isFallThroughEdge) {
        expr = SMTSolverZ3.getInstance().negate(expr, true);
      }
      IConstraint newConstraint =
          new ConstraintZ3(expr, source.getSymbolicName(), WitnessPath.copy(constraint.getPath()));
      constraint = constraint.and(newConstraint, false);
    }
    return constraint;
  }

  /**
   * Creates a new constraint when the condition expression contains only one imprecise taint.
   *
   * @param imprecise the imprecise taint
   * @param conditionExpr the condition expression of the if-statement
   * @param taintOnLeft true, if the imprecise taint is on the left side of the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  private static IConstraint createConstraintFromImpreciseTaint(
      ImpreciseTaint imprecise,
      ConditionExpr conditionExpr,
      boolean taintOnLeft,
      boolean isFallThroughEdge) {
    IConstraint constraint = imprecise.getConstraint();
    Type type = imprecise.getAccessPath().getType();
    BoolExpr expr = null;
    if (type instanceof BooleanType) {
      if (conditionExpr instanceof EqExpr) {
        Value constant = conditionExpr.getOp1();
        if (taintOnLeft) {
          constant = conditionExpr.getOp2();
        }
        boolean negate = false;
        if (constant.equals(IntConstant.v(0))) {
          negate = !isFallThroughEdge;
        } else {
          negate = isFallThroughEdge;
        }
        expr = SMTSolverZ3.getInstance().makeBoolTerm(imprecise.getSymbolicName(), negate);
      } else if (conditionExpr instanceof NeExpr) {
        Value constant = conditionExpr.getOp1();
        if (taintOnLeft) {
          constant = conditionExpr.getOp2();
        }
        boolean negate = false;
        if (constant.equals(IntConstant.v(0))) {
          negate = isFallThroughEdge;
        } else {
          negate = !isFallThroughEdge;
        }
        expr = SMTSolverZ3.getInstance().makeBoolTerm(imprecise.getSymbolicName(), negate);
      }
    }
    if (expr == null) {
      expr = SMTSolverZ3.getInstance().makeBoolTerm(imprecise.getSymbolicName(), isFallThroughEdge);
    }
    IConstraint newConstraint =
        new ConstraintZ3(
            expr, imprecise.getSourceSymbolics(), WitnessPath.copy(constraint.getPath()));
    constraint = constraint.and(newConstraint, false);
    return constraint;
  }

  private static IConstraint createConstraintFromStringTaint(
      StringTaint stringTaint,
      ConditionExpr conditionExpr,
      boolean taintOnLeft,
      boolean isFallThroughEdge) {

    IConstraint constraint = stringTaint.getConstraint();
    Type type = stringTaint.getAccessPath().getType();
    IConstraint newConstraint;
    if (type instanceof IntType) {
      Operator operator;
      if (conditionExpr instanceof LeExpr) {
        operator = Operator.LE;
      } else if (conditionExpr instanceof LtExpr) {
        operator = Operator.LT;
      } else if (conditionExpr instanceof GeExpr) {
        operator = Operator.GE;
      } else if (conditionExpr instanceof GtExpr) {
        operator = Operator.GT;
      } else if (conditionExpr instanceof NeExpr) {
        operator = Operator.NE;
      } else if (conditionExpr instanceof EqExpr) {
        operator = Operator.EQ;
      } else {
        throw new RuntimeException("Wrong condition: " + conditionExpr.getClass().toString());
      }

      Value constant = conditionExpr.getOp1();
      Value val = conditionExpr.getOp2();
      if (taintOnLeft) {
        constant = conditionExpr.getOp2();
        val = conditionExpr.getOp1();
      }

      String name = stringTaint.getSymbolicName();
      String constantString = constant.toString();
      newConstraint =
          createIntCompareConstraint(
              name,
              Integer.parseInt(constantString),
              operator,
              isFallThroughEdge,
              stringTaint.getStringMethod());

      constraint = constraint.and(newConstraint, false);
      return constraint;

    } else if (type instanceof BooleanType) {
      newConstraint =
          createConstantInStringConstraint(
              stringTaint.getSymbolicName(),
              stringTaint.getConstant(),
              stringTaint.getStringMethod());
      if (conditionExpr instanceof EqExpr) {
        // normal case
      } else if (conditionExpr instanceof NeExpr) {
        // inverted case
        newConstraint = newConstraint.negate(false);
      } else {
        throw new RuntimeException("Wrong condition: " + conditionExpr.getClass().toString());
      }
      if (!isFallThroughEdge) {
        newConstraint = newConstraint.negate(false);
      }

    } else {
      throw new RuntimeException(type.toString());
    }

    constraint = constraint.and(newConstraint, false);

    return constraint;
  }

  /**
   * Creates a new constraint when two taints appear in the same condition expression.
   *
   * @param t1 the first taint
   * @param t2 the second taint
   * @param conditionExpr the condition expression
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  public static IConstraint createConstraint(
      boolean recordPath,
      AbstractTaint t1,
      AbstractTaint t2,
      ConditionExpr conditionExpr,
      boolean isFallThroughEdge) {
    IConstraint constraint = ConstraintZ3.getTrue();
    if (t1 instanceof SourceTaint) {
      if (t2 instanceof SourceTaint) {
        constraint =
            createConstraintFromSourceTaints(
                (SourceTaint) t1, (SourceTaint) t2, conditionExpr, isFallThroughEdge);
      }
      if (t2 instanceof ConcreteTaint) {
        constraint =
            createConstraintFromSourceAndConcreteTaints(
                (SourceTaint) t1, (ConcreteTaint) t2, conditionExpr, true, isFallThroughEdge);
      }
      if (t2 instanceof ImpreciseTaint) {
        constraint =
            createConstraintFromSourceAndImpreciseTaint(
                (SourceTaint) t1, (ImpreciseTaint) t2, conditionExpr, true, isFallThroughEdge);
      }
    }
    if (t1 instanceof ConcreteTaint) {
      if (t2 instanceof SourceTaint) {
        constraint =
            createConstraintFromSourceAndConcreteTaints(
                (SourceTaint) t2, (ConcreteTaint) t1, conditionExpr, false, isFallThroughEdge);
      }
      if (t2 instanceof ConcreteTaint) {
        constraint =
            createConstraintFromConcreteTaints(
                (ConcreteTaint) t1, (ConcreteTaint) t2, conditionExpr, isFallThroughEdge);
      }
      if (t2 instanceof ImpreciseTaint) {
        constraint =
            createConstraintFromImpreciseAndConcreteTaints(
                (ImpreciseTaint) t2, (ConcreteTaint) t1, conditionExpr, false, isFallThroughEdge);
      }
    }
    if (t1 instanceof ImpreciseTaint) {
      if (t2 instanceof SourceTaint) {
        constraint =
            createConstraintFromSourceAndImpreciseTaint(
                (SourceTaint) t2, (ImpreciseTaint) t1, conditionExpr, false, isFallThroughEdge);
      }
      if (t2 instanceof ConcreteTaint) {
        constraint =
            createConstraintFromImpreciseAndConcreteTaints(
                (ImpreciseTaint) t1, (ConcreteTaint) t2, conditionExpr, true, isFallThroughEdge);
      }
      if (t2 instanceof ImpreciseTaint) {
        constraint =
            createConstraintFromImpreciseTaints(
                (ImpreciseTaint) t1, (ImpreciseTaint) t2, conditionExpr, isFallThroughEdge);
      }
    }
    if (recordPath) {
      constraint.getPath().addAll(t1.getExtraInfo());
      constraint.getPath().addAll(t2.getExtraInfo());
    }
    return constraint;
  }

  /**
   * Creates a new constraint when the condition expression contains only one taint.
   *
   * @param t the taint
   * @param conditionExpr the condition expression of the if-statement
   * @param negate true, if the constraint should be negated
   * @param isFallThroughEdge true, if the edge is fall through edge
   * @return the constraint
   */
  public static IConstraint createConstraint(
      boolean recordPath,
      AbstractTaint t,
      ConditionExpr conditionExpr,
      boolean negate,
      boolean isFallThroughEdge) {
    IConstraint constraint = ConstraintZ3.getTrue();
    boolean taintOnLeft = !negate;
    if (t instanceof SourceTaint) {
      constraint =
          createConstraintFromSourceTaint(
              (SourceTaint) t, conditionExpr, taintOnLeft, isFallThroughEdge);
    }
    if (t instanceof ConcreteTaint) {
      constraint =
          createConstraintFromConcreteTaint(
              (ConcreteTaint) t, conditionExpr, taintOnLeft, isFallThroughEdge);
    }
    if (t instanceof ImpreciseTaint) {
      constraint =
          createConstraintFromImpreciseTaint(
              (ImpreciseTaint) t, conditionExpr, taintOnLeft, isFallThroughEdge);
    }
    if (t instanceof StringTaint) {
      constraint =
          createConstraintFromStringTaint(
              (StringTaint) t, conditionExpr, taintOnLeft, isFallThroughEdge);
    }

    if (recordPath) constraint.getPath().addAll(t.getExtraInfo());
    return constraint;
  }

  /**
   * Creates a new constraint for callbacks.
   *
   * @param callbackName the callback name
   * @return the constraint
   */
  public static IConstraint createConstraint(String callbackName) {
    BoolExpr expr = SMTSolverZ3.getInstance().makeBoolTerm(callbackName, false);
    return new ConstraintZ3(expr, callbackName, new WitnessPath());
  }

  public static IConstraint createConstantInStringConstraint(
      String name, String constant, StringMethod method) {
    if (name == null || constant == null) {
      throw new RuntimeException("Name or constant is null");
    }
    BoolExpr contains =
        SMTSolverZ3.getInstance().makeStrTermWithOneVariable(name, constant, method);

    return new ConstraintZ3(contains, name, new WitnessPath());
  }

  public static IConstraint createIntCompareConstraint(
      String name,
      int constant,
      Operator operator,
      boolean isFallThroughEdge,
      StringMethod method) {

    BoolExpr expr =
        SMTSolverZ3.getInstance()
            .makeCompareTerm(name, constant, operator, isFallThroughEdge, method);
    return new ConstraintZ3(expr, name, new WitnessPath());
  }
}
