package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class BooleanSingle2 {

  public void test() {
    boolean featureA = Configuration.featureA();
    if (!featureA) {
      System.out.println();// !A
    }
    System.out.println();
  }

}
