package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle7 {

  public void test() {
    String FA = Configuration.fieldA;
    String low = FA.toLowerCase();
    callee(low);
  }

  private void callee(String in) {
    for (int i = 0; i < 10; i++) {
      if (in.endsWith("FA")) {
        System.out.println();// im(FA)
      }
      System.out.println();
    }
    System.out.println();
  }
}
