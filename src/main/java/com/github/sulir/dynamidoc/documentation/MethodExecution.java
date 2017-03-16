package com.github.sulir.dynamidoc.documentation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;

public class MethodExecution {
	private final Method method;
	private final String[] argumentValues;
	private String returnValue;
	private String exception;
	private String stateBefore;
	private String stateAfter;
	private int count = 1;
	
	private static final Object[][] TEMPLATES_TABLE = {
		{true, false, false, false, false,
		    ""},
		{true, false, true, false, false,
		    "The method returned {return}."},
		{true, false, false, true, false,
		    "The method threw {exception}."},
		{true, true, false, false, false,
		    "The method took arguments: {arguments}."},
		{true, true, true, false, false,
		    "When {arguments}, the method returned {return}."},
		{true, true, false, true, false,
		    "When {arguments}, the method threw {exception}."},
		{false, false, false, false, false,
		    "The method was called on {before}."},
		{false, false, false, false, true,
		    "When called on {before}, the object changed to {after}."},
		{false, false, true, false, false,
		    "When called on {before}, the method returned {return}."},
		{false, false, true, false, true,
		    "When called on {before}, the object changed to {after} and the method returned {return}."},
		{false, false, false, true, false,
		    "When called on {before}, the method threw {exception}."},
		{false, false, false, true, true,
		    "When called on {before}, the object changed to {after} and the method threw {exception}."},
		{false, true, false, false, false,
		    "The method was called on {before} with {arguments}."},
		{false, true, false, false, true,
		    "When called on {before} with {arguments}, the object changed to {after}."},
		{false, true, true, false, false,
		    "When called on {before} with {arguments}, the method returned {return}."},
		{false, true, true, false, true,
		    "When called on {before} with {arguments}, the object changed to {after} and the method "
		    + "returned {return}."},
		{false, true, false, true, false,
		    "When called on {before} with {arguments}, the method threw {exception}."},
		{false, true, false, true, true,
		    "When called on {before} with {arguments}, the object changed to {after} and the method "
		    + "threw {exception}."}
	};
	private static final Map<List<Boolean>, String> TEMPLATES = new HashMap<>();
	
	static {
		for (Object[] row : TEMPLATES_TABLE) {
			Boolean[] conditions = Arrays.copyOf(row, row.length - 1, Boolean[].class);
			TEMPLATES.put(Arrays.asList(conditions), (String) row[row.length - 1]);
		}
	}
	
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
	
	public boolean returnedValue() {
		return returnValue != null;
	}
	
	public String getException() {
		return exception;
	}

	public void setException(String exception) {
		this.exception = exception;
	}
	
	public String getStateBefore() {
		return stateBefore;
	}

	public void setStateBefore(String stateBefore) {
		this.stateBefore = stateBefore;
	}

	public String getStateAfter() {
		return stateAfter;
	}

	public void setStateAfter(String stateAfter) {
		this.stateAfter = stateAfter;
	}

	public boolean threwException() {
		return exception != null;
	}
	
	public boolean changedState() {
		return stateBefore != null && !stateBefore.equals(stateAfter);
	}
	
	public int getCount() {
		return count;
	}

	public void incrementCount() {
		count++;
	}
	
	public String generateDocumentation() {
		String template = getTemplate(method.isStatic(), method.hasParameters(),
				returnedValue(), threwException(), changedState());
		
		Map<String, String> values = new HashMap<>();
		values.put("before", stateBefore);
		values.put("arguments", generateArgumentMapping(" = "));
		values.put("return", returnValue);
		values.put("exception", exception);
		values.put("after", stateAfter);
		
		StrSubstitutor substitutor = new StrSubstitutor(values, "{", "}");
		return substitutor.replace(template);
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

	private String getTemplate(Boolean... conditions) {
		return TEMPLATES.get(Arrays.asList(conditions));
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof MethodExecution))
			return false;
		MethodExecution other = (MethodExecution) obj;
		if (!Arrays.equals(argumentValues, other.argumentValues))
			return false;
		if (exception == null) {
			if (other.exception != null)
				return false;
		} else if (!exception.equals(other.exception))
			return false;
		if (returnValue == null) {
			if (other.returnValue != null)
				return false;
		} else if (!returnValue.equals(other.returnValue))
			return false;
		if (stateAfter == null) {
			if (other.stateAfter != null)
				return false;
		} else if (!stateAfter.equals(other.stateAfter))
			return false;
		if (stateBefore == null) {
			if (other.stateBefore != null)
				return false;
		} else if (!stateBefore.equals(other.stateBefore))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(argumentValues);
		result = prime * result + ((exception == null) ? 0 : exception.hashCode());
		result = prime * result + ((returnValue == null) ? 0 : returnValue.hashCode());
		result = prime * result + ((stateAfter == null) ? 0 : stateAfter.hashCode());
		result = prime * result + ((stateBefore == null) ? 0 : stateBefore.hashCode());
		return result;
	}
}
