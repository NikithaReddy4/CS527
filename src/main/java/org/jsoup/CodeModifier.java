package org.jsoup;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.Range;


public class CodeModifier extends VoidVisitorAdapter
{

	/**
	 * This visit function will be automatically applied to all binary
	 * expressions in the given Java file
	 */
	@Override
	public void visit(BinaryExpr n, Object arg) {
		super.visit(n, arg);
		int modified=0;
	int flag=0;
	if(n.getOperator() ==BinaryExpr.Operator.EQUALS)
     {
		flag=1;
		System.out.println("Modification on "+"\""+n);
		n.setOperator(BinaryExpr.Operator.NOT_EQUALS);
		 modified++;
	 }
    else if(flag!=1 && n.getOperator() ==BinaryExpr.Operator.NOT_EQUALS)
     {
		 modified++;
		System.out.println("Modification on "+"\""+n);
        n.setOperator(BinaryExpr.Operator.EQUALS);
     }
	if(n.getOperator() ==BinaryExpr.Operator.EQUALS)
		{
			flag=1;
			System.out.println("Modification on "+"\""+n);
			n.setOperator(BinaryExpr.Operator.NOT_EQUALS);
			modified++;
		}

	if(modified!=0)
		System.out.println("Modified statement "+"\""+n);
	}

	

	// The following three visit functions are used to remove all comments to
	// facilicate the comparison with the expected version; please do not change
	@Override
	public void visit(LineComment n, Object arg) {
		n.remove();
	}

	@Override
	public void visit(BlockComment n, Object arg) {
		n.remove();
	}

	@Override
	public void visit(JavadocComment n, Object arg) {
		n.remove();
	}

}