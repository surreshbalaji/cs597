package dk.brics.soot;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import soot.Body;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AssignStmt;
import soot.jimple.FieldRef;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.ForwardFlowAnalysis;
import soot.toolkits.scalar.Pair;

class MyInterProcedureAnalysis extends ForwardFlowAnalysis {
	/*private final static Logger logger = Logger
			.getLogger(MyInterProcedureAnalysis.class.getName());
*/
	FlowSet emptySet = new DataFlowSet();
	DataFlowSet methodFlowSet = new DataFlowSet();
	Value methodReturnValue=null;
	// HashSet<Value> fieldRefVar = new HashSet<Value>();
	LoggerWrapper logWrap=LoggerWrapper.getInstance();
	Logger logger=logWrap.getLogger();
	
	HashMap<Integer, HashSet<SootField>> paramTrackList = new HashMap<Integer, HashSet<SootField>>();

	@SuppressWarnings("unchecked")
	MyInterProcedureAnalysis(UnitGraph g) {

		super(g);
		logger.fine("Method name: " + g.getBody().getMethod().getName());
		/*
		 * System.out.println("running livevariable analysis for the method");
		 * lva = new LiveVariablesAnalysis(new
		 * ExceptionalUnitGraph(g.getBody()));
		 */
		// doAnalysis(g);
		doAnalysis();
	}

	MyInterProcedureAnalysis(UnitGraph g,
			HashMap<Integer, HashSet<SootField>> params) {
		super(g);
		this.paramTrackList = params;
		doAnalysis();

	}

	/*
	 * public void doAnalysis(UnitGraph g) { Iterator unitIt = graph.iterator();
	 * while (unitIt.hasNext()) { Unit s = (Unit) unitIt.next(); if
	 * (!ignoreStatement(s)) { if (containsMethodInvocation(s)) { // If s
	 * contains an // invocation to // a method SootMethod m =
	 * getInvokedMethodFromUnit(s); // Get the // invoked // method
	 * MyInterProcedureAnalysis newAanalysis = new MyInterProcedureAnalysis( new
	 * UnitGraph(m.getActiveBody()) { }); }
	 * 
	 * analyseUnit(s); } } }
	 */
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
				logger.fine(st.toString());
				logger.fine("Field referenced" + fr.getField());
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

