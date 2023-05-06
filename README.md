Mutation Testing Framework for Jsoup Application

This project is a mutation testing framework for Jsoup application. It uses mutation testing to evaluate the quality of test suites by introducing small changes (mutants) to the application at the source code level and check if the tests can detect these mutants.

The framework consists of a set of mutators, each of which introduces a specific type of mutation to the code. The framework uses Maven to compile and run the tests against each mutant.

Functions of the CodeModifierTest class:

`runMavenTests`: This function runs the mvn test command, handles the timeout using threads, and generates the Surefire report for each mutant.

`getJavaFilesAndApplyMutators`: This function consists of the implementation which includes the following: 
a. Firstly, it iterates over the mutators loop. 
b. Then, tracks the java files in the jsoup directory. 
c. Further, for each file, it creates an object of the chosen mutator class and calls the visit function of the class. The visit function returns the list of mutants for each file from the mutator class. 
d. Next, it iterates over the list of mutants and calls the runMavenTests function to run the test suite against each mutant, generate the Surefire reports, and write the details like mutants generated and mutants killed details into a result file. 
e. It then replaces the java file with the original contents of the file.

`deleteFolderContents`: This function deletes the set of Surefire reports that are generated as a part of one mutation testing execution and is called every time before executing the mutation testing from the main.

Execution flow of the project:

Trigger the mvn test command for the first time, which will generate the .class files.
Run the getJavaFilesAndApplyMutators function to execute the flow accordingly as mentioned above.

Usage: To use this framework, clone the repository and run the mvn test which generates the class files and makes sure that all the testcases are run succesfully on the unmodified code. Then execute the main function in getJavaFilesAndApplyMutators to start the mutation testing. You can change the variable values for mutatorlimit and filelimit to limit the number of mutants generated. Leaving filelimit as default value and updating mutatorlimit accordingly will run the mutation testing on entire source code for the required mutators.

Results: The required surefire reports will be present under report folder in the repository. The metrics for each operator like mutants generated and killed will be present under results folder in repository.

Dependencies:

Java 8 or higher
Maven 3 or higher

Contributors: 

Nikitha Reddy Sankepally(nikitha6) - Generation of mutants, evaluation of metrics for analysis

Eesha Rajesh Chawhan(chawhan2) - Implementation of mutators
