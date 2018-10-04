package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * foo.a is set to feature A in test02(), foo is given as argument to callee(Foo foo), foo.a is used in if statement in
 * callee(Foo foo).
 * 
 *
 */
public class InstanceField2 {

  private class Foo {
    public boolean a;
    public boolean b;
  }

  private void callee(Foo foo) {
    int flag = 0;
    if (foo.a) {
      flag = 1; // A
    }
    if (foo.b) {
      flag = 2;
    }
    System.out.println(flag);
  }

  public void test() {
    Foo foo = new Foo();
    foo.a = Configuration.featureA();
    callee(foo);
  }

}
