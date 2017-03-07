package com.github.sulir.dynamidoc.documentation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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
	private final Map<MethodExecution, Integer> executions = new HashMap<>();
	
	public Method(SourceFile file, int line) {
		this.declaration = file.getMethodDeclarationAt(line);
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
		Integer count = executions.get(execution);
		executions.put(execution, (count == null) ? 1 : count + 1);
	}
	
	public String generateDocumentation() {
		return "";
	}
	
	public String generateJavadoc() {
		Iterator<MethodExecution> executionSet = executions.keySet().iterator();
		StringBuilder result = new StringBuilder();
		
		while (executionSet.hasNext()) {
			String sentence = executionSet.next().generateDocumentation();
			
			if (!sentence.isEmpty())
				result.append(sentence);
			
			if (executionSet.hasNext())
				result.append("<br>\n    ");
		}
		
		return result.toString();
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
