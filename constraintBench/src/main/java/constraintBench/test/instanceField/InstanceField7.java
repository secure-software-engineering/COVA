package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * foo.bar.b is set to feature B in callee(Foo foo) and used in if-statement after calling callee(Foo foo), Constraint B
 * should be created.
 * 
 * 
 *
 */
public class InstanceField7 {

  private class Bar {
    public boolean b;
  }

  private class Foo {
    public Bar bar;

    public Foo() {
      bar = new Bar();
    }
  }

  public void test() {
    Foo foo = new Foo();
    callee(foo);
    if (foo.bar.b) {
      System.out.println("B");// B
    }
  }

  private void callee(Foo foo) {
    foo.bar.b = Configuration.featureB();
  }

}
