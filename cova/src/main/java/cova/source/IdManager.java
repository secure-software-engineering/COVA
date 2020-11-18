package cova.source;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class IdManager {
  private static IdManager instance;

  private static final int START_VALUE = 20000;

  private Map<Integer, List<SourceInformation>> sources = new HashMap<>();

  private Map<Integer, String> ids = new HashMap<>();
  private Map<Integer, String> layouts = new HashMap<>();

  private Map<Integer, SourceInformation> mapping = new HashMap<>();

  private Map<String, Integer> activityToIdMapping = new HashMap<>();

  private Map<String, Integer> classnameToIntegerMapping = new HashMap<>();

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

  public Map<Integer, List<SourceInformation>> getSources() {
    return sources;
  }

  public Map<Integer, String> getLayouts() {
    return layouts;
  }

  public Map<Integer, String> getIds() {
    return ids;
  }

  public Map<String, Integer> getActivityToIdMapping() {
    return activityToIdMapping;
  }

  public Map<String, Integer> getClassnameToIntegerMapping() {
    return classnameToIntegerMapping;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void enable() {
    enabled = true;
  }
}
