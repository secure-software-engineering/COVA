package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String4 {

  public void test() {
    String a = Configuration.fieldA;
    if (a.startsWith("abc")) {
      System.out.println(); // str.prefixof("abc", FA)
    } else {
      System.out.println(); // !str.prefixof("abc", FA)
    }
  }
}
