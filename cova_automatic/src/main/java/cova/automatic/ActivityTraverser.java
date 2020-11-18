package cova.automatic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cova.automatic.results.ConstraintInformation;
import soot.SootClass;
import soot.Unit;
import soot.Value;
import soot.jimple.ClassConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;

public class ActivityTraverser {

	private Map<String, List<ConstraintInformation>> activityConstraints;
	private ConstraintInformation choosenInfo;
	private String mainActivity;
	private List<List<ConstraintInformation>> paths;
	private List<ConstraintInformation> allInformation;
	private boolean traversed;

	public ActivityTraverser(List<ConstraintInformation> information, ConstraintInformation choosenInfo,
			String mainActivity) {
		paths = new ArrayList<>();
		activityConstraints = new HashMap<>();
		this.choosenInfo = choosenInfo;
		this.mainActivity = mainActivity;
		this.allInformation = information;
		traversed = false;
	}

	public void traverse() {
		if (!traversed) {
			parseIntents();
			traverse(new ArrayList<ConstraintInformation>(), choosenInfo);
		}
	}

	private void parseIntents() {
		for (ConstraintInformation info : allInformation) {
			Unit u = info.getUnit();
			if (u instanceof InvokeStmt) {
				InvokeStmt stmt = (InvokeStmt) u;
				String name = stmt.getInvokeExpr().getMethod().getName();
				InvokeExpr inv = stmt.getInvokeExpr();
				if (inv != null && inv.getArgCount() > 0) {
					Value v = inv.getArg(0);
					// TODO more precise detection
					if (name.equals("startActivity")) {
						Unit tmpUnit = u;
						// Look for initialisation of intent for this startActivity
						while (tmpUnit != null) {
							tmpUnit = info.getMethod().getActiveBody().getUnits().getPredOf(tmpUnit);
							if (!(tmpUnit instanceof InvokeStmt)) {
								continue;
							}
							InvokeStmt constStmt = (InvokeStmt) tmpUnit;
							InvokeExpr constExpr = constStmt.getInvokeExpr();
							// Only consider constructors
							if (!(constExpr instanceof SpecialInvokeExpr)) {
								continue;
							}
							SpecialInvokeExpr specialConstExpr = (SpecialInvokeExpr) constExpr;
							// Look only for the correct intent
							if (!specialConstExpr.getBase().equals(v)) {
								continue;
							}
							ClassConstant clValue = (ClassConstant) specialConstExpr.getArg(1);
							String cl = clValue.getValue();
							cl = cl.substring(1, cl.length() - 1).replace("/", ".");
							if (!activityConstraints.containsKey(cl)) {
								activityConstraints.put(cl, new ArrayList<>());
							}
							activityConstraints.get(cl).add(info);

						}
					}

				}
			}
		}
	}

	private void traverse(List<ConstraintInformation> path, ConstraintInformation info) {
		path = new ArrayList<>(path);
		path.add(info);

		SootClass infoClass = info.getClazz();
		if (infoClass.isInnerClass()) {
			infoClass = infoClass.getOuterClass();
		}
		String activityName = infoClass.getName();

		if (activityName.equals(mainActivity)) {
			Collections.reverse(path);
			paths.add(path);
			return;
		}
		if (!activityConstraints.containsKey(activityName)) {
			return;
		}
		for (ConstraintInformation candidate : activityConstraints.get(activityName)) {
			traverse(path, candidate);
		}
	}

	public List<List<ConstraintInformation>> getPaths() {
		return paths;
	}
}
