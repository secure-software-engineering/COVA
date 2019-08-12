
package cova.reporter;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import soot.SootClass;

import cova.data.IConstraint;

/**
 * The Class LineConstraints stores the constraints of each line of code in a class after the
 * analysis.
 * 
 */
public class LineConstraints {

  /** The class. */
  private final SootClass cl;

  /** Map java line number to constraint of the line. */
  private TreeMap<Integer, IConstraint> javaLnConstraintMap;

  /** True, if all constraints which are true are filtered. */
  private boolean filtered = false;

  /**
   * Instantiates a new line results.
   *
   * @param cl
   *          the class
   */
  public LineConstraints(SootClass cl) {
    this.cl = cl;
    javaLnConstraintMap = new TreeMap<Integer, IConstraint>();
  }

  /**
   * Adds the line result.
   *
   * @param lineNumber
   *          the java line number
   * @param constraint
   *          the constraint of the line
   */
  public void addLineResult(int lineNumber, IConstraint constraint) {
    javaLnConstraintMap.put(lineNumber, constraint);
  }

  /**
   * remove all constraints which are true.
   */
  private void filterResult() {
    Set<Integer> removed = new HashSet<Integer>();
    for (Entry<Integer, IConstraint> entry : javaLnConstraintMap.entrySet()) {
      if (entry.getValue().isTrue()) {
        removed.add(entry.getKey());
      }
    }
    for (Integer line : removed) {
      javaLnConstraintMap.remove(line);
    }
    filtered = true;
  }

  /**
   * Gets the line number constraint map.
   *
   * @return the line number constraint map
   */
  public TreeMap<Integer, IConstraint> getLineNumberConstraintMap() {
    if (!filtered) {
      filterResult();
    }
    return javaLnConstraintMap;
  }

  /**
   * Checks if the filtered {@link LineConstraints#javaLnConstraintMap} is empty.
   *
   * @return true, if is empty
   */
  public boolean isEmpty() {
    if (!filtered) {
      filterResult();
    }
    return javaLnConstraintMap.isEmpty();
  }

  /**
   * Gets the class.
   *
   * @return the class
   */
  public SootClass getSootClass() {
    return cl;
  }

  @Override
  public String toString() {
    if (!filtered) {
      filterResult();
    }
    StringBuilder sb = new StringBuilder(cl.toString());
    sb.append(":\n");
    for (Entry<Integer, IConstraint> entry : javaLnConstraintMap.entrySet()) {
      sb.append(entry.getKey().toString());
      sb.append(".\t");
      sb.append(entry.getValue().toString());
      sb.append("\n");
    }
    return sb.toString();
  }

  public String toReadableString() {
    if (!filtered) {
      filterResult();
    }
    StringBuilder sb = new StringBuilder(cl.toString());
    sb.append(":\n");
    for (Entry<Integer, IConstraint> entry : javaLnConstraintMap.entrySet()) {
      sb.append(entry.getKey().toString());
      sb.append(".\t");
      sb.append(entry.getValue().toReadableString());
      sb.append("\n");
    }
    return sb.toString();
  }
  
  public int size()
  {
    if (!filtered) {
      filterResult();
    }
    return javaLnConstraintMap.size();
  }
}
