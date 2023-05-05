package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//have separate classes for each operator
//< <=
//<= <
//> >=
//>= >
public class AORVisitor extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;

    public AORVisitor(CompilationUnit originalCu) {
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
        if (!mutated) {
            mutated = true;
            List<BinaryExpr> exprList = originalCu.findAll(BinaryExpr.class);
            NodeList<BinaryExpr> exprNodeList = new NodeList<>();
            exprNodeList.addAll(exprList);
            for (BinaryExpr binaryExpr : exprNodeList) {
                CompilationUnit mutant = originalCu.clone();
                BinaryExpr mutantExpr = mutant.findFirst(BinaryExpr.class, expr -> expr.equals(binaryExpr)).orElse(null);
                BinaryExpr before_Expr = mutantExpr;
                BinaryExpr after_Expr = mutantExpr;
                if(mutantExpr!=null) {
                    if (binaryExpr.getOperator() == BinaryExpr.Operator.PLUS) {
                        before_Expr = binaryExpr;
                        mutantExpr.setOperator(BinaryExpr.Operator.MINUS);
                        after_Expr = mutantExpr;
                        mutants.add(mutant);
                        mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
                    } else if (binaryExpr.getOperator() == BinaryExpr.Operator.MINUS) {
                        before_Expr = binaryExpr;
                        mutantExpr.setOperator(BinaryExpr.Operator.PLUS);
                        after_Expr = mutantExpr;
                        mutants.add(mutant);
                        mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
                    } else if (binaryExpr.getOperator() == BinaryExpr.Operator.MULTIPLY) {
                        before_Expr = binaryExpr;
                        mutantExpr.setOperator(BinaryExpr.Operator.DIVIDE);
                        after_Expr = mutantExpr;
                        mutants.add(mutant);
                        mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
                    } else if (binaryExpr.getOperator() == BinaryExpr.Operator.DIVIDE) {
                        before_Expr = binaryExpr;
                        mutantExpr.setOperator(BinaryExpr.Operator.MULTIPLY);
                        after_Expr = mutantExpr;
                        mutants.add(mutant);
                        mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
                    } else if (binaryExpr.getOperator() == BinaryExpr.Operator.REMAINDER) {
                        before_Expr = binaryExpr;
                        mutantExpr.setOperator(BinaryExpr.Operator.MULTIPLY);
                        after_Expr = mutantExpr;
                        mutants.add(mutant);
                        mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
                    }
                }
            }
        }
    }


}