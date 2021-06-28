package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String1 {

  public void test() {
    String a = Configuration.fieldA;
    if (a.equals("abc")) {
      System.out.println(); // "abc" = FA
    }
  }
}
