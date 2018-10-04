package constraintBench.test.imprecise;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class ImpreciseMultiple3 {

  public void test() {
    if (!Configuration.fieldA.startsWith("FA") && !Configuration.fieldB.startsWith("B")) {
      return; // !im(FA) ∧ !im(FB)
    } else if (Configuration.fieldC.equals("C")) {
      return;// (im(FA) ∨ im(FB)) ∧ im(FC)
    }
    System.out.println();// (im(FA) ∨ im(FB)) ∧ !im(FC)
  }

}
