
package cova.core;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import soot.Scene;
import soot.SootMethod;
import soot.jimple.InvokeExpr;

import cova.source.data.SourceMethod;

/**
 * The Class SkipMethodOrClassRuleManager is used to check if some method or class should be skipped
 * from the analysis.
 * 
 */
public class SkipMethodOrClassRuleManager {

  /** The skip calls. */
  private ArrayList<SourceMethod> toBeSkiped;

  /** The object init. */
  private final SootMethod objectInit;

  /** The object clinit. */
  private final SootMethod objectClinit;

  /** The object get class. */
  private final SootMethod objectGetClass;

  /** The thread init. */
  private final SootMethod threadInit;

  /** The instance. */
  private static SkipMethodOrClassRuleManager instance;

  private LinkedHashSet<InvokeExpr> skipedCalls;

  private static LoadingCache<InvokeExpr, String> signatureCache;

  static {
    signatureCache = CacheBuilder.newBuilder().build(new CacheLoader<InvokeExpr, String>() {
      @Override
      public String load(InvokeExpr invokeExpr) throws Exception {
        return getSignature(invokeExpr);
      }
    });
  }

  private static String getSignature(InvokeExpr invokeExpr) {
    String signature = signatureCache.getIfPresent(invokeExpr);
    if (signature == null) {
      StringBuilder sb = new StringBuilder(invokeExpr.getMethod().toString());
      // TODO: smarkus: performance
      int count = invokeExpr.getArgCount();
      if (count > 0) {
        sb.append("(");
        for (int i = 0; i < count; i++) {
          sb.append(invokeExpr.getArgs().get(i).toString());
          sb.append(",");
        }
        sb.setLength(sb.length() - 1);
        sb.append(")");
      }
      signature = sb.toString();
      signatureCache.put(invokeExpr, signature);
    }
    return signature;
  }

  /**
   * Instantiates a new SkipMethodOrClassRuleManager object.
   */
  private SkipMethodOrClassRuleManager() {
    toBeSkiped = new ArrayList<SourceMethod>();
    skipedCalls = new LinkedHashSet<InvokeExpr>();

    // Get the system methods
    // skip "java.lang.object void<init>()"
    objectInit = Scene.v().getObjectType().getSootClass().getMethodUnsafe("void <init>()");
    // skip "java.lang.object void<clinit>()"
    objectClinit = Scene.v().getObjectType().getSootClass().getMethodUnsafe("void <clinit>()");
    objectGetClass = Scene.v().getObjectType().getSootClass()
        .getMethodUnsafe("java.lang.Class getClass()");
    threadInit = Scene.v().grabMethod("<java.lang.Thread: void <init>()>");
  }

  /**
   * Gets the single instance of SkipMethodOrClassRuleManager.
   *
   * @return single instance of SkipMethodOrClassRuleManager
   */
  public static SkipMethodOrClassRuleManager getInstance() {
    if (instance == null) {
      instance = new SkipMethodOrClassRuleManager();
    }
    return instance;
  }

  /**
   * Normally we skip all source methods.
   *
   * @param sourceMethods
   *          the new skip methods
   */
  public void setSkipMethods(Set<SourceMethod> sourceMethods) {
    for (SourceMethod method : sourceMethods) {
      toBeSkiped.add(method);
    }
  }

  /**
   * Checks if the given method is system method.
   *
   * @param method
   *          the method
   * @return true, if the given method is system method
   */
  private boolean isSystemMethod(SootMethod method) {
    return method == objectInit || method == objectClinit || method == objectGetClass
        || method == threadInit;
  }

  /**
   * Checks if the method called in the given invoke expression should be skipped during the
   * analysis.
   * 
   * @param invokeExpr
   *          the invoke expression
   * @return true, if the method called in the invoke expression should be skipped during the
   *         analysis.
   */
  public boolean isSkipCall(InvokeExpr invokeExpr) {
    if (skipedCalls.contains(invokeExpr)) {
      return true;
    } else {
      SootMethod method = invokeExpr.getMethod();
      if (isSystemMethod(method)) {
        skipedCalls.add(invokeExpr);
        return true;
      }
      String signature = getSignature(invokeExpr);
      final int length = toBeSkiped.size();
      for (int i = 0; i < length; i++) {
        Matcher matcher = toBeSkiped.get(i).getPattern().matcher(signature);
        if (matcher.find()) {
          skipedCalls.add(invokeExpr);
          return true;
        }
      }
      return false;
    }
  }

}
