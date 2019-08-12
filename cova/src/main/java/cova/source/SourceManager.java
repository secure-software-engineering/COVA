package cova.source;

import java.util.Set;

import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;

import cova.source.data.Source;
import cova.source.symbolic.SymbolicNameManager;

/**
 * The Class SourceManager manages all source-lated operations.
 * 
 * <p>
 * It contains a {@link #fieldOrMethodMatcher} and a {@link #callbackMatcher} and resets the
 * {@link SymbolicNameManager} by instantiation.
 * </p>
 * 
 **/
public class SourceManager {

  /** The FieldOrMethodMetcher. */
  private FieldOrMethodMatcher fieldOrMethodMatcher;

  /** The CallbackLMatcher. */
  private CallbackMatcher callbackMatcher;

  /**
   * Instantiates a new source manager.
   *
   * @param resourcePath
   *          the path of folder contains files used by {@link FieldOrMethodMatcher} and
   *          {@link CallbackMatcher}.
   * @param callbacksInApk
   *          the callbacks in the apk found by FlowDroid.
   */
  public SourceManager(String resourcePath, Set<CallbackDefinition> callbacksInApk) {
    this.fieldOrMethodMatcher = new FieldOrMethodMatcher(resourcePath);
    this.callbackMatcher = new CallbackMatcher(resourcePath, callbacksInApk);
    SymbolicNameManager.reset();
  }

  /**
   * Call {@link CallbackMatcher#searchCallback(SootMethod, Unit)}.
   *
   * @param parent
   *          the method contains the unit
   * @param unit
   *          the unit
   * @return the symbolic name of found callback. It can be null when no callback is found.
   */
  public String searchCallback(SootMethod parent, Unit unit) {
    return this.callbackMatcher.searchCallback(parent, unit);
  }

  /**
   * Call {@link FieldOrMethodMatcher#searchFieldOrMethod(SootMethod, Unit)}.
   *
   * @param parent
   *          the method contains the unit
   * @param unit
   *          the unit
   * @return the symbolic name of found source field/method. It can be null when no source in found.
   */
  public String searchFieldOrMethod(SootMethod parent, Unit unit) {
    return this.fieldOrMethodMatcher.searchFieldOrMethod(parent, unit);
  }

  public Set<Source> getSources()
  {
    Set<Source> sources = fieldOrMethodMatcher.getSources();
    for (Source s : callbackMatcher.getSources()) {
      sources.add(s);
    }
    return sources;
  }
}
