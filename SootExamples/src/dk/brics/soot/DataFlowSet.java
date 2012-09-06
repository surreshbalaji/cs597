package dk.brics.soot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import soot.SootField;
import soot.Value;
import soot.jimple.Stmt;
import soot.toolkits.scalar.AbstractFlowSet;
import soot.toolkits.scalar.ArraySparseSet;
import soot.toolkits.scalar.FlowSet;
import soot.toolkits.scalar.Pair;

/*To be implemented instead of using HashMap<Value, Constraints> in MyDataFlowAnalysis*/

public class DataFlowSet extends AbstractFlowSet {

	// map of the elements
	/*
	 * An if (or switch) statement -> set of variables controlling the condition
	 * in the statement
	 */
	private HashMap<SootField, HashSet<Value>> cntrlTaintFlow;

	/* the innermost condition */

	public DataFlowSet() {
		cntrlTaintFlow = new HashMap<SootField, HashSet<Value>>(1);
	}

	public DataFlowSet(HashMap<SootField, HashSet<Value>> map) {
		this.cntrlTaintFlow = map;
	}

	@Override
	public void clear() {
		cntrlTaintFlow = new HashMap<SootField, HashSet<Value>>(1);
	}

	@Override
	public void add(Object obj) {
		// TODO Auto-generated method stub
		Pair<SootField, HashSet<Value>> pair = (Pair<SootField, HashSet<Value>>) obj;
		if (cntrlTaintFlow.containsKey(pair.getO1())) {
			HashSet<Value> vars = cntrlTaintFlow.get(pair.getO1());
			vars.addAll(pair.getO2());

		} else {
			cntrlTaintFlow.put(pair.getO1(), pair.getO2());
		}
	}

	@Override
	public AbstractFlowSet clone() {
		// TODO Auto-generated method stub
		/* Not cloning the keys for now..i think its not needed */
		HashMap<SootField, HashSet<Value>> clonedMap = new HashMap<SootField, HashSet<Value>>();

		for (SootField stmt : cntrlTaintFlow.keySet()) {
			HashSet<Value> vars = cntrlTaintFlow.get(stmt);
			/* implement clone */
			clonedMap.put(stmt, new HashSet<Value>(vars));
		}
		DataFlowSet clonedSet = new DataFlowSet(clonedMap);
		return clonedSet;
	}

