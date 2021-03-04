package cova.automatic.data;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
  private List<String> logs;

  private String selectedOutput;
  private boolean reachedDestination;
  private List<TestResultActivity> path;

  public TestResult() {
    logs = new ArrayList<>();
    path = new ArrayList<>();
  }

  public List<String> getLogs() {
    return logs;
  }

  public void setLogs(List<String> logs) {
    this.logs = logs;
  }

  public boolean isReachedDestination() {
    return reachedDestination;
  }

  public void setReachedDestination(boolean reachedDestination) {
    this.reachedDestination = reachedDestination;
  }

  public List<TestResultActivity> getPath() {
    return path;
  }

  public void setPath(List<TestResultActivity> path) {
    this.path = path;
  }

  public String getSelectedOutput() {
    return selectedOutput;
  }

  public void setSelectedOutput(String selectedOutput) {
    this.selectedOutput = selectedOutput;
  }
}
