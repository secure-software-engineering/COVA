package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class InterSingle3 {

  public void test() {
    callee(Configuration.featureA());
  }

  private void callee(boolean a) {
    if (a) {
      System.out.println();// A
    }
  }

}
