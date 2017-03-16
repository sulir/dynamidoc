package com.github.sulir.dynamidoc.tracing;

import java.io.Serializable;
import java.util.Arrays;

import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

public class TraceEvent implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private final String file;
	private final int line;
	private final String[] arguments;
	private String returnValue;
	private String exception;
	private String stateBefore;
	private String stateAfter;
	
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
	
	public String getStateBefore() {
		return stateBefore;
	}

	public void setStateBefore(Object stateBefore) {
		if (stateBefore != null)
			this.stateBefore = objectToString(stateBefore);
	}

	public String getStateAfter() {
		return stateAfter;
	}

	public void setStateAfter(Object stateAfter) {
		if (stateAfter != null)
			this.stateAfter = objectToString(stateAfter);
	}

	private String objectToString(Object object) {
		try {
			if (object == null)
				return "null";
			else if (object instanceof Character)
				return "'" + ((((char) object) == '\0') ? "\\0" : object) + "'";
			else if (object instanceof String)
				return "\"" + ((String) object).replace("\0", "\\0") + "\"";
			else if (object.getClass().isArray()) {
				String converted = Arrays.deepToString(new Object[] {object});
				return converted.substring(1, converted.length() - 1);
			} else
				return object.toString();
		} catch (Exception e) {
			return "an Exception-throwing object";
		}
	}
}