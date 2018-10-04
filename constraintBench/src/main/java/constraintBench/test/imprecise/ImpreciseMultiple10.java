package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;
import constraintBench.utils.Property;

/**
 * 
 * 
 */
public class ImpreciseMultiple10 {

  public void test() {
    Property p = new Configuration().featureP();
    Property q = new Configuration().featureQ();
    if (p.moreThanCompare(q) > 0) {// this method set the field of q to feature A
      System.out.println(); // !im(P+Q)_0
    }
    if (q.isFeatureEnable()) {
      System.out.println();// A
    }
  }
}
