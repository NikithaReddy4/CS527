package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//have separate classes for each operator

public class NegateConditions extends VoidVisitorAdapter<String>
{
	private final CompilationUnit originalCu;
	private final List<CompilationUnit> mutants = new ArrayList<>();

	private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
	public boolean mutated = false;
	private int mutant = 0;
	public NegateConditions(CompilationUnit originalCu) {
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
			 BinaryExpr before_Expr = mutantExpr;
			 BinaryExpr after_Expr = mutantExpr;
			 if (binaryExpr.getOperator()==BinaryExpr.Operator.NOT_EQUALS ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.EQUALS);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }else if (binaryExpr.getOperator()==BinaryExpr.Operator.EQUALS ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.NOT_EQUALS);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }else if (binaryExpr.getOperator()==BinaryExpr.Operator.LESS_EQUALS ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.GREATER);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }else if (binaryExpr.getOperator()==BinaryExpr.Operator.GREATER_EQUALS ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.LESS);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }else if (binaryExpr.getOperator()==BinaryExpr.Operator.LESS ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.GREATER_EQUALS);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }else if (binaryExpr.getOperator()==BinaryExpr.Operator.GREATER ) {
				 before_Expr = binaryExpr;
				 mutantExpr.setOperator(BinaryExpr.Operator.LESS_EQUALS);
				 after_Expr = mutantExpr;
				 mutants.add(mutant);
				 mutantexp_withmutants.put(mutant, "The expression "+ before_Expr.toString()+ " changed to "+ after_Expr.toString()+" after mutation.");
			 }
		 }
	 }
	}

	

	// The following three visit functions are used to remove all comments to
	// facilicate the comparison with the expected version; please do not change


}