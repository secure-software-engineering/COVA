package unitTestSuite.testRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import cova.runner.JavaAppAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForJava;
import utils.UnitTestFramework;

public class JavaAppAnalyzerTest extends UnitTestFramework {

  @Test
  public void test() {
    try {
      Config config = new DefaultConfigForJava();
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-ITaint",
            "false" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertFalse(config.isImpreciseTaintCreationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-ITaint",
            "true" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isImpreciseTaintCreationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-CTaint",
            "false" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertFalse(config.isConcreteTaintCreationRuleOn());
        assertFalse(config.isConcreteTaintAtAssignStmtOn());
        assertFalse(config.isConcreteTaintAtReturnStmtOn());
        assertFalse(config.isConcreteTaintAtCalleeOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-CTaint",
            "true" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isConcreteTaintCreationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-CTaint",
            "true", "-CTA", "true", "-CTR", "true", "-CTC", "true" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isConcreteTaintCreationRuleOn());
        assertTrue(config.isConcreteTaintAtAssignStmtOn());
        assertTrue(config.isConcreteTaintAtReturnStmtOn());
        assertTrue(config.isConcreteTaintAtCalleeOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-STP",
            "false" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertFalse(config.isStaticFieldPropagationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-STP",
            "true" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isStaticFieldPropagationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-ITP",
            "false" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertFalse(config.isImprecisePropagationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-ITP",
            "true" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isImprecisePropagationRuleOn());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config",
            "-output_html", "src" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isWriteHtmlOutput());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config",
            "-output_jimple", "src" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isWriteJimpleOutput());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-t",
            "5" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isTimeOutOn());
        assertEquals(300, config.getTimeOutDuration());
      }
      {
        String[] args = { "-app", "test", "-cp", "test//bin", "-config", "test//config", "-all" };
        JavaAppAnalyzer.parseArgs(args, config);
        assertTrue(config.isConcreteTaintAtAssignStmtOn());
        assertTrue(config.isConcreteTaintAtCalleeOn());
        assertTrue(config.isConcreteTaintAtReturnStmtOn());
        assertTrue(config.isConcreteTaintCreationRuleOn());
        assertTrue(config.isImprecisePropagationRuleOn());
        assertTrue(config.isImpreciseTaintCreationRuleOn());
        assertTrue(config.isSourceTaintCreationRuleOn());
        assertTrue(config.isStaticFieldPropagationRuleOn());
        assertTrue(config.isTaintConstraintCreationRuleOn());
        assertTrue(config.isTaintPropagationRuleOn());
      }
    } catch (ParseException e) {
      e.printStackTrace();
    }
  }
}
