package cova.reporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import soot.Unit;

import cova.data.CombinedResult;
import cova.data.ConstraintType;
import cova.data.ConstraintZ3;
import cova.data.MetaData;
import cova.data.CombinedResult.LeakConstraint;

public class ResultPrinter {
  private final CombinedResult combinedResults;

  public ResultPrinter(CombinedResult results) {
    combinedResults = results;
  }

  public void print(boolean timeout) {
    String separator = "\t";
    File csvFile = new File("cova_results.txt");
    if (timeout) {
      csvFile = new File("cova_results_timeout.txt");
    }
    if (!csvFile.exists()) {
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
        writer.write(String.join(separator, "#", "APK", "Dex Size(KB)", "#Reachable Methods",
            "FlowDroid-Time(s)", "Cova-Timeout", "Cova-Time(s)", "Z3-Time(s)", "#Z3-Queries",
            "%Failed Aliasing", "#Leaks", "Sink", "SinkLine", "SinkMethod", "SinkClass",
            "SinkConstraint", "Source", "SourceLine", "SourceMethod", "SourceClass",
            "SourceConstraint", "Constraint", "AST Size",
            "#U-Constraint", "#I-Constraint", "#C-Constraint", "#UI-Constraint", "#UC-Constraint",
            "#IC-Constraint", "#UIC-Constraint", "#Infeasible", "#None", "Appeared U", "Appeared I",
            "Apeared C"));
        writer.newLine();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
      MetaData meta = combinedResults.getApkMetaData();
      StringBuilder sb = new StringBuilder();
      boolean writeMeta = false;
      for (LeakConstraint leak : combinedResults) {
        ConstraintZ3 constraint = (ConstraintZ3) leak.getConstraint();
        if (!writeMeta) {
          sb.append(1);
          sb.append(separator);
          sb.append(meta.getApkName());
          sb.append(separator);
          sb.append(meta.getSize());
          sb.append(separator);
          sb.append(meta.getReachableMethods());
          sb.append(separator);
          sb.append(meta.getTimeForFlowDroid());
          sb.append(separator);
          sb.append(meta.getTimeout());
          sb.append(separator);
          sb.append(meta.getTime());
          sb.append(separator);
          sb.append(meta.getZ3Time());
          sb.append(separator);
          sb.append(meta.getZ3Queries());
          sb.append(separator);
          sb.append(meta.getFailedAliasing());
          sb.append(separator);
          sb.append(combinedResults.size());
          sb.append(separator);
          writeMeta = true;
        } else {
          for (int i = 0; i < 11; i++) {
            sb.append(separator);
          }
        }
        Unit sink = leak.getSink();
        Unit source = leak.getSource();
        sb.append(sink.toString());
        sb.append(separator);
        sb.append(sink.getJavaSourceStartLineNumber());
        sb.append(separator);
        if (leak.getSinkMethod() != null) {
          sb.append(leak.getSinkMethod().getSignature());
        }
        else {
          sb.append("null");
        }
        sb.append(separator);
        if (leak.getSinkClass() != null) {
          sb.append(leak.getSinkClass().getName());
        } else {
          sb.append("null");
        }
        sb.append(separator);
        sb.append(leak.constraintAtSink());
        sb.append(separator);
        sb.append(source.toString());
        sb.append(separator);
        sb.append(source.getJavaSourceStartLineNumber());
        sb.append(separator);
        if (leak.getSourceMethod() != null) {
          sb.append(leak.getSourceMethod().getSignature());
        } else {
          sb.append("null");
        }
        sb.append(separator);
        if (leak.getSourceClass() != null) {
          sb.append(leak.getSourceClass().getName());
        } else {
          sb.append("null");
        }
        sb.append(separator);
        sb.append(leak.constraintAtSource());
        sb.append(separator);
        sb.append(constraint.toString());
        sb.append(separator);
        sb.append(constraint.getSize());
        sb.append(separator);
        ConstraintType type = constraint.getConstraintType();
        if (type.equals(ConstraintType.U_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.I_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.C_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.UI_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.UC_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.IC_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.UIC_Constraint)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.INFEASIBLE)) {
          sb.append(1);
        }
        sb.append(separator);
        if (type.equals(ConstraintType.NONE)) {
          sb.append(1);
        }
        sb.append(separator);
        ArrayList<String> appearedU = new ArrayList<>();
        ArrayList<String> appearedI = new ArrayList<>();
        ArrayList<String> appearedC = new ArrayList<>();
        for (String symbol : constraint.getSymbolicNames()) {
          if (symbol.startsWith("U")) {
            if (constraint.toString().contains(symbol)) {
              appearedU.add(symbol);
            }
          }
          if (symbol.startsWith("I")) {
            if (constraint.toString().contains(symbol)) {
              appearedI.add(symbol);
            }
          }
          if (symbol.startsWith("C")) {
            if (constraint.toString().contains(symbol)) {
              appearedC.add(symbol);
            }
          }
        }
        for (int i = 0; i < appearedU.size(); i++) {
          sb.append(appearedU.get(i));
          if (i != appearedU.size() - 1) {
            sb.append(",");
          }
        }
        sb.append(separator);
        for (int i = 0; i < appearedI.size(); i++) {
          sb.append(appearedI.get(i));
          if (i != appearedI.size() - 1) {
            sb.append(",");
          }
        }
        sb.append(separator);
        for (int i = 0; i < appearedC.size(); i++) {
          sb.append(appearedC.get(i));
          if (i != appearedC.size() - 1) {
            sb.append(",");
          }
        }
        sb.append(separator);
        sb.append("\n");
      }
      writer.write(sb.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
