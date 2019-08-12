import utils.Configuration;

/***
 * The anonymous class sets the field of main class to a configuration option
 * 
 * 
 *
 */
public class Main {

  private boolean field;

  public boolean getField() {
    return field;
  }

  public static void main(String[] args) {
    InterfaceFoo foo = new InterfaceFoo() {
      @Override
      public void set(Main main) {
        main.field = new Configuration().featureA();
      }
    };
    Main main = new Main();
    foo.set(main);
    if (main.field) {
      System.out.println("A");// A
    }
  }
}
