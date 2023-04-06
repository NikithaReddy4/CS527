Steps to run the project and test suite:

1. Run “mvn test” command in terminal on exisiting directory to generate .class files of all the Java files of jsoup project and run test suite. 
2. Run CodeModifierTest class to generate mutants
3. The mutated code will be present in the “new_code” directory.
4. Now, change the pom.xml file by adding parameter “sourceDirectory” pointing to modified code which is “new_code” : Paste the below command and add that element inside “build”.
<sourceDirectory>new_code<sourceDirectory>
5.Finally, run “mvn test” in terminal to test the mutated code against the test suite.
