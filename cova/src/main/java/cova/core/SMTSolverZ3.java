/*
 * @version 1.0
 */

package cova.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.z3.ApplyResult;
import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Goal;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Tactic;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;

import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.LongType;
import soot.PrimType;
import soot.RefType;
import soot.ShortType;
import soot.Type;
import soot.Value;
import soot.jimple.ConditionExpr;
import soot.jimple.EqExpr;
import soot.jimple.GeExpr;
import soot.jimple.GtExpr;
import soot.jimple.LeExpr;
import soot.jimple.LtExpr;
import soot.jimple.NeExpr;

import cova.data.Operator;

/**
 * The Class SMTSolverZ3 creates Z3 boolean expression and solves boolean constraints.
 * 
 */
public class SMTSolverZ3 {

  /** The instance. */
  private static SMTSolverZ3 instance;

  /** The Z3 context. */
  private Context ctx;

  /** The true expression. */
  private BoolExpr TRUE;

  /** The false expression. */
  private BoolExpr FALSE;

  /** The Z3 Query count. */
  private static int count;

  private static double usedTime;

  /** Cache the results of Z3 queries */
  private static LoadingCache<BoolExpr, Status> solverCache;

  /**
   * Gets the single instance of SMTSolverZ3.
   *
   * @return single instance of SMTSolverZ3
   */
  public static SMTSolverZ3 getInstance() {
    if (instance == null) {
      instance = new SMTSolverZ3();
    }
    return instance;
  }

  private SMTSolverZ3() {
    ctx = new Context();
    TRUE = ctx.mkTrue();
    FALSE = ctx.mkFalse();
    count = 0;
    usedTime = 0;
    solverCache = CacheBuilder.newBuilder().build(new CacheLoader<BoolExpr, Status>() {
      @Override
      public Status load(BoolExpr constraint) throws Exception {
        Status status = solverCache.getIfPresent(constraint);
        if (status == null) {
          Solver s = ctx.mkSolver();
          s.add(constraint);
          status = s.check();
          solverCache.put(constraint, status);
        }
        return status;
      }

    });
  }

  /**
   * Reset.
   */
  public void reset() {
    count = 0;
    usedTime = 0;
  }

  /**
   * This function solves the boolean formula (op1 op op2) using the "ctx-solver-simplify" tactic of Z3 and returns the
   * result in CNF(Conjunctive Normal Form). So far it only supports AND and OR operators.
   *
   * @param op1
   *          the first operand
   * @param op2
   *          the second operand (optional )
   * @param op
   *          the operator
   * @param simplify
   *          true if the constraint should be simplified.
   * @return the simplified CNF by applying the operator op to op1 and op2.
   */
  public BoolExpr solve(BoolExpr op1, BoolExpr op2, Operator op, boolean simplify) {
    BoolExpr result = ctx.mkBool(true);
    if (op.equals(Operator.AND)) {
      result = ctx.mkAnd(op1, op2);
    } else if (op.equals(Operator.OR)) {
      result = ctx.mkOr(op1, op2);
    } else {
      throw new RuntimeException("Operator can only be AND and OR.");
    }
    if (simplify) {
      result = applyCtxSolverSimplify(result);
    }
    return result;
  }

  /**
   * This function proves if the given two boolean expressions are equivalent.
   *
   * @param expr1
   *          the first expression
   * @param expr2
   *          the second expression
   * @return true, if expr1 and expr2 are equivalent.
   */
  public boolean prove(BoolExpr expr1, BoolExpr expr2) {
    BoolExpr equlity = ctx.mkEq(expr1, expr2);
    Solver solver = ctx.mkSolver();
    solver.add(ctx.mkNot(equlity));
    Status q = solver.check();
    boolean ret = false;
    switch (q) {
      case UNKNOWN:
        break;
      case SATISFIABLE:
        // System.out.println("PROOF: " + solver.getModel());
        break;
      case UNSATISFIABLE:
        ret = true;
        break;
    }
    return ret;
  }

  /**
   * Negate the given expression.
   *
   * @param expr
   *          the given expression
   * @param simplify
   *          true if the constraint should be simplified.
   * @return the negation
   */
  public BoolExpr negate(BoolExpr expr, boolean simplify) {
    BoolExpr result = ctx.mkNot(expr);
    if (simplify) {
      result = applyCtxSolverSimplify(result);
    }
    return result;
  }

  /**
   * Gets the true expression.
   *
   * @return the boolean value "true" in Z3 BoolExpr form
   */
  public BoolExpr getTrue() {
    return TRUE;
  }

  /**
   * Gets the false expression.
   *
   * @return the boolean value "false" in Z3 BoolExpr form
   */
  public BoolExpr getFalse() {
    return FALSE;
  }