	public SootMethod getInvokedMethodFromUnit(Unit s, FlowSet currentFlowSet) {
		Stmt st = (Stmt) s;
		DataFlowSet current = (DataFlowSet) currentFlowSet;
		SootMethod sm = null;
		paramTrackList.clear();
		if (st instanceof AssignStmt) {
			AssignStmt ast = (AssignStmt) st;

			for (ValueBox vb : ast.getUseBoxes()) {
				if (vb.getValue() instanceof JVirtualInvokeExpr) {
					JVirtualInvokeExpr expr = (JVirtualInvokeExpr) vb
							.getValue();
					sm = (SootMethod) expr.getMethod();
					for (int i = 0; i < expr.getArgCount(); i++) {
						HashSet<SootField> sf = current.updateTaintList(expr
								.getArg(i));
						if (sf.size() > 0) {
							paramTrackList.put(i, sf);
						}
					}

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
					for (int i = 0; i < expr.getArgCount(); i++) {
						HashSet<SootField> sf = current.updateTaintList(expr
								.getArg(i));
						if (sf.size() > 0) {
							paramTrackList.put(i, sf);
						}
					}
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
	 * OUT is the same as IN plus the TAINTLIST AT THIS STATEMENT
	 **/
	protected void flowThrough(Object inValue, Object unit, Object outValue) {
		DataFlowSet in = (DataFlowSet) inValue;
		DataFlowSet out = (DataFlowSet) outValue;
		Value calleeReturnValue=null;
		HashSet<Value> taintVar = new HashSet<Value>();
		HashSet<Value> varUses=new HashSet<Value>();
		Pair<SootField, HashSet<Value>> taintMap = new Pair<SootField, HashSet<Value>>();
		Stmt st = (Stmt) unit;
		logger.fine("Current Statement " + st);
		logger.fine("Statement type" + st.getClass());
		// Copy out to in
		
		in.copy(out);

		if (st instanceof IdentityStmt) {
			IdentityStmt is = (IdentityStmt) st;
			if (is.getRightOp() instanceof ParameterRef) {
				ParameterRef pr = (ParameterRef) is.getRightOp();
				logger.fine("Parameter reference" + is.getLeftOp());
				if (paramTrackList.containsKey(pr.getIndex()))
					;
				{

					Iterator boxIt = is.getDefBoxes().iterator();
					while (boxIt.hasNext()) {
						final ValueBox box = (ValueBox) boxIt.next();
						Value value = box.getValue();

						// if (value instanceof Local)
						taintVar.add(value);

						// remove taintlist for the Sootfield
						// because redefinition happened
						// out.removeValue(value);

					}
					logger.fine("Contains field params");
					if (!paramTrackList.isEmpty()) {
						if (paramTrackList.containsKey(pr.getIndex())) {
							HashSet<SootField> sfSet = paramTrackList.get(pr
									.getIndex());
							for (SootField sf : sfSet) {

								taintMap.setPair(sf, taintVar);
								// fieldRefVar.addAll(taintVar);
								out.add(taintMap);
							}
						}
					}
				}

			}
		}
		// invocation of method and do analysis on the method
		if (containsMethodInvocation(st)) {
			logger.fine("Method invocation");
			SootMethod m = getInvokedMethodFromUnit(st, out);
			Body b = m.retrieveActiveBody();
			UnitGraph graph = new ExceptionalUnitGraph(b);
			logger.fine("caller out flowset :" + out);
			MyInterProcedureAnalysis newAnalysis = new MyInterProcedureAnalysis(
					graph, this.paramTrackList);
			// newAnalysis.setParamTrackList(this.paramTrackList);
			logger.fine("method flowset :" + newAnalysis.getMethodFlowSet());
			logger.fine("out flowset :" + out);
			out.union(newAnalysis.getMethodFlowSet(), out);
			logger.fine("After union : " + out.toString());
			calleeReturnValue=newAnalysis.getMethodReturnValue();
			
		}
		// consider only the assignment statement

		if (st instanceof AssignStmt) {
			logger.fine("assign check");
			AssignStmt ast = (AssignStmt) st;
			// get the defboxes
			Iterator boxIt = st.getDefBoxes().iterator();
			while (boxIt.hasNext()) {
				final ValueBox box = (ValueBox) boxIt.next();
				Value value = box.getValue();

				// if (value instanceof Local)
				taintVar.add(value);

				// remove taintlist for the Sootfield
				// because redefinition happened
				// out.removeValue(value);

			}
			
			boxIt = st.getUseBoxes().iterator();
			while (boxIt.hasNext()) {
				final ValueBox box = (ValueBox) boxIt.next();
				varUses.add(box.getValue());
			}
			
			//add the return value to the use list if this 
			//statement is a method invocation
			if(containsMethodInvocation(st) && calleeReturnValue!=null)
			{
				logger.info("callee return value" + calleeReturnValue);
				varUses.add(calleeReturnValue);
			}
			for(Value value:varUses)
			{
				// field ref
				if (ast.containsFieldRef()) {
					// check for fieldref
					FieldRef fr = (FieldRef) ast.getFieldRef();

					if (value.equals(fr)) {
						// System.out.println(st);
						logger.fine("Field referenced" + fr.getField());
						taintMap.setPair(fr.getField(), taintVar);
						// fieldRefVar.addAll(taintVar);
						out.add(taintMap);

					}
				}
			
				
				// fields referenced via temp var
				logger.fine("here we are" +value.toString());
				if (out.contains(value)) {

					logger.fine("indirect reference");
					HashSet<SootField> sf = out.updateTaintList(value);
					for (SootField s : sf) {
						HashSet<Value> currentList = out.getVars(s);
						// remove this variable
						/*
						 * if(fieldRefVar.contains(value)) {
						 * System.out.println("removed");
						 * currentList.remove(value); }
						 */
						// add the new tainted variable
						currentList.addAll(taintVar);
						taintMap.setPair(s, currentList);
						out.add(taintMap);
					}
				}

			}

		}
		if (st instanceof ReturnVoidStmt) {
			logger.fine("return here");
			out.copy(methodFlowSet);
			logger.fine(out.toString());
		}
		
		if(st instanceof ReturnStmt)
		{
			logger.fine("return value");
			out.copy(methodFlowSet);
			methodReturnValue=((ReturnStmt) st).getOp();
			
		}
    logger.fine(out.toString());
	}

	/**
	 * All paths == Union.
	 **/
	protected void merge(Object in1, Object in2, Object out) {
		DataFlowSet inSet1 = (DataFlowSet) in1, inSet2 = (DataFlowSet) in2, outSet = (DataFlowSet) out;

		inSet1.union(inSet2, outSet);
	}

	protected void copy(Object source, Object dest) {
		DataFlowSet sourceSet = (DataFlowSet) source, destSet = (DataFlowSet) dest;

		sourceSet.copy(destSet);
	}

	public DataFlowSet getMethodFlowSet() {
		return methodFlowSet;
	}

	public void setParamTrackList(HashMap<Integer, HashSet<SootField>> params) {
		this.paramTrackList = params;
	}
	
	public Value getMethodReturnValue()
	{
		return methodReturnValue;
	}
}
