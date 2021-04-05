package cova.automatic.data;

import cova.automatic.instrument.TargetStrings;
import cova.automatic.results.ConstraintInformation;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class AnalysisResult {
  private String mainActivity;
  private List<ConstraintInformation> constraints;
  private Path apkPath;
  private long instrumentTimeInMillis;
  private long preprocessTimeInMillis;
  private long covaTimeInMillis;
  private long activityTimeInMillis;

  private List<TargetStrings> possibleTargets;
  private Map<Integer, String> mapping;
  private String appiumURL;
  private TargetInformation targetInformation;

  public AnalysisResult() {}

  public AnalysisResult(AnalysisResult result) {
    this();
    this.mainActivity = result.getMainActivity();
    this.constraints = result.getConstraints();
    this.apkPath = result.getApkPath();
    this.mapping = result.getMapping();
    this.targetInformation = result.getTargetInformation();
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

  public long getInstrumentTimeInMillis() {
    return instrumentTimeInMillis;
  }

  public void setInstrumentTimeInMillis(long instrumentTimeInMillis) {
    this.instrumentTimeInMillis = instrumentTimeInMillis;
  }

  public long getPreprocessTimeInMillis() {
    return preprocessTimeInMillis;
  }

  public void setPreprocessTimeInMillis(long preprocessTimeInMillis) {
    this.preprocessTimeInMillis = preprocessTimeInMillis;
  }

  public long getCovaTimeInMillis() {
    return covaTimeInMillis;
  }

  public void setCovaTimeInMillis(long covaTimeInMillis) {
    this.covaTimeInMillis = covaTimeInMillis;
  }

  public List<TargetStrings> getPossibleTargets() {
    return possibleTargets;
  }

  public void setPossibleTargets(List<TargetStrings> possibleTargets) {
    this.possibleTargets = possibleTargets;
  }

  public long getActivityTimeInMillis() {
    return activityTimeInMillis;
  }

  public void setActivityTimeInMillis(long activityTimeInMillis) {
    this.activityTimeInMillis = activityTimeInMillis;
  }

  public Map<Integer, String> getMapping() {
    return mapping;
  }

  public void setMapping(Map<Integer, String> mapping) {
    this.mapping = mapping;
  }

  public String getAppiumURL() {
    return appiumURL;
  }

  public void setAppiumURL(String appiumURL) {
    this.appiumURL = appiumURL;
  }

  public TargetInformation getTargetInformation() {
    return targetInformation;
  }

  public void setTargetInformation(TargetInformation targetInformation) {
    this.targetInformation = targetInformation;
  }
}
