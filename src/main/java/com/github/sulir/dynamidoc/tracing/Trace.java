package com.github.sulir.dynamidoc.tracing;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.github.sulir.dynamidoc.documentation.Project;

public class Trace {
	private List<TraceEvent> events = new ArrayList<>();
	
	public Trace() {
		Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
	}
	
	public void record(TraceEvent event) {
		events.add(event);
	}
	
	public List<TraceEvent> getEvents() {
		return events;
	}
	
	private void onShutdown() {
		try {
			Project project = new Project();
			project.addTraceEvents(events);
			project.writeJavadoc();
		} catch (Exception e) {
			Logger.getGlobal().severe(e.toString());
		}
	}
}
