package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//have separate classes for each operator

public class AssignIncrements extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public AssignIncrements(CompilationUnit originalCu) {
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
    public void visit(AssignExpr n, String filename) {
            super.visit(n, filename);

        if (!mutated && n.getOperator() == AssignExpr.Operator.PLUS) {
            mutated = true;
            CompilationUnit mutant = originalCu.clone();
            AssignExpr mutantExpr = mutant.findFirst(AssignExpr.class, expr -> expr.equals(n)).orElse(null);
            if (mutantExpr != null) {
                mutantExpr.setOperator(AssignExpr.Operator.MINUS);
                mutants.add(mutant);
                mutantexp_withmutants.put(mutant, "The expression " + n.toString() + " changed to " + mutantExpr.toString() + " after mutation.");
            }
        } else if (!mutated && n.getOperator() == AssignExpr.Operator.MINUS) {
            mutated = true;
            CompilationUnit mutant = originalCu.clone();
            AssignExpr mutantExpr = mutant.findFirst(AssignExpr.class, expr -> expr.equals(n)).orElse(null);
            if (mutantExpr != null) {
                mutantExpr.setOperator(AssignExpr.Operator.PLUS);
                mutants.add(mutant);
                mutantexp_withmutants.put(mutant, "The expression " + n.toString() + " changed to " + mutantExpr.toString() + " after mutation.");
            }
        }


    }





    // The following three visit functions are used to remove all comments to
    // facilicate the comparison with the expected version; please do not change


}