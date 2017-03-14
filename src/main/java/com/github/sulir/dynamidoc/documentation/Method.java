package com.github.sulir.dynamidoc.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.Javadoc;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TagElement;
import org.eclipse.jdt.core.dom.TextElement;

public class Method {
	private static final String EXAMPLES_TAG = "@examples";
	
	private final MethodDeclaration declaration;
	private final Map<MethodExecution, MethodExecution> executions = new HashMap<>();
	
	public Method(SourceFile file, int line) {
		declaration = file.getMethodDeclarationAt(line);
	}

	public String getName() {
		return declaration.getName().getIdentifier();
	}
	
	public Class<?> getReturnType() {
		return declaration.getReturnType2().getClass();
	}
	
	public String[] getParameterNames() {
		List<String> names = new ArrayList<>();
		
		for (Object node : declaration.parameters()) {
			SingleVariableDeclaration parameter = (SingleVariableDeclaration) node;
			names.add(parameter.getName().getIdentifier());
		}
		
		return names.toArray(new String[] {});
	}
	
	public void addExecution(MethodExecution execution) {
		if (executions.containsKey(execution))
			executions.get(execution).incrementCount();
		else
			executions.put(execution, execution);
	}
	
	public String[] generateDocumentation() {
		ExampleSelector selector = new ExampleSelector(executions.values());
		selector.sort();
		
		return selector.getTop(5).stream()
				.map(MethodExecution::generateDocumentation)
				.filter(e -> !e.isEmpty())
				.toArray(String[]::new);
	}
	
	public String generateJavadoc() {
		return String.join("<br>\n    ", generateDocumentation());
	}
	
	@SuppressWarnings("unchecked")
	public void writeJavadoc() {
		String documentation = generateJavadoc();
		if (documentation.isEmpty())
			return;
		
		Javadoc javadoc = declaration.getJavadoc();
		
		if (javadoc == null) {
			javadoc = declaration.getAST().newJavadoc();
			declaration.setJavadoc(javadoc);
		}

		TagElement tag = javadoc.getAST().newTagElement();
		tag.setTagName(EXAMPLES_TAG);
		
		TextElement text = javadoc.getAST().newTextElement();
		text.setText(documentation);
		tag.fragments().add(text);
		
		javadoc.tags().removeIf(t -> EXAMPLES_TAG.equals(((TagElement) t).getTagName()));
		javadoc.tags().add(tag);
	}
	
	MethodDeclaration getDeclaration() {
		return declaration;
	}
}
