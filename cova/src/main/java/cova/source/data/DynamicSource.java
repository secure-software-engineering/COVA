package cova.source.data;

public class DynamicSource extends Source {

  private String signature;

  public DynamicSource(SourceType type, String name, int id, String signature) {
    super(type, name, id);
    this.signature = signature;
  }

  @Override
  public String getSignature() {
    return signature;
  }
}
