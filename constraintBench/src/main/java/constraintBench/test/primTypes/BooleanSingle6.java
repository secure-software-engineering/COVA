package constraintBench.test.primTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class BooleanSingle6 {

  public void test() {
    if (Configuration.featureA() == true) {
      System.out.println();// A
    }
  }
}
