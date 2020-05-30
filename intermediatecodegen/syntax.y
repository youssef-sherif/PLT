%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    extern FILE *yyin;
    extern int lineno;
    extern int yylex();
    void yyerror();
%}

%union {              /* define stack type */
  double dval;
  int ival
  char cval;
  float fval;  
}

/* token definition */
%token <cval>   CHAR
%token <ival>   INT
%token <fval>   FLOAT
%token <dval>   DOUBLE
%token <idval>  IDENTIFIER
%token <aopval> OROP
%token <aopval> ANDOP
%token <aopval> RELOP
%token <aopval> ADDOP
%token <aopval> MULOP

%token IF
%token ELSE
%token WHILE
%token FOR
%token CONTINUE
%token BREAK
%token VOID
%token RETURN
%token INCR
%token LPAREN
%token RPAREN
%token LBRACE
%token RBRACE
%token SEMI
%token DOT
%token COMMA
%token ASSIGN
%token ICONST
%token FCONST
%token CCONST
%token STRING
%token NUMBER


%type <s_type>    PRIMITIVE_TYPE
%type <decl_type> DECLARATION
%type <expr_type> EXPRESSION
%type <asgn_type> ASSIGNMENT
%type <stmt_type> STATEMENT
%type <stmt_type> STATEMENT_LIST
%type <stmt_type> IF_STATEMENT
%type <stmt_type> WHILE_STATEMENT
%type <aop_type>  INFIX_OPERATOR

%start METHOD_BODY

%%

/* expression priorities and rules */

METHOD_BODY: STATEMENT_LIST ;
STATEMENT_LIST: STATEMENT | STATEMENT_LIST STATEMENT ;
STATEMENT: 
            DECLARATION {$$}
          | IF_STATEMENT {$$}
          | WHILE_STATEMENT {$$}
          | ASSIGNMENT {$$}
          ;
DECLARATION: PRIMITIVE_TYPE IDENTIFIER SEMI ;
PRIMITIVE_TYPE: 
            INT {$$}
          | FLOAT {$$}
          ;
IF_STATEMENT: IF LPAREN EXPRESSION RPAREN LBRACE STATEMENT RBRACE ELSE LBRACE STATEMENT RBRACE ;
WHILE_STATEMENT: WHILE LPAREN EXPRESSION RPAREN LBRACE STATEMENT RBRACE ;
ASSIGNMENT: IDENTIFIER ASSIGN EXPRESSION SEMI ;
EXPRESSION: 
            NUMBER {$$}
          | EXPRESSION INFIX_OPERATOR EXPRESSION {$$}
          | IDENTIFIER {$$}
          | LPAREN EXPRESSION RPAREN {$$}
          ;
INFIX_OPERATOR: 
            INCR {$$}
          | MULOP {$$}
          | RELOP {$$}
          | OROP {$$}
          | ANDOP {$$}
          ;        

%%

void yyerror ()
{
  fprintf(stderr, "Syntax error at line %d\n", lineno);
  exit(1);
}

int main (int argc, char *argv[]){

    // parsing
    int flag;
    yyin = fopen("code.txt", "r");
    flag = yyparse();
    fclose(yyin);
    
    return flag;
}