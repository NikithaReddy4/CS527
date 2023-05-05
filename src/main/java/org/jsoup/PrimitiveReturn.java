package org.jsoup;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.Range;

import java.util.*;

import static com.github.javaparser.ast.type.PrimitiveType.Primitive.*;

//have separate classes for each operator

public class PrimitiveReturn extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public PrimitiveReturn(CompilationUnit originalCu) {
        this.originalCu = originalCu;
    }
    public List<CompilationUnit> getMutants() {
        return mutants;
    }

    public Map<CompilationUnit, String> getMutantsMap() {
        return mutantexp_withmutants;
    }

    /**
     * This visit function will be automatically applied to all binary
     * expressions in the given Java file
     */
    @Override
    public void visit(MethodDeclaration n, String filename) {
        super.visit(n, filename);

        if (n.getType().isPrimitiveType() && (n.getType().asPrimitiveType().getType() == INT ||n.getType().asPrimitiveType().getType() == SHORT || n.getType().asPrimitiveType().getType() == CHAR || n.getType().asPrimitiveType().getType() == FLOAT || n.getType().asPrimitiveType().getType() == LONG || n.getType().asPrimitiveType().getType() == DOUBLE)) {
            // mutated = true;
            BlockStmt body = n.getBody().orElse(null);
            if (body != null) {
                body.findAll(ReturnStmt.class).forEach(returnStmt -> {
                    Expression expr = returnStmt.getExpression().orElse(null);
                    if (expr != null && expr.isIntegerLiteralExpr()) {
                            CompilationUnit mutant = originalCu.clone();
                            ReturnStmt mutantReturnStmt = mutant.findFirst(ReturnStmt.class, stmt -> stmt.equals(returnStmt)).orElse(null);
                            if (mutantReturnStmt != null) {
                                mutantReturnStmt.setExpression(new IntegerLiteralExpr("0"));
                                mutants.add(mutant);
                                mutantexp_withmutants.put(mutant, "The expression " + returnStmt.toString() + " changed to " + mutantReturnStmt.toString() + " after mutation.");
                            }
                    }else if (expr != null && expr.isLongLiteralExpr()) {
                        CompilationUnit mutant = originalCu.clone();
                        ReturnStmt mutantReturnStmt = mutant.findFirst(ReturnStmt.class, stmt -> stmt.equals(returnStmt)).orElse(null);
                        if (mutantReturnStmt != null) {
                            mutantReturnStmt.setExpression(new LongLiteralExpr("0"));
                            mutants.add(mutant);
                            mutantexp_withmutants.put(mutant, "The expression " + returnStmt.toString() + " changed to " + mutantReturnStmt.toString() + " after mutation.");
                        }
                    }else if (expr != null && expr.isDoubleLiteralExpr()) {
                        CompilationUnit mutant = originalCu.clone();
                        ReturnStmt mutantReturnStmt = mutant.findFirst(ReturnStmt.class, stmt -> stmt.equals(returnStmt)).orElse(null);
                        if (mutantReturnStmt != null) {
                            mutantReturnStmt.setExpression(new DoubleLiteralExpr("0"));
                            mutants.add(mutant);
                            mutantexp_withmutants.put(mutant, "The expression " + returnStmt.toString() + " changed to " + mutantReturnStmt.toString() + " after mutation.");
                        }
                    }else if (expr != null && expr.isCharLiteralExpr()) {
                        CompilationUnit mutant = originalCu.clone();
                        ReturnStmt mutantReturnStmt = mutant.findFirst(ReturnStmt.class, stmt -> stmt.equals(returnStmt)).orElse(null);
                        if (mutantReturnStmt != null) {
                            mutantReturnStmt.setExpression(new CharLiteralExpr("0"));
                            mutants.add(mutant);
                            mutantexp_withmutants.put(mutant, "The expression " + returnStmt.toString() + " changed to " + mutantReturnStmt.toString() + " after mutation.");
                        }
                    }
                });
            }
        }
    }



}