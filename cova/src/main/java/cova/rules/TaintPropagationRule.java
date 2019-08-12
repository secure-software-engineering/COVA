package cova.rules;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import soot.Local;
import soot.RefType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.BinopExpr;
import soot.jimple.CastExpr;
import soot.jimple.Constant;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.InstanceOfExpr;
import soot.jimple.InvokeExpr;
import soot.jimple.LengthExpr;
import soot.jimple.NegExpr;
import soot.jimple.NewExpr;
import soot.jimple.ReturnStmt;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.UnopExpr;

import boomerang.util.AccessPath;
import cova.core.InterproceduralCFG;
import cova.core.RuleManager;
import cova.data.Abstraction;
import cova.data.ConstraintZ3;
import cova.data.WrappedAccessPath;
import cova.data.WrappedTaintSet;
import cova.data.taints.AbstractTaint;
import cova.vasco.Context;

public class TaintPropagationRule implements IRule<SootMethod, Unit, Abstraction> {
  private InterproceduralCFG icfg;
  private RuleManager ruleManager;

  private LinkedHashSet<Unit> callNodes;

  public TaintPropagationRule(RuleManager ruleManager) {
    icfg = ruleManager.getIcfg();
    this.ruleManager = ruleManager;
    callNodes = new LinkedHashSet<Unit>();
  }

  private void killTaintsAndAliases(SootMethod method, Unit node, Value leftOp,
      Set<AbstractTaint> leftTaints, Abstraction in) {
    // if leftOp is tainted, kill taints on leftOp and aliases
    in.taints().removeAll(leftTaints);

    // kill aliasing taints of leftOp;
    if (cova.core.Aliasing.canBeQueried(leftOp)) {
      Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(leftOp, (Stmt) node,
          method);
      for (AccessPath alias : aliases) {
        WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
        if (aliasAccessPath.getFields() != null) {
          Set<AbstractTaint> aliasTaints = in.taints().getTaintsStartWith(aliasAccessPath);
          if (!aliasTaints.isEmpty()) {
            in.taints().removeAll(aliasTaints);
          }
        }
      }
    }
  }

  private void createTaintsAndAliases(SootMethod method, Unit node, Value leftOp, Value rightOp,
      Set<AbstractTaint> leftTaints, Set<AbstractTaint> rightTaints, Abstraction in) {
    WrappedAccessPath right = new WrappedAccessPath(rightOp);
    Set<WrappedAccessPath> aliasesOfLeftOp = new HashSet<WrappedAccessPath>();
    // if leftOp is already tainted, kill taints on leftOp and aliases at first
    if (leftTaints != null) {
      in.taints().removeAll(leftTaints);
    }
    // find the aliases of the leftOp
    if (cova.core.Aliasing.canBeQueried(leftOp)) {
      Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(leftOp, (Stmt) node,
          method);
      // kill existing taints start with aliasing access path
      for (AccessPath alias : aliases) {
        WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
        aliasesOfLeftOp.add(aliasAccessPath);
        Set<AbstractTaint> aliasTaints = in.taints().getTaintsStartWith(aliasAccessPath);
        in.taints().removeAll(aliasTaints);
      }
    }
    // taint leftOp and its aliases with new value
    for (AbstractTaint t : rightTaints) {
      // create taint with leftOp as its access path
      WrappedAccessPath leftAccessPath = t.getAccessPath()
          .replacePrefix(new WrappedAccessPath(leftOp), right.getDepth());
      AbstractTaint leftTaint = t.createNewTaintFromAccessPath(leftAccessPath);
      in.taints().add(leftTaint);
      // create taints from aliases of leftOp
      for (WrappedAccessPath alias : aliasesOfLeftOp) {
        if (!alias.equals(leftAccessPath)) {
          // Note: boomerang returns also the query value back
          WrappedAccessPath aliasAccessPath = t.getAccessPath().replacePrefix(alias,
              right.getDepth());
          AbstractTaint aliasTaint = t.createNewTaintFromAccessPath(aliasAccessPath);
          in.taints().add(aliasTaint);
        }
      }
    }
  }

