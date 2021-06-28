package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String2 {
  public void test() {
    String a = Configuration.fieldA;
    String b = Configuration.fieldB;
    if (a.equals(b)) {
      System.out.println(); // FA = FB
    }
  }
}