  /**
   * Checks if the given boolean constraint is satisfiable.
   *
   * @param constraint
   *          the constraint
   * @return true, if the given boolean constraint is satisfiable
   */
  public boolean isSatisfiable(BoolExpr constraint) {
    boolean ret = false;
    if (constraint != null) {
      try {
        Status q = solverCache.get(constraint);
        switch (q) {
          case UNKNOWN:
            break;
          case SATISFIABLE:
            ret = true;
            break;
          case UNSATISFIABLE:
            break;
        }
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
    } else {
      throw new RuntimeException("Constraint sent to solver is null");
    }
    return ret;
  }

  /**
   * Applies tatic ctx-solver-simplify.
   *
   * @param expr
   *          the expression
   * @return the simplified expression
   */
  private BoolExpr applyCtxSolverSimplify(BoolExpr expr) {
    if (expr.equals(TRUE) || expr.equals(FALSE)) {
      return expr;
    } else {
      if (isSatisfiable(expr)) {
        Goal goal = ctx.mkGoal(false, false, false);
        goal.add(expr);
        Tactic tac = ctx.mkTactic("ctx-solver-simplify");
        ApplyResult a = tac.apply(goal);
        if (a.getSubgoals().length > 0) {
          BoolExpr ret = null;
          for (Goal subgoal : a.getSubgoals()) {
            BoolExpr[] formulas = subgoal.getFormulas();
            if (formulas.length > 0) {
              ret = formulas[0];
              if (formulas.length > 1) {
                for (int i = 1; i < formulas.length; i++) {
                  ret = ctx.mkAnd(ret, formulas[i]);
                }
              }
            }
          }
          if (ret != null) {
            return ret;
          } else {
            return ctx.mkBool(true);// if ret==null, it means the result is true
          }
        }
      }
      return FALSE;
    }
  }

  /**
   * Gets the count.
   *
   * @return the count
   */
  public int getCount() {
    return count;
  }

  /**
   * Make boolean equality expr1==expr2.
   *
   * @param expr1
   *          the expr1
   * @param expr2
   *          the expr2
   * @return the equality expression.
   */
  public BoolExpr makeEquality(BoolExpr expr1, BoolExpr expr2) {
    return ctx.mkEq(expr1, expr2);
  }

  /**
   * Makes boolean term.
   *
   * @param name
   *          the name of the term
   * @param negate
   *          true, if the boolean expression should be negated.
   * @return the boolean expression
   */
  public BoolExpr makeBoolTerm(String name, boolean negate) {
    BoolExpr expr = ctx.mkBoolConst(name);
    if (negate) {
      return ctx.mkNot(expr);
    } else {
      return expr;
    }
  }

  /**
   * Makes boolean term.
   *
   * @param name
   *          the name
   * @param v
   *          the value
   * @param equal
   *          true, if the condition expression is an equality
   * @param negate
   *          true, if the boolean expression should be negated
   * @return the boolean expression
   */
  public BoolExpr makeBoolTerm(String name, int v, boolean equal, boolean negate) {
    BoolExpr ret = null;
    BoolExpr expr = ctx.mkBoolConst(name);
    BoolExpr nExpr = ctx.mkNot(expr);
    if (v == 0 && equal) {
      ret = nExpr;
    } else if (v == 0 && !equal) {
      ret = expr;
    } else if (v == 1 && equal) {
      ret = expr;
    } else if (v == 1 && !equal) {
      ret = nExpr;
    } else {
      return ret;
    }
    if (negate) {
      if (ret == nExpr) {
        ret = expr;
      } else if (ret == expr) {
        ret = nExpr;
      }
    }
    return ret;
  }

  /**
   * Translate.
   *
   * @param conditionExpr
   *          the condition expr
   * @return the operator
   */
  public Operator translate(ConditionExpr conditionExpr) {
    Operator operator = null;
    if (conditionExpr instanceof EqExpr) {
      operator = Operator.EQ;
    } else if (conditionExpr instanceof GeExpr) {
      operator = Operator.GE;
    } else if (conditionExpr instanceof GtExpr) {
      operator = Operator.GT;
    } else if (conditionExpr instanceof LeExpr) {
      operator = Operator.LE;
    } else if (conditionExpr instanceof LtExpr) {
      operator = Operator.LT;
    } else if (conditionExpr instanceof NeExpr) {
      operator = Operator.NE;
    }
    return operator;
  }

  /**
   * Make non-terminal boolean expression of form left op right.
   *
   * @param left
   *          the left operand
   * @param leftConstant
   *          the left operand is constant
   * @param right
   *          the right operand
   * @param rightConstant
   *          the right operand constant
   * @param type
   *          the type of the operands
   * @param operator
   *          the operator
   * @return the boolean expression
   */
  public BoolExpr makeNonTerminalExpr(String left, boolean leftConstant, String right, boolean rightConstant, Type type,
      Operator operator) {
    Expr leftExpr = null;
    Expr rightExpr = null;
    if (type instanceof PrimType) {
      if (type instanceof BooleanType || left.equals("true") || left.equals("false") || right.equals("true")
          || right.equals("false")) { // z3 boolsort: it can happen a
                                      // boolean value has integer type
                                      // because of type inference
        if (leftConstant) {
          leftExpr = ctx.mkBool(Boolean.parseBoolean(left));
        } else {
          leftExpr = ctx.mkBoolConst(left);
        }
        if (rightConstant) {
          rightExpr = ctx.mkBool(Boolean.parseBoolean(right));
        } else {
          rightExpr = ctx.mkBoolConst(right);
        }
      } else if (type instanceof ByteType || type instanceof CharType || type instanceof ShortType || type instanceof IntType
          || type instanceof LongType) {// z3 intsort
        if (leftConstant) {
          if (type instanceof LongType) {
            leftExpr = ctx.mkInt(left.substring(0, left.length() - 1));
          } else {
            leftExpr = ctx.mkInt(left);
          }
        } else {
          leftExpr = ctx.mkIntConst(left);
        }
        if (rightConstant) {
          if (type instanceof LongType) {
            rightExpr = ctx.mkInt(right.substring(0, right.length() - 1));
          } else {
            rightExpr = ctx.mkInt(right);
          }
        } else {
          rightExpr = ctx.mkIntConst(right);
        }
      } else if (type instanceof FloatType || type instanceof DoubleType) {// z3 realsort
        if (leftConstant) {
          leftExpr = ctx.mkReal(left);
        } else {
          leftExpr = ctx.mkRealConst(left);
        }
      }
    } else if (type instanceof RefType) {
      if (((RefType) type).getClassName().equals(String.class.getName())) {// z3 stringsort
        if (leftConstant && !left.equals("null")) {
          leftExpr = ctx.MkString(left);
        } else {
          leftExpr = ctx.mkConst(left, ctx.getStringSort());
        }
        right = StringUtils.remove(right, "\"");
        if (rightConstant && !right.equals("null")) {
          rightExpr = ctx.MkString(right);
        } else {
          rightExpr = ctx.mkConst(right, ctx.getStringSort());
        }
      } else {
        Sort uninterpretedSort = ctx.mkUninterpretedSort(type.toString());// z3 uninterpretedsort
        leftExpr = ctx.mkConst(left, uninterpretedSort);
        rightExpr = ctx.mkConst(right, uninterpretedSort);
      }
    } else {
      Sort uninterpretedSort = ctx.mkUninterpretedSort(type.toString());// z3 uninterpretedsort
      leftExpr = ctx.mkConst(left, uninterpretedSort);
      rightExpr = ctx.mkConst(right, uninterpretedSort);
    }
    BoolExpr expr = getTrue();
    if (leftExpr != null && rightExpr != null) {
      switch (operator) {
        case EQ:
          expr = ctx.mkEq(leftExpr, rightExpr);
          break;
        case NE:
          expr = ctx.mkNot(ctx.mkEq(leftExpr, rightExpr));
          break;
        case LT:
          expr = ctx.mkLt((ArithExpr) leftExpr, (ArithExpr) rightExpr);
          break;
        case LE:
          expr = ctx.mkLe((ArithExpr) leftExpr, (ArithExpr) rightExpr);
          break;
        case GT:
          expr = ctx.mkGt((ArithExpr) leftExpr, (ArithExpr) rightExpr);
          break;
        case GE:
          expr = ctx.mkGe((ArithExpr) leftExpr, (ArithExpr) rightExpr);
          break;
        default:
          break;
      }
    }
    return expr;
  }

  /**
   * Make non-terminal boolean expression of form left op right.
   *
   * @param left
   *          the left operand
   * @param leftConstant
   *          the left operand is constant
   * @param right
   *          the right operand
   * @param rightConstant
   *          the right operand constant
   * @param conditionExpr
   *          the conditional expression.
   * 
   * @return the boolean expression
   */
  public BoolExpr makeNonTerminalExpr(Value left, boolean leftConstant, Value right, boolean rightConstant,
      ConditionExpr conditionExpr) {
    Operator operator = translate(conditionExpr);
    if (operator != null) {
      return makeNonTerminalExpr(left.toString(), leftConstant, right.toString(), rightConstant, left.getType(), operator);
    } else {
      throw new RuntimeException("Unsupported conditional expr: " + conditionExpr.toString());
    }
  }

  /**
   * Only used for test
   * 
   * @param exprs
   * @param simplify
   * @return
   */
  public BoolExpr makeConjunction(List<BoolExpr> exprs, boolean simplify) {
    if (exprs.size() < 2) {
      throw new RuntimeException("Number of expressions to be conjucated must be at lease 2");
    } else {
      BoolExpr ret = exprs.get(0);
      for (int i = 1; i < exprs.size(); i++) {
        ret = solve(ret, exprs.get(i), Operator.AND, false);
      }
      if (simplify) {
        ret = applyCtxSolverSimplify(ret);
      }
      return ret;
    }
  }

  public void incUsedTime(double duration) {
    usedTime += duration;
  }

  public void incCount() {
    count++;
  }

  public double getUsedTimeInSeconds() {
    return usedTime / 1000;
  }
}
