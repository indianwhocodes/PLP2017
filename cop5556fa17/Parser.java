package cop5556fa17;

import java.util.*;

import cop5556fa17.Scanner.Kind;
import cop5556fa17.Scanner.Token;
import cop5556fa17.AST.ASTNode;
import cop5556fa17.AST.Declaration;
import cop5556fa17.AST.Declaration_Image;
import cop5556fa17.AST.Declaration_SourceSink;
import cop5556fa17.AST.Declaration_Variable;
import cop5556fa17.AST.Expression;
import cop5556fa17.AST.Expression_Binary;
import cop5556fa17.AST.Expression_BooleanLit;
import cop5556fa17.AST.Expression_Conditional;
import cop5556fa17.AST.Expression_FunctionApp;
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
import cop5556fa17.AST.Statement;
import cop5556fa17.AST.Statement_Assign;
import cop5556fa17.AST.Statement_In;
import cop5556fa17.AST.Statement_Out;
import cop5556fa17.Parser.SyntaxException;

import static cop5556fa17.Scanner.Kind.*;

public class Parser {

	@SuppressWarnings("serial")
	public class SyntaxException extends Exception {
		Token t;

		public SyntaxException(Token t, String message) {
			super(message);
			this.t = t;
		}
	}

	Scanner scanner;
	Token t;
	ArrayList<ASTNode> decsAndStatements = new ArrayList<ASTNode>();

	Parser(Scanner scanner) {
		this.scanner = scanner;
		t = scanner.nextToken();
	}

	/**
	 * Main method called by compiler to parser input. Checks for EOF
	 * 
	 * @throws SyntaxException
	 */
	public Program parse() throws SyntaxException {
		Program p = program();
		matchEOF();
		return p;
	}

