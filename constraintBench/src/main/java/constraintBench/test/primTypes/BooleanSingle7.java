package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanSingle7 {

  public void test() {
    boolean a = Configuration.featureA();
    boolean b = false;
    if (b == a) {
      System.out.println();// !im(A)
    }
  }
  
}
