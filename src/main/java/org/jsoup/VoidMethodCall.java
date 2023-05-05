package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.quality.NotNull;

import java.util.*;

//have separate classes for each operator

public class VoidMethodCall extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public VoidMethodCall(CompilationUnit originalCu) {
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
    public void visit(MethodDeclaration n, String filename) {
      //  if(!mutated)
      //  {
       //     mutated=true;
            List<MethodCallExpr> exprList = originalCu.findAll(MethodCallExpr.class);
          //  System.out.println("************************************ "+Arrays.toString(exprList.toArray()));
            List<String> MethodNames = new ArrayList<>();
            for (MethodDeclaration method : originalCu.findAll(MethodDeclaration.class)) {
                if (method.getType().isVoidType()) {
                    MethodNames.add(method.getNameAsString());
                }
            }
         //   System.out.println("************************************ "+Arrays.toString(MethodNames.toArray()));
            for (MethodCallExpr methodExpr : exprList) {
                CompilationUnit mutant = originalCu.clone();
              //  MethodDeclaration mutantmethodExpr = mutant.findFirst(MethodDeclaration.class, stmt -> stmt.equals(n)).orElse(null);
             //   if(mutantmethodExpr!=null){
                    String methodName = methodExpr.getNameAsString();
                    if (methodName != null && MethodNames.contains(methodName)) {
                        for (Statement s : n.getBody().get().getStatements()){
                            if(s.toString().contains(methodName)){
                                    s.remove();
                                   mutants.add(mutant);
                                   mutantexp_withmutants.put(originalCu.clone(), "The method call " + methodExpr.toString() + " was removed after mutation.");
                                    break;
                            }

                    }
                }

            }
    }


}



