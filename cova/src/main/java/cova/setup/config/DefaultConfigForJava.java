package cova.setup.config;

public class DefaultConfigForJava extends Config {
  public DefaultConfigForJava() {
    setUIConstraintCreationRuleOn(true);
    setTaintConstraintCreationRuleOn(true);
    setSourceTaintCreationRuleOn(true);
    setTaintPropagationRuleOn(true);
    setImpreciseTaintCreationRuleOn(true);
    setImprecisePropagationRuleOn(false);
    setConcreteTaintCreationRuleOn(true, false, false, false);
    setStaticFieldPropagationRuleOn(false);
    setWriteJimpleOutput(false);
    setWriteHtmlOutput(false);
  }
}
