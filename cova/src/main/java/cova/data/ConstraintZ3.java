
package cova.data;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Expr;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;

import cova.core.SMTSolverZ3;
import cova.source.symbolic.SymbolicNameManager;

/**
 * The Class ConstraintZ3 represented by the Z3 boolean expression {@link BoolExpr}.
 *
 * @date 07.08.2017
 */
public class ConstraintZ3 implements IConstraint {

  /** The Z3 boolean expression represents this constraint. */
  private BoolExpr expr;

  /** The symbolic names used in this constraint. */
  private final List<String> symbolicNames;

  /**
   * True, for better readability print the user-defined names rather than symbolic names of sources
   * created by {@link SymbolicNameManager}.
   */
  private static boolean printSourceName = false;

  private static boolean showInfix = false;

  /** The Z3 SMT solver. */
  private final static SMTSolverZ3 solver = SMTSolverZ3.getInstance();

  private static LoadingCache<ConstraintZ3, String> expressionCache;

  private static ConstraintZ3 TRUE = new ConstraintZ3(SMTSolverZ3.getInstance().getTrue(),
      new ArrayList<String>());

  private static ConstraintZ3 FALSE = new ConstraintZ3(SMTSolverZ3.getInstance().getFalse(),
      new ArrayList<String>());

  static {
    expressionCache = CacheBuilder.newBuilder().build(new CacheLoader<ConstraintZ3, String>() {
      @Override
      public String load(ConstraintZ3 constraint) throws Exception {
        BoolExpr expr = constraint.expr;
        String rep = expressionCache.getIfPresent(constraint);
        if (rep == null) {
          if (expr.isTrue() || expr.isFalse()) {
            rep = expr.toString();
          } else {
            rep = convertToInfixExpr(expr, false);
          }
          rep = StringUtils.replace(rep, "|", "");
          if (printSourceName) {
            for (final String symbolicName : constraint.symbolicNames) {
              final String sourceName = SymbolicNameManager.getInstance()
                  .getSourceName(symbolicName);
              rep = rep.replace(symbolicName, sourceName);
            }
          }
          expressionCache.put(constraint, rep);
        }
        return rep;
      }
    });
  }

  /**
   * Instantiates a new constraint.
   *
   * @param expr
   *          the Z3 boolean expression represents this constraint.
   * @param symbolicName
   *          the single symbolic name used in the boolean expression.
   */
  public ConstraintZ3(BoolExpr expr, String symbolicName) {
    this.expr = expr;
    symbolicNames = new ArrayList<String>();
    symbolicNames.add(symbolicName);
  }

  /**
   * Instantiates a new constraint.
   *
   * @param expr
   *          the Z3 boolean expression represents this constraint.
   * @param symbolicNames
   *          the symbolic names used in the boolean expression,.
   */
  public ConstraintZ3(BoolExpr expr, List<String> symbolicNames) {
    this.expr = expr;
    this.symbolicNames = symbolicNames;
  }

  public BoolExpr getExpr() {
    return expr;
  }

  @Override
  public IConstraint and(IConstraint other, boolean simplify) {
    if (other.isTrue() || equals(other)) {
      return this;
    }
    if (isTrue()) {
      return other;
    }
    if (isFalse() || other.isFalse()) {
      return getFalse();
    }
    final ConstraintZ3 otherZ3 = (ConstraintZ3) other;
    if (simplify) {
      solver.incCount();
    }
    long startTime = System.nanoTime();
    final BoolExpr and = solver.solve(expr, otherZ3.expr, Operator.AND, simplify);
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1000000;
    solver.incUsedTime(duration);
    ConstraintZ3 ret;
    if (symbolicNames != null) {
      final ArrayList<String> combinedSymbolicNames = new ArrayList<String>(symbolicNames);
      for (String name : otherZ3.getSymbolicNames()) {
        if (!combinedSymbolicNames.contains(name)) {
          combinedSymbolicNames.add(name);
        }
      }
      ret = new ConstraintZ3(and, combinedSymbolicNames);
    } else {
      ret = new ConstraintZ3(and, otherZ3.getSymbolicNames());
    }
    return ret;
  }

