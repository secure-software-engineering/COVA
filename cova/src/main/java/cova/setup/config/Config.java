/*
 * @version 1.0
 */
package cova.setup.config;

import java.io.File;

import cova.rules.ConcreteTaintCreationRule;

// TODO: Auto-generated Javadoc
/**
 * The Class Configuration.
 */
public class Config {

  /** True, if the UI constraint creation rule is on. */
  private boolean uIConstraintCreationRuleOn;

  /** True, if the taint constraint creation rule is on. */
  private boolean taintConstraintCreationRuleOn;

  /** True, if the source taint creation rule is on. */
  private boolean sourceTaintCreationRule;

  /** True, if the taint propagation rule is on. */
  private boolean taintPropagationRuleOn;

  /** True, if the imprecise taint creation rule is on. */
  private boolean impreciseTaintCreationRuleOn;

  /** The imprecise propagation rule on. */
  private boolean imprecisePropagationRuleOn;

  /** True, if the concrete taint propagation rule is on. */
  private boolean concreteTaintCreationRuleOn;

  /** The concrete taint at assignStmt on. */
  private boolean concreteTaintAtAssignStmtOn;

  /** The concrete taint at returnStmt on. */
  private boolean concreteTaintAtReturnStmtOn;

  /** The concrete taint at callee on. */
  private boolean concreteTaintAtCalleeOn;

  /** True, if propagate taints which are static fields. */
  private boolean staticFieldPropagationOn;

  /** True, if allows time out. */
  private boolean timeOutOn;

  /** The time out duration. */
  private int timeOutDuration;

  /** True, if print jimple output. */
  private boolean writeJimpleOutput;

  /** True, if print html output. */
  private boolean writeHtmlOutput;

  /** The location for configuration files. */
  private String configDir;

  /**
   * Instantiates a new configuration with nothing enabled.
   */
  public Config() {
    uIConstraintCreationRuleOn = false;
    taintConstraintCreationRuleOn = false;
    sourceTaintCreationRule = false;
    taintPropagationRuleOn = false;
    impreciseTaintCreationRuleOn = false;
    concreteTaintCreationRuleOn = false;
    concreteTaintAtAssignStmtOn = false;
    concreteTaintAtReturnStmtOn = false;
    concreteTaintAtCalleeOn = false;
    staticFieldPropagationOn = false;
    timeOutOn = false;
    timeOutDuration = 0;
    writeJimpleOutput = false;
    writeHtmlOutput = false;
    this.configDir = System.getProperty("user.dir") + File.separator + "config";
  }

  /**
   * Instantiates a new config.
   *
   * @param uIConstraintCreationRuleOn
   *          the u I constraint creation rule on
   * @param taintConstraintCreationRuleOn
   *          the taint constraint creation rule on
   * @param sourceTaintCreationRule
   *          the source taint creation rule
   * @param taintPropagationRuleOn
   *          the taint propagation rule on
   * @param impreciseTaintCreationRule
   *          the imprecise taint creation rule
   * @param concreteTaintPropagationRuleOn
   *          the concrete taint propagation rule on
   * @param staticFieldPropagation
   *          the static field propagation
   * @param timeOutOn
   *          the time out on
   * @param timeOutDuration
   *          the time out duration
   * @param writeJimpleOutput
   *          the write jimple output
   * @param writeHtmlOutput
   *          the write html output
   */
  public Config(boolean uIConstraintCreationRuleOn, boolean taintConstraintCreationRuleOn,
      boolean sourceTaintCreationRule, boolean taintPropagationRuleOn,
      boolean impreciseTaintCreationRule, boolean concreteTaintPropagationRuleOn,
      boolean staticFieldPropagation, boolean timeOutOn, int timeOutDuration,
      boolean writeJimpleOutput, boolean writeHtmlOutput) {
    this();
    this.uIConstraintCreationRuleOn = uIConstraintCreationRuleOn;
    this.taintConstraintCreationRuleOn = taintConstraintCreationRuleOn;
    this.sourceTaintCreationRule = sourceTaintCreationRule;
    this.taintPropagationRuleOn = taintPropagationRuleOn;
    impreciseTaintCreationRuleOn = impreciseTaintCreationRule;
    concreteTaintCreationRuleOn = concreteTaintPropagationRuleOn;
    staticFieldPropagationOn = staticFieldPropagation;
    this.timeOutOn = timeOutOn;
    this.timeOutDuration = timeOutDuration;
    this.writeJimpleOutput = writeJimpleOutput;
    this.writeHtmlOutput = writeHtmlOutput;
  }

