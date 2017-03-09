package com.github.sulir.dynamidoc.documentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExampleSelector {

	private List<MethodExecution> executions;
	
	public ExampleSelector(Collection<MethodExecution> executions) {
		this.executions = new ArrayList<>(executions);
	}
	
	public void sort() {
		Map<MethodExecution, Integer> scores = new HashMap<>();
		
		for (MethodExecution execution : executions) {
			scores.put(execution, getScore(execution));
		}
		
		executions.sort((e1, e2) -> scores.get(e2).compareTo(scores.get(e1)));
	}
	
	public List<MethodExecution> getTop(int count) {
		return executions.subList(0, Math.min(count, executions.size()));
	}
	
	public List<MethodExecution> getAll() {
		return executions;
	}
	
	private int getScore(MethodExecution execution) {
		return execution.getCount();
	}
}
