package cop5556fa17;

import java.util.ArrayList;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
/*import cop5556fa17.image.ImageFrame;
import cop5556fa17.image.ImageSupport;*/

public class CodeGenVisitor implements ASTVisitor, Opcodes {

	/**
	 * All methods and variable static.
	 */


	/**
	 * @param DEVEL
	 *            used as parameter to genPrint and genPrintTOS
	 * @param GRADE
	 *            used as parameter to genPrint and genPrintTOS
	 * @param sourceFileName
	 *            name of source file, may be null.
	 */
	public CodeGenVisitor(boolean DEVEL, boolean GRADE, String sourceFileName) {
		super();
		this.DEVEL = DEVEL;
		this.GRADE = GRADE;
		this.sourceFileName = sourceFileName;
	}

	ClassWriter cw;
	String className;
	String classDesc;
	String sourceFileName;

	MethodVisitor mv; // visitor of method currently under construction

	/** Indicates whether genPrint and genPrintTOS should generate code. */
	final boolean DEVEL;
	final boolean GRADE;
	


	@Override
	public Object visitProgram(Program program, Object arg) throws Exception {
		cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
		className = program.name;  
		classDesc = "L" + className + ";";
		String sourceFileName = (String) arg;
		cw.visit(52, ACC_PUBLIC + ACC_SUPER, className, null, "java/lang/Object", null);
		cw.visitSource(sourceFileName, null);
		// create main method
		mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
		// initialize
		mv.visitCode();		
		//add label before first instruction
		Label mainStart = new Label();
		mv.visitLabel(mainStart);		
		// if GRADE, generates code to add string to log
		FieldVisitor fv = cw.visitField(ACC_STATIC, "x", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "y", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "X", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "Y", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "r", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "R", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "a", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "A", "I", null, null);
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "DEF_X", "I", null, new Integer(256));
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "DEF_Y", "I", null, new Integer(256));
        fv.visitEnd();
        fv = cw.visitField(ACC_STATIC, "Z", "I", null, new Integer(16777215));
        fv.visitEnd();

		/*CodeGenUtils.genLog(GRADE, mv, "entering main");*/

		// visit decs and statements to add field to class
		//  and instructions to main method, respectively
		
		ArrayList<ASTNode> decsAndStatements = program.decsAndStatements;
		for (ASTNode node : decsAndStatements) {
			node.visit(this, arg);
		}

		//generates code to add string to log
		/*CodeGenUtils.genLog(GRADE, mv, "leaving main");*/
		
		//adds the required (by the JVM) return statement to main
		mv.visitInsn(RETURN);
		
		//adds label at end of code
		Label mainEnd = new Label();
		mv.visitLabel(mainEnd);
		
		//handles parameters and local variables of main. Right now, only args
		mv.visitLocalVariable("args", "[Ljava/lang/String;", null, mainStart, mainEnd, 0);

		//Sets max stack size and number of local vars.
		//Because we use ClassWriter.COMPUTE_FRAMES as a parameter in the constructor,
		//asm will calculate this itself and the parameters are ignored.
		//If you have trouble with failures in this routine, it may be useful
		//to temporarily set the parameter in the ClassWriter constructor to 0.
		//The generated classfile will not be correct, but you will at least be
		//able to see what is in it.
		mv.visitMaxs(0, 0);
		
		//terminate construction of main method
		mv.visitEnd();
		
		//terminate class construction
		cw.visitEnd();

		//generate classfile as byte array and return
		return cw.toByteArray();
	}

	@Override
	public Object visitDeclaration_Variable(Declaration_Variable declaration_Variable, Object arg) throws Exception {
		// TODO 
		FieldVisitor fv;
		String fieldName = declaration_Variable.name;
		String fieldType = IntOrBoolCheck(declaration_Variable.localtype);
		Object initValue = declaration_Variable.type.kind == Kind.INTEGER_LITERAL?new Integer(0):new Boolean(true);
		Expression e = declaration_Variable.e;
		
		fv = cw.visitField(ACC_STATIC, fieldName, fieldType, null, initValue);
		fv.visitEnd();
		
		if(e != null) {
			e.visit(this, arg);

			mv.visitFieldInsn(PUTSTATIC, className, fieldName, fieldType);
		}		
		return null;
	}

	@Override
	public Object visitExpression_Binary(Expression_Binary expression_Binary, Object arg) throws Exception {
		// TODO 
		Label enab_true = new Label();
		Label term_true = new Label();
		
		Expression e0 = expression_Binary.e0;
		e0.visit(this, arg);
		Expression e1 = expression_Binary.e1;
		e1.visit(this, arg);
			
		Kind op = expression_Binary.op;
		// Cases for op are enumerated here
		switch(op) {
		case OP_OR:{
			mv.visitInsn(IOR);
			break;
			}
		case OP_AND:{
			mv.visitInsn(IAND);
			break;
			}

		case OP_EQ:{
			mv.visitJumpInsn(IF_ICMPEQ, enab_true);
			mv.visitLdcInsn(false);
			break;
			}
		case OP_NEQ:{
			mv.visitJumpInsn(IF_ICMPNE, enab_true);
			mv.visitLdcInsn(false);
			break;
			}
		case OP_LE:{
			mv.visitJumpInsn(IF_ICMPLE, enab_true);
			mv.visitLdcInsn(false);
			break;
			}
		case OP_GE:{
			mv.visitJumpInsn(IF_ICMPGE, enab_true);
			mv.visitLdcInsn(false);
			break;
			}
		case OP_LT:{
			mv.visitJumpInsn(IF_ICMPLT, enab_true);
			mv.visitLdcInsn(false);
			break;
			}
		case OP_GT:{
			mv.visitJumpInsn(IF_ICMPGT, enab_true);
			mv.visitLdcInsn(false);
			break;
			}

		case OP_PLUS:{
			mv.visitInsn(IADD);
			break;
			}
		case OP_MINUS:{
			mv.visitInsn(ISUB);
			break;
			}
		case OP_TIMES:{
			mv.visitInsn(IMUL);
			break;
			}
		case OP_DIV:{
			mv.visitInsn(IDIV);
			break;
			}
		case OP_MOD:{
			mv.visitInsn(IREM);
			break;
			}
		default:
			throw new RuntimeException("visitexpression_Binary called unimplemented type " + expression_Binary.localtype);
		
		}
		mv.visitJumpInsn(GOTO, term_true);
		mv.visitLabel(enab_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(term_true);
		
		return null;
	}

	@Override
	public Object visitExpression_Unary(Expression_Unary expression_Unary, Object arg) throws Exception {
		// TODO 
		Label enab_true = new Label();
		Label term_true = new Label();
		
		Expression e = expression_Unary.e;
		e.visit(this, arg);
		Kind op = expression_Unary.op;
		
		switch(op) {
		case OP_MINUS:{
			/*if(e.localtype == Type.INTEGER) Type check will already throw an error for this case no need to check again {*/
			mv.visitInsn(INEG);
			break;
		
			}
		case OP_EXCL:{
			if(e.localtype == Type.BOOLEAN) {
				mv.visitJumpInsn(IFEQ, enab_true);
				mv.visitLdcInsn(false);
			}
			else if(e.localtype == Type.INTEGER) {
				mv.visitLdcInsn(INTEGER.MAX_VALUE);
				mv.visitInsn(IXOR);
			}
			break;
		}
		
		default:// do nothing because unaryExpr3 was not working here
			//throw new RuntimeException("visitexpression_Unary called unimplemented type " + expression_Unary.localtype);
		}
		mv.visitJumpInsn(GOTO, term_true);
		mv.visitLabel(enab_true);
		mv.visitLdcInsn(true);
		mv.visitLabel(term_true);
		
		return null;
	}

	// generate code to leave the two values on the stack
	@Override
	public Object visitIndex(Index index, Object arg) throws Exception {
		// TODO HW6
		Expression e0 = index.e0;
		Expression e1 = index.e1;
		
		e0.visit(this, arg);
		e1.visit(this, arg);
		
		if(index.isCartesian()) {
			// do nothing
		}
		else {
			mv.visitInsn(DUP2);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_x", RuntimeFunctions.cart_xSig, false);
			mv.visitInsn(DUP_X2);
			mv.visitInsn(POP);
			mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "cart_y", RuntimeFunctions.cart_ySig, false);
		}
		
		return null;
	}

	@Override
	public Object visitExpression_PixelSelector(Expression_PixelSelector expression_PixelSelector, Object arg)
			throws Exception {
		// TODO HW6
		Index index = expression_PixelSelector.index;
		String name = expression_PixelSelector.name;
		
		mv.visitFieldInsn(GETSTATIC, className, name, ImageSupport.ImageDesc);
		index.visit(this, arg);
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getPixel", ImageSupport.getPixelSig, false);
		
		return null;

	}

	@Override
	public Object visitExpression_Conditional(Expression_Conditional expression_Conditional, Object arg)
			throws Exception {
		// TODO 
		
		Label enab_true = new Label();
		Label term_true = new Label();
		Expression cond = expression_Conditional.condition;
		cond.visit(this, arg);
		
		mv.visitJumpInsn(IFEQ, term_true);
		Expression true_exp = expression_Conditional.trueExpression;
		true_exp.visit(this, arg);
		
		mv.visitJumpInsn(GOTO, enab_true);
		mv.visitLabel(term_true);
		
		Expression false_exp = expression_Conditional.falseExpression;
		false_exp.visit(this, arg);
		mv.visitLabel(enab_true);
		
		return null;
	}


	@Override
	public Object visitDeclaration_Image(Declaration_Image declaration_Image, Object arg) throws Exception {
		// TODO HW6
		FieldVisitor fv;
		Source source = declaration_Image.source;
		Expression xSize = declaration_Image.xSize;
		Expression ySize = declaration_Image.ySize;
		String name = declaration_Image.name;

		Object initValue = null;
		
		fv = cw.visitField(ACC_STATIC, name, ImageSupport.ImageDesc, null, initValue);
		fv.visitEnd();
		
		if(source != null) {
			source.visit(this, arg);
			if(xSize == null && ySize == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else if(xSize != null && ySize != null){
				xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);//dpubt
				ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}

			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, name, ImageSupport.ImageDesc);
		}
		else {
			if(xSize == null && ySize == null) {
				mv.visitFieldInsn(GETSTATIC, className, "DEF_X", "I");
				mv.visitFieldInsn(GETSTATIC, className, "DEF_Y", "I");
			}
			else if (xSize != null && ySize != null){
				xSize.visit(this, arg);
				ySize.visit(this, arg);
			}
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "makeImage", ImageSupport.makeImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, name, ImageSupport.ImageDesc);
		}
		return null;

	}
	
  
	@Override
	public Object visitSource_StringLiteral(Source_StringLiteral source_StringLiteral, Object arg) throws Exception {
		// TODO HW6
		String fileOrUrl = source_StringLiteral.fileOrUrl;
		mv.visitLdcInsn(new String(fileOrUrl));

		return null;
		
	}

	

	@Override
	public Object visitSource_CommandLineParam(Source_CommandLineParam source_CommandLineParam, Object arg)
			throws Exception {
		// TODO 
		
		mv.visitVarInsn(ALOAD, 0);
		Expression param = source_CommandLineParam.paramNum;
		param.visit(this, arg);
		mv.visitInsn(AALOAD);
		return null;
		
	}

	@Override
	public Object visitSource_Ident(Source_Ident source_Ident, Object arg) throws Exception {
		// TODO HW6
		String name = source_Ident.name;
		
		mv.visitFieldInsn(GETSTATIC, className, name, "Ljava/lang/String;");
		
		return null;

	}


	@Override
	public Object visitDeclaration_SourceSink(Declaration_SourceSink declaration_SourceSink, Object arg)
			throws Exception {
		// TODO HW6
		FieldVisitor fv;
		String name = declaration_SourceSink.name;
		String fieldType = IntOrBoolCheck(declaration_SourceSink.localtype);
		Object initValue = null;
		Source source = declaration_SourceSink.source;
		
		fv = cw.visitField(ACC_STATIC, name, fieldType, null, initValue);
		fv.visitEnd();
		
		if(source != null) {
			source.visit(this, arg);
			mv.visitFieldInsn(PUTSTATIC, className, name, fieldType);
		}
		return null;

	}
	


	@Override
	public Object visitExpression_IntLit(Expression_IntLit expression_IntLit, Object arg) throws Exception {
		// TODO 
		mv.visitLdcInsn(new Integer(expression_IntLit.value));
		
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithExprArg(
			Expression_FunctionAppWithExprArg expression_FunctionAppWithExprArg, Object arg) throws Exception {
		// TODO HW6
		Expression exprArg = expression_FunctionAppWithExprArg.arg;
		String className = RuntimeFunctions.className;
		Kind function = expression_FunctionAppWithExprArg.function;
		exprArg.visit(this, arg);
		
		if(function == Kind.KW_log) {
			mv.visitMethodInsn(INVOKESTATIC, className, "log", RuntimeFunctions.logSig, false);
		}
		else if(function == Kind.KW_abs) {
			mv.visitMethodInsn(INVOKESTATIC, className, "abs", RuntimeFunctions.absSig, false);
		}
		else {
			throw new UnsupportedOperationException();
		}
		
		return null;
	}

	@Override
	public Object visitExpression_FunctionAppWithIndexArg(
			Expression_FunctionAppWithIndexArg expression_FunctionAppWithIndexArg, Object arg) throws Exception {
		// TODO HW6
		Index indexArg = expression_FunctionAppWithIndexArg.arg;
		Kind function = expression_FunctionAppWithIndexArg.function;
		String className = RuntimeFunctions.className;
		Expression e0 = indexArg.e0;
		Expression e1 = indexArg.e1;

		e0.visit(this, arg);
		e1.visit(this, arg);
		
		switch(function) { 
		case KW_cart_x: {
			mv.visitMethodInsn(INVOKESTATIC, className, "cart_x", RuntimeFunctions.cart_xSig, false);
			break;
		}
		
		case KW_cart_y: {
			mv.visitMethodInsn(INVOKESTATIC, className, "cart_y", RuntimeFunctions.cart_ySig, false);
			break;
		}
		case KW_polar_r: {	
			mv.visitMethodInsn(INVOKESTATIC, className, "polar_r", RuntimeFunctions.polar_rSig, false);
			break;
		}
		case KW_polar_a:{
			mv.visitMethodInsn(INVOKESTATIC, className, "polar_a", RuntimeFunctions.polar_aSig, false);
			break;
		}
		default: throw new UnsupportedOperationException();	//throw exception since the code should never reach here
		}
		
		return null;

	}

	@Override
	public Object visitExpression_PredefinedName(Expression_PredefinedName expression_PredefinedName, Object arg)
			throws Exception {
		// TODO HW6

		Kind kind = expression_PredefinedName.kind;
		
		switch(kind) {
		case KW_DEF_X:{
			mv.visitLdcInsn(new Integer(256));
			break;
		}
		case KW_DEF_Y:{
			mv.visitLdcInsn(new Integer(256));
			break;
		}
		case KW_x:{
			mv.visitFieldInsn(GETSTATIC, className, "x", "I");
			break;
			
		}
		case KW_X:{
			mv.visitFieldInsn(GETSTATIC, className, "X", "I");
			break;
		
		}
		case KW_Y:{
			mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
			break;
			
		}
		case KW_y:{
			mv.visitFieldInsn(GETSTATIC, className, "y", "I");
			break;
			
		}
		case KW_r:{
			mv.visitFieldInsn(GETSTATIC, className, "r", "I");
			break;
			
		}
		case KW_a:{
			mv.visitFieldInsn(GETSTATIC, className, "a", "I");
			break;
			
		}
		case KW_R:{
			mv.visitFieldInsn(GETSTATIC, className, "R", "I");
			break;
			
		}
		case KW_A:{
			mv.visitFieldInsn(GETSTATIC, className, "A", "I");
			break;
			
		}
		case KW_Z:{
			mv.visitLdcInsn(new Integer(16777215));
			break;
		}
		
		default: throw new UnsupportedOperationException();	//throw exception since the code should never reach here
		}
		return null;
	}

	/** For Integers and booleans, the only "sink"is the screen, so generate code to print to console.
	 * For Images, load the Image onto the stack and visit the Sink which will generate the code to handle the image.
	 */
	@Override
	public Object visitStatement_Out(Statement_Out statement_Out, Object arg) throws Exception {
		// TODO in HW5:  only INTEGER and BOOLEAN
		// TODO HW6 remaining cases
		Declaration dec = statement_Out.getDec();
		String fieldName = statement_Out.name;
		Sink sink = statement_Out.sink;
		
		
		switch(dec.localtype) {
		case INTEGER:{
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, fieldName, IntOrBoolCheck(dec.localtype));
			
			CodeGenUtils.genLogTOS(GRADE, mv, dec.localtype);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream","println", "(I)V", false);
			break;
		}
		case BOOLEAN:{
			mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
			mv.visitFieldInsn(GETSTATIC, className, fieldName, IntOrBoolCheck(dec.localtype));

			CodeGenUtils.genLogTOS(GRADE, mv, dec.localtype);
			mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream","println", "(Z)V", false);
			break;
		}
		// Image case added in HomeWork6
		case IMAGE:{
			mv.visitFieldInsn(GETSTATIC, className, fieldName, IntOrBoolCheck(dec.localtype));
			CodeGenUtils.genLogTOS(GRADE, mv, dec.localtype);
			sink.visit(this, arg);
			break;
			}
		default:throw new UnsupportedOperationException();

		}
		return null;

	}

	/**
	 * Visit source to load rhs, which will be a String, onto the stack
	 * 
	 *  In HW5, you only need to handle INTEGER and BOOLEAN
	 *  Use java.lang.Integer.parseInt or java.lang.Boolean.parseBoolean 
	 *  to convert String to actual type. 
	 *  
	 *  TODO HW6 remaining types
	 */

	@Override
	public Object visitStatement_In(Statement_In statement_In, Object arg) throws Exception {
		// TODO (see comment )
		Source source = statement_In.source;
		String name = statement_In.name;
		Declaration dec = statement_In.getDec();
		
		switch(dec.localtype) 
		{
		case INTEGER:{
			source.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "parseInt", "(Ljava/lang/String;)I", false);
			mv.visitFieldInsn(PUTSTATIC, className, name, IntOrBoolCheck(dec.localtype));
			break;
		}
		case BOOLEAN:{
			source.visit(this, arg);
			mv.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "parseBoolean", "(Ljava/lang/String;)Z", false);
			mv.visitFieldInsn(PUTSTATIC, className, name, IntOrBoolCheck(dec.localtype));
			break;
		}
		case IMAGE:{
			Declaration_Image decImage = (Declaration_Image)dec;
	        Expression xSize = decImage.xSize;
	        Expression ySize = decImage.ySize;
	        
	        source.visit(this, arg);
			if(xSize == null && ySize == null) {
				mv.visitInsn(ACONST_NULL);
				mv.visitInsn(ACONST_NULL);
			}
			else if(xSize != null && ySize != null){
				xSize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
				ySize.visit(this, arg);
				mv.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
			}

			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className , "readImage", ImageSupport.readImageSig, false);
			mv.visitFieldInsn(PUTSTATIC, className, name, IntOrBoolCheck(dec.localtype));
			break;
			}
		default: throw new UnsupportedOperationException();// code should never reach here
		
		}
		
		return null;
	}

	
	@Override
	public Object visitStatement_Assign(Statement_Assign statement_Assign, Object arg) throws Exception {
		//TODO  (see comment)
		// TO BE VERIFIED
		Expression e = statement_Assign.e;
		LHS lhs = statement_Assign.lhs;
		String name = statement_Assign.lhs.name;
		
		
		if(lhs.localtype == Type.INTEGER || lhs.localtype == Type.BOOLEAN) {
			e.visit(this, arg);
			lhs.visit(this, arg);
		}// lhs.visit will be able to put the value on the top of the stack
		 else if(lhs.localtype == Type.IMAGE){
	            
			 	mv.visitFieldInsn(GETSTATIC, className, name , ImageSupport.ImageDesc);
			 	mv.visitInsn(DUP);
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getX", ImageSupport.getXSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "X", "I");
				mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "getY", ImageSupport.getYSig, false);
				mv.visitFieldInsn(PUTSTATIC, className, "Y", "I");
			 	
				mv.visitInsn(ICONST_0);
				mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
				Label l1 = new Label();
				mv.visitJumpInsn(GOTO, l1);
				Label l2 = new Label();
				mv.visitLabel(l2);				
				mv.visitInsn(ICONST_0);
				mv.visitFieldInsn(PUTSTATIC,className, "y", "I");
				Label l3 = new Label();
				mv.visitJumpInsn(GOTO, l3);
				Label l4 = new Label();
				mv.visitLabel(l4);
				
								
				if(lhs.isCartesian() == false) {
					mv.visitFieldInsn(GETSTATIC, className, "x", "I");
					mv.visitFieldInsn(GETSTATIC, className, "y", "I");
					mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_r", RuntimeFunctions.polar_rSig, false);
					mv.visitFieldInsn(PUTSTATIC, className, "r", "I");
					mv.visitFieldInsn(GETSTATIC, className, "x", "I");
					mv.visitFieldInsn(GETSTATIC, className, "y", "I");
					mv.visitMethodInsn(INVOKESTATIC, RuntimeFunctions.className, "polar_a", RuntimeFunctions.polar_aSig, false);
					mv.visitFieldInsn(PUTSTATIC, className, "a", "I");
					}
				e.visit(this, arg);
				mv.visitFieldInsn(GETSTATIC, className, name , ImageSupport.ImageDesc);
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				lhs.visit(this, arg);

				
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IADD);
				mv.visitFieldInsn(PUTSTATIC, className, "y", "I");
				mv.visitLabel(l3);
				mv.visitFieldInsn(GETSTATIC, className, "y", "I");
				mv.visitFieldInsn(GETSTATIC, className, "Y", "I");
				mv.visitJumpInsn(IF_ICMPLT, l4);
				Label l6 = new Label();
				mv.visitLabel(l6);
				
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitInsn(ICONST_1);
				mv.visitInsn(IADD);
				mv.visitFieldInsn(PUTSTATIC, className, "x", "I");
				mv.visitLabel(l1);
				mv.visitFieldInsn(GETSTATIC, className, "x", "I");
				mv.visitFieldInsn(GETSTATIC, className, "X", "I");
				mv.visitJumpInsn(IF_ICMPLT, l2);

	        }
		return null;

	}

	/**
	 * In HW5, only handle INTEGER and BOOLEAN types.
	 */
	@Override
	public Object visitLHS(LHS lhs, Object arg) throws Exception {
		//TODO  (see comment)
		String name = lhs.name;
		
		if(lhs.localtype == Type.INTEGER || lhs.localtype == Type.BOOLEAN) {
			mv.visitFieldInsn(PUTSTATIC, className, name, IntOrBoolCheck(lhs.localtype));
		}
		else if (lhs.localtype == Type.IMAGE) {
			
			mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "setPixel", ImageSupport.setPixelSig, false );
		}
		
		return null;
	}
	

	@Override
	public Object visitSink_SCREEN(Sink_SCREEN sink_SCREEN, Object arg) throws Exception {
		//TODO HW6
		String className = ImageSupport.className;
		mv.visitMethodInsn(INVOKESTATIC, className, "makeFrame", ImageSupport.makeFrameSig, false);
		mv.visitInsn(POP);
		return null;

	}

	@Override
	public Object visitSink_Ident(Sink_Ident sink_Ident, Object arg) throws Exception {
		//TODO HW6
		String name = sink_Ident.name;
		mv.visitFieldInsn(GETSTATIC, className, name , IntOrBoolCheck(sink_Ident.localtype));
		mv.visitMethodInsn(INVOKESTATIC, ImageSupport.className, "write", ImageSupport.writeSig, false);
		return null;

	}

	@Override
	public Object visitExpression_BooleanLit(Expression_BooleanLit expression_BooleanLit, Object arg) throws Exception {
		//TODO

		mv.visitLdcInsn(expression_BooleanLit.value);
		
		return null;
	}

	@Override
	public Object visitExpression_Ident(Expression_Ident expression_Ident,
			Object arg) throws Exception {
		//TODO
		String name = expression_Ident.name;
		String desc = IntOrBoolCheck(expression_Ident.localtype); 
		
		mv.visitFieldInsn(GETSTATIC, className, name, desc);	
		
		return null;
	
	}
	
	public String IntOrBoolCheck(Type kind) {
		String res = null;
		if(kind == Type.INTEGER)
			res = "I";
		else if(kind == Type.BOOLEAN)
			res = "Z";
		else if(kind == Type.IMAGE)
			res = "Ljava/awt/image/BufferedImage;";
		else if(kind == Type.URL || kind ==Type.FILE)
			res = ImageSupport.StringDesc;
		
		return res;
	}

}
