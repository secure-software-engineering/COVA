/**
 * Copyright (C) 2019 Linghui Luo
 *
 * <p>This library is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */
package cova.core;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import cova.source.data.SourceMethod;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.InvokeExpr;

/**
 * The Class SkipMethodOrClassRuleManager is used to check if some method or class should be skipped
 * from the analysis.
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
    signatureCache =
        CacheBuilder.newBuilder()
            .build(
                new CacheLoader<InvokeExpr, String>() {
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

  /** Instantiates a new SkipMethodOrClassRuleManager object. */
  private SkipMethodOrClassRuleManager() {
    toBeSkiped = new ArrayList<SourceMethod>();
    skipedCalls = new LinkedHashSet<InvokeExpr>();

    // Get the system methods
    // skip "java.lang.object void<init>()"
    objectInit = Scene.v().getObjectType().getSootClass().getMethodUnsafe("void <init>()");
    // skip "java.lang.object void<clinit>()"
    objectClinit = Scene.v().getObjectType().getSootClass().getMethodUnsafe("void <clinit>()");
    objectGetClass =
        Scene.v().getObjectType().getSootClass().getMethodUnsafe("java.lang.Class getClass()");
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
   * @param sourceMethods the new skip methods
   */
  public void setSkipMethods(Set<SourceMethod> sourceMethods) {
    for (SourceMethod method : sourceMethods) {
      toBeSkiped.add(method);
    }
  }

  /**
   * Checks if the given method is system method.
   *
   * @param method the method
   * @return true, if the given method is system method
   */
  private boolean isSystemMethod(SootMethod method) {
    return method == objectInit
        || method == objectClinit
        || method == objectGetClass
        || method == threadInit;
  }

  /**
   * Checks if the method called in the given invoke expression should be skipped during the
   * analysis.
   *
   * @param invokeExpr the invoke expression
   * @return true, if the method called in the invoke expression should be skipped during the
   *     analysis.
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
