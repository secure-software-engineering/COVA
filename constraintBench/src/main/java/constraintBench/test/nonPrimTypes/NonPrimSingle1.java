package constraintBench.test.nonPrimTypes;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class NonPrimSingle1 {

  public void test() {
    int flag = 0;
    if (Configuration.fieldA == null) {
      flag = 1; // FA = null
    } else {
      flag = 2; // FA !=null
    }
    System.out.println(flag);
  }

}
