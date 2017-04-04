grammar Helix;

options {
    output = AST;
}

tokens {
    LIST_FUNCTIONS; // List of functions (the root of the tree)
    ASSIGN;     // Assignment instruction
    ACCESSVEC;
    SIZEVEC;
    PARAMS;     // List of parameters in the declaration of a function
    FUNCALL;    // Function call
    ARGLIST;    // List of arguments passed in a function call
    LIST_INSTR; // Block of instructions
    BOOLEAN;    // Boolean atom (for Boolean constants "true" or "false")
    PVALUE;     // Parameter by value in the list of parameters
    PREF;       // Parameter by reference in the list of parameters
}

@header {
package parser;
}

@lexer::header {
package parser;
}

// A program is a list of functions
prog	: func+ EOF -> ^(LIST_FUNCTIONS func+)
        ;
            
// A function has a name, a list of parameters and a block of instructions	
func	: DEF ID params block_instructions -> ^(DEF ID params block_instructions)
        ;

// The list of parameters grouped in a subtree (it can be empty)
params	: '(' paramlist? ')' -> ^(PARAMS paramlist?)
        ;

// Parameters are separated by commas
paramlist: param (','! param)*
        ;

// Parameters with & as prefix are passed by reference
// Only one node with the name of the parameter is created
param   :   '&' id=ID -> ^(PREF[$id,$id.text])
        |   id=ID -> ^(PVALUE[$id,$id.text])
        ;

// A list of instructions, all of them gouped in a subtree
block_instructions
        :	 '{' instruction (EOLS instruction)* '}' EOLS
            -> ^(LIST_INSTR instruction+)
        ;

// The different types of instructions
instruction
        :	assign          // Assignment
        |	ite_stmt        // if-then-else
        |	while_stmt      // while statement
        |   funcall         // Call to a procedure (no result produced)
        |	return_stmt     // Return statement
        |	read            // Read a variable
        | 	write           // Write a string or an expression
        |                   // Nothing
        ;

// Assignment
assign	:	accessor eq=EQUAL expr -> ^(ASSIGN[$eq,":="] accessor expr)
        ;

accessor :	(id=ID -> $id) (( '[' e=expr ']') -> ^(ACCESSVEC $id $e))?
	 ;

//
//vecindex : 	'[' e=expr ']' -> (ACCESSVEC^ e)
//	 ;

// if-then-else (else is optional)
ite_stmt	:	IF^ expr block_instructions (ELSE! block_instructions)?
            ;

// while statement
while_stmt	:	WHILE^ expr block_instructions
            ;

// Return statement with an expression
return_stmt	:	RETURN^ expr?
        ;

// Read a variable
read	:	READ^ ID
        ;

// Write an expression or a string
write	:   WRITE^ (expr | STRING )
        ;

// Grammar for expressions with boolean, relational and aritmetic operators
expr    :   boolterm (OR^ boolterm)*
        ;

boolterm:   boolfact (AND^ boolfact)*
        ;

boolfact:   num_expr ((EQUAL^ | NOT_EQUAL^ | LT^ | LE^ | GT^ | GE^) num_expr)?
        ;

num_expr:   term ( (PLUS^ | MINUS^) term)*
        ;

term    :   factor ( (MUL^ | DIV^ | MOD^) factor)*
        ;

factor  :   (NOT^ | PLUS^ | MINUS^)? atom
        ;

// Atom of the expressions (variables, integer and boolean literals).
// An atom can also be a function call or another expression
// in parenthesis
atom    :   accessor
        |   INT
        |   (b=TRUE | b=FALSE)  -> ^(BOOLEAN[$b,$b.text])
        |   funcall
        |   '('! expr ')'!
        ;

// A function call has a lits of arguments in parenthesis (possibly empty)
funcall :   ID '(' expr_list? ')' -> ^(FUNCALL ID ^(ARGLIST expr_list?))
        ;

// A list of expressions separated by commas
expr_list:  expr (','! expr)*
        ;

// Basic tokens
EOLS    : '\n' '\n'* ;
EQUAL	: '=' ;
NOT_EQUAL: '!=' ;
LT	    : '<' ;
LE	    : '<=';
GT	    : '>';
GE	    : '>=';
PLUS	: '+' ;
MINUS	: '-' ;
MUL	    : '*';
DIV	    : '/';
MOD	    : '%' ;
NOT	    : 'not';
AND	    : 'and' ;
OR	    : 'or' ;	
IF  	: 'if' ;
THEN	: 'then' ;
ELSE	: 'else' ;
ENDIF	: 'endif' ;
WHILE	: 'while' ;
DO	    : 'do' ;
ENDWHILE: 'endwhile' ;
DEF	    : 'def' ;
ENDFUNC	: 'endfunc' ;
RETURN	: 'return' ;
READ	: 'read' ;
WRITE	: 'write' ;
TRUE    : 'true' ;
FALSE   : 'false';
ID  	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
INT 	:	'0'..'9'+ ;

// C-style comments
COMMENT	: '//' ~('\n'|'\r')* '\r'? '\n' {$channel=HIDDEN;}
    	| '/*' ( options {greedy=false;} : . )* '*/' {$channel=HIDDEN;}
    	;

// Strings (in quotes) with escape sequences        
STRING  :  '"' ( ESC_SEQ | ~('\\'|'"') )* '"'
        ;

fragment
ESC_SEQ
    :   '\\' ('b'|'t'|'n'|'f'|'r'|'\"'|'\''|'\\')
    ;

// White spaces
WS  	: ( ' '
        | '\t'
        | '\r'
//        | '\n'
        ) {$channel=HIDDEN;}
    	;


