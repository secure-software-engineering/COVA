package constraintBench.test.switchStmts;

import constraintBench.utils.Configuration;

/**
 * 
 *
 */
public class TableSwitchStmt1 {

  public void test() {
    int d = Configuration.featureD();
    switch (d) {
    case 2:
      System.out.println();// D = 2
      break;
    case 4:
      System.out.println();// D = 4
      break;
    case 8:
      System.out.println();// D = 8
      break;
    default:
      System.out.println("default");// !(D = 2)^!(D = 4)^!(D = 8)
      break;
    }
    System.out.println();
  }

}