  /**
   * Checks if is UI constraint creation rule on.
   *
   * @return true, if is UI constraint creation rule on
   */
  public boolean isUIConstraintCreationRuleOn() {
    return uIConstraintCreationRuleOn;
  }

  /**
   * Sets the u I constraint creation rule on.
   *
   * @param uIConstraintCreationRuleOn
   *          the new u I constraint creation rule on
   */
  public void setUIConstraintCreationRuleOn(boolean uIConstraintCreationRuleOn) {
    this.uIConstraintCreationRuleOn = uIConstraintCreationRuleOn;
  }

  /**
   * Checks if is taint constraint creation rule on.
   *
   * @return true, if is taint constraint creation rule on
   */
  public boolean isTaintConstraintCreationRuleOn() {
    return taintConstraintCreationRuleOn;
  }

  /**
   * Sets the taint constraint creation rule on.
   *
   * @param taintConstraintCreationRuleOn
   *          the new taint constraint creation rule on
   */
  public void setTaintConstraintCreationRuleOn(boolean taintConstraintCreationRuleOn) {
    this.taintConstraintCreationRuleOn = taintConstraintCreationRuleOn;
  }

  /**
   * Checks if is source taint creation rule on.
   *
   * @return true, if is source taint creation rule on
   */
  public boolean isSourceTaintCreationRuleOn() {
    return sourceTaintCreationRule;
  }

  /**
   * Sets the source taint creation rule on.
   *
   * @param sourceTaintCreationRule
   *          the new source taint creation rule on
   */
  public void setSourceTaintCreationRuleOn(boolean sourceTaintCreationRule) {
    this.sourceTaintCreationRule = sourceTaintCreationRule;
  }

  /**
   * Checks if is taint propagation rule on.
   *
   * @return true, if is taint propagation rule on
   */
  public boolean isTaintPropagationRuleOn() {
    return taintPropagationRuleOn;
  }

  /**
   * Sets the taint propagation rule on.
   *
   * @param taintPropagationRuleOn
   *          the new taint propagation rule on
   */
  public void setTaintPropagationRuleOn(boolean taintPropagationRuleOn) {
    this.taintPropagationRuleOn = taintPropagationRuleOn;
  }



  /**
   * Checks if is imprecise taint propagation rule on.
   *
   * @return true, if is imprecise taint propagation rule on
   */
  public boolean isImpreciseTaintCreationRuleOn() {
    return impreciseTaintCreationRuleOn;
  }

  /**
   * Sets the imprecise taint propagation rule on.
   *
   * @param impreciseTaintCreationRuleOn
   *          the new imprecise taint propagation rule on
   */
  public void setImpreciseTaintCreationRuleOn(boolean impreciseTaintCreationRuleOn) {
    this.impreciseTaintCreationRuleOn = impreciseTaintCreationRuleOn;
  }

  /**
   * Checks if is imprecise propagation rule on.
   *
   * @return true, if is imprecise propagation rule on
   */
  public boolean isImprecisePropagationRuleOn() {
    return imprecisePropagationRuleOn;
  }

  /**
   * Sets the imprecise propagation rule on.
   *
   * @param imprecisePropagationRuleOn
   *          the new imprecise propagation rule on
   */
  public void setImprecisePropagationRuleOn(boolean imprecisePropagationRuleOn) {
    this.imprecisePropagationRuleOn = imprecisePropagationRuleOn;
  }

  /**
   * Checks if is concrete taint creation rule on.
   *
   * @return true, if is concrete taint creation rule on
   */
  public boolean isConcreteTaintCreationRuleOn() {
    return concreteTaintCreationRuleOn;
  }

