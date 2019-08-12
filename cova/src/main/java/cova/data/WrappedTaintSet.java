/*
 * @version 1.0
 */

package cova.data;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import soot.Local;
import soot.PrimType;
import soot.SootMethod;
import soot.Value;
import soot.toolkits.scalar.Pair;

import cova.data.taints.AbstractTaint;
import cova.data.taints.ConcreteTaint;
import cova.data.taints.ImpreciseTaint;
import cova.data.taints.SourceTaint;
import cova.data.taints.SymbolicTaint;
import cova.data.taints.ZeroTaint;

/**
 * The Class WrappedTaintSet is a container of taint set.
 * 
 * <p>
 * It provides all operations that can be done to the taint set.
 * </p>
 * 
 */
public class WrappedTaintSet implements Iterable<AbstractTaint> {

  /** The taints. */
  private final Set<AbstractTaint> taints;

  /**
   * Instantiates a new wrapped taint set.
   */
  public WrappedTaintSet() {
    taints = new HashSet<AbstractTaint>();
  }

  /**
   * Instantiates a new wrapped taint set with the given wrapped taint set.
   *
   * @param t
   *          the given wrapped taint set.
   * 
   */
  public WrappedTaintSet(WrappedTaintSet t) {
    taints = new HashSet<AbstractTaint>(t.taints);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("{");
    int i = 0;
    for (AbstractTaint t : taints) {
      sb.append(t.toString());
      if (i != taints.size() - 1) {
        sb.append(", ");
        i++;
      }
    }
    sb.append("}");
    return sb.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((taints == null) ? 0 : taints.hashCode());
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    WrappedTaintSet other = (WrappedTaintSet) obj;
    if (taints == null) {
      if (other.taints != null) {
        return false;
      }
    } else {
      if (taints.size() != other.taints.size()) {
        return false;
      } else {
        for (AbstractTaint taint : taints) {
          if (!other.contains(taint)) {
            return false;
          }
        }
      }
    }
    return true;
  }

