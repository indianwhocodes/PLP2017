package cop5556fa17;

import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import cop5556fa17.Scanner.LexicalException;
import cop5556fa17.Scanner.Token;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class SimpleParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    //To make it easy to print objects and turn this output on and off
    static final boolean doPrint = true;
    private void show(Object input) {
   	 if (doPrint) {
   		 System.out.println(input.toString());
   	 }
    }

    @Test
    public void testEmpty() throws LexicalException, SyntaxException {
   	 String input = "";  //The input is the empty string.  This is not legal
   	 show(input);    	//Display the input
   	 Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
   	 show(scanner);   //Display the Scanner
   	 Parser parser = new Parser(scanner);  //Create a parser
   	 thrown.expect(SyntaxException.class);
   	 try
   	 {
   		 parser.parse();  //Parse the program
   	 }
   	 catch (SyntaxException e)
   	 {
   		 show(e);
   		 throw e;
   	 }
    }

    @Test
    public void testDec1() throws LexicalException, SyntaxException {
   	 String input = "prog int k;";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
   	 show(scanner);   //Display the Scanner
   	 Parser parser = new Parser(scanner);  //
   	 parser.parse();
    }

    @Test
    public void expression16() throws SyntaxException, LexicalException
    {
   	 String input = "ashwin (a+b,m+n)";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();  
   	 show(scanner);   
   	 Parser parser = new Parser(scanner);  
   	 parser.expression();  //Parse the program  
    }

    @Test
    public void andExpression_invalid() throws SyntaxException, LexicalException {
   	 String input = "sin[+1,0123]";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();
   	 show(scanner);
   	 Parser parser = new Parser(scanner);
   	 thrown.expect(SyntaxException.class);
   	 try {
   		 parser.expression();  //Parse the program
   	 }catch (SyntaxException e) {
   		 show(e);
   		 throw e;
   	 }
    }

    @Test
    public void testprogram12() throws SyntaxException, LexicalException {
   	 String input = "prog int k = ;"; 
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();  
   	 show(scanner);   
   	 Parser parser = new Parser(scanner);  
   	 thrown.expect(SyntaxException.class);
   	 try {
   		 parser.parse();  //Parse the program
   	 }
   	 catch (SyntaxException e) {
   		 show(e);
   		 throw e;
   	 }  
    }

    @Test
    public void addExpressionTest() throws SyntaxException, LexicalException
    {
   	 String input = "f*d++c+d-d-c+b/a";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();
   	 show(scanner);
   	 Parser parser = new Parser(scanner);
   	 parser.expression();
    }

    

    @Test
    public void expression51() throws SyntaxException, LexicalException {
   	 String input = "a+-b";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();  
   	 show(scanner);   
   	 Parser parser = new Parser(scanner);  
   	 parser.expression();
    }
    
    
    
    @Test
    public void testcase57() throws SyntaxException, LexicalException {
   	 String input =  "a identifier";
   	 show(input);
   	 Scanner scanner = new Scanner(input).scan();  
   	 show(scanner);   
   	 Parser SimpleParser = new Parser(scanner);
   	 SimpleParser.expression();   //Parse the program
    }
    

	
	@Test
	public void expressioninfra() throws SyntaxException, LexicalException {
		String input = "x:y:z";
		show(input);
		Scanner scanner = new Scanner(input).scan();  
		show(scanner);   
		Parser parser = new Parser(scanner);  
		parser.expression();    
	}
	
	@Test
	public void expressionTest2() throws SyntaxException, LexicalException {
		String input = "x = (y > z) ? a : b ";
		show(input);
		Scanner scanner = new Scanner(input).scan();
		show(scanner);
		Parser parser = new Parser(scanner);
		parser.expression();
	}

	
	@Test
	public void testDec12() throws LexicalException, SyntaxException {
		String input = "ash si <- @A|dig[false,true] ;";
		show(input);
		Scanner scanner = new Scanner(input).scan();  //Create a Scanner and initialize it
		show(scanner);   //Display the Scanner
		Parser parser = new Parser(scanner);  //
		parser.parse();  //Parse the program	
	}
	
	
}




