%option noyywrap c++

%{
    #include <stdio.h>
    #include <stdlib.h>
    #include <string.h>
    #include "symtab.h"
    #include "parser.tab.h"
    extern FILE *yyin;
    extern FILE *yyout;
    
    int lineno = 1; // initialize to 1
    void ret_print(char *token_type);
    void yyerror();
%}

%x ML_COMMENT

letter      [a-zA-Z]
digit       [0-9]
digits      {digit}+
ID          {letter}({letter}|{digit})*
NUM         {digit}+|{digit}+"."{digits}(\L|"E"{digits})

%%

"char"           { return CHAR; }
"int"            { return INT; }
"float"          { return FLOAT; }
"double"         { return DOUBLE; }
"if"             { return IF; }
"else"           { return ELSE; }
"while"          { return WHILE; }
"for"            { return FOR; }


"+"|"-"                         { return ADDOP; }
"*"|"/"                         { return MULOP; }
"++"|"--"                       { return INCR; }
"||"                            { return OROP; }
"&&"                            { return ANDOP; }
"=="|"!="">"|">="|"<"|"<="      { return RELOP; }


"("             { return LPAREN; }
")"             { return RPAREN; }
"{"             { return LBRACE; }
"}"             { return RBRACE; }
";"             { return SEMI; }
"."             { return DOT; }
","             { return COMMA; }
"="             { return ASSIGN; }


{ID}            {
                    // insert identifier into symbol table
                    insert(yytext, strlen(yytext), UNDEF, lineno);
                    return ID;
                }
{NUM}           { return NUM; }


"\n"            { lineno += 1; }
[ \t\r\f]+          /* eat up whitespace */

.               { yyerror("Unrecognized character"); }

%%