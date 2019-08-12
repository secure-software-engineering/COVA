
package cova.source.symbolic;

import java.util.HashMap;
import java.util.List;

import soot.Unit;

import cova.source.data.Source;
import cova.source.data.SourceUICallback;

/**
 * The Class SymbolicNameManager manages the symbolic names used in constraints.
 */
public class SymbolicNameManager {

  /** The instance. */
  private static SymbolicNameManager instance;

  /**
   * The map stores symbolic name and the corresponding index counter. The counter is used for
   * create symbolic name for constraints caused by same callbacks and imprecise taints.
   */
  private HashMap<String, Integer> symbolicNameIndexMap;

  /** The map stores symbolic name created at an unit. */
  private HashMap<Unit, String> unitSymbolicNameMap;

  /** The map stores symbolicName and the corresponding user-defined name of the source. */
  private HashMap<String, String> symbolicToName;

  /**
   * Instantiates a new symbolic name manager.
   */
  private SymbolicNameManager() {
    symbolicNameIndexMap = new HashMap<String, Integer>();
    unitSymbolicNameMap = new HashMap<Unit, String>();
    symbolicToName = new HashMap<String, String>();
  }

  /**
   * Return the instance of the symbolic name manager.
   * 
   * @return the instance of the symbolic name manager
   */
  public static SymbolicNameManager getInstance() {
    if (instance == null) {
      instance = new SymbolicNameManager();
    }
    return instance;
  }

  /**
   * Reset the symbolic name manager.
   */
  public static void reset() {
    instance = new SymbolicNameManager();
  }

  /**
   * Creates the symbolic name for a given source.
   *
   * @param unit
   *          the unit
   * @param source
   *          the source
   * @return the symbolic name
   */
  public String createSymbolicName(Unit unit, Source source) {
    if (unitSymbolicNameMap.containsKey(unit)) {
      return unitSymbolicNameMap.get(unit);
    } else {
      String symbolicName = source.getUniqueName();
      String sourceName = source.getName();
      if (source instanceof SourceUICallback) {
        // a callback can be used multiple times, thus the symbolic name differs from index.

        int index = 0;
        if (symbolicNameIndexMap.containsKey(symbolicName)) {
          index = symbolicNameIndexMap.get(symbolicName) + 1;
        }
        symbolicNameIndexMap.put(symbolicName, index);
        StringBuilder sb = new StringBuilder(symbolicName);
        sb.append("_");
        sb.append(index);
        symbolicName = sb.toString();
        sb = new StringBuilder(sourceName);
        sb.append("_");
        sb.append(index);
        sourceName = sb.toString();
      }
      unitSymbolicNameMap.put(unit, symbolicName);
      symbolicToName.put(symbolicName, sourceName);
      return symbolicName;
    }
  }

  /**
   * Gets the source unique name.
   *
   * @param symbolicName
   *          the symbolic name
   * @return the source unique name
   */
  public String getSourceUniqueName(String symbolicName) {
    String sourceName = symbolicName.split("_")[0];
    return sourceName;
  }

  /**
   * Creates the imprecise symbolic name.
   * 
   * <p>
   * Imprecise symbolic name has the form im(S)_i, where S is the symbolic name of the source taint
   * and i is the index which differs imprecise symbolic name issued from the same source taint.
   * </p>
   *
   * @param unit
   *          the unit
   * @param symbolicName
   *          the symbolic names
   * @return the string
   */
  public String createImpreciseSymbolicName(Unit unit, List<String> symbolicNames) {
    if (unitSymbolicNameMap.containsKey(unit)) {
      String impreciseName = unitSymbolicNameMap.get(unit);
      boolean exist = true;
      for (String s : symbolicNames) {
        if (!impreciseName.contains(s)) {
          exist = false;
        }
      }
      if (exist) {
        return impreciseName;
      }
    }
    StringBuilder sb1 = new StringBuilder("im(");
    StringBuilder sb2 = new StringBuilder();
    for (int i = 0; i < symbolicNames.size(); i++) {
      String sourceName = getSourceUniqueName(symbolicNames.get(i));
      sb1.append(sourceName);
      sb2.append(sourceName);
      if (i != symbolicNames.size() - 1) {
        sb1.append("+");
      }
    }
    sb1.append(")");
    String sourceNames = sb2.toString();
    int index = 0;
    if (symbolicNameIndexMap.containsKey(sourceNames)) {
      index = symbolicNameIndexMap.get(sourceNames) + 1;
    }
    symbolicNameIndexMap.put(sourceNames, index);
    sb1.append("_");
    sb1.append(index);
    String impreciseName = sb1.toString();
    unitSymbolicNameMap.put(unit, impreciseName);
    symbolicToName.put(impreciseName, sourceNames);
    return impreciseName;
  }

  /**
   * Gets the source name.
   *
   * @param symbolicName
   *          the symbolic name
   * @return the source name
   */
  public String getSourceName(String symbolicName) {
    return symbolicToName.get(symbolicName);
  }
}
