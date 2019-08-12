import utils.Configuration;

/**
 * The inner class sets the field of out class to a configuration option
 * 
 * 
 *
 */
public class Outer {
  private boolean field;

  class Inner {
    void setField() {
      field = new Configuration().featureA();
    }
  }

  public static void main(String[] args) {
    Outer outer = new Outer();
    Inner inner = outer.new Inner();
    inner.setField();
    if (outer.field) {
      System.out.println("A");// A
    }
  }
}
