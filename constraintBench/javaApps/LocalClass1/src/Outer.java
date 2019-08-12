import utils.Configuration;

/**
 * Local class sets the field of outer class to a configuration option
 * 
 * 
 *
 */
public class Outer {

  private boolean field;

  public void callee()
  {
    class Local {
      public void set()
      {
        field = new Configuration().featureA();
      }
    }
    Local local = new Local();
    local.set();
    if (field) {
      System.out.println("A");// A
    }
  }

  public static void main(String[] args) {
    new Outer().callee();
  }

}
