package constraintBench.utils;

;

/**
 * This is the class contains faked configuration options for test cases
 * 
 * 
 *
 */
public class Configuration {
  public static String fieldA = "FA";
  public static String fieldB = "FB";
  public static String fieldC = "FC";
  public static double k;
  public static float h;
  public static boolean featureA() {
    boolean var = true;
    return var;
  }

  public static boolean featureB() {
    return false;
  }

  public static boolean featureC() {
    return false;
  }

  public static int featureD() {
    return 1;
  }


  public static boolean featureE() {
    return false;
  }

  public static int featureF() {
    return 2;
  }

  public static double featureK() {
    return k;
  }

  public static float featureH()
  {
    return h;
  }

  public Property featureP() {
    return new Property();
  }

  public Property featureQ() {
    return new Property();
  }

  public SuperProperty featureX() {
    return new Property();
  }

}