	/**
	 * Program ::= IDENTIFIER ( Declaration SEMI | Statement SEMI )*
	 * 
	 * Program is start symbol of our grammar.
	 * 
	 * @throws SyntaxException
	 */
	Program program() throws SyntaxException {
		// TODO implement this
		Program res = null;
		Token firstToken = t;
		Token name = null;

		if (t.kind == IDENTIFIER) {
			name = t;
			match(IDENTIFIER);

			if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)
					|| isKind(IDENTIFIER)) {
				while (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)
						|| isKind(IDENTIFIER)) {
					if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)) {
						decsAndStatements.add(declaration()); // call declaration
						if (isKind(SEMI)) {
							match(SEMI);

						} else {
							String message = "Expected Error in PROGRAM non-terminal at " + t.line + ":"
									+ t.pos_in_line;
							throw new SyntaxException(t, message);
						}
					} else {
						decsAndStatements.add(statement()); // call statement
						if (isKind(SEMI)) {
							match(SEMI);
						} else {
							String message = "Expected Error in PROGRAM non-terminal at " + t.kind + t.line + ":"
									+ t.pos_in_line;
							throw new SyntaxException(t, message);
						}
					}

				}
			}
			else if(isKind(EOF)) {
				
			}
			
		} else {
			String message = "Expected Error in PROGRAM non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return new Program(firstToken, name, decsAndStatements);
	}

	Declaration declaration() throws SyntaxException {
		// Implement declaration
		Declaration d = null;
		if (isKind(KW_int) || isKind(KW_boolean) || isKind(KW_image) || isKind(KW_url) || isKind(KW_file)) {
			if (isKind(KW_int) || isKind(KW_boolean))
				d = variabledeclaration();

			else if (isKind(KW_image))
				d = imagedeclaration();

			else if (isKind(KW_url) || isKind(KW_file))
				d = sourcesinkdeclaration();

			else {
				String message = "Expected Error in DECLARATION non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		}
		return d;
	}

	Declaration_Variable variabledeclaration() throws SyntaxException {
		// Implement variable_declaration
		Declaration_Variable res = null;
		Token firstToken = t;
		Token type = null;
		Token name = null;
		Expression e0 = null;
		if (isKind(KW_int) || isKind(KW_boolean)) {
			type = t;
			vartype();
			if (isKind(IDENTIFIER)) {
				name = t;
				match(IDENTIFIER);
				if (isKind(OP_ASSIGN)) {
					match(OP_ASSIGN);
					e0 = expression();
					res = new Declaration_Variable(firstToken, type, name, e0);
				} /*
				 * else return res;
				 */
			} else {
				String message = "Expected Error in VARIABLE DECLARATION non-terminal at " + t.line + ":"
						+ t.pos_in_line;
				throw new SyntaxException(t, message);
			}

		}
		return new Declaration_Variable(firstToken, type, name, e0);
	}

	void vartype() throws SyntaxException {
		// Implement var_type
		if (isKind(KW_int) || isKind(KW_boolean)) {
			if (isKind(KW_int))
				match(KW_int);

			else if (isKind(KW_boolean))
				match(KW_boolean);
			else {
				String message = "Expected Error in VARTYPE non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in VARTYPE non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
	}

	Declaration_SourceSink sourcesinkdeclaration() throws SyntaxException {
		// Implement source_sink_declaration
		Declaration_SourceSink res = null;
		Token firstToken = t;
		Token type = null;
		Token name = null;
		Source sor = null;
		if (isKind(KW_url) || isKind(KW_file)) {
			type = t;
			sourcesinktype();
			if (isKind(IDENTIFIER)) {
				name = t;
				match(IDENTIFIER);

				if (isKind(OP_ASSIGN)) {
					match(OP_ASSIGN);
					sor = source();
					res = new Declaration_SourceSink(firstToken, type, name, sor);
				} else {
					String message = "Expected Error in SOURCE_SINK_DECLARATION non-terminal at " + t.line + ":"
							+ t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			} else {
				String message = "Expected Error in SOURCE_SINK_DECLARATION at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in SOURCE_SINK_DECLARATION non-terminal at " + t.line + ":"
					+ t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return res;
	}

	Source source() throws SyntaxException {
		// Implement source
		Source res = null;
		Token firstToken = t;
		Expression paramNum = null;
		if (isKind(STRING_LITERAL) || isKind(OP_AT) || isKind(IDENTIFIER)) {
			if (isKind(STRING_LITERAL)) {
				match(STRING_LITERAL);
				res = new Source_StringLiteral(firstToken,firstToken.getText());

			}

			else if (isKind(OP_AT)) {
				match(OP_AT);
				paramNum = expression();
				res = new Source_CommandLineParam(firstToken, paramNum);
			}

			else if (isKind(IDENTIFIER)) {
				Token name = t;
				match(IDENTIFIER);
				res = new Source_Ident(firstToken, name);
			} else {
				String message = "Expected Error in SOURCE non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in SOURCE non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return res;
	}

	void sourcesinktype() throws SyntaxException {
		// Implement Source_sink_type
		if (isKind(KW_url) || isKind(KW_file)) {
			if (isKind(KW_url))
				match(KW_url);

			else if (isKind(KW_file))
				match(KW_file);

			else {
				String message = "Expected Error in SOURCE_SINK_TYPE non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in SOURCE_SINK_TYPE non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
	}

	Declaration_Image imagedeclaration() throws SyntaxException {
		// Implement image_declaration
		Declaration_Image res = null;
		Token firstToken = t;
		Token name = t;
		Expression e0 = null;
		Expression e1 = null;
		Source sour = null;
		if (isKind(KW_image)) {
			match(KW_image);
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				e0 = expression();
				if (isKind(COMMA)) {
					match(COMMA);
					e1 = expression();
					if (isKind(RSQUARE)) {
						match(RSQUARE);
					} else {
						String message = "Expected Error in IMAGE_DECLARATION non-terminal at " + t.line + ":"
								+ t.pos_in_line;
						throw new SyntaxException(t, message);
					}
				} else {
					String message = "Expected Error in IMAGE_DECLARATION non-terminal at " + t.line + ":"
							+ t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			}

			if (isKind(IDENTIFIER)) {
				name = t;
				match(IDENTIFIER);

				if (isKind(OP_LARROW)) {
					match(OP_LARROW);
					sour = source();
					res = new Declaration_Image(firstToken, e0, e1, name, sour);
				} /*
				 * else return;
				 */
			} else {
				String message = "Expected Error in IMAGE_DECLARATION non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		}
		return new Declaration_Image(firstToken, e0, e1, name, sour);
	}

	Sink sink() throws SyntaxException {
		// Implement image_declaration
		Token firstToken = t;
		Token name = null;
		Sink res = null;
		if (isKind(IDENTIFIER) || isKind(KW_SCREEN)) {
			if (isKind(IDENTIFIER)) {
				name = t;
				match(IDENTIFIER);
				res = new Sink_Ident(firstToken, name);
			} else if (isKind(KW_SCREEN)) {
				name = t;
				match(KW_SCREEN);
				res = new Sink_SCREEN(firstToken);
			} else {
				String message = "Expected Error in SINK non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in SINK non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return res;
	}

	Expression expression() throws SyntaxException {
		Token firstToken = t;
		Expression res = null;
		Expression e0 = null;
		Expression e1 = null;
		res = orexpression();
		if (isKind(OP_Q)) {
			match(OP_Q);
			e0 = expression();
			match(OP_COLON);
			e1 = expression();
			res = new Expression_Conditional(firstToken, res, e0, e1);
		}
		return res;
	}

	Expression orexpression() throws SyntaxException {
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = andexpression();
		if (isKind(OP_OR)) {
			while (isKind(OP_OR)) {
				op = t;
				match(OP_OR);
				e0 = new Expression_Binary(firstToken, res, op, andexpression());
				res = e0;
			}
		}
		return res;
	}

	Expression andexpression() throws SyntaxException {
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = eqexpression();
		if (isKind(OP_AND)) {
			while (isKind(OP_AND)) {
				op = t;
				match(OP_AND);
				e0 = new Expression_Binary(firstToken, res, op, eqexpression());
				res = e0;
			}
		}

		return res;
	}

	Expression eqexpression() throws SyntaxException {
		// Implement eq_expression
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = relexpression();
		if (isKind(OP_EQ) || isKind(OP_NEQ)) {
			while (isKind(OP_EQ) || isKind(OP_NEQ)) {
				op = t;
				if (isKind(OP_EQ)) {
					match(OP_EQ);
					e0 = new Expression_Binary(firstToken, res, op, relexpression());
					res = e0;
				} else if (isKind(OP_NEQ)) {
					match(OP_NEQ);
					e0 = new Expression_Binary(firstToken, res, op, relexpression());
					res = e0;
				} else {
					String message = "Expected Error in EQ_EXPRESSION non-terminal at " + t.line + ":" + t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			}
		}
		return res;
	}

	Expression relexpression() throws SyntaxException {
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = addexpression();
		if (isKind(OP_LT) || isKind(OP_GT) || isKind(OP_LE) || isKind(OP_GE)) {
			while (isKind(OP_LT) || isKind(OP_GT) || isKind(OP_LE) || isKind(OP_GE)) {
				op = t;
				if (isKind(OP_LT)) {
					match(OP_LT);
					e0 = new Expression_Binary(firstToken, res, op, addexpression());
					res = e0;
				} else if (isKind(OP_GT)) {
					match(OP_GT);
					e0 = new Expression_Binary(firstToken, res, op, addexpression());
					res = e0;
				} else if (isKind(OP_LE)) {
					match(OP_LE);
					e0 = new Expression_Binary(firstToken, res, op, addexpression());
					res = e0;
				} else if (isKind(OP_GE)) {
					match(OP_GE);
					e0 = new Expression_Binary(firstToken, res, op, addexpression());
					res = e0;
				} else {
					String message = "Expected Error in REL_EXPRESSION non-terminal at " + t.line + ":" + t.pos_in_line;
					throw new SyntaxException(t, message);
				}

			}
		}
		return res;
	}

	Expression addexpression() throws SyntaxException {
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = multexpression();
		if (isKind(OP_PLUS) || isKind(OP_MINUS)) {
			while (isKind(OP_PLUS) || isKind(OP_MINUS)) {
				op = t;
				if (isKind(OP_PLUS)) {
					match(OP_PLUS);
					e0 = new Expression_Binary(firstToken, res, op, multexpression());
					res = e0;
				} else if (isKind(OP_MINUS)) {
					match(OP_MINUS);
					e0 = new Expression_Binary(firstToken, res, op, multexpression());
					res = e0;
				} else {
					String message = "Expected Error in ADD_EXPRESSION non-terminal at " + t.line + ":" + t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			}
		}
		return res;

	}

	Expression multexpression() throws SyntaxException {
		Expression res = null;
		Expression e0 = null;
		Token firstToken = t;
		Token op = t;
		res = unaryexpression();
		if ((isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD))) {
			while (isKind(OP_TIMES) || isKind(OP_DIV) || isKind(OP_MOD)) {
				op = t;
				if (isKind(OP_TIMES)) {
					match(OP_TIMES);
					e0 = new Expression_Binary(firstToken, res, op, unaryexpression());
					res = e0;
				} else if (isKind(OP_DIV)) {
					match(OP_DIV);
					e0 = new Expression_Binary(firstToken, res, op, unaryexpression());
					res = e0;
				} else if (isKind(OP_MOD)) {
					match(OP_MOD);
					e0 = new Expression_Binary(firstToken, res, op, unaryexpression());
					res = e0;
				} else {
					String message = "Expected Error in MULT_EXPRESSION non-terminal at " + t.line + ":"
							+ t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			}
		}
		return res;

	}

	Expression unaryexpression() throws SyntaxException {
		// Implement unary_expression
		Token firstToken = t;
		Expression e = null;
		Expression res = null;
		if (isKind(OP_PLUS) || isKind(OP_MINUS)) {
			match(t.kind);
			e = unaryexpression();
			res = new Expression_Unary(firstToken, firstToken, e);
		} else if (isKind(OP_EXCL)) {
			match(OP_EXCL);
			e = unaryexpression();
			res = new Expression_Unary(firstToken, firstToken, e);
		} else if (isKind(INTEGER_LITERAL) || isKind(LPAREN) || isKind(KW_sin) || isKind(KW_cos) || isKind(KW_atan)
				|| isKind(KW_abs) || isKind(KW_cart_x) || isKind(KW_cart_y) || isKind(KW_polar_a) || isKind(KW_polar_r)
				|| isKind(BOOLEAN_LITERAL)) {
			res = primary();
		} else if (t.kind == IDENTIFIER) {
			res = identorpixelselectorexpression();
		} else if (t.kind == KW_x || t.kind == KW_y || t.kind == KW_r || t.kind == KW_a || t.kind == KW_X
				|| t.kind == KW_Y || t.kind == KW_Z || t.kind == KW_A || t.kind == KW_R || t.kind == KW_DEF_X
				|| t.kind == KW_DEF_Y) {
			match(t.kind);
			res = new Expression_PredefinedName(firstToken, firstToken.kind);
		} else {
			throw new SyntaxException(t, "Error in unary expression not plus-minus()");
		}
		return res;
	}

	Expression primary() throws SyntaxException {
		// Implement primary
		Token firstToken = t;
		Expression res = null;
		Expression e0 = null;
		if (isKind(BOOLEAN_LITERAL)) {
			match(BOOLEAN_LITERAL);
			res = new Expression_BooleanLit(firstToken, firstToken.length == 4 ? true : false);
		} else if (isKind(INTEGER_LITERAL)) {
			match(INTEGER_LITERAL);
			res = new Expression_IntLit(firstToken, firstToken.intVal());
		} else if (isKind(LPAREN)) {
			match(LPAREN);
			e0 = expression();// SOME CHANGES NEED TO BE MADE HERE DOUBT!!
			match(RPAREN);
			return e0;
		} else {
			res = functionapplication();
		}
		return res;
	}

	Expression identorpixelselectorexpression() throws SyntaxException {
		// Implement ident_or_pixel_selector_expression
		Token firstToken = t;
		Token name = null;
		Expression res = null;
		Index index = null;
		if (isKind(IDENTIFIER)) {
			name = t;
			match(IDENTIFIER);
			if (isKind(LSQUARE)) {
				match(LSQUARE);
				index = selector();
				if (isKind(RSQUARE)) {
					match(RSQUARE);
					res = new Expression_PixelSelector(firstToken, name, index);
				} else {
					String message = "Expected Error in IDENT/PIXELSELECTOR_EXP non-terminal at " + t.line + ":"
							+ t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			} else {
				res = new Expression_Ident(firstToken, name);
				// return ;
			}
		} else {
			String message = "Expected Error in IDENT/PIXELSELECTOR_EXP non-terminal at " + t.line + ":"
					+ t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return res;
	}

	Expression_FunctionApp functionapplication() throws SyntaxException {// Resolve errors
		// Implement function_application
		Token firstToken = t;
		Index index = null;
		Expression_FunctionApp res = null;
		Expression e0 = null;
		if (isKind(KW_sin) || isKind(KW_cos) || isKind(KW_atan) || isKind(KW_abs) || isKind(KW_cart_x)
				|| isKind(KW_cart_y) || isKind(KW_polar_a) || isKind(KW_polar_r)) {

			functionname();
			if (isKind(LPAREN)) {
				match(LPAREN);
				e0 = expression();
				if (isKind(RPAREN)) {
					match(RPAREN);
					res = new Expression_FunctionAppWithExprArg(firstToken, firstToken.kind, e0);
				}
			} else if (isKind(LSQUARE)) {
				match(LSQUARE);
				index = selector();
				if (isKind(RSQUARE)) {
					match(RSQUARE);
					res = new Expression_FunctionAppWithIndexArg(firstToken, firstToken.kind, index);
				} else {
					String message = "Expected Error in FUNCTION_APPLICATION non-terminal at " + t.line + ":"
							+ t.pos_in_line;
					throw new SyntaxException(t, message);
				}
			} else {
				String message = "Expected Error in FUNCTION_APPLICATION non-terminal at " + t.line + ":"
						+ t.pos_in_line;
				throw new SyntaxException(t, message);
			}

		} else {
			String message = "Expected Error in FUNCTION_APPLICATION non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return res;
	}

	void functionname() throws SyntaxException {
		// Implement function_name

		if (isKind(KW_sin) || isKind(KW_cos) || isKind(KW_atan) || isKind(KW_abs) || isKind(KW_cart_x)
				|| isKind(KW_cart_y) || isKind(KW_polar_a) || isKind(KW_polar_r)) {
			if (isKind(KW_sin))
				match(KW_sin);
			else if (isKind(KW_cos))
				match(KW_cos);
			else if (isKind(KW_atan))
				match(KW_atan);
			else if (isKind(KW_abs))
				match(KW_abs);
			else if (isKind(KW_cart_x))
				match(KW_cart_x);
			else if (isKind(KW_cart_y))
				match(KW_cart_y);
			else if (isKind(KW_polar_r))
				match(KW_polar_r);
			else if (isKind(KW_polar_a))
				match(KW_polar_a);
			else {
				String message = "Expected Error in FUNCTION_NAME non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		} else {
			String message = "Expected Error in FUNCTION_NAME non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
	}

	Index lhsselector() throws SyntaxException {
		// Implement lhs_selector
		Index index = null;
		if (isKind(LSQUARE)) {
			match(LSQUARE);
			if (isKind(KW_x) || isKind(KW_r)) {
				if (isKind(KW_x)) {
					// propagating the syntax exception upwards check to see if correct or not
					index = xyselector();
					match(RSQUARE);

				} else if (isKind(KW_r)) {
					index = raselector();
					match(RSQUARE);
				}

			} else {
				String message = "Expected Error in LHSSELECTOR non-terminal at " + t.line + ":" + t.pos_in_line;
				throw new SyntaxException(t, message);
			}
		}

		else {
			String message = "Expected Error in LHSSELECTOR non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return index;
	}

	Index xyselector() throws SyntaxException {
		// Implement xy_selector
		Token firstToken = t;
		Token second = null;
		Expression_PredefinedName e0 = null;
		Expression_PredefinedName e1 = null;
		Index index = null;
		if (isKind(KW_x)) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			match(KW_x);
			match(COMMA);
			second =  t;
			e1 = new Expression_PredefinedName(second, second.kind);
			match(KW_y);

			index = new Index(firstToken, e0, e1);
		} else {
			String message = "Expected Error in XYSELECTOR non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return index;
	}

	Index raselector() throws SyntaxException {
		// Implement ra_selector
		Token firstToken = t;
		Token second = null;
		Expression_PredefinedName e0 = null;
		Expression_PredefinedName e1 = null;
		Index index = null;
		if (isKind(KW_r)) {
			e0 = new Expression_PredefinedName(firstToken, firstToken.kind);
			match(KW_r);
			match(COMMA);
			second = t;
			e1 = new Expression_PredefinedName(second, second.kind);
			match(KW_a);
			index = new Index(firstToken, e0, e1);
		} else {
			String message = "Expected Error in RASELECTOR non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		return index;
	}

	Index selector() throws SyntaxException {
		Token firstToken = t;
		Expression e0 = null;
		Expression e1 = null;
		Index index = null;
		e0 = expression();

		if (isKind(COMMA)) {
			match(COMMA);
		} else {
			String message = "Expected Error in SELECTOR non-terminal at " + t.line + ":" + t.pos_in_line;
			throw new SyntaxException(t, message);
		}
		e1 = expression();
		index = new Index(firstToken, e0, e1);
		return index;

	}

	Statement statement() throws SyntaxException {
		// Implement statement

		Token firstToken = t;
		Statement res = null;
		Sink s1 = null;
		Source sou = null;
		Expression e0 = null;
		Token name = t;
		Index index = null;
		
		if (t.kind == IDENTIFIER) 
		{

			match(IDENTIFIER);

			if (isKind(LSQUARE) || isKind(OP_ASSIGN)) 
			{
				if (isKind(LSQUARE)) 
				{
					match(LSQUARE);
					index = lhsselector();
					match(RSQUARE);

					if (isKind(OP_ASSIGN)) {
						match(OP_ASSIGN);
					} else {
						throw new SyntaxException(t, "Error at OP_ASSIGN of the STATEMENT RULE");
					}
					e0 = expression();
				}
				if (isKind(OP_ASSIGN)) 
				{
				
					match(OP_ASSIGN);
					e0 = expression();
					
				}
				LHS l=new LHS(firstToken, firstToken, index);
				
				res = new Statement_Assign(firstToken, l, e0);

			}
			
			else if (isKind(OP_LARROW)) 
			{
				match(OP_LARROW);
				sou = source();
				res = new Statement_In(firstToken, name, sou);
			} 
			else if (isKind(OP_RARROW)) {
				match(OP_RARROW);
				s1 = sink();
				res = new Statement_Out(firstToken, name, s1);
			}
			else
			{
				throw new SyntaxException(t, "Error in statement 3");
			}
		}
		

		else {
			throw new SyntaxException(t, "Error in statement 3");
		}

		return res;

	}

	/**
	 * Only for check at end of program. Does not "consume" EOF so no attempt to get
	 * nonexistent next Token.
	 * 
	 * @return
	 * @throws SyntaxException
	 */
	private Token matchEOF() throws SyntaxException {
		if (t.kind == EOF) {
			return t;
		}
		String message = "Expected EOL at " + t.kind + t.line + ":" + t.pos_in_line;
		throw new SyntaxException(t, message);
	}

	// User-defined operations
	private Token consume() {
		t = scanner.nextToken();
		return t;
	}

	private void match(Kind kind) throws SyntaxException {
		if (t.kind == kind) {
			consume();
		} else
			throw new SyntaxException(t, "Syntax Exception in MATCH");
	}

	private boolean isKind(Kind kind) {
		if (t.kind == kind)
			return true;
		else
			return false;
	}
}