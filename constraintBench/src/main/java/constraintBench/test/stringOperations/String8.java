package constraintBench.test.stringOperations;

import constraintBench.utils.Configuration;

public class String8 {
  public void test() {
    String a = Configuration.fieldA;
    if (a.contains("@") && a.length() > 10 && a.endsWith(".com")) {
      System.out
          .println(); // (str.contains(FA, "@") ^ !(str.len(FA)<=10) ^ str.suffixof(".com", FA))
    }
  }
}
