package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String3 {

  public void test() {
    String a = Configuration.fieldA;
    if (a.length() < 8) {
      System.out.println(); // len(FA)<8
    } else {
      System.out.println(); // len(FA)>=8
    }
  }
}