  /**
   * Sets the concrete taint creation rule on.
   *
   * @param concreteTaintCreationRuleOn
   *          the new concrete taint creation rule on
   * @param assignOn
   *          the concrete taint at assign stmt rule on
   * @param returnOn
   *          the concrete taint at return stmt rule on
   * @param calleeOn
   *          the concrete taint at callee rule on
   */
  public void setConcreteTaintCreationRuleOn(boolean concreteTaintCreationRuleOn, boolean assignOn,
      boolean returnOn, boolean calleeOn) {
    this.concreteTaintCreationRuleOn = concreteTaintCreationRuleOn;
    if (concreteTaintCreationRuleOn) {
      concreteTaintAtAssignStmtOn = assignOn;
      concreteTaintAtReturnStmtOn = returnOn;
      concreteTaintAtCalleeOn = calleeOn;
    } else {
      concreteTaintAtAssignStmtOn = false;
      concreteTaintAtReturnStmtOn = false;
      concreteTaintAtCalleeOn = false;
    }
  }

  /**
   * Sets the concrete taint creation rule on. The sub rules are all enabled if
   * concreteTaintCreationRuleOn is true, otherwise they are all disabled.
   * {@link ConcreteTaintCreationRule}.
   *
   * @param concreteTaintCreationRuleOn
   *          the new concrete taint creation rule on
   */
  public void setConcreteTaintCreationRuleOn(boolean concreteTaintCreationRuleOn) {
    this.concreteTaintCreationRuleOn = concreteTaintCreationRuleOn;
    if (!concreteTaintCreationRuleOn) {
      concreteTaintAtAssignStmtOn = false;
      concreteTaintAtReturnStmtOn = false;
      concreteTaintAtCalleeOn = false;

    }
  }

  /**
   * Checks if is concrete taint at assign stmt on.
   *
   * @return true, if is concrete taint at assign stmt on
   */
  public boolean isConcreteTaintAtAssignStmtOn() {
    return concreteTaintAtAssignStmtOn;
  }

  /**
   * Checks if is concrete taint at return stmt on.
   *
   * @return true, if is concrete taint at return stmt on
   */
  public boolean isConcreteTaintAtReturnStmtOn() {
    return concreteTaintAtReturnStmtOn;
  }

  /**
   * Checks if is concrete taint at callee on.
   *
   * @return true, if is concrete taint at callee on
   */
  public boolean isConcreteTaintAtCalleeOn() {
    return concreteTaintAtCalleeOn;
  }

  /**
   * Checks if is static field propagation rule is on.
   *
   * @return true, if is static field propagation
   */
  public boolean isStaticFieldPropagationRuleOn() {
    return staticFieldPropagationOn;
  }

  /**
   * Sets the static field propagation.
   *
   * @param staticFieldPropagation
   *          the new static field propagation
   */
  public void setStaticFieldPropagationRuleOn(boolean staticFieldPropagation) {
    staticFieldPropagationOn = staticFieldPropagation;
  }

  /**
   * Checks if is time out on.
   *
   * @return true, if is time out on
   */
  public boolean isTimeOutOn() {
    return timeOutOn;
  }

  /**
   * Sets the time out on.
   *
   * @param timeOutDuration
   *          the time out duration in seconds
   */
  public void setTimeOutOn(int timeOutDuration) {
    timeOutOn = true;
    this.timeOutDuration = timeOutDuration;
  }

  /**
   * Gets the time out duration.
   *
   * @return the time out duration
   */
  public int getTimeOutDuration() {
    return timeOutDuration;
  }

  /**
   * Checks if is write jimple output.
   *
   * @return true, if is write jimple output
   */
  public boolean isWriteJimpleOutput() {
    return writeJimpleOutput;
  }

  /**
   * Sets the write jimple output.
   *
   * @param writeJimpleOutput
   *          the new write jimple output
   */
  public void setWriteJimpleOutput(boolean writeJimpleOutput) {
    this.writeJimpleOutput = writeJimpleOutput;
  }

