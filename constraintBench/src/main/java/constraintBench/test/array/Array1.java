package constraintBench.test.array;

import constraintBench.utils.Configuration;

/**
 *
 */
public class Array1 {

  public void test() {
    int d = Configuration.featureD();
    int f = Configuration.featureF();
    int[] arr = new int[2];
    arr[0] = d;
    arr[1] = f;
    if (arr[0] > 0) {
      System.out.println();// !(D<=0)
    }
  }
}
