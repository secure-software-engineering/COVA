package constraintBench.test.loops;

import constraintBench.utils.Configuration;

/**
 * 
 * @author Linghui Luo
 */
public class Loop1 {

  public void test() {
    while (true) {
      if (Configuration.featureA()) {
        System.out.println();// A
      } else {
        System.out.println();// !A
      }
    }
  }
}
