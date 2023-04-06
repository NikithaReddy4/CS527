Steps to run the project and test suite:

Run “mvn test” command in terminal on exisiting directory to generate .class files of all the Java files of jsoup project and run test suite. 
Run CodeModifierTest class to generate mutants
The mutated code will be present in the “new_code” directory.
Now, change the pom.xml file by adding parameter “sourceDirectory” pointing to modified code which is “new_code” : Paste the below command and add that element inside “build”.
<sourceDirectory>new_code<sourceDirectory>
Finally, run “mvn test” in terminal to test the mutated code against the test suite.
