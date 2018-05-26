import Lexer.*;
import Error.*;
import AST.*;
import java.util.*;

public class Compiler {
    // Geração de código
    public Program compile( char []p_input ) {
        currentFunction = null;
        isComparation = false;

        symbolTable = new SymbolTable();
        error = new CompilerError(null);
        lexer = new Lexer(p_input, error);
        
        error.setLexer(lexer);
        
        lexer.nextToken();
        Program p = program();

        if (lexer.token != Symbol.EOF) {
            error.signal("Não chegou ao fim do arquivo");
        }
        
        // Verifica existência de função main
        if (symbolTable.getInGlobal("main") == null) {
          error.show("Não foi declarada função sem parâmetros 'main'");
        }
        
        // Verifica ocorrência de erro durante compilação
        if(error.wasAnErrorSignalled()) {
            return null;
        } else {
            return p;
        }
    }   

    /* Program */

    // program := PROGRAM id BEGIN pgm_body END
    public Program program() {
        if (lexer.token != Symbol.PROGRAM) {
            error.signal("Faltando 'PROGRAM'");
        }
        lexer.nextToken();

        Identifier id = id();

        if (lexer.token != Symbol.BEGIN) {
            error.signal("Faltando 'BEGIN'");
        }
        lexer.nextToken();

        Pgm_body pb = pgm_body();

        if (lexer.token != Symbol.END) {
            error.signal("Faltando 'END'");
        }
        lexer.nextToken();

         // Verificar se existe função main() ai então retornar o program;
        return new Program(id, pb);
    }

    // id := IDENTIFIER
    public Identifier id() {
        if (lexer.token != Symbol.IDENT) {
            error.signal("Faltando identificador (uma string)");
        }

        String st = lexer.getStringValue();
        lexer.nextToken();

        return new Identifier(st);
    }

        // pgm_body := decl func_declarations
        public Pgm_body pgm_body() {
            Decl d = decl();
            ArrayList<Func_decl> fd = func_declarations();

            return new Pgm_body(d, fd);
        }

    // decl := string_decl_list {decl} | var_decl_list {decl} | empty
    public Decl decl() {
        ArrayList<String_decl> sd = new ArrayList<String_decl>();
        ArrayList<Var_decl> vd = new ArrayList<Var_decl>();

        while(lexer.token == Symbol.STRING || lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            if (lexer.token == Symbol.STRING) {
                sd.addAll(string_decl_list());
            } else {
                vd.addAll(var_decl_list());
            }
        }
        return (new Decl(sd, vd));
    }

    /* Global String Declaration */

    // string_decl_list := string_decl {string_decl_tail}
    public ArrayList<String_decl> string_decl_list() {
        ArrayList<String_decl> st = new ArrayList<String_decl>();

        // Evitando adição de objetos nulos (empty)
        if (lexer.token == Symbol.STRING) {
            st.add(string_decl());
        }

        if (lexer.token == Symbol.STRING) {
            st.addAll(string_decl_tail());
        }
        return(st);
    }

    // string_decl := STRING id := str ; | empty
    public String_decl string_decl() {
        // Checagem sendo realizada em string_decl_list() e string_decl_tail()
        //if (lexer.token == Symbol.STRING) {
            lexer.nextToken();

            Identifier id = id();

            if (lexer.token != Symbol.ASSIGN) {
                error.signal("Faltando ':=' (símbolo de atribuição)");
            }
            lexer.nextToken();

            String st = str();

            // Cria objeto String_decl
            String_decl sd = new String_decl(id, st);
            Object type;

            // Verifica existência do identificador na tabela hash global, pois não estamos em uma função,
            // caso contrário verifica na hash local
            if ((type = symbolTable.getInGlobal(id.getName())) != null) {
                if ((type instanceof Func_decl)){
                    error.show("Função com identificador '" + id.getName() + "' já declarada");
                } else if(currentFunction == null){
                    error.show("Variável global com identificador '" + id.getName() + "' já declarada");
                } else if (symbolTable.getInLocal(id.getName()) != null) {
                    error.show("Variável local com identificador '" + id.getName() + "' já declarada");
                } else {
                    symbolTable.putInLocal(id.getName(), sd);
                }
            } else if (symbolTable.getInLocal(id.getName()) != null) {
                error.show("Variável local com identificador '" + id.getName() + "' já declarada");
            } else {
                // Verifica se o escopo é global ou local
                if (currentFunction == null) {
                    symbolTable.putInGlobal(id.getName(), sd);
                } else {
                    symbolTable.putInLocal(id.getName(), sd);
                }
            }

            // Verifica existência do identificador na tabela hash global e local
            /*if (symbolTable.getInGlobal(id.getName()) != null) {
                error.show("Variável global ou função com identificador '" + id.getName() + "' já declarada");
            } else if (symbolTable.getInLocal(id.getName()) != null) {
                error.show("Variável local com identificador '" + id.getName() + "' já declarada");
            } else {
                // Verifica se o escopo é global ou local
                if (currentFunction == null) {
                    symbolTable.putInGlobal(id.getName(), sd);
                } else {
                    symbolTable.putInLocal(id.getName(), sd);
                }
            }*/
            
            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Faltando ';' (ponto e vírgula)");
            }
            lexer.nextToken();

