package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class IntegerMultiple1 {

  public void test() {
    int d = Configuration.featureD();
    int f = Configuration.featureF();
    if (d == 5) {
      System.out.println(); // D = 5
      if (f == 6) { // D = 5
        System.out.println(56);// D = 5 ^ F = 6
        if (d == f) {// D = 5 ^ F = 6
          System.out.println("dead code"); // false
        }
      }
    }
  }

}
