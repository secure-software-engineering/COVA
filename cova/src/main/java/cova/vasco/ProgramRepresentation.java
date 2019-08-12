

package cova.vasco;

import java.util.List;

import soot.toolkits.graph.DirectedGraph;

/**
 * A wrapper for the API used by the underlying intermediate representation over which
 * inter-procedural analysis is to be performed.
 * 
 * <p>
 * In particular, the program representation should specify program entry points, build control flow
 * graphs for a given method and resolve virtual method calls.
 * </p>
 * 
 *
 * @param <M>
 *          the type of a method
 * @param <N>
 *          the type of a node in the CFG
 */
public interface ProgramRepresentation<M, N> {

  /**
   * Returns a list of program entry points (methods). The entry points may be static or non-static.
   * 
   * <p>
   * Client analyses implementing an {@link InterProceduralAnalysis} must implement the
   * {@link InterProceduralAnalysis#boundaryValue(Object) boundaryValue} method for each entry point
   * specified by the program representation.
   * </p>
   * 
   * @return a list of program entry points (methods)
   */
  public List<M> getEntryPoints();

  /**
   * Returns an intra-procedural control-flow-graph for a given procedure (method).
   * 
   * <p>
   * The returned CFG may include exceptional control transfer in addition to conditional and
   * unconditional jumps, but does not include inter-procedural call/return edges. Nodes containing
   * method calls are treated like nodes containing ordinary imperative instructions.
   * </p>
   * 
   * @param method
   *          the method whose CFG to return
   * @return an intra-procedural control-flow-graph for a given procedure (method)
   */
  public DirectedGraph<N> getControlFlowGraph(M method);

  /**
   * Returns whether a given node contains a method call.
   * 
   * @param node
   *          a node in the control-flow graph
   * @return whether a given node contains a method call.
   */
  public boolean isCall(N node);

  public boolean isAnalyzable(M method);

  /**
   * Returns a list of target methods for call in the given node.
   * 
   * <p>
   * For static methods and special invocations (such as constructors), there will be only one
   * target, and hence a singleton list will be returned.
   * </p>
   * 
   * <p>
   * For virtual calls, there may be multiple targets which are resolved using an available call
   * graph.
   * </p>
   * 
   * <p>
   * If even a single target does not have an analysable method body (e.g. native methods in Java),
   * then <code>null</code> is returned, to indicate that the targets cannot be properly resolved.
   * TODO: Native method flow functions?
   * </p>
   * 
   * @param callerMethod
   *          the method in which the call statement originates
   * @param callNode
   *          the node containing the call statement
   * @return a list of methods which are the target of this call, if their bodies are available, or
   *         else <code>null</code> in the case of native targets
   */
  public List<M> resolveTargets(M callerMethod, N callNode);

  /**
   * Checks if the given node contains a callee which should be skipped during the analysis.
   *
   * @param node
   *          the node
   * @return true, if the callee in the given node should be skipped.
   */
  public boolean isSkipCall(N node);
}
