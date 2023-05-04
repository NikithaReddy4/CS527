package org.jsoup;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.Range;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class MutantVisitor extends VoidVisitorAdapter<String> {

    private int numMutants = 0;
    private int numKilledMutants = 0;


    @Override
    public void visit(BinaryExpr n, String fileName) {

        if (n.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
            BinaryExpr mutant = n.clone();
            mutant.setOperator(BinaryExpr.Operator.EQUALS);
            String mutantFileName = fileName.substring(0, fileName.lastIndexOf('.')) + "_mutant" + numMutants + ".java";
            File mutantFile = new File(mutantFileName);
            try (BufferedReader br = new BufferedReader(new FileReader(mutantFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
            try {
                FileUtils.writeStringToFile(mutantFile, mutant.toString(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
            numMutants++;
            runMvnTest(mutantFile);
        }
        super.visit(n, fileName);
    }

    private void runMvnTest(File mutantFile) {
        try {
            System.out.println("mvn test");
            ProcessBuilder pb = new ProcessBuilder("mvn", "test");
            pb.directory(mutantFile.getParentFile());
            Process p = pb.start();
            int exitValue = p.waitFor();
            if (exitValue == 0) {
                System.out.println(mutantFile.getName() + " survived!");
            } else {
                System.out.println(mutantFile.getName() + " killed!");
                numKilledMutants++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//private void runMvnTest(File mutatedFile) {
//    try {
//        ProcessBuilder pb = new ProcessBuilder("mvn", "test");
//        pb.directory(new File("."));
//        pb.redirectErrorStream(true);
//        pb.redirectOutput(ProcessBuilder.Redirect.PIPE);
//        pb.redirectInput(mutatedFile);
//        Process p = pb.start();
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String line;
//        boolean testPassed = true;
//        while ((line = reader.readLine()) != null) {
//            if (line.contains("Tests in error") || line.contains("Tests run: 0, Failures: 0, Errors: 0, Skipped: 0")) {
//                testPassed = true;
//                break;
//            } else if (line.contains("Failed tests:")) {
//                testPassed = false;
//                break;
//            }
//        }
//
//        p.waitFor();
//        if (testPassed) {
//            System.out.println(String.format("Mutant %s was killed.", mutatedFile.getName()));
//        } else {
//            System.out.println(String.format("Mutant %s survived.", mutatedFile.getName()));
//        }
//    } catch (IOException | InterruptedException e) {
//        e.printStackTrace();
//    }
//}

}

