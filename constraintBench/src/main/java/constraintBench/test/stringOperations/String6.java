package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String6 {

  public void test() {
    String a = Configuration.fieldA;
    if (Integer.parseInt(a) > 5) {
      System.out.println(); // str.to_int(FA)>=5
    } else {
      System.out.println(); // str.to_int(FA)<=5
    }
  }
}
