package constraintBench.test.infeasible;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Infeasible1 {

  public void test() {
    boolean b = Configuration.featureB();
    if (b) {
      System.out.println();// B
      boolean a = !b;// B
      if (a) {// B
        System.out.println("false");// false
        System.out.println("false");// false
      }
    }
  }

}
