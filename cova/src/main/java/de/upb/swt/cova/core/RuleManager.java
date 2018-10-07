/*
 * @version 1.0
 */

package de.upb.swt.cova.core;

import de.upb.swt.cova.data.Abstraction;
import de.upb.swt.cova.rules.ConcreteTaintCreationRule;
import de.upb.swt.cova.rules.IRule;
import de.upb.swt.cova.rules.ImpreciseTaintCreationRule;
import de.upb.swt.cova.rules.SourceTaintCreationRule;
import de.upb.swt.cova.rules.TaintConstraintCreationRule;
import de.upb.swt.cova.rules.TaintPropagationRule;
import de.upb.swt.cova.rules.UIConstraintCreationRule;
import de.upb.swt.cova.setup.config.Config;
import de.upb.swt.cova.source.SourceManager;
import de.upb.swt.cova.vasco.Context;

import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import soot.SootMethod;
import soot.Unit;

/**
 * The Class RuleManager manages all rules in the analysis.
 * 
 */
public class RuleManager {

  /** The interprocedural control flow graph. */
  private InterproceduralCFG icfg;

  /** The source manager. */
  private SourceManager sourceManager;

  /** The rules applied in the analysis. */
  private ArrayList<IRule<SootMethod, Unit, Abstraction>> rules;

  private SourceTaintCreationRule sourceTaintCreationRule;
  private ImpreciseTaintCreationRule impreciseTaintCreationRule;
  private ConcreteTaintCreationRule concreteTaintCreationRule;

  public SourceTaintCreationRule getSourceTaintCreationRule() {
    return sourceTaintCreationRule;
  }

  public ImpreciseTaintCreationRule getImpreciseTaintCreationRule() {
    return impreciseTaintCreationRule;
  }

  public ConcreteTaintCreationRule getConcreteTaintCreationRule() {
    return concreteTaintCreationRule;
  }

  private Aliasing aliasing;

  /** The skip manager. */
  private SkipMethodOrClassRuleManager skipManager;

  private Config config;

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * Instantiates a new taint analysis rule manager.
   *
   * @param icfg
   *          the icfg
   * @param sourceManager
   *          the source manager
   * @param config
   *          the configuration
   */
  public RuleManager(InterproceduralCFG icfg, SourceManager sourceManager, Config config) {
    this.icfg = icfg;
    this.sourceManager = sourceManager;
    this.config = config;
    printConfigInfo();
    rules = new ArrayList<IRule<SootMethod, Unit, Abstraction>>();
    skipManager = SkipMethodOrClassRuleManager.getInstance();
    setupPropagationRules();
  }

  private void printConfigInfo() {
    if (logger.isInfoEnabled()) {
      logger.info(config.toString());
      logger.info(new Date(System.currentTimeMillis()).toString());
    }
  }

  /**
   * Setup constraint creation and taint propagation rules.
   */
  private void setupPropagationRules() {
    if (config.isSourceTaintCreationRuleOn()) {
      sourceTaintCreationRule = new SourceTaintCreationRule(this);
    }
    if (config.isImpreciseTaintCreationRuleOn()) {
      impreciseTaintCreationRule = new ImpreciseTaintCreationRule(this);
    }
    if (config.isConcreteTaintCreationRuleOn()) {
      concreteTaintCreationRule = new ConcreteTaintCreationRule(
          config.isConcreteTaintAtAssignStmtOn(), config.isConcreteTaintAtReturnStmtOn(),
          config.isConcreteTaintAtCalleeOn(), this);
    }
    if (config.isUIConstraintCreationRuleOn()) {
      rules.add(new UIConstraintCreationRule(this));
    }
    if (config.isTaintConstraintCreationRuleOn()) {
      rules.add(new TaintConstraintCreationRule(this));
    }
    if (config.isTaintPropagationRuleOn()) {
      rules.add(new TaintPropagationRule(this));
    }
  }

  /**
   * Apply normal flow function.
   *
   * @param context
   *          the context
   * @param node
   *          the node
   * @param succ
   *          the succ
   * @param in
   *          the in
   * @return the abstraction
   */
  public Abstraction applyNormalFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      Unit node, Unit succ, Abstraction in) {
    Abstraction out = new Abstraction(in);
    for (IRule<SootMethod, Unit, Abstraction> rule : rules) {
      Abstraction ruleOut = rule.normalFlowFunction(context, node, succ, out);
      if (ruleOut != null) {
        out = ruleOut;
      }
    }
    return out;
  }

  /**
   * Apply call entry flow function.
   *
   * @param context
   *          the context
   * @param callee
   *          the callee
   * @param node
   *          the node
   * @param succ
   *          the successor node
   * @param in
   *          the in
   * @return the abstraction
   */
  public Abstraction applyCallEntryFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction in) {
    Abstraction out = new Abstraction(in);
    for (IRule<SootMethod, Unit, Abstraction> rule : rules) {
      Abstraction ruleOut = rule.callEntryFlowFunction(context, callee, node, succ, out);
      if (ruleOut != null) {
        out = ruleOut;
      }
    }
    return out;
  }

  /**
   * Apply call exit flow function.
   *
   * @param context
   *          the context
   * @param callee
   *          the callee
   * @param node
   *          the node
   * @param succ
   *          the successor node
   * @param in
   *          the in
   * @return the abstraction
   */
  public Abstraction applyCallExitFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      SootMethod callee, Unit node, Unit succ, Abstraction in) {
    Abstraction out = new Abstraction(in);
    for (IRule<SootMethod, Unit, Abstraction> rule : rules) {
      Abstraction ruleOut = rule.callExitFlowFunction(context, callee, node, succ, out);
      if (ruleOut != null) {
        out = ruleOut;
      }
    }
    return out;

  }

  /**
   * Apply call local flow function.
   *
   * @param context
   *          the context
   * @param node
   *          the node
   * @param succ
   *          the successor node
   * @param in
   *          the in
   * @return the abstraction
   */
  public Abstraction applyCallLocalFlowFunction(Context<SootMethod, Unit, Abstraction> context,
      Unit node, Unit succ, Abstraction in) {
    Abstraction out = new Abstraction(in);
    for (IRule<SootMethod, Unit, Abstraction> rule : rules) {
      Abstraction ruleOut = rule.callLocalFlowFunction(context, node, succ, out);
      if (ruleOut != null) {
        out = ruleOut;
      }
    }
    return out;
  }

  /**
   * Gets the interprocedural control flow graph.
   *
   * @return the icfg
   */
  public InterproceduralCFG getIcfg() {
    return icfg;
  }

  /**
   * Gets the aliasing.
   *
   * @return the aliasing
   */
  public Aliasing getAliasing() {
    if (aliasing == null) {
      aliasing = new Aliasing(icfg.getDelegateICFG());
    }
    return aliasing;
  }

  public Config getConfig() {
    return config;
  }

  /**
   * Gets the source manager.
   *
   * @return the source manager
   */
  public SourceManager getSourceAndCallbackManager() {
    return sourceManager;
  }

  /**
   * Gets the skip manager.
   *
   * @return the skip manager
   */
  public SkipMethodOrClassRuleManager getSkipMethodOrClassRuleManager() {
    return skipManager;
  }
}
