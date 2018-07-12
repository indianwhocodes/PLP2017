package cop5556fa17.AST;

import cop5556fa17.Scanner.Token;
import cop5556fa17.TypeUtils.Type;

public abstract class Expression extends ASTNode {
	
	public Type localtype;
	
	public Expression(Token firstToken) {
		super(firstToken);
	}

}
