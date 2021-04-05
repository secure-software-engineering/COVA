package cova.automatic.instrument;

import cova.automatic.data.Target;
import cova.automatic.data.TargetInformation;
import cova.automatic.executor.AutomaticRunner;
import cova.core.InterproceduralCFG;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AssignStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.StringConstant;
import soot.options.Options;

public class SootInstrumenter {

  public List<TargetStrings> instrument(
      Path apkFile, Path targetApk, Path jarPath, TargetInformation targetInformation)
      throws IOException {
    final List<TargetStrings> outputs = new ArrayList<>();
    Path resultingPath = Paths.get("sootOutput").resolve(apkFile.getFileName());
    Files.deleteIfExists(resultingPath);

    Options.v().set_keep_line_number(true);

    // prefer Android APK files// -src-prec apk
    Options.v().set_src_prec(Options.src_prec_apk);

    // output as APK, too//-f J
    Options.v().set_output_format(Options.output_format_dex);

    // resolve the PrintStream and System soot-classes
    Scene.v().addBasicClass("java.io.PrintStream", SootClass.SIGNATURES);
    Scene.v().addBasicClass("java.lang.System", SootClass.SIGNATURES);
    PackManager.v()
        .getPack("jtp")
        .add(
            new Transform(
                "jtp.myInstrumenter",
                new BodyTransformer() {

                  @Override
                  protected void internalTransform(
                      final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {

                    String signature = b.getMethod().getSignature();
                    if (InterproceduralCFG.isExcludedMethod(b.getMethod())) {
                      return;
                    }
                    if (b.getMethod().getDeclaringClass().toString().endsWith(".R")
                        || b.getMethod().getDeclaringClass().toString().contains(".R$")) {
                      return;
                    }

                    if (b.getMethod().getDeclaringClass().isPhantomClass()) {
                      return;
                    }
                    final PatchingChain<Unit> units = b.getUnits();

                    // important to use snapshotIterator here
                    int i = 0;
                    for (Iterator<Unit> iter = units.snapshotIterator(); iter.hasNext(); ) {
                      final Unit u = iter.next();
                      int lineNumber = u.getJavaSourceStartLineNumber();
                      if (!(u instanceof InvokeStmt
                          || u instanceof AssignStmt
                          || u instanceof IfStmt)) {
                        continue;
                      }

                      String tmpStr2 = AutomaticRunner.PRE_INFO_STRING + ":" + u.toString();
                      addPrint(units, b, u, tmpStr2);

                      if (u instanceof InvokeStmt || u instanceof AssignStmt) {

                        String tmpStr = AutomaticRunner.PRE_STRING + ":" + signature + ":" + i;

                        if (targetInformation != null) {
                          for (Target t : targetInformation.getTargets()) {
                            if (t.getMethod().equals(b.getMethod().getSignature())
                                && t.getUnit().equals(u.toString())) {
                              if (t.getLineNumber() == null
                                  || t.getLineNumber() == -1
                                  || t.getLineNumber() == lineNumber) {
                                t.getTargetStrings().put(tmpStr, null);
                              }
                            }
                          }
                        }

                        addPrint(units, b, u, tmpStr);

                        outputs.add(
                            new TargetStrings(
                                tmpStr,
                                tmpStr2,
                                lineNumber,
                                b.getMethod().getDeclaringClass().toString()));
                      }

                      // check that we did not mess up the Jimple
                      b.validate();
                      i++;
                    }
                  }
                }));
    String[] cmd = {
      "-w",
      "-android-jars",
      jarPath.toAbsolutePath().toString(),
      "-process-dir",
      apkFile.toAbsolutePath().toString(),
      "-allow-phantom-refs"
    };
    soot.Main.main(cmd);
    Files.move(resultingPath, targetApk);
    // System.exit(0);
    return outputs;
  }

  void addPrint(PatchingChain<Unit> units, Body b, Unit u, String tmpStr) {
    Local tmpRef = addTmpRef(b);
    Local tmpString = addTmpString(b);

    // insert "tmpRef = java.lang.System.out;"
    units.insertBefore(
        Jimple.v()
            .newAssignStmt(
                tmpRef,
                Jimple.v()
                    .newStaticFieldRef(
                        Scene.v()
                            .getField("<java.lang.System: java.io.PrintStream out>")
                            .makeRef())),
        u);

    // insert "tmpStr = 'STRING';"
    units.insertBefore(Jimple.v().newAssignStmt(tmpString, StringConstant.v(tmpStr)), u);

    // insert "tmpRef.println(tmpString);"
    SootMethod toCall =
        Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
    units.insertBefore(
        Jimple.v()
            .newInvokeStmt(Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)),
        u);
  }

  private Local addTmpRef(Body body) {
    Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
    body.getLocals().add(tmpRef);
    return tmpRef;
  }

  private Local addTmpString(Body body) {
    Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
    body.getLocals().add(tmpString);
    return tmpString;
  }
}
