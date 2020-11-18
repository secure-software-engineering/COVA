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

import cova.core.SkipMethodOrClassRuleManager;
import cova.source.data.DynamicSource;
import cova.source.data.Source;
import cova.source.data.SourceField;
import cova.source.data.SourceMethod;
import cova.source.data.SourceType;
import cova.source.parser.SourceParser;
import cova.source.symbolic.SymbolicNameManager;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeExpr;

/**
 * This class is used to search configuration-related or input-related field or method in a given
 * statement.
 *
 * <p>The sourceParser reads user-defined configuration-related or input-related APIs(sources) from
 * ConfigurationSources.txt and InputSources.txt. The method {@link #searchFieldOrMethod(SootMethod,
 * Unit)} checks if a given statement contains a source.
 *
 * @date 29.08.2017
 */
public class FieldOrMethodMatcher {

  /** The parser to parse ConfigurationSources.txt and InputSources.txt. */
  private SourceParser sourceParser;

  /** The sources read by the parser. */
  private Set<Source> sources;

  /**
   * Instantiates a new FieldOrMethodMatcher.
   *
   * @param resourcePath the path of the folder that contains ConfigurationSources.txt and
   *     InputSources.txt.
   */
  public FieldOrMethodMatcher(String resourcePath) {
    sources = new HashSet<Source>();
    sourceParser = new SourceParser();
    loadSources(resourcePath);
    SkipMethodOrClassRuleManager.getInstance().setSkipMethods(getAllSourceMethods());
  }

  private String searchDynamicFieldOrMethod(SootMethod parent, Unit unit) {
    if (!IdManager.getInstance().isEnabled()) {
      return null;
    }
    if (unit instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) unit;
      if (assignStmt.containsInvokeExpr()) {
        InvokeExpr invoke = assignStmt.getInvokeExpr();
        SootMethod m = invoke.getMethod();
        if ("android.view.View".equals(m.getReturnType().toString())) {
          if ("findViewById".equals(m.getName())) {
            int activityId = SourceIdHelper.getActivityId(parent.getDeclaringClass());
            int fieldId = Integer.parseInt(invoke.getArg(0).toString());

            int resultingId = IdManager.getInstance().put(activityId, fieldId);

            Source source =
                new DynamicSource(SourceType.I, "I" + resultingId, resultingId, m.getSignature());
            return SymbolicNameManager.getInstance().createSymbolicName(unit, source);
          }
        }
      }
    }
    return null;
  }

  /**
   * Check if an unit(a statement in soot) contains a source. If a source is found in the unit,
   * return the symbolic name of this source. Otherwise, return null.
   *
   * @param parent the method contains this unit
   * @param unit the unit
   * @return null when no source is found in this unit. symbolicName when a source is found in this
   *     unit
   */
  public String searchFieldOrMethod(SootMethod parent, Unit unit) {
    String symbolicName = null;
    symbolicName = searchDynamicFieldOrMethod(parent, unit);
    if (symbolicName != null) {
      return symbolicName;
    }
    if (unit instanceof AssignStmt) {
      AssignStmt assignStmt = (AssignStmt) unit;
      Value rightOp = assignStmt.getRightOp();
      for (Source source : sources) {
        Pattern pattern = null;
        if (source instanceof SourceMethod) {
          pattern = ((SourceMethod) source).getPattern();
        } else if (source instanceof SourceField) {
          pattern = ((SourceField) source).getPattern();
        } else {
          throw new RuntimeException("Source type unsupported");
        }
        Matcher matcher = pattern.matcher(rightOp.toString());
        if (matcher.find()) {
          symbolicName = SymbolicNameManager.getInstance().createSymbolicName(unit, source);
          break;
        }
      }
    }
    return symbolicName;
  }

  /**
   * Load sources from ConfigurationSources.txt and InputSources.txt
   *
   * @param resourcePath the path of the folder that contains ConfigurationSources.txt and
   *     InputSources.txt
   */
  private void loadSources(String resourcePath) {
    try {
      if (!resourcePath.endsWith(File.separator)) {
        resourcePath += File.separator;
      }
      File confiFile = new File(resourcePath + "Configuration_APIs.txt");
      if (confiFile.exists()) {
        sourceParser.readFile(confiFile.getCanonicalPath());
      }
      File inputFile = new File(resourcePath + "IO_APIs.txt");
      if (inputFile.exists()) {
        sourceParser.readFile(inputFile.getCanonicalPath());
      }
      sources.addAll(sourceParser.getAllSources());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Gets the all source methods.
   *
   * @return the all source methods
   */
  private Set<SourceMethod> getAllSourceMethods() {
    Set<SourceMethod> methods = new HashSet<SourceMethod>();
    for (Source source : sources) {
      if (source instanceof SourceMethod) {
        methods.add((SourceMethod) source);
      }
    }
    return methods;
  }

  Set<Source> getSources() {
    return sources;
  }
}