  @Override
  public IConstraint or(IConstraint other, boolean simplify) {
    if (isTrue() || other.isTrue()) {
      return getTrue();
    }
    if (other.isFalse() || equals(other)) {
      return this;
    }
    if (isFalse()) {
      return other;
    }
    final ConstraintZ3 otherZ3 = (ConstraintZ3) other;
    if (expr.equals(otherZ3.expr)) {
      return this;
    }
    if (simplify) {
      solver.incCount();
    }
    long startTime = System.nanoTime();
    final BoolExpr or = solver.solve(expr, otherZ3.expr, Operator.OR, simplify);
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1000000;
    solver.incUsedTime(duration);
    ConstraintZ3 ret;
    if (symbolicNames != null) {
      final ArrayList<String> combinedSymbolicNames = new ArrayList<String>(symbolicNames);
      for (String name : otherZ3.getSymbolicNames()) {
        if (!combinedSymbolicNames.contains(name)) {
          combinedSymbolicNames.add(name);
        }
      }
      ret = new ConstraintZ3(or, combinedSymbolicNames);
    } else {
      ret = new ConstraintZ3(or, otherZ3.getSymbolicNames());
    }
    return ret;
  }

  @Override
  public IConstraint negate(boolean simplify) {
    if (simplify) {
      solver.incCount();
    }
    long startTime = System.nanoTime();
    final BoolExpr negation = solver.negate(expr, simplify);
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1000000;
    solver.incUsedTime(duration);
    ConstraintZ3 ret = new ConstraintZ3(negation, symbolicNames);
    return ret;
  }

  @Override
  public IConstraint copy() {
    return new ConstraintZ3(expr, symbolicNames);
  }

  @Override
  public boolean isTrue() {
    return equals(TRUE);
  }

  @Override
  public boolean isFalse() {
    return equals(FALSE);
  }

  /**
   * This function convert the Z3 s-expression to infix expression for better readability.
   *
   * @param expr
   *          the Z3 s-expression.
   * @param negate
   *          if the expression should be negated.
   * @return the string of infix expression
   */
  private static String convertToInfixExpr(Expr expr, boolean negate) {
    String s = "";
    if (expr.getNumArgs() > 1) {
      // Non-terminal expression
      String symbol = expr.getFuncDecl().getName().toString();
      if (symbol.equals("and")) {
        symbol = " \u2227 ";// and
      } else if (symbol.equals("or")) {
        symbol = " \u2228 ";// or
      } else if (symbol.equals("<=")) {
        if (negate) {
          symbol = " > ";
        }
      } else if (symbol.equals("<")) {
        if (negate) {
          symbol = " >= ";
        }
      } else if (symbol.equals(">=")) {
        if (negate) {
          symbol = " < ";
        }
      } else if (symbol.equals(">")) {
        if (negate) {
          symbol = " <= ";
        }
      } else if (symbol.equals("=") || symbol.equals("iff")) {
        symbol = " = ";
        if (negate) {
          symbol = " != ";
        }
      }
      s += "(";
      final Expr[] children = expr.getArgs();
      for (int i = 0; i < children.length; i++) {
        s += convertToInfixExpr(children[i], false);
        if (i < children.length - 1) {
          s += symbol;
        }
      }
      s += ")";

    } else {
      if (expr.getNumArgs() == 1) {
        // Negation
        final Expr child = expr.getArgs()[0];
        final String symbol = expr.getFuncDecl().getName().toString();
        if (symbol.equals("not")) {
          s += "!" + convertToInfixExpr(child, false);
        }

      } else {
        // Term
        if (expr != null) {
          if (expr.equals(solver.getTrue())) {
            s = "true";
          } else if (expr.equals(solver.getFalse())) {
            s = "false";
          } else {
            s = expr.toString();
          }
        }
      }
    }
    return s;
  }

  @Override
  public String toString() {
    if (showInfix) {
      String s = null;
      try {
        s = expressionCache.get(this);
      } catch (ExecutionException e) {
        e.printStackTrace();
      }
      return s;
    } else {
      String s = StringUtils.replace(expr.toString(), "|", "");
      return StringUtils.replace(s, "\n", "");
    }
  }

