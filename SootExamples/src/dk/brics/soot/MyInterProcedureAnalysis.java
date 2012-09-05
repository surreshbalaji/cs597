package dk.brics.soot;

import java.beans.Statement;
import java.util.*;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.options.Options;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;

// Referenced classes of package soot.toolkits.scalar:
//            BackwardFlowAnalysis, ArraySparseSet, FlowSet

class MyInterProcedureAnalysis extends ForwardFlowAnalysis {

	FlowSet emptySet = new ArraySparseSet();
	// LiveVariablesAnalysis lva;
	/*
	 * a map of variables tainted using df propagation by variables in
	 * 
	 * @variablesToTrack
	 */
	private HashMap<SootField, HashSet<Value>> dfTaintMap = new HashMap<SootField, HashSet<Value>>();

	/*
	 * a map of variables tainted using cf propagation by variables in
	 * 
	 * @variablesToTrack
	 */
	/* The set of the statements invoking methods */

	@SuppressWarnings("unchecked")
	MyInterProcedureAnalysis(UnitGraph g) {
		super(g);
		System.out.println("Method name: " + g.getBody().getMethod().getName());
		/*
		 * System.out.println("running livevariable analysis for the method");
		 * lva = new LiveVariablesAnalysis(new
		 * ExceptionalUnitGraph(g.getBody()));
		 */doAnalysis(g);

	}

	public void doAnalysis(UnitGraph g) {
		Iterator unitIt = graph.iterator();
		while (unitIt.hasNext()) {
			Unit s = (Unit) unitIt.next();
			if (!ignoreStatement(s)) {
				if (containsMethodInvocation(s)) { // If s contains an
													// invocation to
													// a method
					SootMethod m = getInvokedMethodFromUnit(s); // Get the
																// invoked
																// method
					MyInterProcedureAnalysis newAanalysis = new MyInterProcedureAnalysis(
							new UnitGraph(m.retrieveActiveBody()) {
							});
				}

				analyseUnit(s);
			}
		}
	}

	/*
	 * Add conditions for ignoring statements from consideration for analysis
	 */
	public boolean ignoreStatement(Unit s) {
		Stmt st = (Stmt) s;
		// ignore instance creation init methods
		if (st instanceof InvokeStmt) {
			InvokeStmt invokeStmt = (InvokeStmt) st;
			if (invokeStmt.getInvokeExpr() instanceof SpecialInvokeExpr) {

				return true;
			} else
				return false;
		} else
			return false;

	}

	/*
	 * check whether the given statement is a method invocation
	 */

	public boolean containsMethodInvocation(Unit s) {
		Stmt st = (Stmt) s;
		if (st instanceof AssignStmt) {
			AssignStmt ast = (AssignStmt) st;

			for (ValueBox vb : ast.getUseBoxes()) {
				if (vb.getValue() instanceof JVirtualInvokeExpr) {
					return true;

				}

			}

		}
		if (st.containsInvokeExpr()) {
			if (st instanceof InvokeStmt) {
				// invoke method

				InvokeStmt invokeStmt = (InvokeStmt) st;
				if (invokeStmt.getInvokeExpr() instanceof JVirtualInvokeExpr) {
					return true;
				}
			}
		}

		return false;
	}

	/* analyse each and every statement inside a method */
	public void analyseUnit(Unit st) {
		// checking type of statement
		Stmt s = (Stmt) st;
		// consider only the assignment statement
		if (st instanceof AssignStmt) {
			AssignStmt ast = (AssignStmt) st;
			// checking whether the assignment statement contains fieldref
			if (((AssignStmt) st).containsFieldRef()) {
				// get live variables here
				FieldRef fr = (FieldRef) ast.getFieldRef();
				System.out.println(st);
				System.out.println("Field referenced" + fr.getField());
				List<ValueBox> vdb = st.getDefBoxes();
				for (ValueBox vb : vdb) {
					if (vb.getValue().equals(fr)) {
						System.out.println("field write");
					}
				}
				/*
				 * System.out.println("------------------------------------------"
				 * ); System.out.println("List of uses"); for (ValueBox vb :
				 * ast.getUseBoxes()) { System.out.println(vb.getValue()); }
				 */
				/*
				 * FlowSet liveVariables = (FlowSet) lva.getFlowAfter(s);
				 * Iterator variableIt = liveVariables.iterator(); while
				 * (variableIt.hasNext()) {
				 * 
				 * final Value variable = (Value) variableIt.next();
				 * System.out.println("Live variables:" + variable);
				 */}
		}
	}

	public SootMethod getInvokedMethodFromUnit(Unit s) {
		Stmt st = (Stmt) s;
		SootMethod sm = null;
		if (st instanceof AssignStmt) {
			AssignStmt ast = (AssignStmt) st;

			for (ValueBox vb : ast.getUseBoxes()) {
				if (vb.getValue() instanceof JVirtualInvokeExpr) {
					JVirtualInvokeExpr expr = (JVirtualInvokeExpr) vb
							.getValue();
					sm = (SootMethod) expr.getMethod();

				}

			}

		}
		if (st.containsInvokeExpr()) {
			if (st instanceof InvokeStmt) {
				// invoke method

				InvokeStmt invokeStmt = (InvokeStmt) st;
				if (invokeStmt.getInvokeExpr() instanceof JVirtualInvokeExpr) {
					JVirtualInvokeExpr expr = (JVirtualInvokeExpr) invokeStmt
							.getInvokeExpr();
					sm = (SootMethod) expr.getMethod();

				}
			}
		}

		return sm;
	}

	/**
	 * All INs are initialized to the empty set.
	 **/
	protected Object newInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * IN(Start) is the empty set
	 **/
	protected Object entryInitialFlow() {
		return emptySet.clone();
	}

	/**
	 * OUT is the same as IN plus the genSet.
	 **/
	protected void flowThrough(Object inValue, Object unit, Object outValue) {
		FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

		// perform generation (kill set is empty)
		// in.union(unitToGenerateSet.get(unit), out);
	}

	/**
	 * All paths == Intersection.
	 **/
	protected void merge(Object in1, Object in2, Object out) {
		FlowSet inSet1 = (FlowSet) in1, inSet2 = (FlowSet) in2, outSet = (FlowSet) out;

		inSet1.intersection(inSet2, outSet);
	}

	protected void copy(Object source, Object dest) {
		FlowSet sourceSet = (FlowSet) source, destSet = (FlowSet) dest;

		sourceSet.copy(destSet);
	}
}
