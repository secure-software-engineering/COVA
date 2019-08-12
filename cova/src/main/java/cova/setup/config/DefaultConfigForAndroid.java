package cova.setup.config;

public class DefaultConfigForAndroid extends Config {
  public DefaultConfigForAndroid() {
    setUIConstraintCreationRuleOn(true);
    setTaintConstraintCreationRuleOn(true);
    setSourceTaintCreationRuleOn(true);
    setTaintPropagationRuleOn(true);
    setImpreciseTaintCreationRuleOn(true);
    setImprecisePropagationRuleOn(false);
    setConcreteTaintCreationRuleOn(true, true, true, true);
    setStaticFieldPropagationRuleOn(false);
    setWriteJimpleOutput(false);
    setWriteHtmlOutput(false);
  }
}
