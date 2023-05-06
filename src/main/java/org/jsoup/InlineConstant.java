package org.jsoup;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
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

import static com.github.javaparser.ast.type.PrimitiveType.Primitive.BOOLEAN;

//have separate classes for each operator

public class InlineConstant extends VoidVisitorAdapter<String>
{
    private final CompilationUnit originalCu;
    private final List<CompilationUnit> mutants = new ArrayList<>();

    private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
    public boolean mutated = false;
    private int mutant = 0;
    public InlineConstant(CompilationUnit originalCu) {
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
    public void visit(BinaryExpr n,String filename) {
        if (!mutated) {
            mutated = true;
            List<VariableDeclarationExpr> exprList = originalCu.findAll(VariableDeclarationExpr.class);
            NodeList<VariableDeclarationExpr> exprNodeList = new NodeList<>();
            exprNodeList.addAll(exprList);

      //  super.visit(n, filename);
            for (VariableDeclarationExpr varDeclExpr : exprNodeList) {
        if (!varDeclExpr.isFinal()) {
            NodeList<VariableDeclarator> vars = varDeclExpr.getVariables();
            for (VariableDeclarator var : vars) {
                Expression initExpr = var.getInitializer().orElse(null);
                if (initExpr != null) {
                    if (initExpr instanceof BooleanLiteralExpr) {
                        CompilationUnit mutant = originalCu.clone();
                        VariableDeclarator mutantExpr = mutant.findFirst(VariableDeclarator.class, expr -> expr.equals(var)).orElse(null);
                        if (mutantExpr != null) {
                        Expression temp = mutantExpr.getInitializer().orElse(null);
                            BooleanLiteralExpr boolExpr = (BooleanLiteralExpr) temp;
                            if(boolExpr!=null) {
                                boolean boolValue = boolExpr.getValue();
                                boolean mutatedValue = !boolValue;
                                BooleanLiteralExpr temp2 = new BooleanLiteralExpr(mutatedValue);
                                mutantExpr.setInitializer(temp2);
                                mutants.add(mutant);
                                String message = String.format("The boolean constant %s changed to %s after mutation.", boolValue, mutatedValue);
                                mutantexp_withmutants.put(mutant, message);
                            }
                        }
//                    } else if (initExpr instanceof CharLiteralExpr) {
//                        CharLiteralExpr charExpr = (CharLiteralExpr) initExpr;
//                        String charValue = charExpr.getValue();
//                        String mutatedValue = Character.toString((char) (charValue.charAt(0) + 1));
//                        CharLiteralExpr mutatedExpr = new CharLiteralExpr(mutatedValue);
//                        var.setInitializer(mutatedExpr);
//                        mutants.add(originalCu.clone());
//                        String message = String.format("The char constant '%s' changed to '%s' after mutation.", charValue, mutatedValue);
//                        mutantexp_withmutants.put(mutants.get(mutants.size() - 1), message);
                    } else if (initExpr instanceof DoubleLiteralExpr) {

                        CompilationUnit mutant = originalCu.clone();
                        VariableDeclarator mutantExpr = mutant.findFirst(VariableDeclarator.class, expr -> expr.equals(var)).orElse(null);
                        if (mutantExpr != null) {
                            Expression temp = mutantExpr.getInitializer().orElse(null);
                            DoubleLiteralExpr doubleExpr = (DoubleLiteralExpr) temp;
                            if(doubleExpr!=null) {
                                double doubleValue = doubleExpr.asDouble();
                                double mutatedValue = 0.0;
                                if(doubleValue == 1.0){
                                   mutatedValue = 0.0;
                                }else{
                                    mutatedValue = 1.0;
                                }
                                DoubleLiteralExpr temp2 = new DoubleLiteralExpr(Double.toString(mutatedValue));
                                mutantExpr.setInitializer(temp2);
                                mutants.add(mutant);
                                String message = String.format("The boolean constant %s changed to %s after mutation.", doubleValue, mutatedValue);
                                mutantexp_withmutants.put(mutant, message);
                            }
                        }

//                        DoubleLiteralExpr doubleExpr = (DoubleLiteralExpr) initExpr;
//                        double doubleValue = doubleExpr.asDouble();
//                        double mutatedValue = doubleValue + 1.0;
//                        DoubleLiteralExpr mutatedExpr = new DoubleLiteralExpr(Double.toString(mutatedValue));
//                        var.setInitializer(mutatedExpr);
//                        mutants.add(originalCu.clone());
//                        String message = String.format("The double constant %s changed to %s after mutation.", doubleValue, mutatedValue);
//                        mutantexp_withmutants.put(mutants.get(mutants.size() - 1), message);
                    } else if (initExpr instanceof IntegerLiteralExpr) {

                        CompilationUnit mutant = originalCu.clone();
                        VariableDeclarator mutantExpr = mutant.findFirst(VariableDeclarator.class, expr -> expr.equals(var)).orElse(null);
                        if (mutantExpr != null) {
                            Expression temp = mutantExpr.getInitializer().orElse(null);
                            IntegerLiteralExpr integerExpr = (IntegerLiteralExpr) temp;
                            if(integerExpr!=null) {
                                int integerValue = integerExpr.asNumber().intValue();
                                int mutatedValue = -99;
                                if(integerValue == 1){
                                    mutatedValue = 0;
                                }else if(integerValue == -1){
                                    mutatedValue = 1;
                                }else if(integerValue == 5){
                                    mutatedValue = -1;
                                }else{
                                    mutatedValue += 1;
                                }
                                IntegerLiteralExpr temp2 = new IntegerLiteralExpr(Integer.toString(mutatedValue));
                                mutantExpr.setInitializer(temp2);
                                mutants.add(mutant);
                                String message = String.format("The boolean constant %s changed to %s after mutation.", integerValue, mutatedValue);
                                mutantexp_withmutants.put(mutant, message);
                            }
                        }

//                        IntegerLiteralExpr intExpr = (IntegerLiteralExpr) initExpr;
//                        int intValue = intExpr.asInt();
//                        int mutatedValue = intValue + 1;
//                        IntegerLiteralExpr mutatedExpr = new IntegerLiteralExpr(Integer.toString(mutatedValue));
//                        var.setInitializer(mutatedExpr);
//                        mutants.add(originalCu.clone());
//                        String message = String.format("The int constant %s changed to %s after mutation.", intValue, mutatedValue);
//                        mutantexp_withmutants.put(mutants.get(mutants.size() - 1), message);
                    }else if (initExpr instanceof LongLiteralExpr) {

                        CompilationUnit mutant = originalCu.clone();
                        VariableDeclarator mutantExpr = mutant.findFirst(VariableDeclarator.class, expr -> expr.equals(var)).orElse(null);
                        if (mutantExpr != null) {
                            Expression temp = mutantExpr.getInitializer().orElse(null);
                            LongLiteralExpr longExpr = (LongLiteralExpr) temp;
                            if(longExpr!=null) {
                                long longValue = longExpr.asNumber().longValue();
                                long mutatedValue = -99;
                                if(longValue == 1){
                                    mutatedValue = 0;
                                }else{
                                    mutatedValue += 1;
                                }
                                LongLiteralExpr temp2 = new LongLiteralExpr(Long.toString(mutatedValue));
                                mutantExpr.setInitializer(temp2);
                                mutants.add(mutant);
                                String message = String.format("The boolean constant %s changed to %s after mutation.", longValue, mutatedValue);
                                mutantexp_withmutants.put(mutant, message);
                            }
                        }

                    }
                }
            }
          }
        }
    }



    }
}