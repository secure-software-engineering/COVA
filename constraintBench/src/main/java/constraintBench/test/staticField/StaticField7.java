package constraintBench.test.staticField;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class StaticField7 {

  static class Foo {
    public static boolean field = false;

    public void main() {
      field = Configuration.featureA();
      callee();
    }

    private void callee() {
      if (field) {
        System.out.println();// A
      }
    }
  }

  public void test()
  {
    new Foo().main();
  }
  
}
