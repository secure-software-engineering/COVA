package constraintBench.test.switchStmts;

import constraintBench.utils.Configuration;

/**
 * 
 * 
 */
public class LookUpSwitchStmt1 {

  public void test() {
    String fa = Configuration.fieldA;
    switch (fa) {
    case "A":
      System.out.println();// FA = A
      break;
    case "B":
      System.out.println();// FA = B
      break;
    default:
      System.out.println("default");// !(FA = A) ^ !(FA = B)
      break;
    }
    System.out.println();
  }

}
