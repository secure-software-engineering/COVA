package cova.rules;

import cova.vasco.Context;

public interface IRule<M, N, A> {
  /**
   * Processes the intra-procedural flow function of a statement that does not contain a method
   * call.
   *
   * @param context
   *          the value context at the call-site
   * @param node
   *          the statement whose flow function to process
   * @param succ
   *          the succ
   * @param in
   *          the data flow value before the statement
   * @return the data flow value after the statement
   */
  public abstract A normalFlowFunction(Context<M, N, A> context, N node, N succ, A in);

  /**
   * Processes the inter-procedural flow function for a method call at the start of the call, to
   * handle parameters.
   *
   * @param context
   *          the value context at the call-site
   * @param callee
   *          the target (or one of the targets) of this call site
   * @param node
   *          the statement containing the method call
   * @param succ
   *          the succ
   * @param in
   *          the data flow value before the call
   * @return the data flow value at the entry to the called procedure
   */
  public abstract A callEntryFlowFunction(Context<M, N, A> context, M callee, N node, N succ,
      A in);

  /**
   * Processes the inter-procedural flow function for a method call at the end of the call, to
   * handle return values.
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
  public abstract A callExitFlowFunction(Context<M, N, A> context, M callee, N node, N succ,
      A exitValue);

  /**
   * Processes the intra-procedural flow function for a method call at the call-site itself, to
   * handle propagation of local values that are not involved in the call.
   *
   * @param context
   *          the value context at the call-site
   * @param node
   *          the statement containing the method call
   * @param succ
   *          the succ
   * @param in
   *          the data flow value before the call
   * @return the data flow value after the call (local component)
   */
  public abstract A callLocalFlowFunction(Context<M, N, A> context, N node, N succ, A in);
}
