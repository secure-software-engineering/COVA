package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * bar.field1 is set to feature A, foo.bar is set to bar, foo.bar.field1 is used in if-statement.
 * 
 * 
 *
 */
public class InstanceField4 {

  private class Foo {
    public Bar bar;
    public Foo() {
      bar = null;
    }
  }

  private class Bar {
    public boolean field1;
    public boolean field2;

    public Bar() {
      field1 = false;
      field2 = false;
    }
  }

  public void test() {
    Foo foo = new Foo();
    Bar bar = new Bar();
    bar.field1 = Configuration.featureA();
    foo.bar = bar;
    if (foo != null) {
      int a = 0;
      System.out.println(a);
    }
    if (foo.bar != null) {
      int b = 1;
      System.out.println(b);
    }
    if (foo.bar.field1) {
      int c = 2; // A
      System.out.println(c);// A
    }
    if (foo.bar.field2) {
      int d = 3;
      System.out.println(d);
    }
  }

}
