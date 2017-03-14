package com.github.sulir.dynamidoc.documentation;

import java.util.logging.Logger;

import com.github.sulir.dynamidoc.tracing.Trace;

public class DynamiDoc {
	public static void main(String[] args) {
		if (args.length == 0) {
			System.err.println("The arguments must contain source paths to be rewritten.");
			return;
		}
		
		Project project = new Project(args);
		project.addTraceEvents(Trace.loadEvents());
		project.writeJavadoc();
		Logger.getGlobal().info("Javadoc comments were written.");
	}
}
