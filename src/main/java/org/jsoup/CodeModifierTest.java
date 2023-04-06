package org.jsoup;


import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.ast.PackageDeclaration;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
public class CodeModifierTest
{
	public static File fileInGeneratedTestSourcesDirectory(String packageName, String fileName) {
		String packagePath = packageName.replace(".", "/");
		String testDirPath = System.getProperty("user.dir") + "/new_code";
		String filePath = testDirPath + "/" + packagePath + "/" + fileName;
		return new File(filePath);
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
	

	public void testJsoup() {
		// Initialize the source root as the "target/test-classes" dir, which
		// includes the test resource information (i.e., the source code info
		// for Jsoup for this assignment) copied from src/test/resources
		// during test execution
		SourceRoot sourceRoot = new SourceRoot(
				CodeGenerationUtils.mavenModuleRoot(CodeModifierTest.class)
						.resolve("target/classes"));
		String testDirPath = System.getProperty("user.dir")+"/src/main/java/org/jsoup";
		String dir_path="";
		Path path = Paths.get(testDirPath);
		List<String> javaFileNames = new ArrayList<>();
		File directory = new File(testDirPath);
		File[] files = directory.listFiles();
		for (File file : files) {
			dir_path="";
			if(file.isDirectory())
			{
				dir_path=testDirPath+"/"+file.getName();
				File dir_file = new File(dir_path);
				File[] dir_files = dir_file.listFiles();
				List<String> filePaths = new ArrayList<>();
				if (dir_files != null) {
				for (File d_file : dir_files) {
					if (d_file.isFile() && d_file.getName().endsWith(".java")) {
					String filePath = d_file.getAbsolutePath();
					String packageName = getPackageName(filePath);
					String fileName = d_file.getName();
					CompilationUnit cu = sourceRoot.parse(dir_path,fileName);
					System.out.println(packageName+"/"+fileName);
					CodeModifier codeModifier = new CodeModifier();
					codeModifier.visit(cu, null);
					File outputFile = fileInGeneratedTestSourcesDirectory(packageName, fileName);
        			outputFile.getParentFile().mkdirs();
					try (FileWriter outputWriter = new FileWriter(outputFile)) {
					outputWriter.write(cu.toString());
					}catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	
	}

}
else if (file.isFile() && file.getName().endsWith(".java")) {
					System.out.println(getPackageName(file.getAbsolutePath())+"  "+file.getName());
					CompilationUnit cu = sourceRoot.parse(testDirPath,file.getName());
					// Instantiate the CodeModifier class that you will implement to perform
					// the actual task. This is a visitor class according to the visitor
					// pattern (one of the most important design patterns).
					CodeModifier codeModifier = new CodeModifier();
					// Apply our visitor class on the code representation for the given
					// Java file. In this way, our visit function(s) can be automatically
					// applied to all possible elements of the specified type(s).
					codeModifier.visit(cu, null);
					File outputFile = fileInGeneratedTestSourcesDirectory(getPackageName(file.getAbsolutePath()), file.getName());
					// Write the modified code to the new file
        			outputFile.getParentFile().mkdirs();
					try (FileWriter outputWriter = new FileWriter(outputFile)) {
					outputWriter.write(cu.toString());
					}catch (IOException e) {
					// Handle the exception
					e.printStackTrace();
}}
}
}
	public static void main(String[] args) throws IOException {
	CodeModifierTest cm=new CodeModifierTest();
	cm.testJsoup();
	}
}