package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//have separate classes for each operator

public class OBBN1 extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public OBBN1(CompilationUnit originalCu) {
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
             if(mutantExpr!=null) {
                 if (binaryExpr.getOperator() == BinaryExpr.Operator.BINARY_AND) {
                     mutantExpr.setOperator(BinaryExpr.Operator.BINARY_OR);
                     mutants.add(mutant);
                     mutantexp_withmutants.put(mutant, "The expression " + binaryExpr.toString() + " was changed to " + mutantExpr.toString() + " after mutation.");
                 } else if (binaryExpr.getOperator() == BinaryExpr.Operator.BINARY_OR) {
                     mutantExpr.setOperator(BinaryExpr.Operator.BINARY_AND);
                     mutants.add(mutant);
                     mutantexp_withmutants.put(mutant, "The expression " + binaryExpr.toString() + " was changed to " + mutantExpr.toString() + " after mutation.");
                 }
             }
            }
        }
    }






    // The following three visit functions are used to remove all comments to
    // facilicate the comparison with the expected version; please do not change


}