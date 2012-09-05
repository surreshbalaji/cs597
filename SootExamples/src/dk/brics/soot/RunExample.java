package dk.brics.soot;

import soot.*;
import soot.jimple.AssignStmt;
import soot.jimple.InvokeStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.internal.JVirtualInvokeExpr;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.*;
import java.util.*;

public class RunExample {
	public static void main(String[] args) {
		//argslist to setup the analysis environment
		args = new String[] { "testers.Integration", "testers.A", "testers.B" };
		//validation check for empty arguments to the program
		if (args.length == 0) {
			System.out.println("Usage: java RunExample class_to_analyse");
			System.exit(0);
		}
		//extracting and loading the class to be analysed from the argument list
		for (String arg : args) {

			SootClass sClass = Scene.v().loadClassAndSupport(arg);
			sClass.setApplicationClass();

		}
		//setting the first argument of argument list as the main class
		SootClass sClass = Scene.v().loadClassAndSupport(args[0]);
		Scene.v().setMainClass(sClass);
		//getting a list of methods in the main class
		Iterator methodIt = sClass.getMethods().iterator();
		//iterating through the statements
		while (methodIt.hasNext()) {
			SootMethod m = (SootMethod) methodIt.next();
			Body b = m.retrieveActiveBody();

			System.out.println("=======================================");
			System.out.println(m.getName());
			UnitGraph graph = new ExceptionalUnitGraph(b);
			MyInterProcedure mip=new MyInterProcedure(graph);
		}
	}

}
 