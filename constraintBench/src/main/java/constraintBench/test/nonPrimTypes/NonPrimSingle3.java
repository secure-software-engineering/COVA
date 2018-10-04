package constraintBench.test.nonPrimTypes;

import constraintBench.utils.Configuration;
import constraintBench.utils.Property;

/**
 * 
 * 
 */
public class NonPrimSingle3 {

  public void test() {
    Property p1 = new Configuration().featureP();
    Property p2 = p1;
    if (p1 != null) {
      System.out.println();// P!=null
    }
    if (p2 != null) {
      System.out.println();// P!=null
    }
    p1 = new Configuration().featureQ();
    if (p1 != null) {
      System.out.println();// Q!=null
    }
    if (p2 != null) {
      System.out.println();// P!=null
    }
  }

}
