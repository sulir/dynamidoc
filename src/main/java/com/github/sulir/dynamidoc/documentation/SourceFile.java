package com.github.sulir.dynamidoc.documentation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class SourceFile {
	private final String relativePath;
	private final Path absolutePath;
	private final NavigableMap<Integer, MethodDeclaration> methodDeclarations = new TreeMap<>();
	private final Map<MethodDeclaration, Method> methods = new HashMap<>();
	private CompilationUnit unit;
	private Document document;
	private static final Pattern JAVADOC_START = Pattern.compile("(\\s*)/\\*\\*\\R");
	private static final String JAVADOC_END = "*/";
	
	public SourceFile(Project project, String path) throws IOException {
		this.relativePath = path;
		this.absolutePath = project.findFile(path);
		parse();
	}

	public String getPath() {
		return relativePath;
	}
	
	public Path getAbsolutePath() {
		return absolutePath;
	}
	
	public void addMethod(Method method) {
		methods.put(method.getDeclaration(), method);
	}
	
	public void writeJavadoc() throws IOException {
		for (Method method : methods.values()) {
			method.writeJavadoc();
		}
		
		try {
			unit.rewrite(document, null).apply(document);
			String code = fixJavadocFormatting();
			Files.write(absolutePath, code.getBytes());
		} catch (BadLocationException e) {
			throw new IOException("Cannot write Javadoc", e);
		}
	}

	public Method getMethodAt(int line) {
		return methods.get(getMethodDeclarationAt(line));
	}
	
	public String toString() {
		return relativePath;
	}
	
	CompilationUnit getCompilationUnit() {
		return unit;
	}
	
	MethodDeclaration getMethodDeclarationAt(int line) {
		MethodDeclaration node = methodDeclarations.floorEntry(line).getValue();
		if (line <= unit.getLineNumber(node.getStartPosition() + node.getLength()))
			return node;
		else
			return null;
	}
	
	private void parse() throws IOException {
		String content = new String(Files.readAllBytes(absolutePath));
		document = new Document(content);
		
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		Map<String, String> options = JavaCore.getOptions();
		options.put(JavaCore.COMPILER_COMPLIANCE, JavaCore.VERSION_1_8);
		options.put(JavaCore.COMPILER_SOURCE, JavaCore.VERSION_1_8);
		parser.setCompilerOptions(options);
		parser.setSource(content.toCharArray());
		
		unit = (CompilationUnit) parser.createAST(null);
		unit.recordModifications();
		
		unit.accept(new ASTVisitor() {
			@Override
			public boolean visit(MethodDeclaration node) {
				int startLine = unit.getLineNumber(node.getStartPosition());
				methodDeclarations.put(startLine, node);
				
				return false;
			}
		});
	}

	private String fixJavadocFormatting() {
		String[] lines = document.get().split("(?<=\\R)");
		String prefix = null;
		
		for (int i = 0; i < lines.length; i++) {
			Matcher matcher = JAVADOC_START.matcher(lines[i]);
			
			if (matcher.matches()) {
				prefix = matcher.group(1) + " *";
			} else if (lines[i].contains(JAVADOC_END)) {
				prefix = null;
			} else if (prefix != null && !lines[i].startsWith(prefix)) {
				lines[i] = lines[i].replaceFirst("^(\\s*\\* )?", prefix + " ");
			} 
		}
		
		return String.join("", lines);
	}
}
