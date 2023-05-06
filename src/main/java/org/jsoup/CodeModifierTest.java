package org.jsoup;


import java.io.*;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import java.time.Instant;

import java.util.*;
import java.nio.file.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class CodeModifierTest
{
	public static File fileInGeneratedTestSourcesDirectory(String dirPath) {

		return new File(dirPath);
	}
	public static String getPackageName(String filePath) {
		try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.startsWith("package ")) {
					return line.substring("package ".length(), line.lastIndexOf(';')).trim();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public static int runMavenTests(int i) throws IOException, InterruptedException {
		String[] mvnCommand = {"mvn", "test"};
		ProcessBuilder processBuilder = new ProcessBuilder(mvnCommand);
		processBuilder.directory(new File(System.getProperty("user.dir")));
		processBuilder.redirectErrorStream(true);
		Process process = processBuilder.start();
		ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
		long timeout = 30; // timeout in seconds
		executor.schedule(() -> {
			process.destroy();
		}, timeout, TimeUnit.SECONDS);

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		String line;
		int exitCode = process.waitFor();
		executor.shutdownNow();

		String[] reportCommand = {"mvn", "surefire-report:report-only" };
		ProcessBuilder reportBuilder = new ProcessBuilder(reportCommand);
		reportBuilder.directory(new File(System.getProperty("user.dir")));
		reportBuilder.redirectErrorStream(true);
		Process reportProcess = reportBuilder.start();
		BufferedReader reportReader = new BufferedReader(new InputStreamReader(reportProcess.getInputStream()));
		int reportExitCode = reportProcess.waitFor();
		Path reportSourcePath = Paths.get(System.getProperty("user.dir"), "target", "site", "surefire-report.html");
		Path reportDestPath = Paths.get(System.getProperty("user.dir"), "report", "surefire-report"+i+".html");
		Files.createDirectories(reportDestPath.getParent());
		// copy the surefire from target/site/surefire-report.html to a report folder
		Files.copy(reportSourcePath, reportDestPath, StandardCopyOption.REPLACE_EXISTING);
		if(exitCode==0)
		{
			System.out.println("Mutant Not Killed");
		}
		else
		{
			System.out.println("Mutant Killed");
		}
		return exitCode;
	}

	public void getJavaFilesAndApplyMutators() throws IOException {
		String testDirPath = System.getProperty("user.dir") + "/src/main/java/org/jsoup";
		String dir_path = "";
		Path path = Paths.get(testDirPath);
		List<String> javaFileNames = new ArrayList<>();
		File directory = new File(testDirPath);
		File[] files = directory.listFiles();
		List<CompilationUnit> mutants = new ArrayList<>();
		Map<CompilationUnit, String> mutants_map = Collections.emptyMap();
		String dummdir = System.getProperty("user.dir") + "/src/";
		String resultpath = System.getProperty("user.dir") + "/results/";
		FileWriter writer = new FileWriter(resultpath+"result.txt");
		int filecount=0;
		int mutatorlimit=1;
		int filelimit=61;
		int mutantgenerated = 0;//to keep track of mutants generated throughout the mutation testing process
		int mutantkilled = 0;//to keep track of mutants killed throughout the mutation testing process
		int mutationoperator=0;//used for iterating through the mutators
		int operator_mutantgenerated=0;//to keep track of mutants generated for each operator
		int operator_mutantkilled=0;//to keep track of mutants killed for each operator
		int m = 0;// for keeping track of surefire report corresponding to respective mutant
		Map<Integer, String> operatorMap = new HashMap<Integer, String>();
		operatorMap.put(0, "Conditonal Boundary Mutator");
		operatorMap.put(1, "Math Mutator");
		operatorMap.put(2, "Negate Conditions Mutator");
		operatorMap.put(3, "Increments Mutator");
		operatorMap.put(4, "Invert Negatives Mutator");
		operatorMap.put(5, "Inline Constant Mutator");
		operatorMap.put(6, "False Returns Mutator");
		operatorMap.put(7, "Constructor Calls Mutator");
		operatorMap.put(8, "Assign Increments Mutator");
		operatorMap.put(9, "AOR Visitor Mutator");
		operatorMap.put(10, "Primitive Return Mutator");
		operatorMap.put(11, "Remove Conditionals Mutator");
		operatorMap.put(12, "BitWise Mutator");
		operatorMap.put(13, "True Returns Mutator");
		operatorMap.put(14, "Void Method Call Mutator");
		operatorMap.put(15, "Experimental Switch Mutator");
		operatorMap.put(16, "Remove Increments Mutator");
		operatorMap.put(17, "Empty Returns Mutator");
		operatorMap.put(18, "Null Returns Mutator");
		//iterator for the mutators, in each iterator one mutator is picked and is applied to all the java files
		for (mutationoperator =0; mutationoperator < mutatorlimit; mutationoperator++) {
			operator_mutantgenerated=0;
			operator_mutantkilled=0;
			filecount=0;
			for (File file : files) {
				dir_path = "";
				if (file.isDirectory()) {
					dir_path = testDirPath + "/" + file.getName();
					File dir_file = new File(dir_path);
					File[] dir_files = dir_file.listFiles();
					List<String> filePaths = new ArrayList<>();
					if (dir_files != null) {
						//iterator for all java files
						for (File d_file : dir_files) {
							if (d_file.isFile() && d_file.getName().endsWith(".java")) {
								String filePath = d_file.getAbsolutePath();
								String packageName = getPackageName(filePath);
								String fileName = d_file.getName();//Attributes.java
								if (filecount<filelimit&&!(fileName.equals("CodeModifierTest.java"))) {
									{
										CompilationUnit cu = StaticJavaParser.parse(d_file);
										filecount++;
										File og = new File(filePath);
										//keep copy of original file original
										File copy = new File(System.getProperty("user.dir") + "/src/" + fileName);
										og.renameTo(copy);
										//Map<operator,map<file,list<mutants>>
										//Map<operator,mutationscore>
										switch (mutationoperator) {
											case 0:
												ConditionalsBoundary conditionalsBoundary = new ConditionalsBoundary(cu);
												conditionalsBoundary.visit(cu, fileName);
												operator_mutantgenerated+=conditionalsBoundary.getMutants().size();
												mutantgenerated += conditionalsBoundary.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = conditionalsBoundary.getMutantsMap();
												mutants = conditionalsBoundary.getMutants();
												break;
											case 1:
												System.out.println("entered case");
												MathMutator mathMutator = new MathMutator(cu);
												mathMutator.visit(cu, fileName);
												operator_mutantgenerated+=mathMutator.getMutants().size();
												mutantgenerated += mathMutator.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = mathMutator.getMutantsMap();
												mutants = mathMutator.getMutants();
												break;
											case 2:
												NegateConditions operator = new NegateConditions(cu);
												operator.visit(cu, fileName);
												operator_mutantgenerated+=operator.getMutants().size();
												mutantgenerated += operator.getMutants().size();
												System.out.println("**********************************************");
												System.out.println(operatorMap.get(mutationoperator));
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = operator.getMutantsMap();
												mutants =new ArrayList<>(operator.getMutants());
												break;
											case 3:
												Increments increments = new Increments(cu);
												increments.visit( cu, fileName);
												operator_mutantgenerated+=increments.getMutants().size();
												mutantgenerated += increments.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = increments.getMutantsMap();
												mutants = increments.getMutants();
												break;
											case 4:
												InvertNegatives invertNegatives = new InvertNegatives(cu);
												invertNegatives.visit(cu, fileName);
												operator_mutantgenerated+=invertNegatives.getMutants().size();
												mutantgenerated += invertNegatives.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = invertNegatives.getMutantsMap();
												mutants = invertNegatives.getMutants();
												break;
											case 5:
												InlineConstant inlineConstant = new InlineConstant(cu);
												inlineConstant.visit(cu, fileName);
												operator_mutantgenerated+=inlineConstant.getMutants().size();
												mutantgenerated += inlineConstant.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = inlineConstant.getMutantsMap();
												mutants = inlineConstant.getMutants();
												break;
											case 6:
												System.out.println("entered case");

												FalseReturns falseReturns = new FalseReturns(cu);
												falseReturns.visit(cu, fileName);
												operator_mutantgenerated+=falseReturns.getMutants().size();
												mutantgenerated += falseReturns.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = falseReturns.getMutantsMap();
												mutants = falseReturns.getMutants();
												break;
											case 7:
												System.out.println("entered case");

												ConstructorCalls constructorCalls = new ConstructorCalls(cu);
												constructorCalls.visit(cu, fileName);
												operator_mutantgenerated+=constructorCalls.getMutants().size();
												mutantgenerated += constructorCalls.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = constructorCalls.getMutantsMap();
												mutants = constructorCalls.getMutants();
												break;
											case 8:
												AssignIncrements assignIncrements = new AssignIncrements(cu);
												assignIncrements.visit(cu, fileName);
												operator_mutantgenerated+=assignIncrements.getMutants().size();
												mutantgenerated += assignIncrements.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = assignIncrements.getMutantsMap();
												mutants = assignIncrements.getMutants();
												break;
											case 9:
												AORVisitor aorVisitor = new AORVisitor(cu);
												aorVisitor.visit(cu, fileName);
												operator_mutantgenerated+=aorVisitor.getMutants().size();
												mutantgenerated += aorVisitor.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = aorVisitor.getMutantsMap();
												mutants = aorVisitor.getMutants();
												break;
											case 10:
												PrimitiveReturn primitiveReturn = new PrimitiveReturn(cu);
												primitiveReturn.visit(cu, fileName);
												operator_mutantgenerated+=primitiveReturn.getMutants().size();
												mutantgenerated += primitiveReturn.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = primitiveReturn.getMutantsMap();
												mutants = primitiveReturn.getMutants();
												break;
											case 11:
												RemoveConditionals removeConditionals = new RemoveConditionals(cu);
												removeConditionals.visit(cu, fileName);
												operator_mutantgenerated+=removeConditionals.getMutants().size();
												mutantgenerated += removeConditionals.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = removeConditionals.getMutantsMap();
												mutants = removeConditionals.getMutants();
												break;
											case 12:
												OBBN1 obbn1 = new OBBN1(cu);
												obbn1.visit(cu, fileName);
												operator_mutantgenerated+=obbn1.getMutants().size();
												mutantgenerated += obbn1.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = obbn1.getMutantsMap();
												mutants = obbn1.getMutants();
												break;
											case 13:
												TrueReturns trueReturns = new TrueReturns(cu);
												trueReturns.visit(cu, fileName);
												operator_mutantgenerated+=trueReturns.getMutants().size();
												mutantgenerated += trueReturns.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = trueReturns.getMutantsMap();
												mutants = trueReturns.getMutants();
												break;
											case 14:
												VoidMethodCall voidMethodCall = new VoidMethodCall(cu);
												voidMethodCall.visit(cu, fileName);
												operator_mutantgenerated+=voidMethodCall.getMutants().size();
												mutantgenerated += voidMethodCall.getMutants().size();
												System.out.println("**********************************************"+"\n");
												System.out.println(operatorMap.get(mutationoperator)+"\n");
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = voidMethodCall.getMutantsMap();
												mutants = voidMethodCall.getMutants();
												break;
											default:
												break;
										}
										for (int i = 0; i < mutants.size(); i++) {
											m++;
											File outputFile = fileInGeneratedTestSourcesDirectory(filePath);
											// Write the modified code to the new file which will overwrite the original file
											outputFile.getParentFile().mkdirs();
											try (FileWriter outputWriter = new FileWriter(outputFile)) {
												//System.out.println("The mutants are"+codeModifier.getMutants().get(i).toString());
												System.out.println(mutants_map.get(mutants.get(i)));
												outputWriter.write(mutants.get(i).toString());
											} catch (IOException e) {
												e.printStackTrace();
											}
											try {
//													Instant instant = Instant.now();
//													long timestampSeconds = instant.getEpochSecond();
//													System.out.println("Started mvn test: " + timestampSeconds);

												if (runMavenTests(m) == 0) {
													System.out.println("passed " + m + "\n");
												} else {
													operator_mutantkilled++;
													mutantkilled++;
													System.out.println("failed" + "\n");
												}

											} catch (InterruptedException e) {
												throw new RuntimeException(e);
											}
											//restore the file
										}
//									if (codeModifier.getMutants().isEmpty()) {
//										System.out.println("No mutants generated");
//									}
									}
									try {
										// Create input and output streams for the files
										Path sourceFile = Paths.get(dummdir + fileName);
										File sf = new File(dummdir + fileName);
										Path destinationFile = Paths.get(filePath);

										Files.copy(sourceFile, destinationFile, StandardCopyOption.REPLACE_EXISTING);

										// Delete the source file
										if (sf.delete()) {
											System.out.println("File copied and deleted successfully." + "\n");
										} else {
											System.out.println("Failed to delete source file.");
										}

									} catch (IOException e) {
										System.out.println("An error occurred.");
										e.printStackTrace();
									}
									System.out.println("Mutants generated in total=" + mutantgenerated);
									System.out.println("Mutants killed in total=" + mutantkilled + "\n");
								}
							}
						}
					}
				}
			}
			writer.write(operatorMap.get(mutationoperator)+"\n"+"Mutatnts Generated= "+operator_mutantgenerated+"\n"+"Mutants Killed= "+operator_mutantkilled+"\n"+"Mutation Score= "+((double)operator_mutantkilled/operator_mutantgenerated)*100+"\n");
		}
		writer.close();
	}


	public void deleteFolderContents(File folder) {
		File[] files = folder.listFiles(); // get all files and subdirectories within the folder
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolderContents(file); // recursively delete contents of subdirectories
				}
				file.delete(); // delete the file or empty subdirectory
			}
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException {
		CodeModifierTest cm=new CodeModifierTest();
		//deletes the surefire reports from previous mutation testing in reports folder
		cm.deleteFolderContents(new File(System.getProperty("user.dir")+"/report/"));
		cm.getJavaFilesAndApplyMutators();




	}
}


