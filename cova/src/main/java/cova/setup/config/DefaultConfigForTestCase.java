package cova.setup.config;

public class DefaultConfigForTestCase extends Config {
  public DefaultConfigForTestCase() {
    setUIConstraintCreationRuleOn(true);
    setTaintConstraintCreationRuleOn(true);
    setSourceTaintCreationRuleOn(true);
    setTaintPropagationRuleOn(true);
    setImpreciseTaintCreationRuleOn(true);
    setImprecisePropagationRuleOn(true);
    setConcreteTaintCreationRuleOn(true, true, true, true);
    setStaticFieldPropagationRuleOn(true);
    setWriteJimpleOutput(false);
    setWriteHtmlOutput(false);
  }
}
