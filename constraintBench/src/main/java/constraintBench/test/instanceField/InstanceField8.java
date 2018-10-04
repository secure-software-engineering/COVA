package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 *
 */
public class InstanceField8 {

  public InstanceField8() {
    System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "info");
  }

  private class Bar {
    public String a;
  }

  private class Foo {
    public Bar bar;

    public Foo() {
      bar = new Bar();
    }
  }

  public void test() {
    Foo foo = new Foo();
    foo.bar.a = Configuration.fieldA;
    callee(foo);
  }

  private void callee(Foo c) {
    String b = Configuration.fieldB;
    if (c.bar.a == b) {
      System.out.println("FA == FB");// FA = FB
    }
  }

}
