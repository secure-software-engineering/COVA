package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanSingle5 {

  public void test() {
    boolean A = Configuration.featureA();
    boolean NotA = !Configuration.featureA();
    int flag = 0;
    if (A || NotA) {
      flag = 2; // true
    }
    System.out.println(flag);
  }

}