  /**
   * Return all taints whose access path has the given access path as its prefix.
   *
   * @param a
   *          the given access path
   * @return the taints whose access path has the given access path as its prefix.
   */
  public Set<AbstractTaint> getTaintsStartWith(WrappedAccessPath a) {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      if (t.getAccessPath().hasPrefix(a)) {
        involvedTaints.add(t);
      }
    }
    return involvedTaints;
  }

  /**
   * Gets the taints whose access path is a local variable.
   *
   * @return the local taints
   */
  public Set<AbstractTaint> getLocalTaints() {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      if (t.getAccessPath().isLocal()) {
        involvedTaints.add(t);
      }
    }
    return involvedTaints;
  }

  /**
   * Return all taints whose access path has the given local variable as its base.
   *
   * @param base
   *          the base
   * @return the taints whose access path has the given local as its base.
   */
  public Set<AbstractTaint> getTaintsWithBase(Local base) {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      Local tbase = t.getAccessPath().getBase();
      if (tbase != null) {
        if (tbase.equals(base)) {
          involvedTaints.add(t);
        }
      }
    }
    return involvedTaints;
  }

  /**
   * Return all taints whose access path is public static field.
   *
   * @return the taints whose access path is public static field
   */
  public Set<AbstractTaint> getTaintsWithPublicStaticField() {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      WrappedAccessPath a = t.getAccessPath();
      if (a.isStaticFieldRef() && a.isPublic()) {
        involvedTaints.add(t);
      }
    }
    return involvedTaints;
  }

  /**
   * Return all taints whose access path is static field of the giving class.
   *
   * @param clName
   *          the class name
   * @return the taints whose access path is static field of the giving class
   */
  public Set<AbstractTaint> getPropagateTaintsWithStaticField(String clName) {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      WrappedAccessPath a = t.getAccessPath();
      if (a.isStaticFieldRef() && a.toString().startsWith(clName)) {
        involvedTaints.add(t);
      }
    }
    return involvedTaints;
  }

  /**
   * Gets the taints whose access path has the same String representation as the given access path.
   *
   * @param a
   *          the given access path
   * @return the taints with access path
   */
  public Set<AbstractTaint> getTaintsWithAccessPath(WrappedAccessPath a) {
    Set<AbstractTaint> involvedTaints = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      if (t.getAccessPath().toString().equals(a.toString())) {
        involvedTaints.add(t);
      }
    }
    return involvedTaints;
  }

  /**
   * Gets the symbolic taints whose access path has the same String representation as the given
   * access path.
   *
   * @param a
   *          the given access path
   * @return the taints with access path
   */
  public Set<SymbolicTaint> getSymbolicTaintsWithAccessPath(WrappedAccessPath a) {
    Set<SymbolicTaint> involvedTaints = new HashSet<SymbolicTaint>();
    for (AbstractTaint t : taints) {
      if (t instanceof SymbolicTaint) {
        if (t.getAccessPath().toString().equals(a.toString())) {
          involvedTaints.add((SymbolicTaint) t);
        }
      }
    }
    return involvedTaints;
  }

  /**
   * Check if the taint set contains the given taint.
   *
   * @param t
   *          the taint
   * @return true, if the taint set contains the given taint.
   */
  public boolean contains(AbstractTaint t) {
    for (AbstractTaint taint : taints) {
      if (taint.equals(t)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Gets all taints that are not {@link ZeroTaint} in this taint set.
   *
   * @return the non-zero taints
   */
  public Set<AbstractTaint> getNonZeroTaints() {
    Set<AbstractTaint> nonZero = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      if (!(t instanceof ZeroTaint)) {
        nonZero.add(t);
      }
    }
    return nonZero;
  }

  /**
   * Gets the zero taint in this taint set.
   *
   * @return the zero taint
   */
  public ZeroTaint getZeroTaint() {
    for (AbstractTaint taint : taints) {
      if (taint instanceof ZeroTaint) {
        return (ZeroTaint) taint;
      }
    }
    throw new RuntimeException("There is no zero taint.");
  }

  /**
   * Adds a taint to the taint set.
   *
   * @param t
   *          the given taint
   */
  public void add(AbstractTaint t) {
    if (!contains(t)) {
      taints.add(t);
    }
  }

  /**
   * Adds the given taints to this taint set.
   *
   * @param other
   *          the given taints
   */
  public void addAll(Collection<AbstractTaint> other) {
    for (AbstractTaint t : other) {
      add(t);
    }
  }

  /**
   * Adds the given taint set to this taint set.
   * 
   * @param other
   *          the given taint set
   */
  public void addAll(WrappedTaintSet other) {
    this.addAll(other.taints);
  }

  /**
   * Removes the all given taints from the taint set.
   *
   * @param other
   *          the given taints
   */
  public void removeAll(Collection<AbstractTaint> other) {
    for (AbstractTaint t : other) {
      remove(t);
    }
  }

  /**
   * Removes the given taint from the taint set.
   *
   * @param t
   *          the given taint
   */
  public void remove(AbstractTaint t) {
    taints.remove(t);
  }

  /**
   * Reset zero taint.
   */
  public void resetZeroTaint() {
    taints.remove(getZeroTaint());
    taints.add(ZeroTaint.getInstance());
  }

  /**
   * Merged the return taint set of callee to the local taint set of caller.
   *
   * @param retSet
   *          the return taint set
   * @return the merged taint set at call site.
   */
  public WrappedTaintSet merge(WrappedTaintSet retSet) {
    WrappedTaintSet merged = new WrappedTaintSet(this);
    for (AbstractTaint taint : retSet) {
      WrappedAccessPath accessPath = taint.getAccessPath();
      Set<AbstractTaint> localTaints = getTaintsWithAccessPath(accessPath);
      if (localTaints.size() > 0) {
        // this means the values of taints have been changed in the callee, so we should use the
        // value returned back.
        merged.removeAll(localTaints);
        merged.add(taint);
      } else {
        merged.add(taint);
      }
    }
    return merged;
  }

  /**
   * meet the given WrappedTaintSet otherSet to this taint set. Merge the taints with same access path together by disjoining
   * their constraints.
   *
   * @param otherSet
   *          the other wrapped taint set
   * @return the merged taint set.
   */
  public WrappedTaintSet meet(WrappedTaintSet otherSet) {
    WrappedTaintSet meeted = new WrappedTaintSet(this);
    if (equals(otherSet)) {
      return meeted;
    } else {
      WrappedTaintSet ret = new WrappedTaintSet();
      meeted.addAll(otherSet.taints);
      Set<ConcreteTaint> concreteTaints = new HashSet<ConcreteTaint>();
      Set<SymbolicTaint> symbolicTaints = new HashSet<SymbolicTaint>();
      for (AbstractTaint taint : meeted.taints) {
        if (taint instanceof ConcreteTaint) {
          concreteTaints.add((ConcreteTaint) taint);
        }
        if (taint instanceof SymbolicTaint) {
          symbolicTaints.add((SymbolicTaint) taint);
        }
      }
      if (contains(ZeroTaint.getInstance())) {
        ret.add(ZeroTaint.getInstance());
      }
      if (!concreteTaints.isEmpty()) {
        // concrete taint is identified by the pair of access path and value
        HashMap<Pair<WrappedAccessPath, Value>, ConcreteTaint> taintsMap = new HashMap<>();
        for (ConcreteTaint concreteTaint : concreteTaints) {
          Pair<WrappedAccessPath, Value> pair = new Pair<>(concreteTaint.getAccessPath(),
              concreteTaint.getCurrentValue());
          if (taintsMap.containsKey(pair)) {
            ConcreteTaint old = taintsMap.get(pair);
            ConcreteTaint updated = ConcreteTaint.meetConstraint(old, concreteTaint);
            taintsMap.put(pair, updated);
          } else {
            taintsMap.put(pair, concreteTaint);
          }
        }
        for (ConcreteTaint taint : taintsMap.values()) {
          ret.add(taint);
        }
      }
      if (!symbolicTaints.isEmpty()) {
        // symbolic taint is identified by the pair of access path and symbolic name
        HashMap<Pair<WrappedAccessPath, String>, SymbolicTaint> taintsMap = new HashMap<>();
        for (SymbolicTaint symbolicTaint : symbolicTaints) {
          Pair<WrappedAccessPath, String> pair = new Pair<>(symbolicTaint.getAccessPath(),
              symbolicTaint.getSymbolicName());
          if (taintsMap.containsKey(pair)) {
            SymbolicTaint old = taintsMap.get(pair);
            SymbolicTaint updated = SymbolicTaint.meetConstraint(old, symbolicTaint);
            taintsMap.put(pair, updated);
          } else {
            taintsMap.put(pair, symbolicTaint);
          }
        }
        for (SymbolicTaint taint : taintsMap.values()) {
          ret.add(taint);
        }
      }
      return ret;
    }
  }

  /**
   * This method check if the taint set with the same access path is complete, which means the
   * union(or-operation) of all constraints, say b, should be equal to the constraint of the
   * statement c. If it is not complete, return the missing constraint a. The missing constraint can
   * be computed by (c v -b) due to the following derivation.
   * 
   * <p>
   * a v b = c <br>
   * a v (b v -b) = c v -b <br>
   * a v true = c v -b <br>
   * a = c v -b<br>
   * </p>
   * 
   * @param taints
   *          the taints
   * @param constraintOfStmt
   *          the constraint of the statement
   * @return the missing constraint
   */
  public static IConstraint getMissingConstraint(Set<AbstractTaint> taints,
      IConstraint constraintOfStmt) {
    IConstraint constraint = null;
    for (AbstractTaint taint : taints) {
      if (constraint == null) {
        constraint = taint.getConstraint();
      } else {
        constraint = constraint.or(taint.getConstraint(), true);
      }
    }
    if (constraint.equals(constraintOfStmt)) {
      return ConstraintZ3.getFalse();
    } else {
      IConstraint negation = constraint.negate(true);
      IConstraint c = negation;
      if (!constraintOfStmt.isTrue()) {
        c = c.or(constraintOfStmt, true);
      }
      return c;
    }
  }

  /**
   * Gets the all concrete taints in this taint set.
   *
   * @return the all concrete taints
   */
  public Set<AbstractTaint> getAllConcreteTaints() {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      if (taint instanceof ConcreteTaint) {
        ret.add(taint);
      }
    }
    return ret;
  }

  /**
   * Gets the all source taints.
   *
   * @return the all source taints
   */
  public Set<AbstractTaint> getAllSourceTaints() {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      if (taint instanceof SourceTaint) {
        ret.add(taint);
      }
    }
    return ret;
  }

  /**
   * Gets the all imprecise taints.
   *
   * @return the all imprecise taints
   */
  public Set<AbstractTaint> getAllImpreciseTaints() {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      if (taint instanceof ImpreciseTaint) {
        ret.add(taint);
      }
    }
    return ret;
  }

  /**
   * Search taints which have the given caller base and create new taint at the callee by replacing
   * the base of each access path with the given callee base.
   *
   * @param callerBase
   *          the caller base
   * @param calleeBase
   *          the callee base
   * @return the sets of taints propagated to callee
   * 
   * @see GeneralPropagationRule#callEntryFlowFunction(soot.SootMethod, soot.Unit, soot.Unit,
   *      WrappedTaintSet)
   */
  public Set<AbstractTaint> deriveTaintsAtCallee(Local callerBase, Local calleeBase) {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      WrappedAccessPath accessPath = t.getAccessPath();
      if (!accessPath.isStaticFieldRef()) {
        if (accessPath.getBase().equals(callerBase)) {
          WrappedAccessPath accessPathAtCallee = new WrappedAccessPath(calleeBase,
              accessPath.getFields());
          AbstractTaint taintAtCallee = t.createNewTaintFromAccessPath(accessPathAtCallee);
          ret.add(taintAtCallee);
        }
      }
    }
    return ret;
  }

  /**
   * Search taints which have the given callee base and create new taint at the caller by replacing
   * the base of each access path with the given caller base.
   *
   * @param calleeBase
   *          the callee base
   * @param callerBase
   *          the caller base
   * @return the sets of taints propagated back to caller
   * 
   * @see GeneralPropagationRule#callExitFlowFunction(soot.SootMethod, soot.Unit, soot.Unit,
   *      WrappedTaintSet)
   */
  public Set<AbstractTaint> deriveTaintsAtCaller(Local calleeBase, Local callerBase) {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      WrappedAccessPath accessPath = t.getAccessPath();
      if (!accessPath.isStaticFieldRef() && accessPath.getBase().equals(calleeBase)) {
        WrappedAccessPath accessPathAtCaller = new WrappedAccessPath(callerBase,
            accessPath.getFields());
        AbstractTaint taintAtCaller = t.createNewTaintFromAccessPath(accessPathAtCaller);
        ret.add(taintAtCaller);
      }
    }
    return ret;
  }

  /**
   * Derive ret taints.
   *
   * @param value
   *          the value
   * @return the sets the
   */
  public Set<AbstractTaint> deriveRetTaints(Value value) {
    Set<AbstractTaint> ret = new HashSet<AbstractTaint>();
    for (AbstractTaint t : taints) {
      if (t.isReturnTaint()) {
        WrappedAccessPath ap = t.getAccessPath();
        AbstractTaint retTaint = t.createNewTaintFromAccessPath(
            new WrappedAccessPath(value, ap.getFields()));
        ret.add(retTaint);
      }
    }
    return ret;
  }

  /**
   * This method updates the constraint of each taint by performing AND-operation to the current
   * constraint and given constraint. It returns a taint set contains taints with updated
   * constraint.
   *
   * @param constraint
   *          the constraint
   * @return updated taint set
   */
  public WrappedTaintSet updateConstraintForTaints(IConstraint constraint) {
    WrappedTaintSet ret = new WrappedTaintSet();
    for (AbstractTaint t : taints) {
      IConstraint newConstraint = t.getConstraint().and(constraint, true);
      // only keep the taints whose constraint is not false, except zero taint
      if (!newConstraint.isFalse()) {
        if (newConstraint.equals(t.getConstraint())) {
          ret.add(t);
        } else {
          AbstractTaint copy = t.copy();
          if (!(t instanceof ZeroTaint)) {
            copy.updateConstraint(newConstraint);
          }
          ret.add(copy);

        }
      } else {
        if (t instanceof ZeroTaint) {
          ret.add(t);
        }
      }
    }
    return ret;
  }

  /**
   * Return the size of the taint set.
   *
   * @return the size of the taint set
   */
  public int size() {
    return taints.size();
  }

  /**
   * Checks if the taint set is empty.
   *
   * @return true, if the taint set is empty
   */
  public boolean isEmpty() {
    return taints.isEmpty();
  }

  /**
   * Clear the taint set.
   */
  public void clear() {
    taints.clear();
  }

  /**
   * Kill all taints which can not be propagated back to caller.
   *
   * @param method
   *          the method
   */
  public void killAllNonPropagate(SootMethod method) {
    boolean noParameter = method.getParameterCount() == 0 ? true : false;
    List<Local> params = method.getActiveBody().getParameterLocals();
    Set<AbstractTaint> toRemove = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      WrappedAccessPath accessPath = taint.getAccessPath();
      if (taint.isReturnTaint()) {
        continue;
      } else if (taint instanceof ZeroTaint) {
        continue;
      } else if (accessPath.isLocal()) {
        if (!accessPath.getBase().toString().equals("this")) {
          if (noParameter || accessPath.getType() instanceof PrimType) {
            // if the callee has no parameter, kill all locals, otherwise kill all locals of primitive
            // type
            toRemove.add(taint);
          } else {
            // if the callee has parameters, kill locals that are not on parameter
            if (!params.contains(accessPath.getBase())) {
              toRemove.add(taint);
            }
          }
        }
      }
    }
    removeAll(toRemove);
  }

  /**
   * Kill all public static taints.
   */
  public void killAllPublicStaticTaints() {
    Set<AbstractTaint> toRemove = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      WrappedAccessPath accessPath = taint.getAccessPath();
      if (accessPath.isStaticFieldRef() && accessPath.isPublic()) {
        toRemove.add(taint);
      }
    }
    removeAll(toRemove);
  }

  /**
   * Kill all imprecise taints.
   */
  public void killAllImpreciseTaints() {
    Set<AbstractTaint> toRemove = new HashSet<AbstractTaint>();
    for (AbstractTaint taint : taints) {
      if (taint instanceof ImpreciseTaint) {
        toRemove.add(taint);
      }
    }
    removeAll(toRemove);
  }

  @Override
  public Iterator<AbstractTaint> iterator() {
    return taints.iterator();
  }

  public void simplify() {
    for (AbstractTaint t : taints) {
      t.getConstraint().simplify();
    }
  }
}
