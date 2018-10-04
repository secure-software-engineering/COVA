package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * foo.b is set to feature B in test01 and used in if statement in callee().
 * 
 * 
 */
public class InstanceField1 {

  private class Foo {
    public boolean b;
  }

  private Foo foo;

  private void callee() {
    if (foo.b) {
      System.out.println("B"); // B
    }
  }

  public void test() {
    foo = new Foo();
    foo.b = Configuration.featureB();
    callee();
  }

}
