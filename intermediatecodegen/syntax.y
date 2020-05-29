%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    extern FILE *yyin;
    extern int lineno;
    extern int yylex();
    void yyerror();
%}

/* token definition */
%token <val> CHAR INT FLOAT DOUBLE IF ELSE WHILE FOR CONTINUE BREAK VOID RETURN
%token <val> ADDOP MULOP INCR OROP ANDOP RELOP
%token <val> LPAREN RPAREN LBRACE RBRACE SEMI DOT COMMA ASSIGN
%token <val> IDENTIFIER ICONST FCONST CCONST STRING NUMBER


%type <val> PRIMITIVE_TYPE
%type <val> DECLARATION
%type <val> EXPRESSION
%type <val> ASSIGNMENT
%type <val> STATEMENT
%type <val> STATEMENT_LIST
%type <val> IF_STATEMENT
%type <val> WHILE_STATEMENT
%type <val> INFIX_OPERATOR

%start METHOD_BODY

%%

/* expression priorities and rules */

METHOD_BODY: STATEMENT_LIST ;
STATEMENT_LIST: STATEMENT | STATEMENT_LIST STATEMENT ;
STATEMENT: DECLARATION
          | IF_STATEMENT
          | WHILE_STATEMENT
          | ASSIGNMENT
          ;
DECLARATION: PRIMITIVE_TYPE IDENTIFIER SEMI ;
PRIMITIVE_TYPE: INT | FLOAT ;
IF_STATEMENT: IF LPAREN EXPRESSION RPAREN LBRACE STATEMENT RBRACE ELSE LBRACE STATEMENT RBRACE ;
WHILE_STATEMENT: WHILE LPAREN EXPRESSION RPAREN LBRACE STATEMENT RBRACE ;
ASSIGNMENT: IDENTIFIER ASSIGN EXPRESSION SEMI ;
EXPRESSION: NUMBER
          | EXPRESSION INFIX_OPERATOR EXPRESSION
          | IDENTIFIER
          | LPAREN EXPRESSION RPAREN
          ;
INFIX_OPERATOR: INCR | MULOP | RELOP |  OROP | ANDOP ;        