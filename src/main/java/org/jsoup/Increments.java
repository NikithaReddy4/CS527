package org.jsoup;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//have separate classes for each operator

public class Increments extends VoidVisitorAdapter<String>
{
	private final CompilationUnit originalCu;
	private final List<CompilationUnit> mutants = new ArrayList<>();

	private final Map< CompilationUnit, String> mutantexp_withmutants = new HashMap<>();
	public boolean mutated = false;
	private int mutant = 0;
	public Increments(CompilationUnit originalCu) {
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
			List<UnaryExpr> exprList = originalCu.findAll(UnaryExpr.class);
			NodeList<UnaryExpr> exprNodeList = new NodeList<>();
			exprNodeList.addAll(exprList);

			for (UnaryExpr unaryExpr : exprNodeList) {
				CompilationUnit mutant = originalCu.clone();
				UnaryExpr mutantExpr = mutant.findFirst(UnaryExpr.class, expr -> expr.equals(unaryExpr)).orElse(null);
				UnaryExpr before_Expr = mutantExpr;
				UnaryExpr after_Expr = mutantExpr;

				if (unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_INCREMENT) {  // ++a to --a
					before_Expr = unaryExpr;
					mutantExpr.setOperator(UnaryExpr.Operator.PREFIX_DECREMENT);
					after_Expr = mutantExpr;
					mutants.add(mutant);
					mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
				} else if (unaryExpr.getOperator() == UnaryExpr.Operator.PREFIX_DECREMENT) {  // --a to ++a
					before_Expr = unaryExpr;
					mutantExpr.setOperator(UnaryExpr.Operator.PREFIX_INCREMENT);
					after_Expr = mutantExpr;
					mutants.add(mutant);
					mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
				} else if (unaryExpr.getOperator() == UnaryExpr.Operator.POSTFIX_INCREMENT) {  // a++ to a--
					before_Expr = unaryExpr;
					mutantExpr.setOperator(UnaryExpr.Operator.POSTFIX_DECREMENT);
					after_Expr = mutantExpr;
					mutants.add(mutant);
					mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
				} else if (unaryExpr.getOperator() == UnaryExpr.Operator.POSTFIX_DECREMENT) {  // a-- to a++
					before_Expr = unaryExpr;
					mutantExpr.setOperator(UnaryExpr.Operator.POSTFIX_INCREMENT);
					after_Expr = mutantExpr;
					mutants.add(mutant);
					mutantexp_withmutants.put(mutant, "The expression " + before_Expr.toString() + " changed to " + after_Expr.toString() + " after mutation.");
				}
			}
		}
	}




	// The following three visit functions are used to remove all comments to
	// facilicate the comparison with the expected version; please do not change


}