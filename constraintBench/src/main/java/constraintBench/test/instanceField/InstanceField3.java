package constraintBench.test.instanceField;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 *
 */
public class InstanceField3 {

  class Foo {
    private boolean mode;

    public boolean getMode() {
      return mode;
    }

    public void setMode(boolean mode) {
      this.mode = mode; // A
    }
  }

  public void test() {
    Foo foo = new Foo();
    if (Configuration.featureA()) {
      foo.setMode(true);// A
    }
    if (foo.getMode()) {
      int d = 1; // A
      System.out.println(d);// A
    }
  }

}