            return(sd);
        //}
    }

    // str := STRINGLITERAL
    public String str() {
        if (lexer.token != Symbol.STRINGLITERAL) {
            error.signal("Faltando 'literal' (string)");
        }

        String st = lexer.getStringValue();
        lexer.nextToken();

        return(st);
    }

    // string_decl_tail := string_decl {string_decl_tail}
    public ArrayList<String_decl> string_decl_tail() {
        ArrayList<String_decl> st = new ArrayList<String_decl>();

        while (lexer.token == Symbol.STRING) {
            st.add(string_decl());
        }
        return(st);
    }

    /* Variable Declaration */

    // var_decl_list := var_decl {var_decl_tail}
    public ArrayList<Var_decl> var_decl_list() {
        ArrayList<Var_decl> vd = new ArrayList<Var_decl>();
        vd.addAll(var_decl());

        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            vd.addAll(var_decl_tail());
        }
        return(vd);
    }

    // var_decl := var_type id_list ; | empty
    public ArrayList<Var_decl> var_decl() {
        ArrayList<Var_decl> vd = new ArrayList<Var_decl>();

        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            String vt = var_type();
            ArrayList<Identifier> id = id_list();

            for(Identifier identifier : id){
                Var_decl variable = new Var_decl(identifier, vt);
                Object type = null;

                // Verifica existência do identificador na tabela hash global, pois não estamos em uma função,
                // caso contrário verifica na hash local
                if((type = symbolTable.getInGlobal(identifier.getName())) != null){
                    if (type instanceof Func_decl) {
                        error.show("Função com identificador '" + identifier.getName() + "' já declarada");
                    } else if(currentFunction == null){
                        error.show("Variável global com identificador '" + identifier.getName() + "' já declarada");
                    } else if (symbolTable.getInLocal(identifier.getName()) != null) {
                        error.show("Variável local com identificador '" + identifier.getName() + "' já declarada");
                    } else {
                        symbolTable.putInLocal(identifier.getName(), variable);
                    }
                } else if (symbolTable.getInLocal(identifier.getName()) != null) {
                    error.show("Variável local com identificador '" + identifier.getName() + "' já declarada");
                } else {
                    // Verifica se o escopo é global ou local
                    if (currentFunction == null) {
                        symbolTable.putInGlobal(identifier.getName(), variable);
                    } else {
                        symbolTable.putInLocal(identifier.getName(), variable);
                    }
                }

                // Verifica existência do identificador na tabela hash global e local
                /*if (symbolTable.getInGlobal(identifier.getName()) != null) {
                    error.show("Variável global ou função com identificador '" + identifier.getName() + "' já declarada");
                } else if (symbolTable.getInLocal(identifier.getName()) != null) {
                    error.show("Variável local com identificador '" + identifier.getName() + "' já declarada");
                } else {
                    // Verifica se o escopo é global ou local
                    if (currentFunction == null) {
                        symbolTable.putInGlobal(identifier.getName(), variable);
                    } else {
                        symbolTable.putInLocal(identifier.getName(), variable);
                    }
                }*/
                
                vd.add(variable);
            }

            if (lexer.token != Symbol.SEMICOLON) {
                error.signal("Faltando ';' (ponto e vírgula)");
            }
            lexer.nextToken();
        }
        return(vd);
    }

    // var_type := FLOAT | INT
    public String var_type() {
        if (lexer.token != Symbol.FLOAT && lexer.token != Symbol.INT) {
            error.signal("Tipo de variável inválido");
        }

        boolean isInt = false;
        if (lexer.token == Symbol.INT){
            isInt = true;
        }

        lexer.nextToken();

        return(isInt ? "INT" : "FLOAT");

    }

    // any_type := var_type | VOID
    public String any_type() {
        if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            return(var_type());
        } else {
            if (lexer.token != Symbol.VOID) {
                error.signal("Tipo de variável inválido");
            }
            lexer.nextToken();
            return("VOID");
        }
    }

    // id_list := id id_tail
    public ArrayList<Identifier> id_list() {
        ArrayList<Identifier> id =  new ArrayList<Identifier>();

        id.add(id());
        id.addAll(id_tail());

        return(id);
    }

    // id_tail := , id id_tail | empty
    public ArrayList<Identifier> id_tail() {
        ArrayList<Identifier> id = new ArrayList<Identifier>();

        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            id.add(id());
        }
        return(id);
    }

    // var_decl_tail := var_decl {var_decl_tail}
    public ArrayList<Var_decl> var_decl_tail() {
        ArrayList<Var_decl> var_decl = new ArrayList<Var_decl>();

        while (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
            var_decl.addAll(var_decl());
        }

        return(var_decl);
    }

    /* Function Parameter List */

    // param_decl_list := param_decl param_decl_tail
    public ArrayList<Param_decl> param_decl_list() {
        ArrayList<Param_decl> pd = new ArrayList<Param_decl>();

        pd.add(param_decl());
        pd.addAll(param_decl_tail());

        return (pd);
    }

    // param_decl := var_type id
    public Param_decl param_decl() {
        String vt = var_type();
        Identifier id = id();
        
        Param_decl pd = new Param_decl(vt, id);
        Object type = null;

        // Verifica existência do identificador na tabela hash global e local
        if ((type = symbolTable.getInGlobal(id.getName())) != null) {
            if (type instanceof Func_decl){
                error.show("Função com identificador '" + id.getName() + "' já declarada");    
            }
        } else if (symbolTable.getInLocal(id.getName()) != null) {
            error.show("Parâmetro com identificador '" + id.getName() + "' já declarado");
        } else {
            symbolTable.putInLocal(id.getName(), pd);
        }
        
        return(pd);
    }

    // param_decl_tail := , param_decl param_decl_tail | empty
    public ArrayList<Param_decl> param_decl_tail() {
        ArrayList<Param_decl> pd = new ArrayList<Param_decl>();

        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            pd.add(param_decl());
        }

        return(pd);
    }

    /* Function Declarations */

    // func_declarations := func_decl {func_decl_tail}
    public ArrayList<Func_decl> func_declarations() {
        ArrayList<Func_decl> fd = new ArrayList<Func_decl>();

        // Evitando adição de objetos nulos (empty)
        if (lexer.token == Symbol.FUNCTION) {
            fd.add(func_decl());
        }

        if (lexer.token == Symbol.FUNCTION) {
            fd.addAll(func_decl_tail());
        }

        return(fd);
    }

    // func_decl := FUNCTION any_type id ({param_decl_list}) BEGIN func_body END | empty
    public Func_decl func_decl() {
        // Checagem sendo realizada em func_declarations() e func_decl_tail()
        //if (lexer.token == Symbol.FUNCTION) {
            lexer.nextToken();

            // Começo do escopo da função
            Func_decl fd = currentFunction = new Func_decl(any_type(), id());
            returnDefined = false;
            Object type = null;

            // Verifica existência do identificador na tabela hash global
            if ((type = symbolTable.getInGlobal(fd.getId().getName())) != null) {
               if (type instanceof Func_decl){
                    error.show("Função com identificador '" + fd.getId().getName() + "' já declarada");
               } else {
                    error.show("Variável global com identificador '" + fd.getId().getName() + "' já declarada");
               }
            } else {
                symbolTable.putInGlobal(fd.getId().getName(), fd);
            }

            if (lexer.token != Symbol.LPAR) {
                error.show("Faltando '(' (abre parênteses)");
            }

            lexer.nextToken();

            if (lexer.token == Symbol.FLOAT || lexer.token == Symbol.INT) {
                // Verifica se é main
                if(fd.getId().getName().equals("main")) {
                    error.show("Função 'main' não pode ter parâmetros");
                }
                fd.setPd(param_decl_list());
            } else {
                fd.setPd(new ArrayList<Param_decl>());
            }

            if (lexer.token != Symbol.RPAR) {
                error.signal("Faltando ')' (fecha parênteses)");
            }

            lexer.nextToken();

            if (lexer.token != Symbol.BEGIN) {
                error.signal("Faltando 'BEGIN'");
            }

            lexer.nextToken();

            fd.setFb(func_body());

            if (lexer.token != Symbol.END) {
                error.signal("Faltando 'END'");
            }

            // Esvaziar tabela hash de variáveis locais
            symbolTable.removeLocalIdent();
            
            // Verifica existência de retorno caso não seja uma função Void ou a existência caso contrário
            if(!(fd.getType().equals("VOID")) && (returnDefined == false)) {
                error.show("Função '" + fd.getId().getName() + "' de tipo não 'VOID' terminada sem valor de retorno definido");
            }
            
            // Fim do escopo da função
            currentFunction = null;

            lexer.nextToken();
        //}
        
        return(fd);
    }

    // func_decl_tail := func_decl {func_decl_tail}
    public ArrayList<Func_decl> func_decl_tail() {
        ArrayList<Func_decl> fd = new ArrayList<Func_decl>();

        while (lexer.token == Symbol.FUNCTION) {
            fd.add(func_decl());
        }

        return(fd);
    }

    // func_body := decl stmt_list
    public Func_body func_body() {
        return(new Func_body(decl(), stmt_list()));
    }

    /* Statement List */

    // stmt_list := stmt stmt_tail | empty
    public ArrayList<Stmt> stmt_list() {
        ArrayList<Stmt> st = new ArrayList<Stmt>();

        if (lexer.token == Symbol.IDENT || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR) {
            st.add(stmt());
            st.addAll(stmt_tail());
        }

        return(st);
    }

    // stmt_tail := stmt stmt_tail | empty
    public ArrayList<Stmt> stmt_tail() {
        ArrayList<Stmt> st = new ArrayList<Stmt>();

        while (lexer.token == Symbol.IDENT || lexer.token == Symbol.READ || lexer.token == Symbol.WRITE || lexer.token == Symbol.RETURN || lexer.token == Symbol.IF || lexer.token == Symbol.FOR) {
            st.add(stmt());
        }

        return(st);
    }

    // stmt := assign_stmt | read_stmt | write_stmt | return_stmt | if_stmt | for_stmt | call_stmt
    public Stmt stmt() {
        Stmt st = null;

        switch(lexer.token) {
            case IDENT:
                /* Checar 'assign_stmt' ou 'call_expr' para mais informações */
                Identifier id = id();
                
                if (lexer.token == Symbol.ASSIGN) {
                    st = assign_stmt(id);
                } else if (lexer.token == Symbol.LPAR) {
                    st = call_stmt(id);
                } else {
                    error.signal("Erro de instrução - faltando atribuição ou parênteses");
                }
                break;
            case READ:
                st = read_stmt();
                break;
            case WRITE:
                st = write_stmt();
                break;
            case RETURN:
                st = return_stmt();
                break;
            case IF:
                st = if_stmt();
                break;
            case FOR:
                st = for_stmt();
                break;
            default:
                error.signal("Erro de instrução - inválida ou inexistente");
        }

        return(st);
    }

    /* Basic Statements */

    // assign_stmt := assign_expr ;
    public Assign_stmt assign_stmt(Identifier id) {
        Assign_expr ae = assign_expr(id);
        Assign_stmt as = new Assign_stmt(ae); 


        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        return(as);
    }

    // assign_expr := id := expr
    /*  Para diferenciar 'assign_expr' de 'call_expr' em 'stmt',
        'assign_expr' é iniciado em ':=', não 'id' */
    public Assign_expr assign_expr(Identifier id) {
        //id();
        
        // Verifica existência do identificador na tabela hash global e local
        if (symbolTable.get(id.getName()) == null) {
            error.show("Variável '" + id.getName() + "' não declarada");
        } else {
            // Atribui o tipo como tipo "esperado"
            exprExpectedType = ((Typable) symbolTable.get(id.getName())).getType();
        }
        
        if (lexer.token != Symbol.ASSIGN) {
            error.signal("Faltando ':=' (símbolo de atribuição)");
        }
        
        lexer.nextToken();
        Assign_expr ae = new Assign_expr(id, expr());
        // O tipo não é mais esperado
        exprExpectedType = null;
        
        return(ae);
    }

    // read_stmt := READ ( id_list );
    public Read_stmt read_stmt() {
        if (lexer.token != Symbol.READ) {
            error.signal("Faltando 'READ'");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.LPAR) {
            error.signal("Faltando '(' (abre parênteses)");
        }
        lexer.nextToken();

        ArrayList<Identifier> id = id_list();
        ArrayList<Typable> typable = new ArrayList<Typable>();
        Object variavel = null;

        for(Identifier identifier : id){
            // Verifica existência do identificador na tabela hash global e local
            if ((variavel = symbolTable.get(identifier.getName())) == null) {
              error.show("Variável '" + identifier.getName() + "' não declarada");
            } else {
                // Verifica se o tipo é Void
                /*if (((Typable) symbolTable.get(identifier.getName())).getType().equals("VOID")) {
                    error.show("Escrevendo em algo do tipo 'VOID'");
                } else {
                    typable.add((Typable) symbolTable.get(identifier.getName()));
                }*/

                // Verifica se o parâmetro passado não é função ou string (gramática não permite função como parâmetro)
                // Semanticamente não é permitindo passar STRING como parâmetro para READ
                if (variavel instanceof Func_decl){
                    error.show("Função '" + identifier.getName() + "' é argumento inválido para READ");
                }else if (variavel instanceof String_decl) {
                    error.show("STRING '" + identifier.getName() + "' é argumento inválido para READ");
                }else {
                    typable.add((Typable) symbolTable.get(identifier.getName()));
                }
            }
        }

        if (lexer.token != Symbol.RPAR) {
            error.signal("Faltando ')' (fecha parênteses)");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        return(new Read_stmt(typable));
    }

    // write_stmt := WRITE ( id_list );
    public Write_stmt write_stmt() {
        if (lexer.token != Symbol.WRITE) {
            error.signal("Faltando 'WRITE'");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.LPAR) {
            error.signal("Faltando '(' (abre parênteses)");
        }
        lexer.nextToken();

        ArrayList<Identifier> id = id_list();
        ArrayList<Typable> typable = new ArrayList<Typable>();
        Object variavel = null;

        for(Identifier identifier : id){
            // Verifica existência do identificador na tabela hash global e local
            if ((variavel = symbolTable.get(identifier.getName())) == null) {
              error.show("Variável '" + identifier.getName() + "' não declarada");
            } else {
                // Verifica se o tipo é Void
                /*if (((Typable) symbolTable.get(identifier.getName())).getType().equals("VOID")) {
                    error.show("Lendo de algo do tipo 'VOID'");
                } else {
                    typable.add((Typable) symbolTable.get(identifier.getName()));
                }*/

                // Verifica se o parâmetro é função (gramática não permite função como parâmetro para write)
                if (variavel instanceof Func_decl){
                    error.show("Função '" + identifier.getName() + "' é argumento inválido para WRITE");
                }else {
                    typable.add((Typable) symbolTable.get(identifier.getName()));
                }
            }
        }

        if (lexer.token != Symbol.RPAR) {
            error.signal("Faltando ')' (fecha parênteses)");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        return(new Write_stmt(typable));
    }

    // return_stmt := RETURN expr ;
    public Return_stmt return_stmt() {
        returnDefined = true;
        
        if (lexer.token != Symbol.RETURN) {
            error.signal("Faltando 'RETURN'");
        }
        
        // Verifica se a função atual é void
        if (currentFunction.getType().equals("VOID")) {
            error.show("Função '" + currentFunction.getId().getName() + "' do tipo 'VOID' terminada com retorno");
        }
        
        lexer.nextToken();
        
        // Atribui o tipo da função atual como tipo "esperado"
        exprExpectedType = currentFunction.getType();
        
        Expr expr = expr();
        
        // O tipo não é mais esperado
        exprExpectedType = null;
        
        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        return(new Return_stmt(expr));
    }

    // call_stmt := call_expr ;
    public Call_stmt call_stmt(Identifier id) {
        // Verifica existência do identificador na tabela hash global
        if (symbolTable.getInGlobal(id.getName()) == null) {
          error.show("Função chamada '" + id.getName() + "' não declarada");
        }
        
        Call_expr cexpr = call_expr(id);
        
        if (lexer.token != Symbol.SEMICOLON) {
          error.signal("Faltando ';' (ponto e vírgula)");
        }
        
        lexer.nextToken();
        
        return(new Call_stmt(cexpr));
    }

    /* Expressions */

    // expr := factor expr_tail
    public Expr expr() {
        return(new Expr(factor(), expr_tail()));
    }

    // expr_tail := addop factor expr_tail | empty
    public ArrayList<Expr_tail> expr_tail() {
        ArrayList<Expr_tail> expr = new ArrayList<Expr_tail>();

        while (lexer.token == Symbol.PLUS || lexer.token == Symbol.MINUS) {
            expr.add(new Expr_tail(addop(), factor()));
        }

        return(expr);
    }

    // factor := postfix_expr factor_tail
    public Factor factor() {
        return(new Factor(postfix_expr(), factor_tail()));
    }

    // factor_tail := mulop postfix_expr factor_tail | empty
    public ArrayList<Factor_tail> factor_tail() {
        ArrayList<Factor_tail> factor = new ArrayList<Factor_tail>();

        while (lexer.token == Symbol.MULT || lexer.token == Symbol.DIV) {
            factor.add(new Factor_tail(mulop(), postfix_expr()));
        }

        return(factor);
    }

    // postfix_expr := primary | call_expr
    public Postfix_expr postfix_expr() {
        Postfix_expr ps = null;

        if (lexer.token == Symbol.IDENT) {
            /* Checar 'primary' ou 'call_expr' para mais informações */
            Identifier id = id();
            
            if (lexer.token == Symbol.LPAR) {
                // Verifica existência do identificador na tabela hash global
                if (symbolTable.getInGlobal(id.getName()) == null) {
                  error.show("Função chamada '" + id.getName() + "' não declarada");
                } else {
                    // Verifica o tipo
                    if(exprExpectedType == null) {
                        // Atribui o primeiro tipo como tipo "esperado"
                        exprExpectedType = ((Typable) symbolTable.get(id.getName())).getType();
                    } else {
                        // Verifica compatibilidade de tipos
                        String type = ((Typable) symbolTable.get(id.getName())).getType();

                        if(!exprExpectedType.equals(type)) {
                            error.show("Incompatibilidade de tipos - função '" + id.getName() + "' é do tipo '" + type + "', esperava-se ser do tipo '" + exprExpectedType + "'");
                        }
                    }
                }
                
                ps = call_expr(id);
            } else {
                // Verifica existência do identificador na tabela hash global e local
                if (symbolTable.get(id.getName()) == null) {
                  error.show("Variável '" + id.getName() + "' não declarada");
                } else {
                    // Verifica o tipo
                    if(exprExpectedType == null) {
                        // Atribui o primeiro tipo como tipo "esperado"
                        exprExpectedType = ((Typable) symbolTable.get(id.getName())).getType();
                    } else {
                        // Verifica compatibilidade de tipos
                        String type = ((Typable) symbolTable.get(id.getName())).getType();
                        
                        if (exprExpectedType.equals("FLOAT")){
                            if(!(exprExpectedType.equals("FLOAT") || exprExpectedType.equals("INT"))){
                                error.show("Incompatibilidade de tipos - variável '" + id.getName() + "' é do tipo '" + type + "', esperava-se ser do tipo 'FLOAT'");
                            }

                        }else if(!exprExpectedType.equals(type)) {
                            error.show("Incompatibilidade de tipos - variável '" + id.getName() + "' é do tipo '" + type + "', esperava-se ser do tipo '" + exprExpectedType + "'");
                        }
                    }
                }
                
                ps = id;
            }
        } else if (lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL) {
            return(primary());
        } else {
            error.signal("Erro de expressão - faltando identificador, parênteses ou número");
        }

        return(ps);
    }

    // call_expr := id ( {expr_list} )
    /*  Para diferenciar 'assign_expr' de 'call_expr' em 'stmt',
        e 'primary' de 'call_expr' em 'postfix_expr',
        'call_expr' é iniciado em '(', não 'id' */
    public Call_expr call_expr(Identifier id) {
        ArrayList<Expr> expr = new ArrayList<Expr>();
        //id();
        
        if (lexer.token != Symbol.LPAR) {
            error.signal("Faltando '(' (abre parênteses)");
        }
        lexer.nextToken();

        if (lexer.token == Symbol.IDENT || lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL) {
            expr = expr_list();
        }

        if (lexer.token != Symbol.RPAR) {
            error.signal("Faltando ')' (fecha parênteses)");
        }
        lexer.nextToken();

        return(new Call_expr(id, expr));
    }

    // expr_list := expr expr_list_tail
    public ArrayList<Expr> expr_list() {
        ArrayList<Expr> expr = new ArrayList<Expr>();

        expr.add(expr());
        expr.addAll(expr_list_tail());

        return(expr);
    }

    // expr_list_tail := , expr expr_list_tail | empty
    public ArrayList<Expr> expr_list_tail() {
        ArrayList<Expr> expr = new ArrayList<Expr>();

        while (lexer.token == Symbol.COMMA) {
            lexer.nextToken();
            expr.add(expr());
        }

        return(expr);
    }

    // primary := (expr) | id | INTLITERAL | FLOATLITERAL
    /*  Para 'primary' de 'call_expr' em 'postfix_expr',
        'primary' desconsidera 'id', que é tratado em 'postfix_expr' */
    public Postfix_expr primary() {
        Postfix_expr postfix = null;

        if (lexer.token == Symbol.INTLITERAL) {
            // Verifica o tipo
            if(exprExpectedType == null) {
                // Atribui o primeiro tipo como tipo "esperado"
                exprExpectedType = "INT";
            } else {
                
                if(!(exprExpectedType.equals("INT") || exprExpectedType.equals("FLOAT"))){
                    error.show("Incompatibilidade de tipos - declarado literal 'INT', esperava-se ser do tipo '" + exprExpectedType + "'");
                }

                //if(!exprExpectedType.equals("INT") ) {
                //    error.show("Incompatibilidade de tipos - declarado literal 'INT', esperava-se ser do tipo '" + exprExpectedType + "'");
                //}
            }
            
            postfix = new Int_literal(lexer.getIntValue());
            lexer.nextToken();
        } else if(lexer.token == Symbol.FLOATLITERAL){
            // Verifica o tipo
            if(exprExpectedType == null) {
                // Atribui o primeiro tipo como tipo "esperado"
                exprExpectedType = "FLOAT";
            } else {
                if(!exprExpectedType.equals("FLOAT")) {
                    // Necessário quando há comparações entre INT e FLOAT
                    if(!(exprExpectedType.equals("INT") && isComparation)){
                         error.show("Incompatibilidade de tipos - declarado literal 'FLOAT', esperava-se ser do tipo '" + exprExpectedType + "'");
                    }
                }
            }
            
            postfix = new Float_literal(lexer.getFloatValue());
            lexer.nextToken();
        } else if (lexer.token == Symbol.LPAR) {
            lexer.nextToken();

            postfix = expr();

            if (lexer.token != Symbol.RPAR) {
                error.signal("Faltando ')' (fecha parênteses)");
            }
            lexer.nextToken();
        } /*else if (lexer.token == Symbol.IDENT) {
            id();
        }*/
        else {
            error.signal("Erro de expressão - faltando parênteses ou número");
        }

        return(postfix);
    }

    // addop := + | -
    public String addop() {
        Symbol op;

        if (lexer.token != Symbol.PLUS && lexer.token != Symbol.MINUS) {
            error.signal("Operador inválido - aceita '+' ou '-' (adição ou subtração)");
        }

        op = lexer.token;
        lexer.nextToken();

        return((op == Symbol.PLUS) ? "+" : "-");
    }

    // mulop * | /
    public String mulop() {
        Symbol op;

        if (lexer.token != Symbol.MULT && lexer.token != Symbol.DIV) {
            error.signal("Operador inválido - aceita '*' ou '/' (multiplicação ou divisão)");
        }

        op = lexer.token;
        lexer.nextToken();

        return((op == Symbol.MULT) ? "*" : "/");
    }

    /* Complex Statements and Condition */

    // if_stmt := IF ( cond ) THEN stmt_list else_part ENDIF
    public If_stmt if_stmt() {
        if (lexer.token != Symbol.IF) {
            error.signal("Faltando 'IF'");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.LPAR) {
            error.signal("Faltando '(' (abre parênteses)");
        }
        lexer.nextToken();

        Cond cond = cond();

        if (lexer.token != Symbol.RPAR) {
            error.signal("Faltando ')' (fecha parênteses)");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.THEN) {
            error.signal("Faltando 'THEN'");
        }
        lexer.nextToken();

        ArrayList<Stmt> stmt_list = stmt_list();
        Else_part else_part = else_part();

        if (lexer.token != Symbol.ENDIF) {
            error.signal("Faltando 'ENDIF'");
        }
        lexer.nextToken();

        return(new If_stmt(cond, stmt_list, else_part));
    }

    // else_part := ELSE stmt_list | empty
    public Else_part else_part() {
        if (lexer.token == Symbol.ELSE) {
            lexer.nextToken();
            return(new Else_part(stmt_list()));
        }
        return(null);
    }

    // cond := expr compop expr
    public Cond cond() {
        exprExpectedType = null;
        isComparation = true;

        Expr exprLeft = expr();
        //String exprLeftType = exprExpectedType;
        
        String compop = compop();
        
        //exprExpectedType = null;
        Expr exprRight = expr();
        //String exprRightType = exprExpectedType;
        
        // Verifica o tipo
        //if(!exprLeftType.equals(exprRightType)) {
        //    error.show("Incompatibilidade de tipos - expressão à esquerda é do tipo '" + exprLeftType + "', expressão à direita é do tipo '" + exprRightType + "'");
        //}
        
        // O tipo não é mais esperado
        isComparation = false;
        exprExpectedType = null;
        
        return(new Cond(exprLeft, compop, exprRight));
    }

    // compop := < | > | =
    public String compop() {
        String op = null;

        switch(lexer.token) {
            case LT:
                op = "<";
                break;
            case GT:
                op = ">";
                break;
            case EQUAL:
                op = "=";
                break;
            default:
                error.signal("Condição inválida - aceita '<', '>' ou '=' (menor que, maior que ou igual)");
        }

        lexer.nextToken();
        return(op);
    }

    // for_stmt := FOR ({assign_expr}; {cond}; {assign_expr}) stmt_list ENDFOR
    public For_stmt for_stmt() {
        Assign_expr assign_init = null, assign_step = null;
        Cond cond = null;
        ArrayList<Stmt> stmt_list;

        if (lexer.token != Symbol.FOR) {
            error.signal("Faltando 'FOR'");
        }
        lexer.nextToken();

        if (lexer.token != Symbol.LPAR) {
            error.signal("Faltando '(' (abre parênteses)");
        }
        lexer.nextToken();

        /* Checar 'assign_expr' para mais informações */
        if (lexer.token == Symbol.IDENT) {
            assign_init = assign_expr(id());
        }

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        if (lexer.token == Symbol.IDENT || lexer.token == Symbol.LPAR || lexer.token == Symbol.INTLITERAL || lexer.token == Symbol.FLOATLITERAL) {
            cond = cond();
        }

        if (lexer.token != Symbol.SEMICOLON) {
            error.signal("Faltando ';' (ponto e vírgula)");
        }
        lexer.nextToken();

        /* Checar 'assign_expr' para mais informações */
        if (lexer.token == Symbol.IDENT) {
            assign_step = assign_expr(id());
        }

        if (lexer.token != Symbol.RPAR) {
            error.signal("Faltando ')' (fecha parênteses)");
        }
        lexer.nextToken();

        stmt_list = stmt_list();

        if (lexer.token != Symbol.ENDFOR) {
            error.signal("Faltando 'ENDFOR'");
        }
        lexer.nextToken();

        return(new For_stmt(assign_init, cond, assign_step, stmt_list));
    }

    private SymbolTable symbolTable;
    private Lexer lexer;
    private CompilerError error;
    
    private Func_decl currentFunction;
    private String exprExpectedType;
    private Boolean isComparation;
    private Boolean returnDefined;
}
