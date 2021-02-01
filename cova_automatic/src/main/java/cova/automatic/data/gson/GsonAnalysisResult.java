package cova.automatic.data.gson;

import cova.automatic.data.AnalysisResult;
import cova.automatic.instrument.TargetStrings;
import cova.automatic.results.ConstraintInformation;
import java.util.ArrayList;
import java.util.List;

public class GsonAnalysisResult {
  private String mainActivity;
  private String apkPath;
  private List<GsonConstraintInformation> constraints;
  private long instrumentTimeInMillis;
  private long preprocessTimeInMillis;
  private long activityTimeInMillis;
  private long covaTimeInMillis;
  private List<TargetStrings> possibleTargets;

  public List<TargetStrings> getPossibleTargets() {
    return possibleTargets;
  }

  public GsonAnalysisResult(AnalysisResult result) {
    this.mainActivity = result.getMainActivity();
    this.apkPath = result.getApkPath().getFileName().toString();
    constraints = new ArrayList<>();
    for (ConstraintInformation i : result.getConstraints()) {
      constraints.add(new GsonConstraintInformation(i));
    }
    this.instrumentTimeInMillis = result.getInstrumentTimeInMillis();
    this.preprocessTimeInMillis = result.getPreprocessTimeInMillis();
    this.activityTimeInMillis = result.getActivityTimeInMillis();
    this.covaTimeInMillis = result.getCovaTimeInMillis();
    this.possibleTargets = new ArrayList<>(result.getPossibleTargets());
  }

  public String getMainActivity() {
    return mainActivity;
  }

  public String getApkPath() {
    return apkPath;
  }

  public List<GsonConstraintInformation> getConstraints() {
    return constraints;
  }

  public long getInstrumentTimeInMillis() {
    return instrumentTimeInMillis;
  }

  public long getPreprocessTimeInMillis() {
    return preprocessTimeInMillis;
  }

  public long getCovaTimeInMillis() {
    return covaTimeInMillis;
  }

  public long getActivityTimeInMillis() {
    return activityTimeInMillis;
  }
}
