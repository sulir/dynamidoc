package com.github.sulir.dynamidoc.documentation;

import java.util.Arrays;

public class MethodExecution {
	private final Method method;
	private final String[] argumentValues;
	private String returnValue;
	private String exception;
	
	public MethodExecution(Method method, String[] argumentValues) {
		this.method = method;
		this.argumentValues = argumentValues;
	}
	
	public Method getMethod() {
		return method;
	}

	public String[] getArgumentValues() {
		return argumentValues;
	}

	public String getReturnValue() {
		return returnValue;
	}
	
	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}
	
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	public String generateDocumentation() {
		if (argumentValues.length == 0) {
			if (returnValue != null)
				return "The method can return " + returnValue + ".";
			
			if (exception != null)
				return "The method can throw " + exception + ".";
		}
		
		if (argumentValues.length > 0) {
			if (returnValue == null && exception == null)
				return "The method can take arguments: " + generateArgumentMapping(" = ") + ".";
			
			if (returnValue != null) {
				return "When " + generateArgumentMapping(" was ")
					+ ", the method returned " + returnValue + ".";
			}
			
			if (exception != null) {
				return "When " + generateArgumentMapping(" was ")
					+ ", the method threw " + exception + ".";
			}
		}
		
		return "";
	}

	private String generateArgumentMapping(String mapper) {
		String[] names = method.getParameterNames();
		StringBuilder result = new StringBuilder();
		
		for (int i = 0; i < names.length; i++) {
			result.append(names[i] + mapper + argumentValues[i]);
			
			if (i < names.length - 2)
				result.append(", ");
			else if (i == names.length - 2)
				result.append(" and ");
		}
		
		return result.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = prime + Arrays.hashCode(argumentValues);
		result = prime * result + ((returnValue == null) ? 0 : returnValue.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof MethodExecution))
			return false;
		
		MethodExecution other = (MethodExecution) object;
		return Arrays.equals(other.argumentValues, argumentValues) && other.returnValue == returnValue;
	}
}
