package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * foo.bar.a is set to feature A in test06(), foo is given as argument to callee(Foo foo), foo.bar.a is set to false in
 * callee(Foo foo). foo.bar.a is used in if-statement after calling callee(Foo foo). No constraint should be created.
 * 
 *
 *
 */
public class InstanceField6 {

  private class Bar {
    public boolean a;
  }

  private class Foo {
    public Bar bar;

    public Foo() {
      bar = new Bar();
    }
  }

  public void test() {
    Foo foo = new Foo();
    foo.bar.a = Configuration.featureA();
    callee(foo);
    if (foo.bar.a) {
      System.out.println("No Constraint");
    }
  }

  private void callee(Foo foo) {
    foo.bar.a = false;
  }

}
