package constraintBench.test.callbacks;

import constraintBench.utils.Callback;

/**
 * 
 *
 */
public class Callback1 {
  private class TestClass implements Callback {

    @Override
    public void onClick() {
      System.out.println();// onScroll ^ onClick
    }

    @Override
    public void onScroll() {
      System.out.println(); // onScroll
    }
  }

  public void test() {
    int flag = -50;
    while (flag < 0) {
      TestClass testClass = new TestClass();
      testClass.onScroll();
      testClass.onClick(); // onScroll
      flag++;// onScroll ^ onClick
    }
  }
}
