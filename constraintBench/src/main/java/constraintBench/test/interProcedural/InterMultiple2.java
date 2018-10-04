package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class InterMultiple2 {

  public InterMultiple2() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
  }

  public void test() {
    boolean config = Configuration.featureA();
    if (config) {
      System.out.println();// A
    }
    config = Configuration.featureB();
    boolean newConfig = callee(config);
    if (newConfig) {
      System.out.println();// (B ^ C)
    }
    System.out.println();
  }

  private boolean callee(boolean in) {
    boolean ret = in;
    if (ret) {
      ret = Configuration.featureC();// B
    }
    return ret;
  }
}
