package constraintBench.test.interProcedural;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class InterSingle1 {

  public void test() {
    boolean featureA = Configuration.featureA();
    int flag = 0;
    if (featureA) {
      flag = 1; // A
    }
    featureA = negate(featureA);
    if (featureA) {
      flag = 2; // !A
    }
    System.out.println(flag);
  }

  private boolean negate(boolean in) {
    return !in;
  }

}
