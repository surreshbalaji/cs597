package dk.brics.soot;
import soot.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LiveVariablesMain {
    public static void main(String[] args) 
    {
    	List<String> argsList = new ArrayList<String>(Arrays.asList(args));
 	   argsList.addAll(Arrays.asList(new String[]{
 			   "-main-class",
 			   "Integration",//main-class
 			   "testers.CallGraphs",//argument classes
 			   "testers.A"			//
 	   }));

	if(args.length == 0)
	{
	    System.out.println("Syntax: java "+
		"olhotak.liveness.LiveVariablesMain mainClass "+
		"[soot options]");
	    System.exit(0);
	}            

	PackManager.v().getPack("jtp").add(
	    new Transform("jtp.liveVariables",
                new LiveVariablesTagger() ) );

	soot.Main.main(args);
    }
}


