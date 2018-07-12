package cop5556fa17;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.ASTVisitor;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_FunctionAppWithExprArg;
import cop5556fa17.AST.Expression_IntLit;
import cop5556fa17.AST.Expression_PixelSelector;
import cop5556fa17.AST.Expression_PredefinedName;
import cop5556fa17.AST.Expression_Unary;
import cop5556fa17.AST.Index;
import cop5556fa17.AST.LHS;
import cop5556fa17.AST.Program;
import cop5556fa17.AST.Source_CommandLineParam;
import cop5556fa17.AST.Source_StringLiteral;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.Parser.SyntaxException;
import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeCheckVisitor.SemanticException;

import static cop5556fa17.Scanner.Kind.*;

public class TypeCheckTest {

	// set Junit to be able to catch exceptions
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	// To make it easy to print objects and turn this output on and off
	static final boolean doPrint = true;
	private void show(Object input) {
		if (doPrint) {
			System.out.println(input.toString());
		}
	}
		
	/**
	 * Scans, parses, and type checks given input String.
	 * 
	 * Catches, prints, and then rethrows any exceptions that occur.
	 * 
	 * @param input
	 * @throws Exception
	 */
	void typeCheck(String input) throws Exception {
		show(input);
		try {
			Scanner scanner = new Scanner(input).scan();
			ASTNode ast = new Parser(scanner).parse();
			show(ast);
			ASTVisitor v = new TypeCheckVisitor();
			ast.visit(v, null);
		} catch (Exception e) {
			show(e);
			throw e;
		}
	}

	/**
	 * Simple test case with an almost empty program.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSmallest() throws Exception {
		String input = "n"; //Smallest legal program, only has a name
		show(input); // Display the input
		Scanner scanner = new Scanner(input).scan(); // Create a Scanner and
														// initialize it
		show(scanner); // Display the Scanner
		Parser parser = new Parser(scanner); // Create a parser
		ASTNode ast = parser.parse(); // Parse the program
		TypeCheckVisitor v = new TypeCheckVisitor();
		String name = (String) ast.visit(v, null);
		show("AST for program " + name);
		show(ast);
	}
	
	/**
	 * This test should pass with a fully implemented assignment
	 * @throws Exception
	 */
	 @Test
	 public void testDec1() throws Exception {
		 String input = "prog int k = 42;";
		 typeCheck(input);
	 }
	 
	 /**
	  * This program does not declare k. The TypeCheckVisitor should
	  * throw a SemanticException in a fully implemented assignment.
	  * @throws Exception
	  */
	 @Test
	 public void testUndec() throws Exception {
		 String input = "prog k = 42;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void testDec1_without_int() throws Exception {
		 String input = "prog int k;";
		 typeCheck(input);
	 }
	 
	 @Test 
	 public void program_variableDeclaration_valid() throws Exception {
		 String input = "prog int c = 1;\n int b = 3;\n int k  = sin(c+b/2);";
		 typeCheck(input);
	 }
	 
