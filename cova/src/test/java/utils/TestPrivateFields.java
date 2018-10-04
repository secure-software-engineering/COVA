package utils;

import static org.junit.Assert.fail;
import java.lang.reflect.InvocationTargetException;

public class TestPrivateFields {

  /**
   * This method invokes a non accessible/ not visible (e.g. private) method of the given object
   *
   * @param obj relatet/surrounding object of the method
   * @param method Method of the given obj
   * @param param Parameter to forward to the method
   * 
   * @return return value of the invoked function
   */

  public static Object invokeMethod(Object obj, String method, Object[] param) {

    final java.lang.reflect.Method[] methods = obj.getClass().getDeclaredMethods();
    for (java.lang.reflect.Method m : methods) {

      // ASSUMPTION: the method name and parametercount is currently enough to determine the wanted
      // method
      // how to improve: check signature with respective types of param
      if (m.getName().equals(method) && (param == null || m.getParameterCount() == param.length)) {
        m.setAccessible(true);
        try {
          return m.invoke(obj, param);
        } catch (InvocationTargetException e) {
          fail(e.getCause().getMessage());
        } catch (Exception e) {
          fail(e.getMessage());
        }
      }
    }
    fail("no matching method found\n " + method + " with " + param.length + " Parameter(s)");
    return null;

  }


}
