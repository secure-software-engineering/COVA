package constraintBench.test.indirect;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class IndirectConcrete1 {

  public void test() {
    int d = Configuration.featureD();
    int a = 0;
    if (d == 2) {
      a = 1; // D = 2
    }
    if (d == 3) {
      a = 2;// D = 3
    }
    int e = 0;
    if (a >= 0) {
      e = Integer.parseInt("1"); // (D = 2) v (D = 3)
    } else {
      e = Integer.parseInt("2");// !(D = 2) ^ !(D = 3)
    }
    if (a < 2) {
      e = Integer.parseInt("3");// D = 2
    }
    System.out.print(e);
  }

}
