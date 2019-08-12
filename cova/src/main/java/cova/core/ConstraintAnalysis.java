/*
 * @version 1.0
 */

package cova.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.DirectedGraph;

import cova.data.Abstraction;
import cova.data.IConstraint;
import cova.source.data.Source;
import cova.vasco.Context;
import cova.vasco.ForwardInterProceduralAnalysis;
import cova.vasco.ProgramRepresentation;

/**
 * The Class ConstraintAnalysis defines all flow functions for handling constraints.
 * 
 */
public class ConstraintAnalysis extends ForwardInterProceduralAnalysis<SootMethod, Unit, Abstraction> {

  /** The interprocedural control flow graph. */
  private final InterproceduralCFG icfg;

  /** The rule manager. */
  private final RuleManager ruleManager;

  /** The constraint map that maps each unit to its constraint. */
  private Map<Unit, IConstraint> constraintMap;

  /**
   * Instantiates a new constraint analysis.
   *
   * @param ruleManager
   *          the rule manager
   */
  public ConstraintAnalysis(RuleManager ruleManager) {
    super();
    SMTSolverZ3.getInstance().reset();
    icfg = ruleManager.getIcfg();
    this.ruleManager = ruleManager;
    timeOutOn = ruleManager.getConfig().isTimeOutOn();
    timeOutDuration = ruleManager.getConfig().getTimeOutDuration();
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.ForwardInterProceduralAnalysis#initContext(java.lang.Object, java.lang.Object)
   */
  @Override
  protected Context<SootMethod, Unit, Abstraction> initContext(SootMethod method, Abstraction entryValue) {
    if (icfg.isAnalyzable(method) && !icfg.isExcludedMethod(method)) {
      return super.initContext(method, entryValue);
    } else {
      return super.initContextForPhantomMethod(method, entryValue);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.ForwardInterProceduralAnalysis#normalFlowFunction(vasco.Context, java.lang.Object, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public Abstraction normalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ,
      Abstraction in) {
    if (in.isBottomValue()) {
      return in;
    }
    return ruleManager.applyNormalFlowFunction(context, node, succ, in);
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.ForwardInterProceduralAnalysis#callEntryFlowFunction(vasco.Context, java.lang.Object, java.lang.Object,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public Abstraction callEntryFlowFunction(Context<SootMethod, Unit, Abstraction> context, SootMethod callee, Unit node,
      Unit succ, Abstraction in) {
    if (icfg.isAnalyzable(callee)) {
      if (in.isBottomValue()) {
        return in;
      }
      return ruleManager.applyCallEntryFlowFunction(context, callee, node, succ, in);
    } else {
      return bottomValue();
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.ForwardInterProceduralAnalysis#callExitFlowFunction(vasco.Context, java.lang.Object, java.lang.Object,
   * java.lang.Object, java.lang.Object)
   */
  @Override
  public Abstraction callExitFlowFunction(Context<SootMethod, Unit, Abstraction> context, SootMethod callee, Unit node,
      Unit succ, Abstraction exitValue) {
    if (exitValue.isBottomValue()) {
      return exitValue;
    }
    return ruleManager.applyCallExitFlowFunction(context, callee, node, succ, exitValue);
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.ForwardInterProceduralAnalysis#callLocalFlowFunction(vasco.Context, java.lang.Object, java.lang.Object,
   * java.lang.Object)
   */
  @Override
  public Abstraction callLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node, Unit succ,
      Abstraction in) {
    if (in.isBottomValue()) {
      return in;
    }
    return ruleManager.applyCallLocalFlowFunction(context, node, succ, in);
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#boundaryValue(java.lang.Object)
   */
  @Override
  public Abstraction boundaryValue(SootMethod entryPoint) {
    return Abstraction.topValue();
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#copy(java.lang.Object)
   */
  @Override
  public Abstraction copy(Abstraction src) {
    if (src == null) {
      throw new NullPointerException();
    } else {
      return new Abstraction(src);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#shallowMeet(java.lang.Object, java.lang.Object)
   */
  @Override
  public Abstraction shallowMeet(Abstraction a, Abstraction b) {
    if (a.isBottomValue()) {
      return b;
    }
    if (b.isBottomValue()) {
      return a;
    }
    return Abstraction.shallowMeet(a, b);
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#meet(java.lang.Object, java.lang.Object)
   */
  @Override
  public Abstraction meet(Abstraction a, Abstraction b) {
    if (a == null && b == null) {
      return bottomValue();
    } else if (a != null && b == null) {
      return a;
    } else if (b != null && a == null) {
      return b;
    } else {
      return Abstraction.meet(a, b);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#merge(java.lang.Object, java.lang.Object)
   */
  @Override
  public Abstraction merge(Abstraction local, Abstraction ret) {
    return Abstraction.merge(local, ret);
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#programRepresentation()
   */
  @Override
  public ProgramRepresentation<SootMethod, Unit> programRepresentation() {
    return icfg;
  }

  /*
   * (non-Javadoc)
   * 
   * @see vasco.InterProceduralAnalysis#bottomValue()
   */
  @Override
  public Abstraction bottomValue() {
    return Abstraction.bottomValue();
  }

  /**
   * Compute constraint map by conjoining the in values from all contexts.
   *
   * @return the constraint map
   */
  public Map<Unit, IConstraint> getConstraintMap() {
    if (constraintMap != null && !constraintMap.isEmpty()) {
      // it means the constraintMap is already computed, return it directly
      return constraintMap;
    }
    logger.info("Computing constraint map for all reachable methods...");
    constraintMap = new HashMap<Unit, IConstraint>();
    // Merge the in values over all contexts
    for (SootMethod method : contexts.keySet()) {
      List<Context<SootMethod, Unit, Abstraction>> valueContexts = contexts.get(method);
      DirectedGraph<Unit> cfg = programRepresentation().getControlFlowGraph(method);
      for (Unit node : cfg) {
        IConstraint in = null;
        for (Context<SootMethod, Unit, Abstraction> context : valueContexts) {
          IConstraint c = context.getValueBefore(node).getConstraintOfStmt();
          if (c != null) {
            if (in == null) {
              in = c;
            } else {
              in = in.or(c, true);
            }
          }
        }
        if (in != null) {
          constraintMap.put(node, in);
        }
      }
    }
    logger.info("Constraint map is computed.");
    return constraintMap;
  }

  /**
   * Only compute constraint map for given method.
   *
   * @param method
   *          the method
   * @return the constraint map
   */
  public Map<Unit, IConstraint> getConstraintMap(SootMethod method) {
    Map<Unit, IConstraint> constraintMapForMethod = new HashMap<>();
    logger.info("Computing constraint map for method: " + method.toString());
    if (contexts.containsKey(method)) {
      DirectedGraph<Unit> cfg = programRepresentation().getControlFlowGraph(method);
      List<Context<SootMethod, Unit, Abstraction>> valueContexts = contexts.get(method);
      for (Unit node : cfg) {
        IConstraint in = null;
        if (constraintMap != null && constraintMap.containsKey(node)) {
          // already computed before, directly return constraint.
          in = constraintMap.get(node);
        } else {
          for (Context<SootMethod, Unit, Abstraction> context : valueContexts) {
            IConstraint c = context.getValueBefore(node).getConstraintOfStmt();
            if (c != null) {
              if (in == null) {
                in = c;
              } else {
                in = in.or(c, true);
              }
            }
          }
        }
        if (in != null) {
          constraintMapForMethod.put(node, in);
          if (constraintMap == null) {
            constraintMap=new HashMap<>();
          }
          constraintMap.put(node, in);
        }
      }
    }
    logger.info("Constraint map is computed.");
    return constraintMapForMethod;
  }
  
  public Set<Source> getSources() {
    return this.ruleManager.getSourceAndCallbackManager().getSources();
  }
}
