package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * @author Linghui Luo
 *
 */
public class ImpreciseSingle7 {

  public void test() {
    String a = Configuration.fieldA;
    String b = "";
    String c = b;
    b = a.substring(0, 2);
    if (b.contains("a")) {
      System.out.println();// str.contains(FA, "a")
    }
    if (c.contains("a")) {
      System.out.println();// no constraint
    }
  }

}