  /**
   * Checks if is write html output.
   *
   * @return true, if is write html output
   */
  public boolean isWriteHtmlOutput() {
    return writeHtmlOutput;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String s = "\n";
    if (isUIConstraintCreationRuleOn()) {
      s += "CONFIG: UIConstraintCreationRule is enabled.\n";
    } else {
      s += "CONFIG: UIConstraintCreationRule is NOT enabled.\n";
    }
    if (isTaintConstraintCreationRuleOn()) {
      s += "CONFIG: TaintConstraintCreationRule is enabled.\n";
    } else {
      s += "CONFIG: TaintConstraintCreationRule is NOT enabled.\n";
    }
    if (isTaintPropagationRuleOn()) {
      s += "CONFIG: TaintPropagationRule is enabled.\n";
    } else {
      s += "CONFIG: TaintPropagationRule is NOT enabled.\n";
    }
    if (isSourceTaintCreationRuleOn()) {
      s += "CONFIG: SourceTaintCreationRule is enabled.\n";
    } else {
      s += "CONFIG: SourceTaintCreationRule is NOT enabled.\n";
    }
    if (isImpreciseTaintCreationRuleOn()) {
      s += "CONFIG: ImpreciseTaintCreationRule is enabled.\n";
    } else {
      s += "CONFIG: ImpreciseTaintCreationRule is NOT enabled.\n";
    }

    if (isImprecisePropagationRuleOn()) {
      s += "CONFIG: ImprecisePropagationRule is enabled.\n";
    } else {
      s += "CONFIG: ImprecisePropagationRule is NOT enabled.\n";
    }
    if (isConcreteTaintCreationRuleOn()) {
      s += "CONFIG: ConcreteTaintPropagationRule is enabled.\n";
      if (isConcreteTaintAtAssignStmtOn()) {
        s += "- ConcreteTaintAtAssignStmtOn is enabled\n";
      } else {
        s += "- ConcreteTaintAtAssignStmtOn is NOT enabled\n";
      }
      if (isConcreteTaintAtReturnStmtOn()) {
        s += "- ConcreteTaintAtReturnStmtOn is enabled\n";
      } else {
        s += "- ConcreteTaintAtReturnStmtOn is NOT enabled\n";
      }
      if (isConcreteTaintAtCalleeOn()) {
        s += "- ConcreteTaintAtCalleeOn is enabled\n";
      } else {
        s += "- ConcreteTaintAtCalleeOn is NOT enabled\n";
      }
    } else {
      s += "CONFIG: ConcreteTaintPropagationRule is NOT enabled.\n";
    }
    if (isStaticFieldPropagationRuleOn()) {
      s += "CONFIG: StaticFieldPropagationRule is enabled.\n";
    } else {
      s += "CONFIG: StaticFieldPropagationRule is NOT enabled.\n";
    }
    if (timeOutOn) {
      s += "CONFIG: Timeout in " + timeOutDuration / 60 + " mins.\n";
    }
    if (writeHtmlOutput) {
      s += "CONFIG: Write results into HTML files.\n";
    }
    if (writeJimpleOutput) {
      s += "CONFIG: Write results into Jimple files.";
    }
    return s;
  }

  /**
   * Sets the write html output.
   *
   * @param writeHtmlOutput
   *          the new write html output
   */
  public void setWriteHtmlOutput(boolean writeHtmlOutput) {
    this.writeHtmlOutput = writeHtmlOutput;
  }


  /**
   * Turn on all rules.
   */
  public void turnOnAllRules() {
    setUIConstraintCreationRuleOn(true);
    setTaintConstraintCreationRuleOn(true);
    setSourceTaintCreationRuleOn(true);
    setTaintPropagationRuleOn(true);
    setImpreciseTaintCreationRuleOn(true);
    setImprecisePropagationRuleOn(true);
    setConcreteTaintCreationRuleOn(true, true, true, true);
    setStaticFieldPropagationRuleOn(true);
  }

  public String getConfigDir() {
    return this.configDir;
  }

  public void setConfigDir(String configDir) {
    this.configDir = configDir;
  }
}