  @Override
  public Abstraction normalFlowFunction(Context<SootMethod, Unit, Abstraction> context, Unit node,
      Unit succ, Abstraction in) {
    if (node instanceof AssignStmt) {
      SootMethod method = context.getMethod();
      AssignStmt assignStmt = (AssignStmt) node;
      Value leftOp = assignStmt.getLeftOp();
      Value rightOp = assignStmt.getRightOp();
      if (rightOp instanceof CastExpr) {
        /** CASE: local= (type) imm **/
        CastExpr castExpr = (CastExpr) rightOp;
        rightOp = castExpr.getOp();
      }
      Set<AbstractTaint> leftTaints = null;
      Set<AbstractTaint> rightTaints = null;
      boolean leftTainted = false;
      boolean rightTainted = false;
      if (WrappedAccessPath.isSupportedType(leftOp)) {// check if leftOp is tainted
        leftTaints = in.taints().getTaintsStartWith(new WrappedAccessPath(leftOp));
        if (!leftTaints.isEmpty()) {
          leftTainted = true;
        }
      }
      if (WrappedAccessPath.isSupportedType(rightOp)) {// check if rightOp is tainted
        rightTaints = in.taints().getTaintsStartWith(new WrappedAccessPath(rightOp));
        if (!rightTaints.isEmpty()) {
          rightTainted = true;
        }
      }
      if (leftOp instanceof Local) { /* 1. local=rvalue */
        if (rightOp instanceof Constant) {
          /** CASE: local=constant **/
          if (leftTainted) {
            killTaintsAndAliases(method, node, leftOp, leftTaints, in);
          }
          if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()
              && !in.getConstraintOfStmt().isTrue()) {
            // create concrete taint if the rule is enabled
            ruleManager.getConcreteTaintCreationRule().normalFlowFunction(context, node, succ, in);
          }
        } else if (rightOp instanceof Local) {
          /** CASE: local=local **/
          if (rightTainted) {
            createTaintsAndAliases(method, node, leftOp, rightOp, leftTaints, rightTaints, in);
          }
        } else if (rightOp instanceof FieldRef) {
          /** CASE: local=field or local=local.field **/
          if (rightTainted) {
            createTaintsAndAliases(method, node, leftOp, rightOp, leftTaints, rightTaints, in);
          }
          if (ruleManager.getConfig().isSourceTaintCreationRuleOn()) {
            ruleManager.getSourceTaintCreationRule().flowFunction(context, node, in);
          }
          if (rightOp instanceof InstanceFieldRef) {
            if (ruleManager.getConfig().isImpreciseTaintCreationRuleOn()) {
              ruleManager.getImpreciseTaintCreationRule().normalFlowFunction(context, node, succ,
                  in);
            }
          }
        } else if (rightOp instanceof BinopExpr) {
          /** CASE: local= imm binop imm **/
          if (ruleManager.getConfig().isImpreciseTaintCreationRuleOn()) {
            ruleManager.getImpreciseTaintCreationRule().normalFlowFunction(context, node, succ, in);
          }
        } else if (rightOp instanceof InstanceOfExpr) {
          /** CASE: local= imm instanceof type **/
          if (ruleManager.getConfig().isImprecisePropagationRuleOn()) {
            ruleManager.getImpreciseTaintCreationRule().normalFlowFunction(context, node, succ, in);
          }
        } else if (rightOp instanceof NewExpr) {
          /** CASE: local=new RefType **/
          // TODO:
        } else if (rightOp instanceof UnopExpr) {
          if (rightOp instanceof LengthExpr) {
            /** CASE: local=length imm **/
            if (ruleManager.getConfig().isImpreciseTaintCreationRuleOn()) {
              ruleManager.getImpreciseTaintCreationRule().normalFlowFunction(context, node, succ,
                  in);
            }
          }
          if (rightOp instanceof NegExpr) {
            /** CASE: local =neg imm **/
            if (ruleManager.getConfig().isImpreciseTaintCreationRuleOn()) {
              ruleManager.getImpreciseTaintCreationRule().normalFlowFunction(context, node, succ,
                  in);
            }
          }
        }
      } else if (leftOp instanceof StaticFieldRef) { /* 2. field= imm */
        if (rightOp instanceof Constant) {
          /** CASE: field=constant **/
          if (leftTainted) {
            killTaintsAndAliases(method, node, leftOp, leftTaints, in);
          }
          if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()
              && !in.getConstraintOfStmt().isTrue()) {
            // create concrete taint if the rule is enabled
            ruleManager.getConcreteTaintCreationRule().normalFlowFunction(context, node, succ, in);
          }
        } else if (rightOp instanceof Local) {
          /** CASE: field=local **/
          if (rightTainted) {
            createTaintsAndAliases(method, node, leftOp, rightOp, leftTaints, rightTaints, in);
          } else {
            if (leftTainted) {
              killTaintsAndAliases(method, node, leftOp, leftTaints, in);
            }
          }
        }
      } else if (leftOp instanceof InstanceFieldRef) { /* 3. local.field=imm */
        if (rightOp instanceof Constant) {
          /** CASE: local.field=constant **/
          if (leftTainted) {
            killTaintsAndAliases(method, node, leftOp, leftTaints, in);
          }
          if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()
              && !in.getConstraintOfStmt().isTrue()) {
            // create concrete taint if the rule is enabled
            ruleManager.getConcreteTaintCreationRule().normalFlowFunction(context, node, succ, in);
          }
        } else if (rightOp instanceof Local) {
          /** CASE: local.field=local **/
          if (rightTainted) {
            createTaintsAndAliases(method, node, leftOp, rightOp, leftTaints, rightTaints, in);
          } else {
            if (leftTainted) {
              killTaintsAndAliases(method, node, leftOp, leftTaints, in);
            }
          }
        }
      }
    } else if (node instanceof ReturnStmt) {
      if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()) {
        ruleManager.getConcreteTaintCreationRule().normalFlowFunction(context, node, succ, in);
      }
      ReturnStmt returnStmt = (ReturnStmt) node;
      Value returnValue = returnStmt.getOp();
      // create return taints
      if (!(returnValue instanceof Constant)) {
        Set<AbstractTaint> involved = in.taints()
            .getTaintsStartWith(new WrappedAccessPath(returnValue));
        for (AbstractTaint taint : involved) {
          WrappedAccessPath taintAp = taint.getAccessPath();
          WrappedAccessPath retAp = WrappedAccessPath.getRetAccessPath(taintAp.getFields());
          AbstractTaint retTaint = taint
              .createNewTaintFromAccessPath(retAp);
          in.taints().remove(taint);
          in.taints().add(retTaint);
        }
      }
    }
    // At the tail of each method, reset constraint and zero taint of the exit value. Taints can be
    // killed according to user-defined configuration.
    if (context.getControlFlowGraph().getTails().contains(node)) {
      WrappedTaintSet taintSet = in.taints();
      if (context.hasCallNode()) {
        // reset the constraint to true
        in.setConstraintOfStmt(ConstraintZ3.getTrue());
        // if static field propagation is disabled, kill all taints with public static fields
        if (!ruleManager.getConfig().isStaticFieldPropagationRuleOn()) {
          taintSet.killAllPublicStaticTaints();
        }
        // if imprecise taint propagation is disabled, kill all imprecise taint;
        if (!ruleManager.getConfig().isImprecisePropagationRuleOn()) {
          taintSet.killAllImpreciseTaints();
        }
        taintSet.killAllNonPropagate(context.getMethod());
      }
    }
    return in;
  }

  @Override
  public Abstraction callEntryFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction in) {
    if (!callNodes.contains(node)) {
      callNodes.add(node);
    }
    WrappedTaintSet entry = new WrappedTaintSet();
    // zero taint will be propagated into callee method
    entry.add(in.taints().getZeroTaint());

    // map arguments to parameters
    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    for (int i = 0; i < invokeExpr.getArgCount(); i++) {
      Value arg = invokeExpr.getArg(i);
      if (WrappedAccessPath.isSupportedType(arg)) {
        Local param = callee.getActiveBody().getParameterLocal(i);
        Set<AbstractTaint> involvedTaints = in.taints()
            .getTaintsStartWith(new WrappedAccessPath(arg));
        for (AbstractTaint taint : involvedTaints) {
          WrappedAccessPath calleeSite = taint.getAccessPath().copyFields(param);
          AbstractTaint t = taint.createNewTaintFromAccessPath(calleeSite);
          entry.add(t);
        }
      }
    }

    // o.foo(), for taint of the form o.*, create taint at the callee of the form this.*, where this
    // is the this reference inside callee.
    if (invokeExpr instanceof InstanceInvokeExpr) {
      InstanceInvokeExpr instanceInvoke = (InstanceInvokeExpr) invokeExpr;
      Local callerBase = (Local) instanceInvoke.getBase();
      SootMethod calleeMethod = instanceInvoke.getMethod();
      if (!calleeMethod.isPhantom() && calleeMethod.hasActiveBody()) {
        IdentityStmt identityStmt = icfg.getIdentityStmt(calleeMethod);
        Local calleeBase = (Local) identityStmt.getLeftOp();
        Set<AbstractTaint> taintsAtCallee = in.taints().deriveTaintsAtCallee(callerBase,
            calleeBase);
        entry.addAll(taintsAtCallee);
      }
    }

    // taints whose access path is public static field will be propagated into callee method
    if (ruleManager.getConfig().isStaticFieldPropagationRuleOn()) {
      Set<AbstractTaint> taintWithStaticFiled = in.taints().getTaintsWithPublicStaticField();
      entry.addAll(taintWithStaticFiled);
    }

    // imprecise taint propagation can be disabled
    if (!ruleManager.getConfig().isImprecisePropagationRuleOn()) {
      entry.killAllImpreciseTaints();
    }

    // update new entry value for callee
    in.updateTaintSet(entry);

    // create concrete taints for constant arguments if the rule is on and the constraint of the
    // statement is not true
    if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()
        && !in.getConstraintOfStmt().isTrue()) {
      ruleManager.getConcreteTaintCreationRule().callEntryFlowFunction(context, callee, node, succ,
          in);
    }
    return in;
  }

  @Override
  public Abstraction callExitFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction exitValue) {
    WrappedTaintSet ret = new WrappedTaintSet();
    // zero taint will be propagated back to call site
    ret.add(exitValue.taints().getZeroTaint());

    Stmt stmt = (Stmt) node;
    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    Value leftOp = null;

    // Handle RET taints, map exit value back to call site
    if (node instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) node;
      leftOp = assignStmt.getLeftOp();
      Set<AbstractTaint> retTaints = exitValue.taints().deriveRetTaints(leftOp);
      ret.addAll(retTaints);
    }

    // map parameter back to arguments
    for (int i = 0; i < invokeExpr.getArgCount(); i++) {
      Value arg = invokeExpr.getArg(i);
      // only when it's not an assignStmt or the leftOp is not equal to the argument
      if (leftOp == null || !arg.equals(leftOp)) {
        if (arg.getType() instanceof RefType) {
          if (WrappedAccessPath.isSupportedType(arg) && callee.hasActiveBody()) {
            Local param = callee.getActiveBody().getParameterLocal(i);
            Set<AbstractTaint> involvedTaints = exitValue.taints()
                .getTaintsStartWith(new WrappedAccessPath(param));
            for (AbstractTaint taint : involvedTaints) {
              WrappedAccessPath callerSiteAP = taint.getAccessPath()
                  .replacePrefix(new WrappedAccessPath(arg), 1);
              AbstractTaint taintAtCaller = taint.createNewTaintFromAccessPath(callerSiteAP);
              ret.add(taintAtCaller);
              // also create aliasing taints at caller
              if (cova.core.Aliasing.canBeQueried(arg)) {
                SootMethod method = context.getMethod();
                Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(arg,
                    (Stmt) node, method);
                for (AccessPath alias : aliases) {
                  WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
                  if (!aliasAccessPath.equals(new WrappedAccessPath(arg))) {
                    WrappedAccessPath taintAP = taintAtCaller.getAccessPath()
                        .replacePrefix(aliasAccessPath, 1);
                    AbstractTaint aliasTaint = taintAtCaller.createNewTaintFromAccessPath(taintAP);
                    ret.add(aliasTaint);
                  }
                }
              }
            }
          }
        }
      }
    }

    // o.foo(), for taint of the form this.*, create taint at the caller of the form o.*, where
    // this is the this reference of inside callee.
    if (invokeExpr instanceof InstanceInvokeExpr) {
      InstanceInvokeExpr instanceInvoke = (InstanceInvokeExpr) invokeExpr;
      Local callerBase = (Local) instanceInvoke.getBase();
      SootMethod calleeMethod = instanceInvoke.getMethod();
      if (!calleeMethod.isPhantom() && calleeMethod.hasActiveBody()) {
        IdentityStmt identityStmt = icfg.getIdentityStmt(calleeMethod);
        Local calleeBase = (Local) identityStmt.getLeftOp();
        Set<AbstractTaint> taintsAtCaller = exitValue.taints().deriveTaintsAtCaller(calleeBase,
            callerBase);
        if (!taintsAtCaller.isEmpty()) {
          ret.addAll(taintsAtCaller);
          // also create aliasing taints at caller
          if (cova.core.Aliasing.canBeQueried(callerBase)) {
            SootMethod method = context.getMethod();
            Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(callerBase,
                (Stmt) node, method);
            for (AccessPath alias : aliases) {
              WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
              if (!aliasAccessPath.equals(new WrappedAccessPath(callerBase))) {
                for (AbstractTaint taintAtCaller : taintsAtCaller) {
                  WrappedAccessPath taintAP = taintAtCaller.getAccessPath()
                      .replacePrefix(aliasAccessPath, 1);
                  AbstractTaint aliasTaint = taintAtCaller.createNewTaintFromAccessPath(taintAP);
                  ret.add(aliasTaint);
                }
              }
            }
          }
        }
      }
    }

    // taints whose access path is public static field will be propagated back to caller
    if (ruleManager.getConfig().isStaticFieldPropagationRuleOn()) {
      Set<AbstractTaint> taintWithStaticField = exitValue.taints().getTaintsWithPublicStaticField();
      ret.addAll(taintWithStaticField);
    }

    // imprecise taint propagation can be disabled
    if (!ruleManager.getConfig().isImprecisePropagationRuleOn()) {
      ret.killAllImpreciseTaints();
    }

    // update new exitValue for caller
    exitValue.updateTaintSet(ret);

    return exitValue;
  }

  @Override
  public Abstraction callLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      Unit node, Unit succ, Abstraction in) {
    boolean taintCreated = false;
    SootMethod method = context.getMethod();
    // according to different rules, create taints at caller
    if (node instanceof AssignStmt) {
      if (ruleManager.getConfig().isSourceTaintCreationRuleOn()) {
        boolean createdSourceTaint = ruleManager.getSourceTaintCreationRule().flowFunction(context,
            node, in);
        if (createdSourceTaint) {
          taintCreated = true;
        }
      }
      if (ruleManager.getConfig().isImpreciseTaintCreationRuleOn()) {
        boolean createdImpreciseTaint = ruleManager.getImpreciseTaintCreationRule()
            .callLocalFlowFunction(context, node, succ, in);
        if (createdImpreciseTaint) {
          taintCreated = true;
        }
      }
      if (ruleManager.getConfig().isConcreteTaintCreationRuleOn()) {
        boolean createdConcreteTaint = ruleManager.getConcreteTaintCreationRule()
            .callLocalFlowFunction(context, node, succ, in);
        if (createdConcreteTaint) {
          taintCreated = true;
        }
      }
      if (!taintCreated) {
        // rightOp doesn't contains taints or source API, kill taints that are on the left side of
        // the assignment
        AssignStmt assignStmt = (AssignStmt) node;
        Value leftOp = assignStmt.getLeftOp();
        Set<AbstractTaint> taintsOfLeftOp = in.taints()
            .getTaintsStartWith(new WrappedAccessPath(leftOp));
        killTaintsAndAliases(context.getMethod(), node, leftOp, taintsOfLeftOp, in);
      }
    }

    // the following transformation will only be done when the callee can be resolved
    if (callNodes.contains(node) && !taintCreated) {
      Stmt stmt = (Stmt) node;
      InvokeExpr invokeExpr = stmt.getInvokeExpr();
      if (invokeExpr.getArgCount() > 0) {
        // don't propagate taints that are on an argument of the virtual method call, since the
        // taints can be changed in the callee.
        for (Value arg : invokeExpr.getArgs()) {
          if (WrappedAccessPath.isSupportedType(arg) && arg.getType() instanceof RefType) {
            Set<AbstractTaint> nonPropagateTaints = in.taints()
                .getTaintsStartWith(new WrappedAccessPath(arg));
            if (!nonPropagateTaints.isEmpty()) {
              in.taints().removeAll(nonPropagateTaints);
              // kill also its aliasing taints
              if (cova.core.Aliasing.canBeQueried(arg)) {
                Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(arg,
                    (Stmt) node, method);
                for (AccessPath alias : aliases) {
                  WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
                  Set<AbstractTaint> aliasTaints = in.taints().getTaintsStartWith(aliasAccessPath);
                  for (AbstractTaint aliasTaint : aliasTaints) {
                    for (AbstractTaint nonPropagate : nonPropagateTaints) {
                      WrappedAccessPath accessPathNP = nonPropagate.getAccessPath();
                      if (!accessPathNP.isLocal() && WrappedAccessPath.hasSameSuffix(
                          aliasTaint.getAccessPath(), accessPathNP, accessPathNP.getDepth() - 1)) {
                        in.taints().remove(aliasTaint);
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }

      if (invokeExpr instanceof InstanceInvokeExpr) {
        // don't propagate taints that are on the base object of the virtual method call,
        // since these taints will propagate to callee and back to caller.
        InstanceInvokeExpr instanceInvoke = (InstanceInvokeExpr) invokeExpr;
        Value base = instanceInvoke.getBase();
        SootMethod calleeMethod = instanceInvoke.getMethod();
        if (!calleeMethod.isPhantom() && calleeMethod.hasActiveBody()) {
          Set<AbstractTaint> nonPropagateTaints = in.taints()
              .getTaintsStartWith(new WrappedAccessPath(base));
          if (!nonPropagateTaints.isEmpty()) {
            in.taints().removeAll(nonPropagateTaints);
            // kill also its aliasing taints
            if (cova.core.Aliasing.canBeQueried(base)) {
              Set<AccessPath> aliases = ruleManager.getAliasing().findAliasAtStmt(base, (Stmt) node,
                  method);
              for (AccessPath alias : aliases) {
                WrappedAccessPath aliasAccessPath = WrappedAccessPath.convert(alias);
                Set<AbstractTaint> aliasTaints = in.taints().getTaintsStartWith(aliasAccessPath);
                for (AbstractTaint aliasTaint : aliasTaints) {
                  for (AbstractTaint nonPropagate : nonPropagateTaints) {
                    WrappedAccessPath accessPathNP = nonPropagate.getAccessPath();
                    if (!accessPathNP.isLocal() && WrappedAccessPath.hasSameSuffix(
                        aliasTaint.getAccessPath(), accessPathNP, accessPathNP.getDepth() - 1)) {
                      in.taints().remove(aliasTaint);
                    }
                  }
                }
              }
            }
          }
        }
      }

      // don't propagate taints that are on public static field, since the taints can be changed
      // in the callee.
      if (ruleManager.getConfig().isStaticFieldPropagationRuleOn()) {
        Set<AbstractTaint> nonPropagateTaints = in.taints().getTaintsWithPublicStaticField();
        if (!nonPropagateTaints.isEmpty()) {
          in.taints().removeAll(nonPropagateTaints);
        }
      }
    }

    return in;
  }
}
