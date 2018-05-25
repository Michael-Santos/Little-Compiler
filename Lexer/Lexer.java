package Lexer;

import java.util.*;
import Error.*;

public class Lexer {

	// apenas para verificacao lexica
	public static final boolean DEBUGLEXER = true;

	// "Método consultor"
  public Lexer( char []input, CompilerError error ) {
    this.input = input;
    // add an end-of-file label to make it easy to do the lexer
    input[input.length - 1] = '\0';
    // number of the current line
    lineNumber = 1;
    tokenPos = 0;
    this.error = error;
  }

  // contains the keywords (palavras reservadas da linguagem)
  static private Hashtable<String, Symbol> keywordsTable;

  // this code will be executed only once for each program execution
  static {
    keywordsTable = new Hashtable<String, Symbol>();
		keywordsTable.put( "program", Symbol.PROGRAM );
    keywordsTable.put( "begin", Symbol.BEGIN );
		keywordsTable.put( "end", Symbol.END );
		keywordsTable.put( "function", Symbol.FUNCTION );
		keywordsTable.put( "read" , Symbol.READ );
		keywordsTable.put( "write" , Symbol.WRITE );
		keywordsTable.put( "if" , Symbol.IF );
		keywordsTable.put( "then" , Symbol.THEN );
		keywordsTable.put( "else" , Symbol.ELSE );
		keywordsTable.put( "endif" , Symbol.ENDIF );
		keywordsTable.put( "return" , Symbol.RETURN );
		keywordsTable.put( "for" , Symbol.FOR );
		keywordsTable.put( "endfor" , Symbol.ENDFOR );
		keywordsTable.put( "float" , Symbol.FLOAT );
		keywordsTable.put( "int" , Symbol.INT );
		keywordsTable.put( "void" , Symbol.VOID );
		keywordsTable.put( "string" , Symbol.STRING );
  }

