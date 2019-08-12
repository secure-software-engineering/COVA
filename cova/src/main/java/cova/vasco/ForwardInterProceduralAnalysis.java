
package cova.vasco;

import com.google.common.base.Stopwatch;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import soot.toolkits.graph.DirectedGraph;
import soot.toolkits.scalar.Pair;

/**
 * A generic forward-flow inter-procedural analysis which is fully context-sensitive.
 * 
 * <p>
 * This class essentially captures a forward data flow problem which can be solved using the context-sensitive
 * inter-procedural analysis framework as described in {@link InterProceduralAnalysis}.
 * </p>
 * 
 * <p>
 * This is the class that client analyses will extend in order to perform forward-flow inter-procedural analysis.
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
public abstract class ForwardInterProceduralAnalysis<M, N, A> extends InterProceduralAnalysis<M, N, A> {

  /** The stop watch. */
  private Stopwatch stopWatch;

  /** The time out in seconds */
  protected long timeOutDuration;

  protected boolean timeOutOn;

  private double usedTime;

  private boolean timeout;

  private Set<M> reachableMethods;

  /**
   * Instantiates a new forward interprocedural analysis.
   */
  public ForwardInterProceduralAnalysis() {
    super(false);
    this.usedTime = 0;
    timeout = false;
    this.reachableMethods = new HashSet<M>();
  }

  /**
   * Creates a new value for phantom method.
   *
   * @param method
   *          the method
   * @param entryValue
   *          the entry value
   * @return the context
   */
  protected Context<M, N, A> initContextForPhantomMethod(M method, A entryValue) {
    Context<M, N, A> context = new Context<M, N, A>(method);
    context.setEntryValue(entryValue);
    context.setExitValue(entryValue);
    return context;
  }

  /**
   * Creates a new value context and initialises data flow values for its nodes.
   * 
   * <p>
   * The following steps are performed:
   * <ol>
   * <li>Construct the context.</li>
   * <li>Initialise IN/OUT for all nodes and add them to the work-list</li>
   * <li>Initialise the IN of entry points with a copy of the given entry value.</li>
   * <li>Add this new context to the given method's mapping.</li>
   * <li>Add this context to the global work-list.</li>
   * </ol>
   * </p>
   *
   * @param method
   *          the method whose context to create
   * @param entryValue
   *          the data flow value at the entry of this method
   * @return the context
   */
  protected Context<M, N, A> initContext(M method, A entryValue) {
    if (!reachableMethods.contains(method)) {
      reachableMethods.add(method);
    }

    // Construct the context
    Context<M, N, A> context = new Context<M, N, A>(method, programRepresentation().getControlFlowGraph(method), false);
    context.bottomValue = bottomValue();

    // Initialize IN/OUT for all nodes
    DirectedGraph<N> cfg = context.getControlFlowGraph();
    List<N> heads = cfg.getHeads();
    for (N node : cfg) {
      if (heads.contains(node)) {
        context.setValueBefore(node, entryValue);
      } else {
        context.setValueBefore(node, bottomValue());
      }
    }

    // Add all edges in CFG to the worklist of context
    context.initworklist();

    // Now, initialize the IN of entry points with a copy of the given entry value.
    context.setEntryValue(entryValue);
    context.setExitValue(bottomValue());

    // Add this new context to the given method's mapping.
    if (!contexts.containsKey(method)) {
      contexts.put(method, new LinkedList<Context<M, N, A>>());
    }
    contexts.get(method).add(context);

    // Add this context to the global work-list
    worklistOfContexts.add(context);
    return context;
  }

  /**
   * This method computes the in value for given node
   *
   * @param context
   *          the context
   * @param node
   *          the node
   * @return the merged in value
   */
  protected A computeInValue(Context<M, N, A> context, N node) {
    A in = bottomValue();
    DirectedGraph<N> cfg = context.getControlFlowGraph();
    List<N> predecessors = cfg.getPredsOf(node);
    if (predecessors.size() != 0) {
      // Merge OUT values of all predecessors
      for (int i = 0; i < predecessors.size(); i++) {
        N pred = predecessors.get(i);
        A predOut = context.getEdgeValue(pred, node);
        in = meet(in, predOut);
      }
    } else {
      // This is the first node of a current method
      in = context.getEntryValue();
    }
    // Set the IN value at the node to the result
    context.setValueBefore(node, in);
    return in;
  }

  /**
   * Compute exit value by merging the edge values of synthesized tail edges.
   *
   * @param context
   *          the context
   * @return the exit value
   */
  protected A computeExitValue(Context<M, N, A> context) {
    A exitValue = bottomValue();
    DirectedGraph<N> cfg = context.getControlFlowGraph();
    for (N tailNode : cfg.getTails()) {
      A tailOut = context.getEdgeValue(tailNode, tailNode);
      exitValue = meet(exitValue, tailOut);
    }
    return exitValue;
  }

  @Override
  public void doAnalysis() {
    boolean analysisFinished = true;
    boolean timeOut = false;
    int count = 0;
    stopWatch = Stopwatch.createStarted();
    ProgramRepresentation<M, N> program = programRepresentation();
    for (M method : program.getEntryPoints()) {
      initContext(method, boundaryValue(method));
    }

    while (!worklistOfContexts.isEmpty()) {
      // check if timed out
      if (timeOutOn) {
        if (stopWatch.elapsed(TimeUnit.SECONDS) > TimeUnit.SECONDS.toSeconds(this.timeOutDuration)) {
          analysisFinished = false;
          timeOut = true;
          break;
        }
      }

      // get the newest added context
      Context<M, N, A> currentContext = worklistOfContexts.last();

      // remove currentContext if it's analyzed
      if (currentContext.isAnalysed()) {
        worklistOfContexts.remove(currentContext);
        continue;
      }

      if (!currentContext.getWorklist().isEmpty()) {
        count++;
        if (logger.isDebugEnabled()) {
          logger.debug(count + ". X" + currentContext.getId() + ": " + currentContext.getMethod().toString());
        }

        // remove the first edge from the worklist of the currentContext
        Pair<N, N> edge = currentContext.getWorklist().pollFirst();
        N node = edge.getO1();
        N succ = edge.getO2();

        // compute in value for node
        A in = computeInValue(currentContext, node);

        if (logger.isDebugEnabled()) {
          logger.debug("  IN: " + in);
          logger.debug("EDGE: " + node + " --> " + succ);
        }

        A out = bottomValue();
        if (!program.isCall(node)) {
          if (!currentContext.reanalyzeEdge(node, succ, in)) {
            // we don't need to reanalyze the normal edge
            out = currentContext.getEdgeValue(node, succ);
          } else {
            out = normalFlowFunction(currentContext, node, succ, in);
          }
        } else {
          boolean hit = false;
          if (program.isSkipCall(node)) {
            hit = true;
          } else {
            List<M> callees = program.resolveTargets(currentContext.getMethod(), node);
            if (!callees.isEmpty()) {
              for (M callee : callees) {
                A entryValue = callEntryFlowFunction(currentContext, callee, node, succ, in);

                // check if the targetContext exists, if not initialize it
                Context<M, N, A> targetContext = getContext(callee, entryValue);
                if (targetContext == null) {
                  targetContext = initContext(callee, entryValue);
                  targetContext.setCallNode(node);
                }

                // store the transition from the calling context and call site to the target context.
                CallSite<M, N, A> callSite = new CallSite<M, N, A>(currentContext, node);
                contextTransitions.addTransition(callSite, targetContext);

                // check if the target context has been analyzed (surely not if it is just newly made):
                if (targetContext.isAnalysed()) {
                  hit = true;
                  A exitValue = targetContext.getExitValue();
                  if (logger.isDebugEnabled()) {
                    logger.debug("HIT: reuse exit value " + exitValue);
                  }
                  A returnedValue = callExitFlowFunction(currentContext, callee, node, succ, exitValue);
                  out = meet(out, returnedValue);
                }
              }
            }
          }

          // If there was at least one hit, continue propagation
          if (hit) {
            A localValue = callLocalFlowFunction(currentContext, node, succ, in);
            if (out.equals(bottomValue())) {
              out = localValue;
            } else {
              out = merge(localValue, out);
            }
          } else {
            // handle phantom method
            out = callLocalFlowFunction(currentContext, node, succ, in);
          }
        }

        A oldOut = currentContext.getEdgeValue(node, succ);
        // set the out value for edge (node, succ)
        currentContext.setEdgeValue(node, succ, out);

        if (logger.isDebugEnabled()) {
          logger.debug("  OUT_old: " + oldOut);
          logger.debug("  OUT_new: " + out);
          logger.debug("");
        }

        // if the out value for edge (node, succ) has been changed, add all outgoing edges from succ to the worklist of
        // currentContext.
        if (!oldOut.equals(out)) {
          currentContext.addToWorklist(succ);
        }
      } else {
        // worklist of currentContext is empty
        if (logger.isDebugEnabled()) {
          logger.debug("END X" + currentContext.getId());
        }

        // compute the exit value of currentContext
        A exitValue = computeExitValue(currentContext);
        A oldExitValue = currentContext.getExitValue();

        // set the exit value and mark currentContext as analyzed
        currentContext.setExitValue(exitValue);
        currentContext.markAnalysed();

        if (!oldExitValue.equals(exitValue)) {
          if (logger.isDebugEnabled()) {
            logger.debug("Exit_old: " + oldExitValue);
            logger.debug("Exit_new: " + exitValue);
          }
          Set<CallSite<M, N, A>> callers = contextTransitions.getCallers(currentContext);
          if (callers != null) {
            for (CallSite<M, N, A> callSite : callers) {
              // Extract the calling context and node from the caller site.
              Context<M, N, A> callingContext = callSite.getCallingContext();
              N callNode = callSite.getCallNode();
              if (logger.isDebugEnabled()) {
                logger.debug("X" + callingContext.getId() + "-->" + "X" + currentContext.getId() + " via " + callNode);
              }
              // make sure callingContext is in the worklistOfContexts
              worklistOfContexts.add(callingContext);
              callingContext.addToWorklist(callNode);
            }
          }
        }
        if (logger.isDebugEnabled()) {
          logger.debug("");
        }
      }
    }
    // end of while-loop

    if (analysisFinished) {
      usedTime = (double) stopWatch.elapsed(TimeUnit.MILLISECONDS) / 1000;
      logger.info("Analysis finished normally. Used analysis time: " + usedTime + "s");
    } else {
      usedTime = this.timeOutDuration;
      timeout = true;
      logger.info("Timeout. Timeout duration: " + usedTime + "s");
    }

  }

  /**
   * Processes the intra-procedural flow function of a statement that does not contain a method call.
   *
   * @param context
   *          the value context at the call-site
   * @param node
   *          the statement whose flow function to process
   * @param succ
   *          the succ
   * @param inValue
   *          the data flow value before the statement
   * @return the data flow value after the statement
   */
  public abstract A normalFlowFunction(Context<M, N, A> context, N node, N succ, A inValue);

  /**
   * Processes the inter-procedural flow function for a method call at the start of the call, to handle parameters.
   *
   * @param context
   *          the value context at the call-site
   * @param callee
   *          the target (or one of the targets) of this call site
   * @param node
   *          the statement containing the method call
   * @param succ
   *          the succ
   * @param inValue
   *          the data flow value before the call
   * @return the data flow value at the entry to the called procedure
   */
  public abstract A callEntryFlowFunction(Context<M, N, A> context, M callee, N node, N succ, A inValue);

  /**
   * Processes the inter-procedural flow function for a method call at the end of the call, to handle return values.
   *
   * @param context
   *          the value context at the call-site
   * @param callee
   *          the target (or one of the targets) of this call site
   * @param node
   *          the statement containing the method call
   * @param succ
   *          the succ
   * @param exitValue
   *          the data flow value at the exit of the called procedure
   * @return the data flow value after the call (returned component)
   */
  public abstract A callExitFlowFunction(Context<M, N, A> context, M callee, N node, N succ, A exitValue);

  /**
   * Processes the intra-procedural flow function for a method call at the call-site itself, to handle propagation of local
   * values that are not involved in the call.
   *
   * @param context
   *          the value context at the call-site
   * @param node
   *          the statement containing the method call
   * @param succ
   *          the succ
   * @param inValue
   *          the data flow value before the call
   * @return the data flow value after the call (local component)
   */
  public abstract A callLocalFlowFunction(Context<M, N, A> context, N node, N succ, A inValue);

  public boolean isTimeout() {
    return timeout;
  }

  /**
   * Gets the number of reachable methods that can be analyzed.
   *
   * @return the number of reachable methods
   */
  public int getReachableMethods() {
    return reachableMethods.size();
  }
}
