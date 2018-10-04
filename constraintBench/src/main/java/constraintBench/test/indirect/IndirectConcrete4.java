package constraintBench.test.indirect;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class IndirectConcrete4 {

  public void test() {
    boolean a = Configuration.featureA();
    boolean para = false;
    if (a) {
      para = true;// A
    }
    callee(para);
  }

  public void callee(boolean a) {
    if (a) {
      System.out.println();// A
    }
  }
}