	 @Test 
	 public void program_variableDeclaration_valid1() throws Exception {
		 String input = "identifier boolean bool;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_imageDeclaration_valid() throws Exception {
		 String input = "ident image [x, y] ident2 <- \"jugraj\";";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_imageDeclaration_valid1() throws Exception {
		 String input = "ident1 image ident2 <- @ (123);";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_imageDeclaration_valid2() throws Exception {
		 String input = "ident1 image ident2 <- \"hey you!\";";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_sourceSinkDeclaration_url_valid() throws Exception {
		 String input = "ident1 url ident2 = \"https://www.google.com\";";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_assignmentStatement_withoutLHSSelector_valid() throws Exception {
		 String input = "ident1 boolean ident2;\n ident2 = true;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_assignmentStatement_withLHSSelector_valid1() throws Exception {
		 String input = "ident1 int ident2;\nident2 [[x,y]] = !123 ;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_Statement_Out_valid() throws Exception {
		 String input = "ident1 int ident2 ;\nident2 -> SCREEN;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_image_namePresentInLookUp_Invalid() throws Exception {
		 String input = "prog image [x, y] newImage;\n image [1, 2] newImage;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_image_namePresentInLookUp_Valid() throws Exception {
		 String input = "prog image [x, y] newImage;\n image [1, 2] newImage1;";
		 typeCheck(input);
	 }
	 
	 //@Test
	 public void program_declaration_image_ySizeNotInteger_Invalid() throws Exception {
		 String input = "prog boolean c = true;\nimage [x, y] newImage;\n image [1, c] newImage1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_sourceSink_namePresentInLookUp_Invalid() throws Exception {
		 String input = "ident1 url ident2 = \"https://www.google.com\";\n url ident2 = \"https://www.facebook.com\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_sourceSink_namePresentInLookUp_Valid() throws Exception {
		 String input = "ident1 url ident2 = \"https://www.google.com\";\n url ident3 = \"https://www.facebook.com\";";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_sourceSink_sourceType_differentFrom_sourceSinknType_Invalid() throws Exception {
		 String input = "ident1 url ident2 = @1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declaration_variableType_notEqualTo_ExpressionType_Invalid() throws Exception {
		 String input = "ident1 int c;\n boolean d = true;\n c=d;"; 
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_statementAssign_LHSType_notEqualTo_ExpressionType_Invalid1() throws Exception {
		 //String input = "ident1 int c = 1==1;"; 
		 String input = "ident1 int c = 1;\n boolean d = true; \n c = d;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_expressionCondition_notboolean_Invalid() throws Exception {
		 String input = "prog int k = (1)? 2: 3;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_trueExpressionType_notSameAs_falseExpressionType_Invalid() throws Exception {
		 String input = "prog int k = (1==1)? 2 : true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_declarationVariable_int_assigned_boolean_Invalid() throws Exception {
		 String input = "prog int k = (1==1)? false : true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_Source_StringLiteral_URL_Invalid() throws Exception {
		 String input = "prog url jugraj = \"htps://www.google.com\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_SourceIdent_nameNotInSymbolTable_Invalid() throws Exception{
		 String input = "prog int b =1; \n url jugraj = c;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_SourceIdent_sourceIdentTypeNotFileOrUrl_Invalid() throws Exception{
		 String input = "prog int b =1; \n url jugraj = b;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	  
	 @Test
	 public void program_statemaneOut_Valid() throws Exception {
		// String input = "prog image[1, 2] ident;\n file k = \"efsfsfsf\";  \n ident -> k;";
		 String input = "prog image[1, 2] ident;\n file k = \"efsfsfsf\";  \n ident -> SCREEN;";
		 typeCheck(input);
	 }
	 
	 @Test
	 public void program_statemaneOut_InValid() throws Exception {
		 String input = "prog image[1, 2] ident;\n int k =1;  \n ident -> k;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
	 }
	 
	//UTSA
		 @Test
		 public void testSum() throws Exception {
		 String input = "prog int k = 42; int l = 23; int z = k+l;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testSumException() throws Exception {
		 String input = "prog int k = 42; boolean l = true; int z = k+l;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_image() throws Exception {
		 String input = "prog int k = 42; int l = 23; image[k+l,k-l] i;";
		 typeCheck(input);
		 }
		 
		// @Test
		 public void testDeclaration_imageException() throws Exception {
		 String input = "prog boolean k = true; int l = 23; image[k,l] i;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_variableException() throws Exception {
		 String input = "prog boolean k = true; int k = 23;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_sourcesink() throws Exception {
		 String input = "prog int k = 23; file i = \"Utsa\";";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_sourcesink1() throws Exception {
		 String input = "prog file j = \"Utsa\"; file i = j;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_sourcesink2() throws Exception {
		 String input = "prog url k = \"https:\";url j = k; url i = j;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_sourcesinkException1() throws Exception {
		 String input = "prog int k = 23; file i = k;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testDeclaration_sourcesinkException2() throws Exception {
		 String input = "prog file k = \"https:\";url j = k; url i = j;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testStatement_assign() throws Exception {
		 String input = "prog boolean k = false; k[[r,A]] = true;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testStatement_assignException() throws Exception {
		 String input = "prog int k = 2; k[[r,A]] = true;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testStatement_in() throws Exception {
		 String input = "prog int k = 2; k <- @1;";
		 typeCheck(input);
		 }
		 
		 /*@Test
		 public void testStatement_inException() throws Exception {
		 String input = "prog boolean k = true; k <- @1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }*/
		 
		 @Test
		 public void testStatement_out() throws Exception {
		 String input = "prog int k = 2; k -> SCREEN;";
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testStatement_outException() throws Exception {
		 String input = "prog int k = 2; k <- @1;";
		 typeCheck(input);
		 }
		 //UTSA
		 
		 //ASHWIN
		 @Test
		 public void testUndec1() throws Exception {
		 String input = "Prog url ident = \"asdfadf\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testUndec2() throws Exception {
		 String input = "prog boolean k=true; int k = 5;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testUndec3() throws Exception {
		 String input = "prog int k=1; int b=2; int c = k+b;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 @Test
		 public void testUndec4() throws Exception {
		 String input = "prog int k=1; int b=2; boolean c = k+b;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec5() throws Exception {
		 String input = "prog image image1; image image2; image1 <- image2;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 //@Test
		 public void testUndec6() throws Exception {
		 String input = "prog  image image1; image image2; image1 <- @1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 //@Test
		 public void testUndec7() throws Exception {
		 String input = "prog file imagefile  = \"imageFile2017.\"; image image1;  image1 <- imagefile;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec8() throws Exception {
		 String input = "prog file aa=\"http://example.com/\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec9() throws Exception {
		 String input = "raktima url ab=\"world.docx\";";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec10() throws Exception {
		 String input = "abc int def;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec11() throws Exception {
		 String input = "abc";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec12() throws Exception {
		 String input = "prog int k=2; k=++++k;";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec13() throws Exception {
		 String input = "raktima int k=+r;";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec14() throws Exception {
		 String input = "raktima int k=+r;";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec15() throws Exception {
		 String input = "raktima int k=sin(2);";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec16() throws Exception {
		 String input = "raktima int k=2;k[[x,y]]=2;";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec17() throws Exception {
		 String input = "raktima int k=2;k[[r,A]]=2;";
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec18() throws Exception {
		 String input = "raktima b[[x,y]]=x;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec19() throws Exception {
		 String input = "raktima int b=2;b[[x,y]]=x;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec20() throws Exception {
		 String input = "raktima url b=\"http://www.google.com\";";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec21() throws Exception {
		 String input = "raktima url b=\"http://www.google.com\";";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec22() throws Exception {
		 String input = "raktima int b=2; int c=4; c=b==c;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 
		 
		 
		 @Test
		 public void testUndec23() throws Exception {
		 String input = "prog int k=k+1;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec24() throws Exception {
		 String input = "prog url b=c;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec25() throws Exception {
		 String input = "prog url b=\"https://www.google.com\"; "
		 		         + "url c=b;";
		 //thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 @Test
		 public void testUndec26() throws Exception {
		 String input = "prog int filepng=2;int png=3; image[filepng,png] imageName <- imagepng;";
		 thrown.expect(SemanticException.class);
		 typeCheck(input);
		 }
		 
		 	 
		 	 @Test
		 	 public void test1() throws Exception {
		 	 String input = "prog int k = 42; int k=12;";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test2() throws Exception {
		 	 String input = "prog int k = 42;\n boolean k=true;";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test3() throws Exception {
		 	 String input = "prog file k = 42;\n boolean k=true;";
		 	 thrown.expect(SyntaxException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test4() throws Exception {
		 	 String input = "prog file k = 42;\n boolean k=true;";
		 	 thrown.expect(SyntaxException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test5() throws Exception {
		 	 String input = "prog image[filepng,png] imageName <- imagepng; \n boolean ab=true;"; 
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test6() throws Exception {
		 	 String input = "prog image[filepng,png] imageName; \n boolean ab=true;"; 
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test7() throws Exception {
		 	 String input = "prog int abcd=(true&true);"; 
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test8() throws Exception {
		 	 String input = "prog boolean abcd=(true&true|false&1);"; 
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 //@Test
		 	 public void test9() throws Exception {
		 	 String input = "prog image x;"; 
		 	 thrown.expect(SyntaxException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test10() throws Exception {
		 	 String input = "prog image [10,11] abcd <- \"\";"; 
		 	 //thrown.expect(SyntaxException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test11() throws Exception {
		 	 String input = "prog image [10,11] abcd <- @(1234+234);"; 
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test12() throws Exception {
		 	 String input = "prog url x_y = \"https://www.google.com\"; image [10,11] abcd <- x_y;"; 
		 	 typeCheck(input);
		 	 }
		 	 
		 	 // SourceSinkDeclaration
		 	 @Test
		 	 public void test13() throws Exception {
		 	 String input = "prog url imageurl=\"https://www.google.com\";"; 
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test14() throws Exception {
		 	 String input = "prog url imageurl1=\"https://www.google.com\"; url imageurl=imageurl1;"; 
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test15() throws Exception {
		 	 String input = "prog url imageurl=@(1234+3454);";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 //Statements for Image Out and Image in
		 	 @Test
		 	 public void test16() throws Exception {
		 	 String input = "prog image imageName;image imageName1 <- \"https://www.google.com\";"+ 
		 			        "imageName -> SCREEN;";
		 	 //thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test17() throws Exception {
		 	 String input = "prog file fileName=\"filepng\"; image image1<- fileName;";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 //LHS Assignment Statement
		 	 @Test
		 	 public void test18() throws Exception {
		 	 String input = "prog image imageName; int array; array[[x,y]]=imageName[5,6];";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test19() throws Exception {
		 	 String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test20() throws Exception {
		 	 String input = "prog image imageName;array[[x,y]]=imageName[5,6];";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test21() throws Exception {
		 	 String input = "prog image imageName;int array;array[[r,A]]=imageName[5,6];";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test22() throws Exception {
		 	 String input = "prog int value1=10; int value2 =20; int sinValue=abs(sin(value1)); int cosValue=abs(cos(value2));";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test23() throws Exception {
		 	 String input = "prog int value1=10; int value2 =20; int sinValue; int cosValue; sinValue=abs(sin(atan(cos(value1))));"
		 	 		        + " cosValue=cart_x[value1*10,value2*20];";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test24() throws Exception {
		 	 String input = "prog int x_value=10; int y_value =20; int sinValue; int cosValue; "
		 	 			    + " sinValue=polar_a[x_value*1/2+12+13,y_value*1/3*1/2*3/4%2];"
		 	 		        + " cosValue=cart_y[x_value/2*2+2,(x_value>y_value)?sinValue/234:sinValue*10-23];"
		 	 		        + " int cotValue=polar_r[sinValue/cosValue,sinValue*100/400+cosValue];";
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test25() throws Exception {
		 	 String input = "";
		 	 thrown.expect(SyntaxException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test26() throws Exception {
		 		 String input = "prog boolean k = false;\n k=k;";
		 		 typeCheck(input);
		      }
		 	 
		 	 @Test
		 	 public void test27() throws Exception {
		 	 String input = "prog int k=(5*5+12+3-5+4+false);";
		 	 thrown.expect(SemanticException.class);
		 	 typeCheck(input);
		 	 }
		 	 
		 	 @Test
		 	 public void test28() throws Exception {
		 	 String input = "prog int k=((5+6/0+1/2+2%3));";
		 	 typeCheck(input);
		 	 }
		 
		 //POOJA

}