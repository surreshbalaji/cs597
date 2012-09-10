package dk.brics.soot;

import java.io.IOException;
import java.io.PrintStream;
import java.util.*;
import java.util.logging.Logger;

import soot.*;
import soot.jimple.Stmt;
import soot.options.Options;
import soot.tagkit.StringTag;
import soot.toolkits.graph.UnitGraph;
import soot.toolkits.scalar.FlowSet;

// Referenced classes of package soot.toolkits.scalar:
//            SimpleLiveLocalsAnalysis, FlowSet, LiveLocals

public class MyInterProcedure

{

	public MyInterProcedure(UnitGraph graph) {
		LoggerWrapper logWrap=LoggerWrapper.getInstance();
		Logger logger=logWrap.getLogger();
		
		logger.fine("Starting interprocedure analysis");
		MyInterProcedureAnalysis analysis = new MyInterProcedureAnalysis(graph);
		Iterator sIt = graph.getBody().getUnits().iterator();
		while (sIt.hasNext()) {

			Stmt s = (Stmt) sIt.next();
			System.out.println(s);
			FlowSet ds = (FlowSet) analysis.getFlowAfter(s);
			System.out.println(ds.toString());
			System.out.println("---------------------------");
			// Add StringTags listing live variables
		}
		/*
		 * if(Options.v().time()) Timers.v().livePostTimer.start();
		 * unitToLocalsAfter = new HashMap(graph.size() * 2 + 1, 0.7F);
		 * unitToLocalsBefore = new HashMap(graph.size() * 2 + 1, 0.7F); Unit s;
		 * FlowSet set; for(Iterator unitIt = graph.iterator();
		 * unitIt.hasNext(); unitToLocalsAfter.put(s,
		 * Collections.unmodifiableList(set.toList()))) { s =
		 * (Unit)unitIt.next(); set = (FlowSet)analysis.getFlowBefore(s);
		 * unitToLocalsBefore.put(s,
		 * Collections.unmodifiableList(set.toList())); set =
		 * (FlowSet)analysis.getFlowAfter(s); }
		 * 
		 * if(Options.v().time()) Timers.v().livePostTimer.end();
		 * if(Options.v().time()) Timers.v().liveTimer.end();
		 */}

	/*
	 * public List getLiveLocalsAfter(Unit s) { return
	 * (List)unitToLocalsAfter.get(s); }
	 * 
	 * public List getLiveLocalsBefore(Unit s) { return
	 * (List)unitToLocalsBefore.get(s); }
	 * 
	 * Map unitToLocalsAfter; Map unitToLocalsBefore;
	 */
}
