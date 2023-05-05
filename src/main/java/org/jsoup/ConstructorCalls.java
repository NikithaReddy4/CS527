package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

//have separate classes for each operator

public class ConstructorCalls extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public ConstructorCalls(CompilationUnit originalCu) {
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
    public void visit(ObjectCreationExpr n, String filename) {
        super.visit(n, filename);
        CompilationUnit mutant = originalCu.clone();
        ObjectCreationExpr before = n;
        ObjectCreationExpr mutantExpr = mutant.findFirst(ObjectCreationExpr.class, expr -> expr.equals(n.clone())).orElse(null);
        if (mutantExpr != null) {
            mutantExpr.replace(new NullLiteralExpr());
            mutants.add(mutant);
            mutantexp_withmutants.put(mutant, "The expression " + before.toString() + " changed to " + new NullLiteralExpr().toString() + " after mutation.");
        }
    }







    // The following three visit functions are used to remove all comments to
    // facilicate the comparison with the expected version; please do not change


}