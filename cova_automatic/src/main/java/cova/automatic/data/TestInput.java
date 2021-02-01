package cova.automatic.data;

import cova.automatic.results.ConstraintInformation;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class TestInput {
  private String mainActivity;
  private List<ConstraintInformation> constraints;
  private Path apkPath;
  private ConstraintInformation selectedConstraint;
  private Map<Integer, String> mapping;

  public TestInput(AnalysisResult result, ConstraintInformation selectedConstraint) {

    this.mainActivity = result.getMainActivity();
    this.constraints = result.getConstraints();
    this.apkPath = result.getApkPath();
    this.selectedConstraint = selectedConstraint;
    this.mapping = result.getMapping();
  }

  public String getMainActivity() {
    return mainActivity;
  }

  public void setMainActivity(String mainActivity) {
    this.mainActivity = mainActivity;
  }

  public List<ConstraintInformation> getConstraints() {
    return constraints;
  }

  public void setConstraints(List<ConstraintInformation> constraints) {
    this.constraints = constraints;
  }

  public Path getApkPath() {
    return apkPath;
  }

  public void setApkPath(Path apkPath) {
    this.apkPath = apkPath;
  }

  public ConstraintInformation getSelectedConstraint() {
    return selectedConstraint;
  }

  public void setSelectedConstraint(ConstraintInformation selectedConstraint) {
    this.selectedConstraint = selectedConstraint;
  }

  public Map<Integer, String> getMapping() {
    return mapping;
  }
}
