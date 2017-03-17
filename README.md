# DynamiDoc

DynamiDoc generates Javadoc using dynamic analysis. It adds `@examples` tags with examples of arguments, return values, thrown exceptions, and object states to methods in the source code.

This project is an early prototype.

## Instructions

1. Clone and open the DynamiDoc project in Eclipse (with [AJDT](http://www.eclipse.org/ajdt/downloads/) plugin installed).
2. Add AspectJ capability to the project you would like to document (the "target project"): Configure > Convert to AspectJ Project.
3. Add the DynamiDoc project to the Aspect Path of the target project: AspectJ Tools > Configure AspectJ Build Path > Aspect Path > Add Project > DynamiDoc.
4. Execute the target project manually or by running tests.
5. Add the absolute source path of the target project (e.g., "/home/user/Project/src/main/java/") to the command-line argument list of DynamiDoc.
6. Run DynamiDoc.