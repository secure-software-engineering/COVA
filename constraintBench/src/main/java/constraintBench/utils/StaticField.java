package constraintBench.utils;

/**
 * 
 * 
 */
public class StaticField {
  public static boolean f;
  public static boolean a;
  public static boolean b;
  private static String fieldA;
  private static String fieldB;

  public static void setFeature() {
    if (Configuration.featureB()) {
      f = Configuration.featureA(); // B
    }
  }

  public static void setFieldA() {
    fieldA = Configuration.fieldA;
  }

  public static void reset() {
    fieldA = null;
    fieldB = null;
  }

  public static void setFieldB() {
    fieldB = Configuration.fieldB;
  }

  public static String getFieldA() {
    return fieldA;
  }

  public static String getFieldB() {
    return fieldB;
  }
}
