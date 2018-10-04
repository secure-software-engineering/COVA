package constraintBench.test.nonPrimTypes;

import constraintBench.utils.Configuration;
import constraintBench.utils.Property;
import constraintBench.utils.SuperProperty;

/**
 * 
 * 
 */
public class NonPrimSingle2 {

  public void test() {
    SuperProperty p = new Configuration().featureX();
    if(p instanceof Property) {
      System.out.println("");// im(X)
    }
  }

}
