package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class InterSingle7 {

  public void test() {
    String FA = Configuration.fieldA;
    String low = FA.toLowerCase();
    callee(low);
  }

  private void callee(String in) {
    for (int i = 0; i < 10; i++) {
      if (in.endsWith("fa")) {
        System.out.println();// str.suffixof(str.toLowerCase(FA), "fa")
      }
      System.out.println();
    }
    System.out.println();
  }
}
