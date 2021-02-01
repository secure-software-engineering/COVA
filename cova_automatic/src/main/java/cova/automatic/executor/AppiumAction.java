package cova.automatic.executor;

import io.appium.java_client.MobileElement;

public class AppiumAction {
  private MobileElement element;
  private Object value;
  private String actionType;
  private String id;

  public AppiumAction(String id, MobileElement element, Object value, String actionType) {
    this.element = element;
    this.value = value;
    this.actionType = actionType;
    this.id = id;
  }

  public String getActionType() {
    return actionType;
  }

  public void setActionType(String actionType) {
    this.actionType = actionType;
  }

  public Object getValue() {
    return value;
  }

  public void setValue(Object value) {
    this.value = value;
  }

  public MobileElement getElement() {
    return element;
  }

  public void setElement(MobileElement element) {
    this.element = element;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
