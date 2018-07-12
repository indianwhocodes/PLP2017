package cop5556fa17;

import java.util.HashMap;
import java.util.Map;
import java.net.*;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_FunctionAppWithIndexArg;
import cop5556fa17.AST.Expression_Ident;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Sink;
import cop5556fa17.AST.Sink_Ident;
import cop5556fa17.AST.Sink_SCREEN;
import cop5556fa17.AST.Source;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_Ident;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;

public class TypeCheckVisitor implements ASTVisitor {


	@SuppressWarnings("serial")
	public static class SemanticException extends Exception {
		Token t;

		public SemanticException(Token t, String message) {
			super("line " + t.line + " pos " + t.pos_in_line + ": "+  message);
			this.t = t;
		}



	}

	public Map<String, Declaration> symbolTable = new HashMap<String, Declaration>();



	/**
	 * The program name is only used for naming the class.  It does not rule out
	 * variables with the same name.  It is returned for convenience.
	 * 
	 * @throws Exception 
	 */
	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		for (ASTNode node: program.decsAndStatements) {
			node.visit(this, arg);
		}

		return program.name;
	}

	@Override
	public Object visitDeclaration_Variable(
			Declaration_Variable declaration_Variable, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression e = declaration_Variable.e;

		String name = declaration_Variable.name;
		if(symbolTable.get(name) == null) {
			
			declaration_Variable.localtype = TypeUtils.getType(declaration_Variable.firstToken);
			if(e!= null) {
				e.visit(this, arg);
				if(declaration_Variable.localtype != e.localtype) {
					throw new SemanticException(declaration_Variable.firstToken, "Error occured at declaration_Variable");
				}
			}
			symbolTable.put(name, declaration_Variable);
			
		}
		else {
			throw new SemanticException(declaration_Variable.firstToken, "Error occured at symboltable lookup");
		}
		return declaration_Variable.localtype;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = expression_Binary.e0;
		e0.visit(this, arg);
		Expression e1 = expression_Binary.e1;
		e1.visit(this, arg);
		Kind op = expression_Binary.op;

		if(e0.localtype == e1.localtype) {

			if(op == Kind.OP_NEQ || op == Kind.OP_EQ) {
				expression_Binary.localtype = Type.BOOLEAN;//
			}
			else if(op == Kind.OP_GE || op == Kind.OP_LE || op == Kind.OP_GT || op == Kind.OP_LT
					&& e0.localtype == Type.INTEGER) {
				expression_Binary.localtype = Type.BOOLEAN;//
			}
			else if((op == Kind.OP_AND || op == Kind.OP_OR )&& (e0.localtype == Type.INTEGER 
					|| e0.localtype == Type.BOOLEAN)) {
				expression_Binary.localtype = e0.localtype;//
			}
			else if((op == Kind.OP_DIV || op == Kind.OP_MINUS || op == Kind.OP_PLUS || op == Kind.OP_POWER
					|| op == Kind.OP_TIMES || op == Kind.OP_MOD) && e0.localtype == Type.INTEGER) {
				expression_Binary.localtype = Type.INTEGER;//
			}
			else
				expression_Binary.localtype = null;

		}
		else {
			throw new SemanticException(expression_Binary.firstToken, "Error occured at expression_Binary");
		}
		if(expression_Binary.localtype == null)
		{
			throw new SemanticException(expression_Binary.firstToken, "Error occured at expression_Binary");
		}

		return expression_Binary.localtype;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary,
			Object arg) throws Exception {
		// TODO Auto-generated method stub

		Expression e = expression_Unary.e;		
		Kind op = expression_Unary.op;

		e.visit(this, arg);
		if((op == Kind.OP_EXCL) &&(e.localtype == Type.BOOLEAN || e.localtype == Type.INTEGER)) {
			expression_Unary.localtype = e.localtype;
		}
		else if((op == Kind.OP_MINUS || op == Kind.OP_PLUS) && (e.localtype == Type.INTEGER)) {
			expression_Unary.localtype = Type.INTEGER;
		}
		else {
			expression_Unary.localtype = null;
		}
		if(expression_Unary.localtype == null) {
			throw new SemanticException(expression_Unary.firstToken, "Error occured at Expression_Unary");
		}

		return expression_Unary.localtype;

	}

	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e0 = index.e0;
		e0.visit(this, arg);
		Expression e1 = index.e1;
		e1.visit(this, arg);
		if(e0.localtype == Type.INTEGER && e1.localtype == Type.INTEGER) {
			index.setCartesian(!(e0.toString().contains("KW_r") && e1.toString().contains("KW_a")));
		}
		else {
			throw new SemanticException(index.firstToken, "Error occured at index");
		}
		return null; 
	}

	@Override
	public Object visitExpression_PixelSelector(
			Expression_PixelSelector expression_PixelSelector, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		String name = expression_PixelSelector.name;
		Index index = expression_PixelSelector.index;
		
		Type name_typ = symbolTable.get(name).localtype;
		

			if(index != null) {
				index.visit(this, arg);
			}
			
			if(name_typ == Type.IMAGE) {
				expression_PixelSelector.localtype = Type.INTEGER;
			}
			else if (index == null) {
				expression_PixelSelector.localtype = name_typ;
			}
			else {
				
				expression_PixelSelector.localtype = Type.NONE;
			}
	
		if (expression_PixelSelector.localtype == Type.NONE) {
			throw new SemanticException(expression_PixelSelector.firstToken, "Error occured at Expression_PixelSelector");
		}
		return expression_PixelSelector.localtype; 
	}

	@Override
	public Object visitExpression_Conditional(
			Expression_Conditional expression_Conditional, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression condition = expression_Conditional.condition;
		condition.visit(this, arg);
		Expression true_expr = expression_Conditional.trueExpression;
		true_expr.visit(this, arg);
		Expression false_expr = expression_Conditional.falseExpression;
		false_expr.visit(this, arg);

		if(condition.localtype == Type.BOOLEAN && true_expr.localtype == false_expr.localtype) {
			expression_Conditional.localtype = true_expr.localtype;
		}
		else {
			throw new SemanticException(expression_Conditional.firstToken, "Error occured at expression_Conditional");
		}

		return expression_Conditional.localtype;
	}

	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = declaration_Image.name;
		Expression xSize = declaration_Image.xSize;
		Expression ySize = declaration_Image.ySize;
		Source source = declaration_Image.source;
		if(source != null) {
			source.visit(this, arg);
		}
		if(xSize != null) {
			if(ySize != null && xSize.visit(this, arg) == Type.INTEGER && ySize.visit(this, arg) == Type.INTEGER ) {

			}
			else {
				throw new SemanticException(declaration_Image.firstToken, "Error occured at Declaration_Image");
			}

		}

		if(symbolTable.get(name) == null) {
			symbolTable.put(name, declaration_Image);
			declaration_Image.localtype = Type.IMAGE;
		}
		else {
			throw new SemanticException(declaration_Image.firstToken, "Symbol Table lookup already has name");
		}
		return declaration_Image.localtype;
	}

	@Override
	public Object visitSource_StringLiteral(
			Source_StringLiteral source_StringLiteral, Object arg)
					throws Exception {

		if(source_StringLiteral.isValidURL()) {
			source_StringLiteral.localtype = Type.URL;
		}
		else {
			source_StringLiteral.localtype = Type.FILE;
		}

		return source_StringLiteral.localtype;
	}

	@Override
	public Object visitSource_CommandLineParam(
			Source_CommandLineParam source_CommandLineParam, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		Expression paramnum = source_CommandLineParam.paramNum;
		paramnum.visit(this, arg);
		source_CommandLineParam.localtype = null;
		

		if(paramnum.localtype == Type.INTEGER) {
			
		}
		else {
			throw new SemanticException(source_CommandLineParam.firstToken, "Error occured at source_CommandLineParam");
		}

		return source_CommandLineParam.localtype;
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = source_Ident.name;
		
		if(symbolTable.containsKey(name)) {
			source_Ident.localtype = symbolTable.get(name).localtype;
		}else {
			throw new SemanticException(source_Ident.firstToken, "Error occured at source_Ident");
		}
		if(source_Ident.localtype == Type.FILE || source_Ident.localtype == Type.URL) {

		}
		else {
			throw new SemanticException(source_Ident.firstToken, "Error occured at source_Ident");
		}
		return source_Ident.localtype;
	}

	@Override
	public Object visitDeclaration_SourceSink(
			Declaration_SourceSink declaration_SourceSink, Object arg)
					throws Exception {
		// TODO Auto-generated method stub
		String name = declaration_SourceSink.name;
		Source source = declaration_SourceSink.source;


		if(symbolTable.get(name) == null) {
			symbolTable.put(name, declaration_SourceSink);
			declaration_SourceSink.localtype = TypeUtils.getType(declaration_SourceSink.firstToken);
			source.visit(this, arg);
			if(source.localtype == declaration_SourceSink.localtype || source.localtype == null) {

			}
			else {
				throw new SemanticException(declaration_SourceSink.firstToken, "Symbol Table lookup already has name");
			}
		}
		else {
			throw new SemanticException(declaration_SourceSink.firstToken, "Symbol Table lookup already has name");
		}

		return declaration_SourceSink.localtype;
	}

	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit,
			Object arg) throws Exception {

		expression_IntLit.localtype = Type.INTEGER;

		return expression_IntLit.localtype;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		Expression e = expression_FunctionAppWithExprArg.arg;
		e.visit(this, arg);
		if(e.localtype == Type.INTEGER) {
			expression_FunctionAppWithExprArg.localtype = Type.INTEGER;
		}
		else {
			throw new SemanticException(expression_FunctionAppWithExprArg.firstToken, "Error occured at expression_FunctionAppWithExprArg");
		}
		return expression_FunctionAppWithExprArg.localtype;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg,
			Object arg) throws Exception {
		
		expression_FunctionAppWithIndexArg.localtype = Type.INTEGER;

		return expression_FunctionAppWithIndexArg.localtype;
	}

	@Override
	public Object visitExpression_PredefinedName(
			Expression_PredefinedName expression_PredefinedName, Object arg)
					throws Exception {
		
		expression_PredefinedName.localtype = Type.INTEGER;

		return expression_PredefinedName.localtype;
	}

	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		Sink sink = statement_Out.sink;
		sink.visit(this, arg);
		String name = statement_Out.name;
		
		if(symbolTable.get(name) != null && (((symbolTable.get(name).localtype == Type.INTEGER) 
				|| (symbolTable.get(name).localtype == Type.BOOLEAN)) && (sink.localtype == Type.SCREEN)
				||(symbolTable.get(name).localtype == Type.IMAGE && (sink.localtype == Type.FILE
				|| sink.localtype == Type.SCREEN)))) {
			statement_Out.setDec(symbolTable.get(name));
		}
		else {
			throw new SemanticException(statement_Out.firstToken, "Error occured at statement_Out");
		}
		
		return null;
	}

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = statement_In.name;
		Source source = statement_In.source;
		source.visit(this, arg);
	
		statement_In.setDec(symbolTable.get(name));
		// Correction told to be made in ASSIGNMENT-5
		
		return null;
	}

	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		LHS lhs = statement_Assign.lhs;
		lhs.visit(this, arg);
		
		Expression e = statement_Assign.e;
		e.visit(this, arg);
		
		if(lhs.localtype == e.localtype || (lhs.localtype == Type.IMAGE && e.localtype == Type.INTEGER)) 
		{
			//index.visit(this, arg);
			statement_Assign.setCartesian(lhs.isCartesian());
		}
		else {
			throw new SemanticException(statement_Assign.firstToken, "Error occured at statement_Assign");
		}
		
		return null;
	}

	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = lhs.name;
		Index index = lhs.index;
		
		
		lhs.dec = symbolTable.get(name);
		
		if(lhs.dec == null) {
			throw new SemanticException(lhs.firstToken, "Error occured at lhs");
		}
		lhs.localtype = lhs.dec.localtype;

		if(index != null) {
			index.visit(this, arg);
			lhs.setCartesian(lhs.isCartesian());
		}
		else {
			lhs.setCartesian(false);
		}
		return lhs.localtype;
	}

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
	
		sink_SCREEN.localtype = Type.SCREEN;

		return sink_SCREEN.localtype;

	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg)
			throws Exception {
		// TODO Auto-generated method stub
		String name = sink_Ident.name;
		sink_Ident.localtype = symbolTable.get(name).localtype;
		
		if(sink_Ident.localtype != Type.FILE) {
			throw new SemanticException(sink_Ident.firstToken, "Semantic Error occured at sink_Ident");
		}
		
		return sink_Ident.localtype;
	}

	@Override
	public Object visitExpression_BooleanLit(
			Expression_BooleanLit expression_BooleanLit, Object arg)
					throws Exception {

		expression_BooleanLit.localtype = Type.BOOLEAN;

		return expression_BooleanLit.localtype;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		// TODO Auto-generated method stub
		String name = expression_Ident.firstToken.getText();
	
		if(symbolTable.containsKey(name)) {
		expression_Ident.localtype = symbolTable.get(name).localtype;
		}

		return expression_Ident.localtype;
	}

}
