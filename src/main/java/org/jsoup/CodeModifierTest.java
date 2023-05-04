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


//	public void getJavaFiles() {
//		// Initialize the source root as the "target/test-classes" dir, which
//		// includes the test resource information (i.e., the source code info
//		// for Jsoup for this assignment) copied from src/test/resources
//		// during test execution
//		SourceRoot sourceRoot = new SourceRoot(
//				CodeGenerationUtils.mavenModuleRoot(CodeModifierTest.class)
//						.resolve("target/classes"));
//		String testDirPath = System.getProperty("user.dir")+"/src/main/java/org/jsoup";
//		String dir_path="";
//		Path path = Paths.get(testDirPath);
//		int count=0;
//		List<String> javaFileNames = new ArrayList<>();
//		File directory = new File(testDirPath);
//		File[] files = directory.listFiles();
//		for (File file : files) {
//			dir_path="";
//			if(file.isDirectory())
//			{
//				dir_path=testDirPath+"/"+file.getName();
//				File dir_file = new File(dir_path);
//				File[] dir_files = dir_file.listFiles();
//				List<String> filePaths = new ArrayList<>();
//				if (dir_files != null) {
//				for (File d_file : dir_files) {
//
//					if (d_file.isFile() && d_file.getName().endsWith(".java")) {
//						String filePath = d_file.getAbsolutePath();
//						String packageName = getPackageName(filePath);
//						String fileName = d_file.getName();
//						CompilationUnit cu = sourceRoot.parse(dir_path, fileName);
//						System.out.println(packageName + "/" + fileName);
//						//just select a few files from the java files and call the operators on them TODO:
//						//call specific operator randomly TODO:
////						Random rand = new Random();
////						int randomNumber = rand.nextInt(4) + 1;
////						System.out.println("N:" + randomNumber);
////						int mutator=1;
////						switch (mutator) {
////							case 1:
////								if(count<1) {
////									count++;
////									CodeModifier codeModifier = new CodeModifier();
////
////									codeModifier.visit(cu, null);
////								}
////								break;
////
////							default:
////								System.out.println("default");
////
////						}
//						File outputFile = fileInGeneratedTestSourcesDirectory(packageName, fileName);
//						outputFile.getParentFile().mkdirs();
//						try (FileWriter outputWriter = new FileWriter(outputFile)) {
//							outputWriter.write(cu.toString());
//						} catch (IOException e) {
//							e.printStackTrace();
//						}
//					}
//
//
//	}
//}
//else if (file.isFile() && file.getName().endsWith(".java")) {
//					System.out.println(getPackageName(file.getAbsolutePath())+"  "+file.getName());
//					CompilationUnit cu = sourceRoot.parse(testDirPath,file.getName());
//					// Instantiate the CodeModifier class that you will implement to perform
//					// the actual task. This is a visitor class according to the visitor
//					// pattern (one of the most important design patterns).
//					CodeModifier codeModifier = new CodeModifier();
//					// Apply our visitor class on the code representation for the given
//					// Java file. In this way, our visit function(s) can be automatically
//					// applied to all possible elements of the specified type(s).
//					codeModifier.visit(cu, null);
//					File outputFile = fileInGeneratedTestSourcesDirectory(getPackageName(file.getAbsolutePath()), file.getName());
//					// Write the modified code to the new file
//        			outputFile.getParentFile().mkdirs();
//					try (FileWriter outputWriter = new FileWriter(outputFile)) {
//					outputWriter.write(cu.toString());
//					}catch (IOException e) {
//					// Handle the exception
//					e.printStackTrace();
//}}}
//}
//}

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
//		while ((line = reportReader.readLine()) != null) {
//
//			if (line.contains("Failures:")) {
//				String[] parts = line.split(",");
//				for (String part : parts) {
//					if (part.contains("Failures:")) {
//						System.out.println(Integer.parseInt(part.split(":")[1].trim()));
//					}
//				}
//			}
//		}

		int reportExitCode = reportProcess.waitFor();
		Path reportSourcePath = Paths.get(System.getProperty("user.dir"), "target", "site", "surefire-report.html");
		Path reportDestPath = Paths.get(System.getProperty("user.dir"), "report", "surefire-report"+i+".html");

		Files.createDirectories(reportDestPath.getParent());
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

	public void getJavaFiles() throws IOException {
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
		List<CompilationUnit> mutants=new ArrayList<>();
		Map<CompilationUnit, String> mutants_map=Collections.emptyMap();
		String dummdir = System.getProperty("user.dir") + "/src/";

		int count = 0;
		int tests = 0;
		int filecount = 0;
		int mutantkilled = 0;
		int m = 0;
		int mutantgenerated = 0;
		for (int mutationoperator = 1; mutationoperator <2; mutationoperator++){

			for (File file : files) {
				dir_path = "";
				if (file.isDirectory()) {
					dir_path = testDirPath + "/" + file.getName();
					File dir_file = new File(dir_path);
					File[] dir_files = dir_file.listFiles();
					List<String> filePaths = new ArrayList<>();
					if (dir_files != null) {
						for (File d_file : dir_files) {
							if (d_file.isFile() && d_file.getName().endsWith(".java")) {
								String filePath = d_file.getAbsolutePath();
								String packageName = getPackageName(filePath);
								String fileName = d_file.getName();//Attributes.java
								CompilationUnit cu = sourceRoot.parse(dir_path, fileName);
								//CompilationUnit scu = sourceRoot.parse(dir_path, fileName);
								if (!(fileName.equals("CodeModifierTest.java")) && !(fileName.equals("CodeModifier.java"))) {
									{
										filecount++;
										File og = new File(filePath);
										//keep copy of original file original
										File copy = new File(System.getProperty("user.dir") + "/src/" + fileName);
										og.renameTo(copy);
										//Map<operator,map<file,list<mutants>>
										//Map<operator,mutationscore>
switch(mutationoperator) {
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
		mutantgenerated += operator.getMutants().size();
		System.out.println("Mutations in " + fileName + "\n");
		mutants_map = operator.getMutantsMap();
		mutants = operator.getMutants();
		break;
	case 1:
		ConditionalsBoundary conditionalsBoundary = new ConditionalsBoundary(cu);
		conditionalsBoundary.visit(cu, fileName);
		mutantgenerated += conditionalsBoundary.getMutants().size();
		System.out.println("Mutations in " + fileName + "\n");
		mutants_map = conditionalsBoundary.getMutantsMap();
		mutants = conditionalsBoundary.getMutants();
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
												if (runMavenTests(m) == 0) {
													System.out.println("passed " + m + "\n");
												} else {
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
										Path sourceFile = Paths.get(dummdir+fileName);
										File sf = new File(dummdir+fileName);
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
							}}
//						else if (file.isFile() && file.getName().endsWith(".java")) {
//
//							//System.out.println(getPackageName(file.getAbsolutePath())+"  "+file.getName());
//							CompilationUnit cu = sourceRoot.parse(testDirPath, file.getName());
//							// Instantiate the CodeModifier class that you will implement to perform
//							// the actual task. This is a visitor class according to the visitor
//							// pattern (one of the most important design patterns).
////				CodeModifier codeModifier = new CodeModifier();
////				// Apply our visitor class on the code representation for the given
////				// Java file. In this way, our visit function(s) can be automatically
////				// applied to all possible elements of the specified type(s).
////				codeModifier.visit(cu, null);
////				if(count<1 && !(fileName.equals("CodeModifierTest.java")) && !(fileName.equals("CodeModifier.java"))) {
////					count++;
////					CodeModifier codeModifier = new CodeModifier();
////
////					codeModifier.visit(cu, null);
////				}
//
//
////				File outputFile = fileInGeneratedTestSourcesDirectory(getPackageName(file.getAbsolutePath()), file.getName());
////				// Write the modified code to the new file
////				outputFile.getParentFile().mkdirs();
////				try (FileWriter outputWriter = new FileWriter(outputFile)) {
////					outputWriter.write(cu.toString());
////				}catch (IOException e) {
////					// Handle the exception
////					e.printStackTrace();
////				}
////					String p = System.getProperty("user.dir") + "/new_code";
////
////			try {
////			Path projectDir = Paths.get(p);
////			runMavenTests(projectDir);
//////			runSurefireReport(projectDir);
//////			runTestsAndGenerateReports(projectDir.toString());
////
////		} catch (IOException | InterruptedException e) {
////			e.printStackTrace();
////		}
//						}
						}
					}
				}
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
		//generate a random number to set the number of mutants, and have a for loop to generate that many mutants TODO:
		//lets keep a copy of dir
		cm.deleteFolderContents(new File(System.getProperty("user.dir")+"/report/"));
		cm.getJavaFiles();
		// copy the surefire from target/site/surefire-report.html to a report folder
		//runMavenTests(1);



	}
}
