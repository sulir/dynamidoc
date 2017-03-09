package com.github.sulir.dynamidoc.tracing;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Trace {
	private static final String PATH = System.getProperty("user.home") + "/trace.dat";
	private ObjectOutputStream output;
	
	public static List<TraceEvent> loadEvents() {
		List<TraceEvent> events = new ArrayList<>();
		
		try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(PATH))) {
			while (true) {
				try {
					events.add((TraceEvent) input.readObject());
				} catch (EOFException e) {
					break;
				}
			}
		} catch (IOException | ClassNotFoundException e) {
			Logger.getGlobal().severe(e.toString());
		}
		
		return events;
	}
	
	public Trace() {
		try {
			output = new ObjectOutputStream(new FileOutputStream(PATH));
			Runtime.getRuntime().addShutdownHook(new Thread(this::onShutdown));
		} catch (IOException e) {
			Logger.getGlobal().severe(e.toString());
		}
	}
	
	public void record(TraceEvent event) {
		try {
			if (output != null)
				output.writeObject(event);
		} catch (IOException e) {
			Logger.getGlobal().warning(e.toString());
		}
	}
	
	private void onShutdown() {
		try {
			if (output != null)
				output.close();
		} catch (IOException e) { }
	}
}