	@Override
	public boolean contains(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Value) {
			for (SootField s : cntrlTaintFlow.keySet()) {
				return cntrlTaintFlow.get(s).contains(obj);
			}

		} else if (obj instanceof Pair) {
			HashSet<Value> v1 = (HashSet<Value>) cntrlTaintFlow
					.get(((Pair) obj).getO1());
			HashSet<Value> v2 = (HashSet<Value>) ((Pair) obj).getO2();
			if (v1 == null && v2 != null)
				return false;
			return v1.equals(v2);
		}
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return cntrlTaintFlow.isEmpty();
	}

	@Override
	public void remove(Object obj) {
		// TODO Auto-generated method stub
		if (obj instanceof Pair) {
			cntrlTaintFlow.remove(((Pair) obj).getO1());

		} else
			try {
				throw new Exception("Not handled type : " + obj.getClass());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return cntrlTaintFlow.size();
	}

	@Override
	public List toList() {
		// TODO Auto-generated method stub
		List listOfPairs = new ArrayList(cntrlTaintFlow.size());
		for (SootField s : cntrlTaintFlow.keySet()) {
			Pair<SootField, HashSet<Value>> pair = new Pair<SootField, HashSet<Value>>(
					s, cntrlTaintFlow.get(s));
			listOfPairs.add(pair);
		}
		return listOfPairs;
	}

	/*
	 * public DataFlowSet mergeConstraints (ControlFlowSet flow2) {
	 * ControlFlowSet mergedFlow = new ControlFlowSet(); Iterator<Pair<Stmt,
	 * HashSet<Value>>> it1 = this.toList().iterator();
	 * 
	 * if(this.getLastStatementInStack() != null){
	 * if(!this.getLastStatementInStack
	 * ().equals(flow2.getLastStatementInStack())){ try { throw new
	 * Exception("Innermost not equal!!!"); } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } } } while
	 * (it1.hasNext()) { Pair<Stmt, HashSet<Value>> pair = it1.next();
	 * HashSet<Value> cInFlow2 = flow2.getCntrlTaintFlow().get(pair.getO1());
	 * if(cInFlow2 == null) mergedFlow.add(pair); else{ HashSet<Value>
	 * mergedValues = new HashSet<Value>(1); mergedValues.addAll(pair.getO2());
	 * mergedValues.addAll(cInFlow2); mergedFlow.add(new Pair<Stmt,
	 * HashSet<Value>>(pair.getO1(), mergedValues)); } } /*At constraints in
	 * flow1 but not in flow2
	 */
	/*
	 * Iterator<Pair<Value, Constraints>> it2 = flow2.toList().iterator();
	 * 
	 * while (it2.hasNext()) { Pair<Value, Constraints> pair = it2.next();
	 * HashSet<Value> cAtFlow1 = this.cntrlTaintFlow.get(pair.getO1());;
	 * if(cAtFlow1 == null) mergedFlow.add(pair); } return mergedFlow; }
	 */

	@Override
	public String toString() {
		String s = "";
		for (Object obj : this.toList()) {
			Pair<SootField, HashSet<Value>> pair = (Pair<SootField, HashSet<Value>>) obj;
			String variables = pair.getO2().toString();
			String fields = pair.getO1().toString();
			s = s + "{" + fields + "->" + variables + "}" + ",";
		}
		return s;
	}

	public HashSet<Value> getAllVars() {
		HashSet<Value> vars = new HashSet<Value>(1);
		for (Object obj : this.toList()) {
			Pair<Stmt, HashSet<Value>> pair = (Pair<Stmt, HashSet<Value>>) obj;
			vars.addAll(pair.getO2());
		}
		return vars;
	}

	public void setCntrlTaintFlow(
			HashMap<SootField, HashSet<Value>> cntrlTaintFlow) {
		this.cntrlTaintFlow = cntrlTaintFlow;
	}

	public void union(FlowSet flowset, FlowSet dest)
	{
		DataFlowSet result=new DataFlowSet();
		DataFlowSet in1=(DataFlowSet) flowset;
		if(cntrlTaintFlow.isEmpty() && in1.cntrlTaintFlow.isEmpty())
		{
		  result.copy(dest);
		}
		else if(cntrlTaintFlow.isEmpty())
		{
			in1.copy(dest);
		}
		else if(in1.cntrlTaintFlow.isEmpty())
		{
			this.copy(dest);
		}
		else
		{
			//both flowsets has somethings to do a union operation
			this.copy(result);
			for(SootField s: in1.cntrlTaintFlow.keySet())
			{
				if(result.cntrlTaintFlow.containsKey(s))
				{
				HashSet<Value> in1varset=in1.cntrlTaintFlow.get(s);
				HashSet<Value> resvarset=result.cntrlTaintFlow.get(s);
				resvarset.addAll(in1varset);
				result.cntrlTaintFlow.put(s,resvarset);
			    }
				else
				{
					HashSet<Value> in1varset=in1.cntrlTaintFlow.get(s);
					if(!in1varset.isEmpty())
					{
	
						result.cntrlTaintFlow.put(s,in1varset);
					}
				}	
					
			}
		}
		dest.copy(result);
			
	}

	public void copy(FlowSet dest) {
		DataFlowSet result = (DataFlowSet) dest;
		if (!cntrlTaintFlow.isEmpty()) {
			for (SootField s : cntrlTaintFlow.keySet()) {
				HashSet<Value> varset = new HashSet<Value>();
				varset.addAll(cntrlTaintFlow.get(s));
				result.cntrlTaintFlow.put(s, varset);
				
			}
		} else
			dest = new DataFlowSet();
	}

	public HashMap<SootField, HashSet<Value>> getCntrlTaintFlow() {
		return cntrlTaintFlow;
	}

}
