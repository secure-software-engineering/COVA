package constraintBench.test.staticField;

import constraintBench.utils.Configuration;
import constraintBench.utils.StaticField;

/**
 * 
 * 
 */
public class StaticField3 {

  public void test()
  {
    StaticField.setFeature();
    if (StaticField.f) {
      System.out.println();// (A ^ B) v !B)
    }
    StaticField.f=Configuration.featureC();
    if (StaticField.f) {
      System.out.println();// C
    }
  }
  
}
