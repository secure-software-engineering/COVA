/*
 * @version 1.0
 */
package cova.core;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.BoomerangTimeoutException;
import boomerang.DefaultBoomerangOptions;
import boomerang.jimple.Field;
import boomerang.jimple.Statement;
import boomerang.jimple.Val;
import boomerang.util.AccessPath;
import soot.Local;
import soot.PrimType;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.jimple.InstanceFieldRef;
import soot.jimple.Stmt;
import soot.jimple.toolkits.ide.icfg.BiDiInterproceduralCFG;

public class Aliasing {
  private final boolean turnoff = false;
  private static int notAnswered = 0;
  private static int total = 0;
  private final Boomerang solver;

  private static LoadingCache<BackwardQuery, Set<AccessPath>> queryCache;

  public Aliasing(BiDiInterproceduralCFG<Unit, SootMethod> icfg) {
    notAnswered = 0;
    total = 0;
    // Create a Boomerang solver.
    solver = new Boomerang(new DefaultBoomerangOptions() {
      @Override
      public boolean onTheFlyCallGraph() {
        // Must be turned of if no SeedFactory is specified.
        return false;
      };

      @Override
      public int analysisTimeoutMS() {
        return 300;
      }

    }) {
      @Override
      public BiDiInterproceduralCFG<Unit, SootMethod> icfg() {
        return icfg;
      }

    };

    queryCache = CacheBuilder.newBuilder().build(new CacheLoader<BackwardQuery, Set<AccessPath>>() {
      @Override
      public Set<AccessPath> load(BackwardQuery query) throws Exception {
        Set<AccessPath> aliases = queryCache.getIfPresent(query);
        if (aliases == null) {
          aliases = runBoomerang(query);
          queryCache.put(query, aliases);
        }
        return aliases;
      }

    });
  }

  /**
   * If the queried value is a local primitive type, it can not be queried by boomerang.
   *
   * @param value
   *          the value
   * @return true, if successful
   */
  public static boolean canBeQueried(Value value) {
    if (value.getType() instanceof PrimType) {
      if (value instanceof Local) {
        return false;
      }
    }
    return true;
  }

  private BackwardQuery createQuery(Stmt stmt, SootMethod method, Value value) {
    final Statement statement = new Statement(stmt, method);
    final Val val = new Val(value, method);
    return new BackwardQuery(statement, val);
  }

  /**
   * This method starts a boomerang query at the given statement for searching aliases of the given
   * value. It returns all aliases of the value, including the value itself.
   * 
   * @param value
   *          queried value
   * @param stmt
   *          the statement where the query starts
   * @param method
   *          the method contains the statement
   * @return all aliases of the given value
   * 
   */
  public Set<AccessPath> findAliasAtStmt(Value value, Stmt stmt, SootMethod method) {
    Set<AccessPath> aliases = new HashSet<AccessPath>(1);
    if (turnoff) {
      return aliases;
    } else {
      total++;
      try {
        final BackwardQuery query = createQuery(stmt, method, value);
        aliases = runBoomerang(query);
        if (aliases.isEmpty()) {
          // Boomerang can not find aliases of primitive types, must search aliases of the base at
          // first
          if (value instanceof InstanceFieldRef) {
            final InstanceFieldRef instanceField = (InstanceFieldRef) value;
            final Value base = instanceField.getBase();
            final BackwardQuery queryBase = createQuery(stmt, method, base);
            final Set<AccessPath> aliasesOfBase = runBoomerang(queryBase);
            for (final AccessPath aliasOfBase : aliasesOfBase) {
              final Val b = aliasOfBase.getBase();
              final Collection<Field> fields = aliasOfBase.getFields();
              fields.add(new Field(instanceField.getField()));
              aliases.add(new AccessPath(b, fields));
            }
          }
        }
      } catch (final BoomerangTimeoutException e) {
        notAnswered++;
      }
    }
    return aliases;
  }

  /**
   * Run boomerang with the given query.
   *
   * @param query
   *          the query
   * @return the set of aliases found by boomerang
   */
  private Set<AccessPath> runBoomerang(BackwardQuery query) {
    Set<AccessPath> aliases = queryCache.getIfPresent(query);
    if (aliases == null) {
      // Submit a query to the solver.
      solver.solve(query);
      aliases = solver.getAllAliases(query);
      queryCache.put(query, aliases);
    }
    return aliases;
  }

  public static double failedAliasing() {
    if (total == 0) {
      return 0;
    } else {
      double p = (double) notAnswered / (double) total;
      return p;
    }
  }
}
