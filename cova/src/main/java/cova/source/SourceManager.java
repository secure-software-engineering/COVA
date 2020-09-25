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
package cova.source;

import cova.source.data.Source;
import cova.source.symbolic.SymbolicNameManager;
import java.util.Set;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.infoflow.android.callbacks.CallbackDefinition;

/**
 * The Class SourceManager manages all source-lated operations.
 *
 * <p>It contains a {@link #fieldOrMethodMatcher} and a {@link #callbackMatcher} and resets the
 * {@link SymbolicNameManager} by instantiation.
 */
public class SourceManager {

  /** The FieldOrMethodMetcher. */
  private FieldOrMethodMatcher fieldOrMethodMatcher;

  /** The CallbackLMatcher. */
  private CallbackMatcher callbackMatcher;

  /**
   * Instantiates a new source manager.
   *
   * @param resourcePath the path of folder contains files used by {@link FieldOrMethodMatcher} and
   *     {@link CallbackMatcher}.
   * @param callbacksInApk the callbacks in the apk found by FlowDroid.
   */
  public SourceManager(String resourcePath, Set<CallbackDefinition> callbacksInApk) {
    this.fieldOrMethodMatcher = new FieldOrMethodMatcher(resourcePath);
    this.callbackMatcher = new CallbackMatcher(resourcePath, callbacksInApk);
    SymbolicNameManager.reset();
  }

  /**
   * Call {@link CallbackMatcher#searchCallback(SootMethod, Unit)}.
   *
   * @param parent the method contains the unit
   * @param unit the unit
   * @return the symbolic name of found callback. It can be null when no callback is found.
   */
  public String searchCallback(SootMethod parent, Unit unit) {
    return this.callbackMatcher.searchCallback(parent, unit);
  }

  /**
   * Call {@link FieldOrMethodMatcher#searchFieldOrMethod(SootMethod, Unit)}.
   *
   * @param parent the method contains the unit
   * @param unit the unit
   * @return the symbolic name of found source field/method. It can be null when no source in found.
   */
  public String searchFieldOrMethod(SootMethod parent, Unit unit) {
    return this.fieldOrMethodMatcher.searchFieldOrMethod(parent, unit);
  }

  public Set<Source> getSources() {
    Set<Source> sources = fieldOrMethodMatcher.getSources();
    for (Source s : callbackMatcher.getSources()) {
      sources.add(s);
    }
    return sources;
  }
}
