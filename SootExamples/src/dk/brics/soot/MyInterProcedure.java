

package dk.brics.soot;

import java.io.PrintStream;
import java.util.*;
import soot.*;
import soot.options.Options;
import soot.toolkits.graph.UnitGraph;

// Referenced classes of package soot.toolkits.scalar:
//            SimpleLiveLocalsAnalysis, FlowSet, LiveLocals

public class MyInterProcedure
    
{

    public MyInterProcedure(UnitGraph graph)
    {
        if(Options.v().time())
            Timers.v().liveTimer.start();
        if(Options.v().verbose())
            G.v().out.println((new StringBuilder()).append("[").append(graph.getBody().getMethod().getName()).append("]     Constructing SimpleLiveLocals...").toString());
        MyInterProcedureAnalysis analysis = new MyInterProcedureAnalysis(graph);
        /*if(Options.v().time())
            Timers.v().livePostTimer.start();
        unitToLocalsAfter = new HashMap(graph.size() * 2 + 1, 0.7F);
        unitToLocalsBefore = new HashMap(graph.size() * 2 + 1, 0.7F);
        Unit s;
        FlowSet set;
        for(Iterator unitIt = graph.iterator(); unitIt.hasNext(); unitToLocalsAfter.put(s, Collections.unmodifiableList(set.toList())))
        {
            s = (Unit)unitIt.next();
            set = (FlowSet)analysis.getFlowBefore(s);
            unitToLocalsBefore.put(s, Collections.unmodifiableList(set.toList()));
            set = (FlowSet)analysis.getFlowAfter(s);
        }

        if(Options.v().time())
            Timers.v().livePostTimer.end();
        if(Options.v().time())
            Timers.v().liveTimer.end();
*/    }

/*    public List getLiveLocalsAfter(Unit s)
    {
        return (List)unitToLocalsAfter.get(s);
    }

    public List getLiveLocalsBefore(Unit s)
    {
        return (List)unitToLocalsBefore.get(s);
    }

    Map unitToLocalsAfter;
    Map unitToLocalsBefore;
*/}


