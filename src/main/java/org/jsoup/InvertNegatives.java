package org.jsoup;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.model.SymbolReference;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.util.*;

//have separate classes for each operator

public class InvertNegatives extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public InvertNegatives(CompilationUnit originalCu) {
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
    public void visit( BinaryExpr n,String filename) {

        if (!mutated) {
            mutated = true;
            List<UnaryExpr> exprList = originalCu.findAll(UnaryExpr.class);
            NodeList<UnaryExpr> exprNodeList = new NodeList<>();
            exprNodeList.addAll(exprList);
            for (UnaryExpr unaryExpr : exprNodeList) {

            if (unaryExpr.getOperator() == UnaryExpr.Operator.MINUS && (unaryExpr.getExpression().isDoubleLiteralExpr() || unaryExpr.getExpression().isIntegerLiteralExpr() || unaryExpr.getExpression().isLongLiteralExpr())) {
                return; // Do not mutate negative constants
            }
            if (unaryExpr.getOperator() == UnaryExpr.Operator.LOGICAL_COMPLEMENT) {
                return; // Do not mutate logical complement operator
            }
            if (unaryExpr.getOperator() == UnaryExpr.Operator.MINUS) {
                Expression operand = unaryExpr.getExpression();
                CompilationUnit mutant = originalCu.clone();
                UnaryExpr mutantExpr = mutant.findFirst(UnaryExpr.class, expr -> expr.equals(unaryExpr)).orElse(null);
                if (mutantExpr != null) {
                    mutantExpr.replace(mutantExpr.getExpression());
                    mutants.add(mutant);
                    mutantexp_withmutants.put(mutant, "The expression " + unaryExpr.toString() + " changed to " + mutantExpr.toString() + " after mutation.");
                }
            }
            }
        }
      }
    }










    // The following three visit functions are used to remove all comments to
    // facilicate the comparison with the expected version; please do not change


