package org.jsoup;


import java.io.*;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;

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
//		while ((line = reader.readLine()) != null) {
//			if (line.startsWith("Running ")) {
//				String testCaseName = line.substring("Running ".length());
//
//				// Start a new thread to keep track of the running time of this test case
//				Thread testThread = new Thread(() -> {
//					try {
//						Thread.sleep(30000); // Sleep for the timeout duration
//						System.err.println("Timeout reached for test case: " + testCaseName);
//						System.exit(1); // Exit with non-zero status code to indicate test failure
//					} catch (InterruptedException e) {
//						// Ignore interruption
//					}
//				});
//
//				testThread.start(); // Start the thread
//
//				// Wait for the process to exit or the thread to finish
//				exitCode = process.waitFor();
//				testThread.interrupt(); // Interrupt the thread to stop it from continuing to run
//				testThread.join(); // Wait for the thread to finish before continuing
//			}
//		}
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
		// Initialize the source root as the "target/test-classes" dir, which
		// includes the test resource information (i.e., the source code info
		// for Jsoup for this assignment) copied from src/test/resources
		// during test execution
		SourceRoot sourceRoot = new SourceRoot(
				CodeGenerationUtils.mavenModuleRoot(CodeModifierTest.class)
						.resolve("target/classes"));
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
		int count = 0;
		int tests = 0;
		int filecount = 0;
		int mutantkilled = 0;
		int m = 0;
		int mutantgenerated = 0;
		int mutationoperator=0;
		int operator_mutantgenerated=0;
		int operator_mutantkilled=0;
		Map<Integer, String> operatorMap = new HashMap<Integer, String>();
		operatorMap.put(0, "Negate Conditions");
        operatorMap.put(1, "Conditional Boundary");
		operatorMap.put(2, "Conditional Boundary");
		operatorMap.put(3, "Conditional Boundary");
		operatorMap.put(4, "Conditional Boundary");
		operatorMap.put(5, "Conditional Boundary");
		operatorMap.put(6, "Conditional Boundary");
		operatorMap.put(7, "Conditional Boundary");
		operatorMap.put(8, "Conditional Boundary");
		operatorMap.put(9, "Conditional Boundary");
		operatorMap.put(10, "Conditional Boundary");
		operatorMap.put(11, "Conditional Boundary");
		operatorMap.put(12, "Conditional Boundary");
		operatorMap.put(13, "Conditional Boundary");
		operatorMap.put(14, "Conditional Boundary");
		//iterator for the mutators, in each iterator one mutator is picked and is applied to all the java files
		for (mutationoperator = 0; mutationoperator < 2; mutationoperator++) {
			operator_mutantgenerated=0;
			operator_mutantkilled=0;
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
								CompilationUnit cu = sourceRoot.parse(dir_path, fileName);
								//CompilationUnit scu = sourceRoot.parse(dir_path, fileName);
								if (filecount<1&&!(fileName.equals("CodeModifierTest.java")) && !(fileName.equals("CodeModifier.java"))) {
									{
										filecount++;
										File og = new File(filePath);
										//keep copy of original file original
										File copy = new File(System.getProperty("user.dir") + "/src/" + fileName);
										og.renameTo(copy);
										//Map<operator,map<file,list<mutants>>
										//Map<operator,mutationscore>
										switch (mutationoperator) {
											//NegateConditions operator = new NegateConditions(cu);
											//operator.visit(cu, fileName);

											//ConditionalsBoundary operator = new ConditionalsBoundary(cu);
											//operator.visit(cu, fileName);

//									Increments operator = new Increments(cu);
//									operator.visit(cu, fileName);

											//	AssignIncrements operator = new AssignIncrements(cu);
											//    operator.visit(cu, fileName);

											//	FalseReturns operator = new FalseReturns(cu);
											//	    operator.visit(cu, fileName);

											//	TrueReturns operator = new TrueReturns(cu);
											//	operator.visit(cu, fileName);

											//		EmptyReturns operator = new EmptyReturns(cu);
											//			operator.visit(cu, fileName);

											//	VoidMethodCall operator = new VoidMethodCall(cu);
											//			operator.visit(cu, fileName);

											case 0:
												NegateConditions operator = new NegateConditions(cu);
												operator.visit(cu, fileName);
												operator_mutantgenerated+=operator.getMutants().size();
												mutantgenerated += operator.getMutants().size();
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = operator.getMutantsMap();
												mutants = operator.getMutants();
												break;
											case 1:
												ConditionalsBoundary conditionalsBoundary = new ConditionalsBoundary(cu);
												conditionalsBoundary.visit(cu, fileName);
												operator_mutantgenerated+=conditionalsBoundary.getMutants().size();
												mutantgenerated += conditionalsBoundary.getMutants().size();
												System.out.println("Mutations in " + fileName + "\n");
												mutants_map = conditionalsBoundary.getMutantsMap();
												mutants = conditionalsBoundary.getMutants();
												break;
//											case 2:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 3:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 4:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 5:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 6:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 7:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 8:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 9:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 10:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 11:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 12:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//											case 13:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
//									        case 14:
//												NegateConditions operator = new NegateConditions(cu);
//												operator.visit(cu, fileName);
//												operator_mutantgenerated+=operator.getMutants().size();
//												mutantgenerated += operator.getMutants().size();
//												System.out.println("Mutations in " + fileName + "\n");
//												mutants_map = operator.getMutantsMap();
//												mutants = operator.getMutants();
//												break;
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
										System.out.println("mutantgenerated" + mutantgenerated);
										System.out.println("mutantkilled" + mutantkilled + "\n");
									}
								}
							}

						}
					}
				}
				writer.write(operatorMap.get(mutationoperator)+"\n"+"Mutatnts Generated"+operator_mutantgenerated+"\n"+"Mutants Killed"+operator_mutantkilled+"\n"+"Mutation Score"+(operator_mutantkilled/operator_mutantgenerated)*100+"\n");
        		writer.close();
			}
		
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
		//runMavenTests(1);



	}
}