  public void nextToken() {
	  // Percorrer
	  while(input[tokenPos] == ' ' || input[tokenPos] == '\t' || input[tokenPos] == '\n' || input[tokenPos] == '\r') {
		  if (input[tokenPos] == '\n') {
			  // Quebra de linha
			  lineNumber++;
		  }
		  tokenPos++;
	  }

		// Comentário
		if (input[tokenPos] == '-' && input[tokenPos + 1] == '-') {
			while (input[tokenPos] != '\n' && input[tokenPos] != '\r' && input[tokenPos] != '\0') {
				tokenPos++;
			}
			nextToken();
		} else {
			// Final do arquivo
		  if (input[tokenPos] == '\0') {
			  token = Symbol.EOF;
		  } else {
			  StringBuffer strAux = new StringBuffer();
			  // Reconhecer token quando String entre duas aspas duplas
			  if (input[tokenPos] == '"') {
				  tokenPos++;

				  // Percorre string
				  while (input[tokenPos] != '"' && input[tokenPos] != '\0') {
					  strAux = strAux.append(input[tokenPos]);
					  tokenPos++;
				  }

				  if(input[tokenPos] == '"'){
						tokenPos++;
					} else if(input[tokenPos] == '\0') {
						error.signal("String interminada");
				  }

				  // Verifica tamanho da string
				  if (strAux.length() > MaxLengthString) {
					  error.signal("Overflow de String");
				  }
				  stringValue = strAux.toString();
				  token = Symbol.STRINGLITERAL;
			  } else {
					if(Character.isLetter(input[tokenPos])) {
						strAux = strAux.append(input[tokenPos]);
						tokenPos++;

					  // Reconhecer token quando palavra
					  while(Character.isLetterOrDigit(input[tokenPos])) {
						  // Concatena as letras numa única string
						  strAux = strAux.append(input[tokenPos]);
						  tokenPos++;
					  }
					}
				  // Verifica palavra
				  if (strAux.length() > 0) {
					  Symbol symbolAux;
					  // Busca pela keyword na tabela hash
					  symbolAux = keywordsTable.get(strAux.toString().toLowerCase());

					  if (symbolAux == null) {
						  token = Symbol.IDENT;
						  stringValue = strAux.toString();
					  } else {
						  token = symbolAux;
					  }

				  } else {
					  boolean flagFloat = false;
						if(Character.isDigit(input[tokenPos]) || input[tokenPos] == '.') {
							// Reconhecer token quando número
							while(Character.isDigit(input[tokenPos]) || input[tokenPos] == '.') {
								// Indica que é float (ou erro em caso de mais de um único ponto)
							  if (input[tokenPos] == '.' && !flagFloat) {
									flagFloat = true;
							  } else if (input[tokenPos] == '.' && flagFloat) {
							 		error.signal("Formato de número inválido");
							  }
								// Concatena os digitos numa unica string
								strAux = strAux.append(input[tokenPos]);
								tokenPos++;
							}

							// Verifica número do tipo "x."
							if (!Character.isDigit(input[tokenPos]) && input[tokenPos-1] == '.') {
								error.signal("Formato de número inválido");
							}


							// Detecta caractere
							if (Character.isLetter(input[tokenPos])) {
								error.signal("Identificador iniciado com número/Número inválido");
							}
						}

						// Verifica o número
					  if(strAux.length() > 0) {
						  // Verifica se é inteiro ou float
						  if(!flagFloat) {
							  // Inteiro
							  intValue = Integer.parseInt(strAux.toString());

							  if (intValue > MaxValueInteger) {
								  error.signal("Overflow de Inteiro");
							  }
							  token = Symbol.INTLITERAL;
						  } else {
							  // Float
							  floatValue = Float.parseFloat(strAux.toString());

							  /*if (floatInt > MaxValueFloat) {
								  error.signal("Overflow de Float");
							  }*/
							  token = Symbol.FLOATLITERAL;
						  }
					  } else {
						  // Reconhecer token que será um dos Symbol
						  switch (input[tokenPos]) {
							  case '+':
								  token = Symbol.PLUS;
							  break;
							  case '-':
								  token = Symbol.MINUS;
							  break;
							  case '*':
								  token = Symbol.MULT;
							  break;
							  case '/':
								  token = Symbol.DIV;
							  break;
							  case '=':
								  token = Symbol.EQUAL;
							  break;
							  case '<':
								  token = Symbol.LT;
							  break;
							  case '>':
								  token = Symbol.GT;
							  break;
							  case '(':
								  token = Symbol.LPAR;
							  break;
							  case ')':
								  token = Symbol.RPAR;
							  break;
							  case ':':
								  if (input[tokenPos + 1] == '=') {
									  token = Symbol.ASSIGN;
									  tokenPos++;
								  } else {
									  error.signal("Faltando em atribuição > =");
								  }
							  break;
							  case ',':
								  token = Symbol.COMMA;
							  break;
							  case ';':
								  token = Symbol.SEMICOLON;
							  break;
							  default:
								  error.signal("Erro léxico, {" + input[tokenPos] + "} não reconhecido");
						  }
							tokenPos++;
					  }
				  }
			  }
		  }
		}

	  if (DEBUGLEXER)
		  System.out.println(token.toString());
	  lastTokenPos = tokenPos - 1;
  }

  // return the line number of the last token got with getToken()
  public int getLineNumber() {
    return lineNumber;
  }

  public String getCurrentLine() {
    int i = lastTokenPos;
    if ( i == 0 )
      i = 1;
    else
      if ( i >= input.length )
        i = input.length;

    StringBuffer line = new StringBuffer();
    // go to the beginning of the line
    while ( i >= 1 && input[i] != '\n' )
      i--;
    if ( input[i] == '\n' )
      i++;
    // go to the end of the line putting it in variable line
    while ( input[i] != '\0' && input[i] != '\n' && input[i] != '\r' ) {
      line.append( input[i] );
      i++;
    }
    return line.toString();
  }

  public String getStringValue() {
      return stringValue;
  }

  public int getIntValue() {
      return intValue;
  }

	public float getFloatValue() {
			return floatValue;
	}

  /*public char getCharValue() {
      return charValue;
  }*/

  // current token
  public Symbol token;
  private String stringValue;
  private int intValue;
	private float floatValue;
  //private char charValue;

  private int tokenPos;
  //  input[lastTokenPos] is the last character of the last token
  private int lastTokenPos;
  // program given as input - source code
  private char []input;

  // number of current line. Starts with 1
  private int lineNumber;

  private CompilerError error;
  private static final int MaxValueInteger = 32768;
	//private static final int MaxValueFloat =
	private static final int MaxLengthString = 80;
}
