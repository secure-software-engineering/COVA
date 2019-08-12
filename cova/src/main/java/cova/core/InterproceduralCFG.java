/*
 * @version 1.0
 */

package cova.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import soot.Scene;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.Stmt;
import soot.jimple.ThisRef;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;
import soot.jimple.toolkits.ide.icfg.JimpleBasedInterproceduralCFG;
import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;

import cova.vasco.ProgramRepresentation;

/**
 * The Class InterproceduralCFG represents the interprocedural control flow graph.
 * 
 */
public class InterproceduralCFG implements ProgramRepresentation<SootMethod, Unit> {

  /** The cache for control flow graph. */
  private Map<SootMethod, DirectedGraph<Unit>> cfgCache;

  /** The delegated ICFG. */
  protected final BiDiInterproceduralCFG<Unit, SootMethod> delegateICFG;

  public BiDiInterproceduralCFG<Unit, SootMethod> getDelegateICFG() {
    return delegateICFG;
  }

  private String[] excluded = { "java.lang.*", "android.*", "java.io.Serializable" };

  /**
   * Instantiates a new interprocedural CFG.
   */
  public InterproceduralCFG() {
    cfgCache = new HashMap<SootMethod, DirectedGraph<Unit>>();
    delegateICFG = new JimpleBasedInterproceduralCFG(true);
  }

  /**
   * Returns a list containing the entry points.
   *
   * @return the entry points
   * @see Scene#getMainMethod()
   */
  @Override
  public List<SootMethod> getEntryPoints() {
    return Scene.v().getEntryPoints();
  }

  /**
   * Returns an {@link ExceptionalUnitGraph} for a given method.
   *
   * @param method
   *          the method
   * @return the control flow graph
   */
  @Override
  public DirectedGraph<Unit> getControlFlowGraph(SootMethod method) {
    if (method.hasActiveBody()) {
      if (!cfgCache.containsKey(method)) {
        cfgCache.put(method, delegateICFG.getOrCreateUnitGraph(method));
      }
      return cfgCache.get(method);
    } else {
      return null;
    }
  }

  /**
   * Returns <code>true</code> iff the jimple statement contains an invoke expression.
   *
   * @param node
   *          the given statement
   * @return true, if the given statement contains invoke expression
   */
  @Override
  public boolean isCall(Unit node) {
    return ((soot.jimple.Stmt) node).containsInvokeExpr();
  }

  /**
   * Resolves virtual calls using the default call graph and returns a list of methods which are the
   * targets of explicit edges. TODO: Should we consider thread/clinit edges?
   *
   * @param method
   *          the method
   * @param node
   *          the node
   * @return the list
   */
  @Override
  public List<SootMethod> resolveTargets(SootMethod method, Unit node) {
    List<SootMethod> targets = new LinkedList<SootMethod>();
    Iterator<Edge> it = Scene.v().getCallGraph().edgesOutOf(node);
    while (it.hasNext()) {
      Edge edge = it.next();
      if (edge.isExplicit()) {
        targets.add(edge.tgt());
      }
    }
    return targets;
  }

  /**
   * Gets the method contains the given statement.
   *
   * @param node
   *          the given statement
   * @return the method contains the given statement
   */
  public SootMethod getMethodOf(Unit node) {
    return delegateICFG.getMethodOf(node);
  }

  @Override
  public boolean isAnalyzable(SootMethod method) {
    return !method.isPhantom() && method.hasActiveBody();
  }

  /**
   * Checks if is the successor is a fall through successor.
   *
   * @param unit
   *          the current statement
   * @param succ
   *          the successor statement
   * @return true, if the successor is fall through successor
   */
  public boolean isFallThroughSuccessor(Unit unit, Unit succ) {
    return delegateICFG.isFallThroughSuccessor(unit, succ);
  }

  /**
   * Checks if the given statement is call statement.
   *
   * @param unit
   *          the given statement
   * @return true, if the given statement is a call statement
   */
  public boolean isCallStmt(Unit unit) {
    return delegateICFG.isCallStmt(unit);
  }

  /**
   * Gets the callees of call at the given statement.
   *
   * @param unit
   *          the given statement
   * @return the callees of call at the given statement
   */
  public Collection<SootMethod> getCalleesOfCallAt(Unit unit) {
    return delegateICFG.getCalleesOfCallAt(unit);
  }

  /**
   * Gets the predecessors of the given statement.
   *
   * @param unit
   *          the given statement
   * @return the predecessors of the given statement
   */
  public List<Unit> getPredsOf(Unit unit) {
    return delegateICFG.getPredsOf(unit);
  }

  /**
   * Checks if there is a predecessor is a LookupSwitchStmt.
   *
   * @param unit
   *          the unit
   * @return true, if successful
   */
  public boolean hasPredAsLookupSwitchStmt(Unit unit) {
    for (Unit pred : delegateICFG.getPredsOf(unit)) {
      if (pred instanceof LookupSwitchStmt) {
        return true;
      }
    }
    return false;
  }


  public Unit getPredAsLookupSwitchStmt(Unit unit) {
    for (Unit pred : delegateICFG.getPredsOf(unit)) {
      if (pred instanceof LookupSwitchStmt) {
        return pred;
      }
    }
    return null;
  }
  /**
   * Return the first identity statement assigning from \@this.
   *
   * @param method
   *          the method
   * @return the first identity statement assigning from \@this
   */
  public IdentityStmt getIdentityStmt(SootMethod method) {
    for (Unit s : method.getActiveBody().getUnits()) {
      if (s instanceof IdentityStmt && ((IdentityStmt) s).getRightOp() instanceof ThisRef) {
        return (IdentityStmt) s;
      }
    }
    throw new RuntimeException("couldn't find identityref!" + " in " + method);
  }

  @Override
  public boolean isSkipCall(Unit node) {
    if (node instanceof Stmt) {
      Stmt stmt = (Stmt) node;
      if (stmt.containsInvokeExpr()) {
        return SkipMethodOrClassRuleManager.getInstance().isSkipCall(stmt.getInvokeExpr());
      }
    }
    return false;
  }

  public boolean isExcludedMethod(SootMethod method) {
    String name = method.getDeclaringClass().getName();
    for (String pkg : excluded) {
      if (name.equals(pkg) || ((pkg.endsWith(".*") || pkg.endsWith("$*"))
          && name.startsWith(pkg.substring(0, pkg.length() - 1)))) {
        return true;
      }
    }
    return false;
  }


}
