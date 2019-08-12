
package unitTestSuite.testRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

import cova.runner.AndroidApkAnalyzer;
import cova.setup.config.Config;
import cova.setup.config.DefaultConfigForAndroid;
import utils.UnitTestFramework;

public class AndroidApkAnalyzerTest extends UnitTestFramework {

  @Test
  public void test() {
    try {
      Config config = new DefaultConfigForAndroid();
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-ITaint", "false" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertFalse(config.isImpreciseTaintCreationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-ITaint", "true" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isImpreciseTaintCreationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-CTaint", "false" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertFalse(config.isConcreteTaintCreationRuleOn());
        assertFalse(config.isConcreteTaintAtAssignStmtOn());
        assertFalse(config.isConcreteTaintAtReturnStmtOn());
        assertFalse(config.isConcreteTaintAtCalleeOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-CTaint", "true" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isConcreteTaintCreationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-CTaint", "true", "-CTA", "true",
            "-CTR", "true", "-CTC", "true" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isConcreteTaintCreationRuleOn());
        assertTrue(config.isConcreteTaintAtAssignStmtOn());
        assertTrue(config.isConcreteTaintAtReturnStmtOn());
        assertTrue(config.isConcreteTaintAtCalleeOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-STP", "false" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertFalse(config.isStaticFieldPropagationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-STP", "true" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isStaticFieldPropagationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-ITP", "false" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertFalse(config.isImprecisePropagationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-ITP", "true" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isImprecisePropagationRuleOn());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-output_html", "src" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isWriteHtmlOutput());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-output_jimple", "src" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isWriteJimpleOutput());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-t", "5" };
        AndroidApkAnalyzer.parseArgs(args, config);
        assertTrue(config.isTimeOutOn());
        assertEquals(300, config.getTimeOutDuration());
      }
      {
        String[] args = { "-p", "platform", "-apk", "test.apk", "-all" };
        AndroidApkAnalyzer.parseArgs(args, config);
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
