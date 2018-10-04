package constraintBench.test.mixed;

import constraintBench.utils.Callback;
import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class Mixed1 {

  class TestClass implements Callback {

    @Override
    public void onClick() {
      int d = Configuration.featureD(); // onClick
      if (d > 5) {// onClick
        System.out.println(); // onClick ^ (D>5)
      } else {
        System.out.println(); // onClick ^ (D<=5)
      }
      System.out.println();// onClick
    }

    @Override
    public void onScroll() {
      if (Configuration.featureB()) {// A ^ onClick ^ onScroll
        System.out.println(); // A ^ onClick ^ onScroll ^ B
      }
    }
  }

  private int callee() {
    if (Configuration.featureA()) {
      return 1;// A
    } else {
      return 0;// !A
    }
  }

  public void test() {
    TestClass testClass = new TestClass();
    int i = callee();
    testClass.onClick();
    if (i > 0) { // onClick
      testClass.onScroll();// A^onClick
    }
    System.out.println();// (A^onClick^onScroll)v(!A^onClick)
  }

}