  @Override
  public String toReadableString() {
    if (!printSourceName) {
      printSourceName = true;
    }
    if (!showInfix) {
      showInfix = true;
    }
    String s = toString();
    printSourceName = false;
    showInfix = false;
    return s;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((expr == null) ? 0 : expr.hashCode());
    result = prime * result + ((symbolicNames == null) ? 0 : symbolicNames.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ConstraintZ3 other = (ConstraintZ3) obj;
    if (expr == null) {
      if (other.expr != null) {
        return false;
      }
    } else {
      return expr.equals(other.expr);
    }
    return true;
  }

  /**
   * Gets the true expression.
   *
   * @return the true expression.
   */
  public static ConstraintZ3 getTrue() {
    if (TRUE == null) {
      TRUE = new ConstraintZ3(SMTSolverZ3.getInstance().getTrue(), new ArrayList<String>());
    }
    return TRUE;
  }

  /**
   * Gets the false expression.
   *
   * @return the false expression.
   */
  public static ConstraintZ3 getFalse() {
    if (FALSE == null) {
      FALSE = new ConstraintZ3(SMTSolverZ3.getInstance().getFalse(), new ArrayList<String>());
    }
    return FALSE;
  }

  /**
   * Gets the symbolic names used in this constraints.
   *
   * @return the symbolic names used in this constraints.
   */
  public List<String> getSymbolicNames() {
    return symbolicNames;
  }

  @Override
  public boolean isMoreConstrained(IConstraint other) {
    long startTime = System.nanoTime();
    final ConstraintZ3 otherZ3 = (ConstraintZ3) other;
    boolean ret = false;
    final BoolExpr equality = SMTSolverZ3.getInstance().makeEquality(expr, otherZ3.expr);
    solver.incCount();
    if (!SMTSolverZ3.getInstance().isSatisfiable(equality)) {
      // if the and expression is equal to this constraint, then this constraint is more constrained
      solver.incCount();
      final BoolExpr andExpr = SMTSolverZ3.getInstance().solve(expr, otherZ3.expr, Operator.AND,
          true);
      final BoolExpr equalityAnd = SMTSolverZ3.getInstance().makeEquality(expr, andExpr);
      if (SMTSolverZ3.getInstance().isSatisfiable(equalityAnd)) {
        ret = true;
      }
    }
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1000000;
    solver.incUsedTime(duration);
    return ret;
  }

  @Override
  public void simplify() {
    if (expr.isTrue() || expr.isFalse()) {
      return;
    }
    long startTime = System.nanoTime();
    solver.incCount();
    if (!SMTSolverZ3.getInstance().isSatisfiable(expr)) {
      expr = FALSE.expr;
    } else if (expr.getNumArgs() > 1) {
      solver.incCount();
      expr = (BoolExpr) expr.simplify();
    }
    long endTime = System.nanoTime();
    double duration = (endTime - startTime) / 1000000;
    solver.incUsedTime(duration);
  }

  public int getSize() {
    return astSize(expr);
  }

  /**
   * return the number of nodes in the abstract syntax tree of given expr.
   * 
   * @param expr
   * @return
   */
  private int astSize(Expr expr) {
    if (expr.getNumArgs() <= 1) {
      if (expr.getFuncDecl().getName().toString().equals("not")) {
        return 2;
      } else {
        return 1;
      }
    } else {
      int size = 1;
      for (Expr e : expr.getArgs()) {
        size += astSize(e);
      }
      return size;
    }
  }

  public ConstraintType getConstraintType() {
    expr.simplify();
    String s = expr.toString();
    if (s.equals("true")) {
      return ConstraintType.NONE;
    }
    if (s.equals("false")) {
      return ConstraintType.INFEASIBLE;
    }
    boolean u = s.contains("U");
    boolean i = s.contains("I");
    boolean c = s.contains("C");
    if (u && !i && !c) {
      return ConstraintType.U_Constraint;
    } else if (!u && i && !c) {
      return ConstraintType.I_Constraint;
    } else if (!u && !i && c) {
      return ConstraintType.C_Constraint;
    } else if (u && i && !c) {
      return ConstraintType.UI_Constraint;
    } else if (u && !i && c) {
      return ConstraintType.UC_Constraint;
    } else if (!u && i && c) {
      return ConstraintType.IC_Constraint;
    } else if (u && i && c) {
      return ConstraintType.UIC_Constraint;
    } else {
      return ConstraintType.NONE;
    }
  }
}
