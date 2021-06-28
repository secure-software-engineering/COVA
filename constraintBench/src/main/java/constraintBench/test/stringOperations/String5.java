package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String5 {

  public void test() {
    String a = Configuration.fieldA;
    if (a.endsWith("abc")) {
      System.out.println(); // str.suffixof("abc", FA)
    } else {
      System.out.println(); // !str.suffixof("abc", FA)
    }
  }
}
