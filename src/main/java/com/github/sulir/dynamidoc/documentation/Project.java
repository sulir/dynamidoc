package com.github.sulir.dynamidoc.documentation;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.github.sulir.dynamidoc.tracing.TraceEvent;

public class Project {
	private final String path;
	private final Map<String, SourceFile> sourceFiles = new HashMap<>();
	
	public Project(String path) throws FileNotFoundException {
		if (path != null && Files.isDirectory(Paths.get(path)))
			this.path = path;
		else
			throw new FileNotFoundException("Invalid source path: " + path);
	}
	
	public String getPath() {
		return path;
	}
	
	public void addSourceFile(SourceFile file) {
		sourceFiles.put(file.getPath(), file);
	}

	public void addTraceEvents(Collection<TraceEvent> events) {
		for (TraceEvent event : events) {
			try {
				SourceFile file = sourceFiles.get(event.getFile());
				if (file == null) {
					file = new SourceFile(this, event.getFile());
					addSourceFile(file);
				}
				
				Method method = file.getMethodAt(event.getLine());
				if (method == null) {
					method = new Method(file, event.getLine());
					file.addMethod(method);
				}
				
				MethodExecution execution = new MethodExecution(method, event.getArguments());
				execution.setReturnValue(event.getReturnValue());
				execution.setException(event.getException());
				method.addExecution(execution);
			} catch (IOException e) {
				Logger.getGlobal().warning("Cannot read " + event.getFile());
			}
		}
	}
	
	public void writeJavadoc() {
		for (SourceFile file : sourceFiles.values()) {
			try {
				file.writeJavadoc();
			} catch (IOException e) {
				Logger.getGlobal().warning("Cannot write " + file.getPath());
			}
		}
	}
}
