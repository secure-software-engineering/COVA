package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String7 {

  public void test() {
    String a = Configuration.fieldA;
    if (a.contains("abc")) {
      System.out.println(); // str.contains(FA, "abc")
    } else {
      System.out.println(); // !str.contains(FA, "abc")
    }
  }
}
