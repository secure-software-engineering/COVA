/*
 * @version 1.0
 */
package cova.vasco;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.graph.PseudoTopologicalOrderer;
import soot.toolkits.scalar.Pair;

// TODO: Auto-generated Javadoc
/**
 * A value-based context for a context-sensitive inter-procedural data flow analysis.
 * 
 * <p>
 * A value-based context is identified as a pair of a method and the data flow value at the entry of the method, for forward
 * flows, or the data flow value at the exit of the method, for backward flows. Thus, if two distinct calls are made to a
 * method and each call-site has the same data flow value then it is considered that the target of that call is the same
 * context. This concept allows termination in the presence of recursion as the number of contexts is limited by the size of
 * the lattice (which must be finite).
 * </p>
 * 
 * <p>
 * Each value context has its own work-list of CFG nodes to analyse, and the results of analysis are stored in a map from
 * nodes to the data flow values before/after the node.
 * </p>
 * 
 * 
 * @param <M>
 *          the type of a method
 * @param <N>
 *          the type of a node in the CFG
 * @param <A>
 *          the type of a data flow value
 */
public class Context<M, N, A> implements soot.Context, Comparable<Context<M, N, A>> {

  /** A counter for global context identifiers. */
  private static int count = 0;

  /** Whether or not this context has been fully analysed at least once. */
  private boolean analysed;

  /** The control-flow graph of this method's body. */
  private DirectedGraph<N> controlFlowGraph;

  /** The data flow value associated with the entry to the method. **/
  private A entryValue;

  /** The data flow value associated with the exit of the method. */
  private A exitValue;

  /** A globally unique identifier. */
  private int id;

  /** The method for which this calling context context applies. */
  private M method;

  private List<N> orderedNodes;

  /** The data flow values at the entry of each node. */
  private Map<N, A> inValues;

  /** The pred edge in. */
  private Map<Pair<N, N>, A> predEdgeIn;

  /** The edge values. */
  private Map<Pair<N, N>, A> edgeValues;

  /** The worklist. */
  private NavigableSet<Pair<N, N>> worklist;

  /** The call node. */
  private N callNode;

  public A bottomValue;


  /**
   * Creates a new context for phantom method.
   *
   * @param method
   *          the method
   */
  public Context(M method) {
    this.method = method;
  }

  /**
   * Creates a new context for the given method.
   * 
   * @param method
   *          the method to which this value context belongs
   * @param cfg
   *          the control-flow graph for the body of <tt>method</tt>
   * @param reverse
   *          <tt>true</tt> if the analysis is in the reverse direction, and <tt>false</tt> if the analysis is in the forward
   *          direction
   */
  public Context(M method, DirectedGraph<N> cfg, boolean reverse) {
    // Increment count and set id.
    count++;
    this.id = count;
    // Initialise fields.
    this.method = method;
    this.controlFlowGraph = cfg;
    this.inValues = new HashMap<N, A>(cfg.size());
    this.edgeValues = new HashMap<Pair<N, N>, A>(cfg.size() * 2);
    this.predEdgeIn = new HashMap<Pair<N, N>, A>(cfg.size());
    this.analysed = false;
    orderedNodes = new PseudoTopologicalOrderer().newList(controlFlowGraph, reverse);

    // Then a mapping from an edge to the position in the topological order.
    final Map<Pair<N, N>, Integer> numbers = new HashMap<Pair<N, N>, Integer>();
    int num = 1;
    for (N n : orderedNodes) {
      List<N> succs = controlFlowGraph.getSuccsOf(n);
      if (succs.isEmpty()) {
        numbers.put(new Pair<N, N>(n, n), num);
        num++;
      } else {
        for (N succ : controlFlowGraph.getSuccsOf(n)) {
          numbers.put(new Pair<N, N>(n, succ), num);
          num++;
        }
      }
    }

    // Now, create a sorted set with a comparator created on-the-fly using the total order.
    worklist = new TreeSet<Pair<N, N>>(new Comparator<Pair<N, N>>() {
      @Override
      public int compare(Pair<N, N> u, Pair<N, N> v) {
        return numbers.get(u) - numbers.get(v);
      }
    });
  }

  /**
   * Compares two contexts by their globally unique IDs.
   * 
   * This functionality is useful in the framework's internal methods where ordered processing of newer contexts first helps
   * speed up certain operations.
   *
   * @param other
   *          the other
   * @return the int
   */
  @Override
  public int compareTo(Context<M, N, A> other) {
    return this.getId() - other.getId();
  }

  /**
   * Returns a reference to the control flow graph of this context's method.
   * 
   * @return a reference to the control flow graph of this context's method
   */
  public DirectedGraph<N> getControlFlowGraph() {
    return controlFlowGraph;
  }

  /**
   * Returns the total number of contexts created so far.
   *
   * @return the count
   */
  public static int getCount() {
    return count;
  }

