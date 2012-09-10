package dk.brics.soot;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LoggerWrapper {

	public static final Logger myLogger = Logger.getLogger(RunExample.class
			.getName());

	private static LoggerWrapper instance = null;

	public static LoggerWrapper getInstance(){
		if (instance == null) {
			prepareLogger();
			instance = new LoggerWrapper();
		}
		return instance;
	}

	public Logger getLogger() {
		return instance.myLogger;
	}

	private static void prepareLogger() {
		try {
			FileHandler myFileHandler = new FileHandler("AnalysisLog.txt");
			myFileHandler.setFormatter(new SimpleFormatter());
			myLogger.addHandler(myFileHandler);
			myLogger.setUseParentHandlers(false);
			myLogger.setLevel(Level.FINE);
			
		}

		catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}