package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * foo.bar.a is set to feature A, foo is given as argument to callee(Foo foo), foo.bar.a is used in if-statement in
 * callee(Foo foo).
 * 
 *
 *
 */
public class InstanceField5 {

  private class Bar {
    public boolean a;

    public Bar() {
      a = false;
    }
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
  }

  private void callee(Foo foo) {
    if (foo.bar.a) {
      System.out.println("A"); // A
    }
  }

}