  /**
   * Returns a reference to the data flow value at the method entry.
   * 
   * @return a reference to the data flow value at the method entry
   */
  public A getEntryValue() {
    return entryValue;
  }

  /**
   * Returns a reference to the data flow value at the method exit.
   * 
   * @return a reference to the data flow value at the method exit
   */
  public A getExitValue() {
    return exitValue;
  }

  /**
   * Returns the globally unique identifier of this context.
   * 
   * @return the globally unique identifier of this context
   */
  public int getId() {
    return id;
  }

  /**
   * Returns a reference to this context's method.
   * 
   * @return a reference to this context's method
   */
  public M getMethod() {
    return method;
  }

  /**
   * Gets the edge value.
   *
   * @param node
   *          the node
   * @param succ
   *          the succ
   * @return the edge value
   */
  public A getEdgeValue(N node, N succ) {
    return this.edgeValues.get(new Pair<N, N>(node, succ));
  }

  /**
   * Sets the edge value.
   *
   * @param node
   *          the node
   * @param succ
   *          the succ
   * @param val
   *          the val
   */
  public void setEdgeValue(N node, N succ, A val) {
    this.edgeValues.put(new Pair<N, N>(node, succ), val);
  }

  /**
   * Gets the data flow value at the entry of the given node.
   * 
   * @param node
   *          a node in the control flow graph
   * @return the data flow value at the entry of the given node
   */
  public A getValueBefore(N node) {
    return inValues.get(node);
  }

  /**
   * This method checks if it is necessary to analyze the edge from node to succ. Only when the in value of this edge has
   * been changed or this is first time to analyze the edge.
   *
   * @param node
   *          the node
   * @param succ
   *          the succ
   * @param in
   *          the in
   * @return true, if successful
   */
  public boolean reanalyzeEdge(N node, N succ, A in) {
    A pred = this.predEdgeIn.get(new Pair<N, N>(node, succ));
    this.predEdgeIn.put(new Pair<N, N>(node, succ), in);
    if (pred == null) {
      return true;
    }
    if (!pred.equals(in)) {
      return true;
    }
    return false;
  }

  /**
   * Returns whether or not this context has been analysed at least once.
   *
   * @return <tt>true</tt> if the context has been analysed at least once, or <tt>false</tt> otherwise
   */
  public boolean isAnalysed() {
    return analysed;
  }

  /**
   * Marks this context as analysed.
   */
  public void markAnalysed() {
    this.analysed = true;
  }

  /**
   * Sets the entry flow of this context.
   * 
   * @param entryValue
   *          the new data flow value at the method entry
   */
  public void setEntryValue(A entryValue) {
    this.entryValue = entryValue;
  }

  /**
   * Sets the exit flow of this context.
   * 
   * @param exitValue
   *          the new data flow value at the method exit
   */
  public void setExitValue(A exitValue) {
    this.exitValue = exitValue;
  }

  /**
   * Sets the data flow value at the entry of the given node.
   * 
   * @param node
   *          a node in the control flow graph
   * @param value
   *          the new data flow at the node entry
   */
  public void setValueBefore(N node, A value) {
    inValues.put(node, value);
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return Integer.toString(id) + ":" + this.method.toString();
  }

  /**
   * Initialize worklist of current context by setting analysed to false and add all edges of CFG to worklist.
   */
  public void initworklist() {
    this.analysed = false;
    for (N node : orderedNodes) {
      List<N> succs = controlFlowGraph.getSuccsOf(node);
      if (succs.isEmpty()) {
        this.worklist.add(new Pair<N, N>(node, node));
        this.setEdgeValue(node, node, bottomValue);
      } else {
        for (N succ : succs) {
          this.worklist.add(new Pair<N, N>(node, succ));
          this.setEdgeValue(node, succ, bottomValue);
        }
      }
    }
  }

  /**
   * Gets the worklist.
   *
   * @return the worklist
   */
  public NavigableSet<Pair<N, N>> getWorklist() {
    return this.worklist;
  }

  public void clearWorklist() {
    this.worklist.clear();
  }

  /**
   * Adds all outgoing edges from node to worklist.
   *
   * @param node
   *          the node
   */
  public void addToWorklist(N node) {
    List<N> succs = controlFlowGraph.getSuccsOf(node);
    if (succs.isEmpty()) {
      this.worklist.add(new Pair<N, N>(node, node));
    } else {
      for (N succ : succs) {
        this.worklist.add(new Pair<N, N>(node, succ));
      }
    }
  }

  /**
   * Sets the call node.
   *
   * @param callNode
   *          the new call node
   */
  public void setCallNode(N callNode) {
    this.callNode = callNode;
  }

  /**
   * Gets the call node.
   *
   * @return the call node
   */
  public N getCallNode() {
    return this.callNode;
  }

  /**
   * Checks for call node.
   *
   * @return true, if successful
   */
  public boolean hasCallNode() {
    return callNode != null;
  }

  public static void reset() {
    count = 0;
  }

}
