package com.github.sulir.dynamidoc.tracing;

import java.util.Arrays;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

public class TraceEvent {
	private final String file;
	private final int line;
	private final String[] arguments;
	private String returnValue;
	private String exception;
	
	public TraceEvent(SourceLocation location, Signature signature, Object[] arguments) {
		String packageName = signature.getDeclaringType().getPackage().getName();
		this.file = packageName.replace('.', '/') + '/' + location.getFileName();
		this.line = location.getLine();
		this.arguments = Arrays.stream(arguments).map(this::objectToString).toArray(String[]::new);
	}

	public String getFile() {
		return file;
	}

	public int getLine() {
		return line;
	}

	public String[] getArguments() {
		return arguments;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(Object returnValue) {
		this.returnValue = objectToString(returnValue);
	}

	public String getException() {
		return exception;
	}

	public void setException(Throwable exception) {
		this.exception = exception.toString();
	}

	private String objectToString(Object object) {
		if (object == null)
			return "null";
		else if (object instanceof Character)
			return "'" + ((((char) object) == '\0') ? "\\0" : object) + "'";
		else if (object instanceof String)
			return "\"" + ((String) object).replace("\0", "\\0") + "\"";
		else if (object.getClass().isArray())
			return Arrays.deepToString((Object[]) object);
		else
			return object.toString();
	}
}