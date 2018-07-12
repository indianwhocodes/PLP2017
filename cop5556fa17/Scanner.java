/* *
 * Scanner for the class project in COP5556 Programming Language Principles 
 * at the University of Florida, Fall 2017.
 * 
 * This software is solely for the educational benefit of students 
 * enrolled in the course during the Fall 2017 semester.  
 * 
 * This software, and any software derived from it,  may not be shared with others or posted to public web sites,
 * either during the course or afterwards.
 * 
 *  @Beverly A. Sanders, 2017
 */

package cop5556fa17;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Scanner {

	@SuppressWarnings("serial")
	public static class LexicalException extends Exception {

		int pos;
		int posInLine;
		int Line;

		public LexicalException(String message, int pos) {
			super(message);
			this.pos = pos;
		}

		public int getPos() {
			return pos;
		}

	}

	private static Map<String, Kind> ReservedWords = new HashMap<String, Kind>();
	{
		ReservedWords.put("x", Kind.KW_x);
		ReservedWords.put("X", Kind.KW_X);
		ReservedWords.put("y", Kind.KW_y);
		ReservedWords.put("Y", Kind.KW_Y);
		ReservedWords.put("r", Kind.KW_r);
		ReservedWords.put("R", Kind.KW_R);
		ReservedWords.put("a", Kind.KW_a);
		ReservedWords.put("A", Kind.KW_A);
		ReservedWords.put("Z", Kind.KW_Z);
		ReservedWords.put("DEF_X", Kind.KW_DEF_X);
		ReservedWords.put("DEF_Y", Kind.KW_DEF_Y);
		ReservedWords.put("SCREEN", Kind.KW_SCREEN);
		ReservedWords.put("cart_x", Kind.KW_cart_x);
		ReservedWords.put("cart_y", Kind.KW_cart_y);
		ReservedWords.put("polar_a", Kind.KW_polar_a);
		ReservedWords.put("polar_r", Kind.KW_polar_r);
		ReservedWords.put("abs", Kind.KW_abs);
		ReservedWords.put("sin", Kind.KW_sin);
		ReservedWords.put("cos", Kind.KW_cos);
		ReservedWords.put("atan", Kind.KW_atan);
		ReservedWords.put("log", Kind.KW_log);
		ReservedWords.put("image", Kind.KW_image);
		ReservedWords.put("int", Kind.KW_int);
		ReservedWords.put("boolean", Kind.KW_boolean);
		ReservedWords.put("url", Kind.KW_url);
		ReservedWords.put("file", Kind.KW_file);
		ReservedWords.put("true", Kind.BOOLEAN_LITERAL);
		ReservedWords.put("false", Kind.BOOLEAN_LITERAL);

	}

	public static enum Kind {
		IDENTIFIER, INTEGER_LITERAL, BOOLEAN_LITERAL, STRING_LITERAL, KW_x/* x */, KW_X/* X */, KW_y/* y */, KW_Y/* Y */, KW_r/*
																																 * r
																																 */, KW_R/*
																																			 * R
																																			 */, KW_a/*
																																						 * a
																																						 */, KW_A/*
																																									 * A
																																									 */, KW_Z/*
																																												 * Z
																																												 */, KW_DEF_X/*
																																																 * DEF_X
																																																 */, KW_DEF_Y/*
																																																				 * DEF_Y
																																																				 */, KW_SCREEN/*
																																																								 * SCREEN
																																																								 */, KW_cart_x/*
																																																												 * cart_x
																																																												 */, KW_cart_y/*
																																																																 * cart_y
																																																																 */, KW_polar_a/*
																																																																				 * polar_a
																																																																				 */, KW_polar_r/*
																																																																								 * polar_r
																																																																								 */, KW_abs/*
																																																																											 * abs
																																																																											 */, KW_sin/*
																																																																														 * sin
																																																																														 */, KW_cos/*
																																																																																	 * cos
																																																																																	 */, KW_atan/*
																																																																																				 * atan
																																																																																				 */, KW_log/*
																																																																																							 * log
																																																																																							 */, KW_image/*
																																																																																											 * image
																																																																																											 */, KW_int/*
																																																																																														 * int
																																																																																														 */, KW_boolean/*
																																																																																																		 * boolean
																																																																																																		 */, KW_url/*
																																																																																																					 * url
																																																																																																					 */, KW_file/*
																																																																																																								 * file
																																																																																																								 */, OP_ASSIGN/*
																																																																																																												 * =
																																																																																																												 */, OP_GT/*
																																																																																																															 * >
																																																																																																															 */, OP_LT/*
																																																																																																																		 * <
																																																																																																																		 */, OP_EXCL/*
																																																																																																																					 * !
																																																																																																																					 */, OP_Q/*
																																																																																																																								 * ?
																																																																																																																								 */, OP_COLON/*
																																																																																																																												 * :
																																																																																																																												 */, OP_EQ/*
																																																																																																																															 * ==
																																																																																																																															 */, OP_NEQ/*
																																																																																																																																		 * !=
																																																																																																																																		 */, OP_GE/*
																																																																																																																																					 * >=
																																																																																																																																					 */, OP_LE/*
																																																																																																																																								 * <=
																																																																																																																																								 */, OP_AND/*
																																																																																																																																											 * &
																																																																																																																																											 */, OP_OR/*
																																																																																																																																														 * |
																																																																																																																																														 */, OP_PLUS/*
																																																																																																																																																	 * +
																																																																																																																																																	 */, OP_MINUS/*
																																																																																																																																																					 * -
																																																																																																																																																					 */, OP_TIMES/*
																																																																																																																																																									 * *
																																																																																																																																																									 */, OP_DIV/*
																																																																																																																																																												 * /
																																																																																																																																																												 */, OP_MOD/*
																																																																																																																																																															 * %
																																																																																																																																																															 */, OP_POWER/*
																																																																																																																																																																			 * **
																																																																																																																																																																			 */, OP_AT/*
																																																																																																																																																																						 * @
																																																																																																																																																																						 */, OP_RARROW/*
																																																																																																																																																																										 * ->
																																																																																																																																																																										 */, OP_LARROW/*
																																																																																																																																																																														 * <-
																																																																																																																																																																														 */, LPAREN/*
																																																																																																																																																																																	 * (
																																																																																																																																																																																	 */, RPAREN/*
																																																																																																																																																																																				 * )
																																																																																																																																																																																				 */, LSQUARE/*
																																																																																																																																																																																							 * [
																																																																																																																																																																																							 */, RSQUARE/*
																																																																																																																																																																																										 * ]
																																																																																																																																																																																										 */, SEMI/*
																																																																																																																																																																																													 * ;
																																																																																																																																																																																													 */, COMMA/*
																																																																																																																																																																																																 * ,
																																																																																																																																																																																																 */, EOF;
	}

	public static enum State {
		start, in_identifier, in_digit, comment, in_string;
	}

	/**
	 * Class to represent Tokens.
	 * 
	 * This is defined as a (non-static) inner class which means that each Token
	 * instance is associated with a specific Scanner instance. We use this when
	 * some token methods access the chars array in the associated Scanner.
	 * 
	 * 
	 * @author Beverly Sanders
	 *
	 */
	public class Token {
		public final Kind kind;
		public final int pos;
		public final int length;
		public final int line;
		public final int pos_in_line;

		public Token(Kind kind, int pos, int length, int line, int pos_in_line) {
			super();
			this.kind = kind;
			this.pos = pos;
			this.length = length;
			this.line = line;
			this.pos_in_line = pos_in_line;
		}

		public String getText() {
			if (kind == Kind.STRING_LITERAL) {
				return chars2String(chars, pos, length);
			} else
				return String.copyValueOf(chars, pos, length);
		}

		/**
		 * To get the text of a StringLiteral, we need to remove the enclosing "
		 * characters and convert escaped characters to the represented character. For
		 * example the two characters \ t in the char array should be converted to a
		 * single tab character in the returned String
		 * 
		 * @param chars
		 * @param pos
		 * @param length
		 * @return
		 */
		private String chars2String(char[] chars, int pos, int length) {
			StringBuilder sb = new StringBuilder();
			for (int i = pos + 1; i < pos + length - 1; ++i) {// omit initial and final "
				char ch = chars[i];
				if (ch == '\\') { // handle escape
					i++;
					ch = chars[i];
					switch (ch) {
					case 'b':
						sb.append('\b');
						break;
					case 't':
						sb.append('\t');
						break;
					case 'f':
						sb.append('\f');
						break;
					case 'r':
						sb.append('\r'); // for completeness, line termination chars not allowed in String literals
						break;
					case 'n':
						sb.append('\n'); // for completeness, line termination chars not allowed in String literals
						break;
					case '\"':
						sb.append('\"');
						break;
					case '\'':
						sb.append('\'');
						break;
					case '\\':
						sb.append('\\');
						break;
					default:
						assert false;
						break;
					}
				} else {
					sb.append(ch);
				}
			}
			return sb.toString();
		}

		/**
		 * precondition: This Token is an INTEGER_LITERAL
		 * 
		 * @returns the integer value represented by the token
		 */
		public int intVal() {
			assert kind == Kind.INTEGER_LITERAL;
			return Integer.valueOf(String.copyValueOf(chars, pos, length));
		}

		public String toString() {
			return "[" + kind + "," + String.copyValueOf(chars, pos, length) + "," + pos + "," + length + "," + line
					+ "," + pos_in_line + "]";
		}

		/**
		 * Since we overrode equals, we need to override hashCode.
		 * https://docs.oracle.com/javase/8/docs/api/java/lang/Object.html#equals-java.lang.Object-
		 * 
		 * Both the equals and hashCode method were generated by eclipse
		 * 
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((kind == null) ? 0 : kind.hashCode());
			result = prime * result + length;
			result = prime * result + line;
			result = prime * result + pos;
			result = prime * result + pos_in_line;
			return result;
		}

		/**
		 * Override equals method to return true if other object is the same class and
		 * all fields are equal.
		 * 
		 * Overriding this creates an obligation to override hashCode.
		 * 
		 * Both hashCode and equals were generated by eclipse.
		 * 
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Token other = (Token) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (kind != other.kind)
				return false;
			if (length != other.length)
				return false;
			if (line != other.line)
				return false;
			if (pos != other.pos)
				return false;
			if (pos_in_line != other.pos_in_line)
				return false;
			return true;
		}

		/**
		 * used in equals to get the Scanner object this Token is associated with.
		 * 
		 * @return
		 */
		private Scanner getOuterType() {
			return Scanner.this;
		}

	}

	/**
	 * Extra character added to the end of the input characters to simplify the
	 * Scanner.
	 */
	static final char EOFchar = 0;

	/**
	 * The list of tokens created by the scan method.
	 */
	final ArrayList<Token> tokens;

	/**
	 * An array of characters representing the input. These are the characters from
	 * the input string plus and additional EOFchar at the end.
	 */
	final char[] chars;

	/**
	 * position of the next token to be returned by a call to nextToken
	 */
	private int nextTokenPos = 0;

	Scanner(String inputString) {
		int numChars = inputString.length();
		this.chars = Arrays.copyOf(inputString.toCharArray(), numChars + 1); // input string terminated with null char
		chars[numChars] = EOFchar;
		tokens = new ArrayList<Token>();
	}

	/**
	 * Method to scan the input and create a list of Tokens.
	 * 
	 * If an error is encountered during scanning, throw a LexicalException.
	 * 
	 * @return
	 * @throws LexicalException
	 */
	public Scanner scan() throws LexicalException {
		/* TODO Replace this with a correct and complete implementation!!! */
		int pos = 0;
		int line = 1;
		int posInLine = 1;
		int startPos = 0;

		State state = State.start;

		while (pos < chars.length - 1) {
			switch (state) {
			case start: {
				startPos = pos;
				
				switch (chars[pos]) {
				case ';':
					tokens.add(new Token(Kind.SEMI, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;
				// Separator of semicolon
				case ',':
					tokens.add(new Token(Kind.COMMA, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;
				// Separator of comma
				case '=':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_EQ, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_ASSIGN, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '<':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_LE, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else if (chars[pos + 1] == '-') {
							tokens.add(new Token(Kind.OP_LARROW, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_LT, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '>':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_GE, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_GT, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '!':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '=') {
							tokens.add(new Token(Kind.OP_NEQ, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_EXCL, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '?':
					tokens.add(new Token(Kind.OP_Q, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case ':':
					tokens.add(new Token(Kind.OP_COLON, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '@':
					tokens.add(new Token(Kind.OP_AT, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '&':
					tokens.add(new Token(Kind.OP_AND, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '|':
					tokens.add(new Token(Kind.OP_OR, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '+':
					tokens.add(new Token(Kind.OP_PLUS, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '-':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '>') {
							tokens.add(new Token(Kind.OP_RARROW, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_MINUS, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '*':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '*') {
							tokens.add(new Token(Kind.OP_POWER, pos, 2, line, posInLine));
							pos = pos + 2;
							posInLine = posInLine + 2;
						} else {
							tokens.add(new Token(Kind.OP_TIMES, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}

					break;

				case '/':
					if ((pos + 1) != chars.length) {
						if (chars[pos + 1] == '/') {
							state = State.comment;
						} else {
							tokens.add(new Token(Kind.OP_DIV, pos, 1, line, posInLine));
							pos++;
							posInLine++;
						}
					}
					break;

				case '%':
					tokens.add(new Token(Kind.OP_MOD, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case ')':
					tokens.add(new Token(Kind.RPAREN, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '(':
					tokens.add(new Token(Kind.LPAREN, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '[':
					tokens.add(new Token(Kind.LSQUARE, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case ']':
					tokens.add(new Token(Kind.RSQUARE, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;

				case '0':
					tokens.add(new Token(Kind.INTEGER_LITERAL, pos, 1, line, posInLine));
					pos++;
					posInLine++;
					break;
				// Separators over
				case '\"':
					state = State.in_string;
					pos++;
					break;
				case '\n':
					line++;
					posInLine = 1;
					pos++;
					break;

				case '\r':
					line++;
					posInLine = 1;
					pos++;
					if (chars[pos] == '\n')
						pos++;
					break;

				default: {
					if (Character.isDigit(chars[pos])) {
						if (chars[pos] != '0') {
							state = State.in_digit;
						}
					} else if (Character.isLetter(chars[pos]) || chars[pos] == '_' || chars[pos] == '$') {
						state = State.in_identifier;
					} else if (Character.isWhitespace(chars[pos])) {
						pos++;
						posInLine++;
					} else if (chars[pos] == '\t' || chars[pos] == '\b' || chars[pos] == '\'' || chars[pos] == '\\'
							|| chars[pos] == '\f') {
						throw new LexicalException("Error out of default", pos);
					} else// if(!isASCIIchar(chars[pos]))
					{
						throw new LexicalException("Error out of default", pos);
					}
					/*
					 * else { pos++; }//default
					 */
				} // switch chars[pos]

				}// end of case: start
			}
				break;
			case in_digit: {

				StringBuilder digits_str = new StringBuilder();
				for (; pos < chars.length - 1 && Character.isDigit(chars[pos]); pos++) {
					digits_str.append(chars[pos]);
				}
				// i=pos;
				try {
					Integer.parseInt(digits_str.toString());
					tokens.add(new Token(Kind.INTEGER_LITERAL, startPos, digits_str.length(), line, posInLine));
					state = State.start;
					posInLine = posInLine + digits_str.length();
					startPos += digits_str.length();
				} catch (NumberFormatException e) {
					throw new LexicalException("The error for out of bounds integer is at :" + startPos + ",",
							startPos);
				}
			}
				break;

			case in_identifier: {// Boolean literals, identifiers and keywords included in this case
				StringBuilder ident_str = new StringBuilder();
				for (; pos < chars.length - 1
						&& (Character.isLetterOrDigit(chars[pos]) || chars[pos] == '_' || chars[pos] == '$'); pos++) {
					ident_str.append(chars[pos]);
				}
				if (ReservedWords.containsKey(ident_str.toString())) {
					Kind k_word = ReservedWords.get(ident_str.toString());
					tokens.add(new Token(k_word, startPos, ident_str.length(), line, posInLine));
					posInLine += ident_str.length();
					startPos += ident_str.length();
					state = State.start;
				} else {
					tokens.add(new Token(Kind.IDENTIFIER, startPos, ident_str.length(), line, posInLine));
					posInLine += ident_str.length();
					startPos += ident_str.length();
					state = State.start;
				}

			}
				break;// end of case: in_identifier
			case in_string: {
				while (pos < chars.length - 1 && chars[pos] != '"') {
					if (chars[pos] == '\\') {
						pos++;
						
						if (pos < chars.length) {
							char temp = chars[pos];
							if (temp == 'b' || temp == 't' || temp == 'n' || temp == 'f' || temp == 'r' || temp == '\"'
									|| temp == '\'' || temp == '\\') {
								
								
								pos++;
							} else {
								
								throw new LexicalException("Invalid escape sequence - " + chars[pos + 1]
										+ " in String Literal after \\ at position: " + pos, pos);
							}
						}
					} else if (chars[pos] == '\n' || chars[pos] == '\r') {
						
						throw new LexicalException(
								"CR or LF is found at position: " + pos + " cannot become a part of String Literal",
								pos);
					} else if (isASCIIchar(chars[pos])) {
						pos++;
					} else if (chars[pos] == '\t' || chars[pos] == '\b' || chars[pos] == '\'' || chars[pos] == '\\'
							|| chars[pos] == '\f')
						pos++;
				}
				if (chars[pos] == '\"') {
					
					pos++;
					tokens.add(new Token(Kind.STRING_LITERAL, startPos, pos - startPos, line, posInLine));
					int str_len = pos - startPos;
					posInLine += str_len;
					startPos += str_len;
					state = State.start;
				} else
					throw new LexicalException("ERROR position" + pos, pos);
			}
				break;
			// Comment State Actions
			case comment: {
				if (chars[pos] == '\n' || chars[pos] == '\r' || chars[pos] == 0) {
					if (chars[pos] == '\n') {
						line++;
						posInLine = 1;
					} else if (chars[pos] == '\r') {
						line++;
						posInLine = 1;
						if (chars[pos + 1] == '\n') {
							pos++;
						}
					}
					pos++;
					state = State.start;
				} else {
					pos++;
				}

			}
				break;

			}// end of switch(state)
		} // end of for loop
		tokens.add(new Token(Kind.EOF, pos, 0, line, posInLine));
		return this;

	}// end of main class

	public static boolean isASCIIchar(char ch) {
		// TODO Auto-generated method stub
		return ch >= 0 && ch < 127;
	}

	/**
	 * Returns true if the internal interator has more Tokens
	 * 
	 * @return
	 */
	public boolean hasTokens() {
		return nextTokenPos < tokens.size();
	}

	/**
	 * Returns the next Token and updates the internal iterator so that the next
	 * call to nextToken will return the next token in the list.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return
	 */
	public Token nextToken() {
		return tokens.get(nextTokenPos++);
	}

	/**
	 * Returns the next Token, but does not update the internal iterator. This means
	 * that the next call to nextToken or peek will return the same Token as
	 * returned by this methods.
	 * 
	 * It is the callers responsibility to ensure that there is another Token.
	 * 
	 * Precondition: hasTokens()
	 * 
	 * @return next Token.
	 */
	public Token peek() {
		return tokens.get(nextTokenPos);
	}

	/**
	 * Resets the internal iterator so that the next call to peek or nextToken will
	 * return the first Token.
	 */
	public void reset() {
		nextTokenPos = 0;
	}

	/**
	 * Returns a String representation of the list of Tokens
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Tokens:\n");
		for (int i = 0; i < tokens.size(); i++) {
			sb.append(tokens.get(i)).append('\n');
		}
		return sb.toString();
	}

}
