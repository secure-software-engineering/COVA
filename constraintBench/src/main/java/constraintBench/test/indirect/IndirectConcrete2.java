package constraintBench.test.indirect;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class IndirectConcrete2 {

  public void test() {
    int v1 = Configuration.featureA() ? 1 : 0;
    int v2 = Configuration.featureB() ? 1 : 0;
    boolean k = v1 == v2;
    if (k) {
      System.out.println();// (A ^ B) ∨ (!A ^ !B)
    }
    boolean a = Configuration.featureA();
    boolean b = Configuration.featureB();
    boolean x = (a == b);
    if (x) {
      System.out.println(); // A = B
    }
  }

}
