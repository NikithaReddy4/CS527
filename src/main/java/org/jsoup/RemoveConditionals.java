package org.jsoup;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.Range;

import java.util.*;

import static com.github.javaparser.ast.type.PrimitiveType.Primitive.BOOLEAN;

//have separate classes for each operator

public class RemoveConditionals extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public RemoveConditionals(CompilationUnit originalCu) {
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

    public void visit(String filename) {
        if(!mutated) {

            mutated = true;
            List<IfStmt> exprList = originalCu.findAll(IfStmt.class);
           // System.out.println("***************************"+Arrays.toString(exprList.toArray()));
            for (IfStmt ifSmt : exprList) {
                if (ifSmt.getCondition() instanceof BinaryExpr) {
                    BinaryExpr condition = (BinaryExpr) ifSmt.getCondition();
                    CompilationUnit mutant = originalCu.clone();
                    IfStmt mutantIfStmt = mutant.findFirst(IfStmt.class, stmt -> stmt.equals(ifSmt)).orElse(null);
                    IfStmt before_Expr = mutantIfStmt;
                    IfStmt after_Expr = mutantIfStmt;

                    if (condition.getOperator() == BinaryExpr.Operator.EQUALS || condition.getOperator() == BinaryExpr.Operator.NOT_EQUALS) {
                        if (mutantIfStmt != null) {
                            before_Expr = ifSmt;
                            mutantIfStmt.replace(mutantIfStmt, mutantIfStmt.getThenStmt());
                            after_Expr = mutantIfStmt;
                            mutants.add(mutant);
                            mutantexp_withmutants.put(mutant, "The if statement " + ifSmt.toString() + " changed to " + mutantIfStmt.toString() + " after mutation.");
                        }
                    }
                }
            }
        }
    }

}