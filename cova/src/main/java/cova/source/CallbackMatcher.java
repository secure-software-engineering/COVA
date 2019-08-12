package cova.source;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;
import soot.jimple.infoflow.data.SootMethodAndClass;
import soot.util.Chain;
import soot.util.HashChain;

import cova.source.data.SourceUICallback;
import cova.source.parser.UICallbackParser;
import cova.source.symbolic.SymbolicNameManager;

/**
 * This class is used to search all input-related callback in a given statement.
 * 
 * <p>
 * The callbackParser reads user-defined input-related callbacks from UICallbacks.txt. The method
 * {@link #searchCallback(SootMethod, Unit)} checks if a given statement contains an UI callback.
 * </p>
 *
 * @date 29.08.2017
 */
public class CallbackMatcher {

  /** The parser to parse UICallbacks.txt. */
  private UICallbackParser callbackParser;

  /** The UI callbacks read by the parser. */
  private Set<SourceUICallback> uiCallbacks;

  /**
   * The callbacks used in an android apk found by FlowDroid. Callbacks declared in .xml file can be
   * found.
   */
  private Set<CallbackDefinition> callbacksInApk;

  /** The UI callbacks used in an android apk found by FlowDroid. */
  private Set<SourceUICallback> uiCallbacksInApk;

  /**
   * Instantiates a new CallbackMatcher.
   *
   * @param resourcePath
   *          the path of the folder contains UICallbacks.txt
   * @param callbacksInApk
   *          the callbacks used in an android apk found by FlowDroid.
   */
  public CallbackMatcher(String resourcePath, Set<CallbackDefinition> callbacksInApk) {
    callbackParser = new UICallbackParser();
    uiCallbacks = new HashSet<SourceUICallback>();
    uiCallbacksInApk = new HashSet<SourceUICallback>();
    loadCallbacks(resourcePath);
    this.callbacksInApk = new HashSet<CallbackDefinition>();
    if (callbacksInApk != null) {
      this.callbacksInApk = callbacksInApk;
    }
    if (!this.callbacksInApk.isEmpty()) {
      uiCallbacksInApk.clear();
      // select the ui callbacks from all callbacks used in the apk
      for (CallbackDefinition callback : callbacksInApk) {
        SootMethod method = callback.getTargetMethod();
        SootMethod parentMethod = callback.getParentMethod();
        for (SourceUICallback uicb : uiCallbacks) {
          String signature = uicb.getCallback().getSignature();
          if (signature.equals(parentMethod.getSignature())) {
            uiCallbacksInApk.add(new SourceUICallback(new SootMethodAndClass(method),
                new SootMethodAndClass(parentMethod), uicb.getId()));
          }
        }
      }
    }
  }

  /**
   * Checks if an unit contains an UI callback. If an UI callback is found in the unit, return the
   * symbolic name of this callback. Otherwise, return null.
   *
   * @param parent
   *          the method contains this unit
   * @param unit
   *          the unit
   * @return null when no UI callback is found in this unit. symbolicName when an UI callback is
   *         found in this unit
   */
  public String searchCallback(SootMethod parent, Unit unit) {
    String symbolicName = null;
    if (unit instanceof Stmt) {
      Stmt invokeStmt = (Stmt) unit;
      SootMethod method = invokeStmt.getInvokeExpr().getMethod();
      String signature = method.toString();
      // first we search a match from the UI callbacks in apk found by FlowDroid.
      if (!uiCallbacksInApk.isEmpty()) {
        for (SourceUICallback callback : uiCallbacksInApk) {
          Pattern pattern = callback.getMethodPattern();
          Matcher matcher = pattern.matcher(signature);
          if (matcher.find()) {
            symbolicName = SymbolicNameManager.getInstance().createSymbolicName(unit, callback);
            return symbolicName;
          }
        }
      }
      // handle UI callbacks which are not found by FlowDroid
      Chain<SootClass> interfaces = method.getDeclaringClass().getInterfaces();
      Chain<SootClass> parents = new HashChain<SootClass>();
      parents.addAll(interfaces);
      SootClass current = method.getDeclaringClass();
      while (current.hasSuperclass()) {
        SootClass superClass = current.getSuperclass();
        if (superClass.getMethodUnsafe(method.getSubSignature()) != null) {
          parents.add(superClass);
        }
        current = superClass;
      }
      for (SootClass i : parents) {
        {
          String parentSignature = StringUtils.replace(signature,
              method.getDeclaringClass().toString(),
              i.toString());
          for (SourceUICallback callback : uiCallbacks) {
            Pattern pattern = callback.getCallbackPattern();
            Matcher matcher = pattern.matcher(parentSignature);
            if (matcher.find()) {
              symbolicName = SymbolicNameManager.getInstance().createSymbolicName(unit, callback);
              return symbolicName;
            }
          }
        }
      }
    }
    return symbolicName;
  }

  /**
   * Load UI callbacks from UICallbacks.txt.
   *
   * @param resourcePath
   *          the resource path
   */
  private void loadCallbacks(String resourcePath) {
    try {
      if (!resourcePath.endsWith(File.separator)) {
        resourcePath += File.separator;
      }
      File callbackFile = new File(resourcePath + "UICallback_APIs.txt");
      if (callbackFile.exists()) {
        callbackParser.readFile(callbackFile.getCanonicalPath());
      }
      uiCallbacks.addAll(callbackParser.getAllCallbacks());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  Set<SourceUICallback> getSources() {
    return uiCallbacks;
  }
}
