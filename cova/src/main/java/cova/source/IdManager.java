package cova.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import soot.SootClass;
import soot.util.HashMultiMap;
import soot.util.MultiMap;

public class IdManager {
  private static IdManager instance;

  private static final int START_VALUE = 20000;

  private MultiMap<SootClass, Integer> layoutClasses = new HashMultiMap<>();

  private Map<Integer, List<SourceInformation>> xmlSources = new HashMap<>();

  private Map<String, Integer> classnameToDynamicIdMapping = new HashMap<>();

  private Map<Integer, SourceInformation> mapping = new HashMap<>();

  private boolean enabled = false;

  public static IdManager getInstance() {
    if (instance == null) {
      instance = new IdManager();
    }
    return instance;
  }

  public boolean contains(int id) {
    return mapping.containsKey(id);
  }

  public synchronized Integer put(int layoutId, int id) {
    for (Entry<Integer, SourceInformation> e : mapping.entrySet()) {
      SourceInformation info = e.getValue();
      if (info.getLayoutId() == layoutId && info.getId() == id) {
        return e.getKey();
      }
    }
    int newId = START_VALUE;
    if (!mapping.isEmpty()) {
      newId = Collections.max(mapping.keySet()) + 1;
    }
    mapping.put(newId, new SourceInformation(layoutId, id));
    return newId;
  }

  public synchronized Integer put(SourceInformation newInfo) {
    for (Entry<Integer, SourceInformation> e : mapping.entrySet()) {
      SourceInformation info = e.getValue();
      if (info.getMethodName() == null) {
        continue;
      }
      if (info.getLayoutId() == newInfo.getLayoutId()
          && info.getId() == newInfo.getId()
          && info.getMethodName().equals(newInfo.getMethodName())
          && info.getTrigger().equals(newInfo.getTrigger())) {
        return e.getKey();
      }
    }
    int newId = START_VALUE;
    if (!mapping.isEmpty()) {
      newId = Collections.max(mapping.keySet()) + 1;
    }
    mapping.put(newId, newInfo);
    return newId;
  }

  public SourceInformation get(int tmpId) {
    return mapping.get(tmpId);
  }

  public Map<Integer, List<SourceInformation>> getXmlSources() {
    return xmlSources;
  }

  public Map<String, Integer> getClassnameToDynamicIdMapping() {
    return classnameToDynamicIdMapping;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void enable() {
    enabled = true;
  }

  public MultiMap<SootClass, Integer> getLayoutClasses() {
    return layoutClasses;
  }

  public void setLayoutClasses(MultiMap<SootClass, Integer> layoutClasses) {
    this.layoutClasses = layoutClasses;
  }
}
