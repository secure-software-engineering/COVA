package cova.automatic.data;

public class TestResultInput {
  private TestResultInputType elementType;
  private String elementClass;
  private String value;
  private String elementId;

  public TestResultInput(
      TestResultInputType elementType, String elementClass, String value, String elementId) {
    this.elementType = elementType;
    this.elementClass = elementClass;
    this.value = value;
    this.elementId = elementId;
  }

  public TestResultInputType getElementType() {
    return elementType;
  }

  public void setElementType(TestResultInputType elementType) {
    this.elementType = elementType;
  }

  public String getElementClass() {
    return elementClass;
  }

  public void setElementClass(String elementClass) {
    this.elementClass = elementClass;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getElementId() {
    return elementId;
  }

  public void setElementId(String elementId) {
    this.elementId = elementId;
  }
}
