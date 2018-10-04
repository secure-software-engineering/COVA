package constraintBench.test.staticField;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class StaticField6 {

  static class Foo {
    private static boolean field = false;

    public static void main() {
      field = Configuration.featureA();
      callee();
    }

    private static void callee() {
      if (field) {
        System.out.println();// A
      }
    }
  }


  public void test()
  {
    Foo.main();
  }

}
