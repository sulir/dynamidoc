package com.github.sulir.dynamidoc.documentation;

import java.io.FileNotFoundException;
import java.util.logging.Logger;

import com.github.sulir.dynamidoc.tracing.Trace;

public class DynamiDoc {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("The first argument must be a source path to be rewritten.");
			return;
		}
		
		try {
			String sourcePath = args[0];
			Project project = new Project(sourcePath);
			project.addTraceEvents(Trace.loadEvents());
			project.writeJavadoc();
			Logger.getGlobal().info("Javadoc comments were written.");
		} catch (FileNotFoundException e) {
			Logger.getGlobal().severe(e.toString());
		}
	}
}
