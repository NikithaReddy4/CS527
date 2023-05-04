package org.jsoup;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.Range;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

//have separate classes for each operator

public class CodeModifier extends VoidVisitorAdapter<String>
{
	private final CompilationUnit originalCu;
	private final List<CompilationUnit> mutants = new ArrayList<>();
	public boolean mutated = false;
	private int mutant = 0;
	public CodeModifier(CompilationUnit originalCu) {
		this.originalCu = originalCu;
	}
	public List<CompilationUnit> getMutants() {
		return mutants;
	}

	/**
	 * This visit function will be automatically applied to all binary
	 * expressions in the given Java file
	 */
	@Override
	public void visit(BinaryExpr n, String filename) {
		super.visit(n, filename);

//	int flag=0;
	if(!mutated)

     {
		 mutated=true;
		 List<BinaryExpr> exprList = originalCu.findAll(BinaryExpr.class);
		 NodeList<BinaryExpr> exprNodeList = new NodeList<>();
		 exprNodeList.addAll(exprList);
		 for (BinaryExpr binaryExpr : exprNodeList) {
			 CompilationUnit mutant = originalCu.clone();
			BinaryExpr mutantExpr = mutant.findFirst(BinaryExpr.class, expr -> expr.equals(binaryExpr)).orElse(null);
			 if (binaryExpr.getOperator()==BinaryExpr.Operator.NOT_EQUALS ) {
				 mutantExpr.setOperator(BinaryExpr.Operator.EQUALS);
				 mutants.add(mutant);
			 }
		 }
	 }
//    else if(flag!=1 && n.getOperator() ==BinaryExpr.Operator.NOT_EQUALS && !mutated)
//     {
//		 modified++;
//		System.out.println("Modification on "+"\""+n);
//        n.setOperator(BinaryExpr.Operator.EQUALS);
//		 mutated = true;
//		 return; // exit the function
//     }
//
//
//	if(modified!=0)
//		System.out.println("Modified statement "+"\""+n);
//		MutationContext context = MutationContext.builder()
//				.withTargetClassName("com.example.MyClass")
//				.withMutators(new ArithmeticOperatorReplacementMutator())
//				.build();
	}

	

	// The following three visit functions are used to remove all comments to
	// facilicate the comparison with the expected version; please do not change


}