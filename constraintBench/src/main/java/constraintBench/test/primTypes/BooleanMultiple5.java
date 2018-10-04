package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class BooleanMultiple5 {

  public void test() {
    if (!Configuration.featureA() && !Configuration.featureB()) {
      System.out.println(); // !A ^ !B
      return;// !A ^ !B
    } else if (Configuration.featureC()) {
      System.out.println(); // ( A v B ) ^ C
      return; // ( A v B ) ^ C
    }
    System.out.println(); // ( A v B ) ^ !C
  }

}
