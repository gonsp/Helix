grammar Helix;

options {
    output = AST;
    ASTLabelType = HelixTree;
}

tokens {
    LIST_FUNCTIONS; // List of functions (the root of the tree)
    DEFFUNC;
    ASSIGN;     // Assignment instruction
    COORD;
    ATTRIB;
    COORDACCESS;
    PARAMS;     // List of parameters in the declaration of a function
    FUNCALL;    // Function call
    ARGLIST;    // List of arguments passed in a function call
    LIST_INSTR; // Block of instructions
    BOOLEAN;    // Boolean atom (for Boolean constants "true" or "false")
    PVALUE;     // Parameter by value in the list of parameters
    PREF;       // Parameter by reference in the list of parameters
}

@header {
package Helix.parser;
import Helix.interpreter.HelixTree;
}

@lexer::header {
package Helix.parser;
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
        :	 '{' instruction (instruction)* '}' NL?
            -> ^(LIST_INSTR instruction+)
        ;

instruction
        : line_instruction NL!
        | block_instruction
        ;

// The different types of instructions
line_instruction
        :	assign          // Assignment
        |   funcall         // Call to a procedure (no result produced)
        |	return_stmt     // Return statement
        |                   // Nothing
        ;

block_instruction
        :   ite_stmt
        |   while_stmt
        ;

// Assignment
assign	:	accessor eq=EQUAL expr -> ^(ASSIGN[$eq,":="] accessor expr)
        ;


// if-then-else (else is optional)
ite_stmt	:	IF^ expr block_instructions (ELSE! block_instructions)?
            ;

// while statement
while_stmt	:	WHILE^ expr block_instructions
            ;

// Return statement with an expression
return_stmt	:	RETURN^ expr?
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
atom    :   NUM
        |   coord
        |   id_access
        |   (b=TRUE | b=FALSE)  -> ^(BOOLEAN[$b,$b.text])
        |   funcall
        |   '('! expr ')'!
        ;

coord   : '[' n1=expr ',' n2=expr ',' n3=expr ']' -> ^(COORD $n1 $n2 $n3)
        ;

accessor    :	id_access
            |   coord_access
	        ;

coord_access :   '[' (id1=id_access | v1=VOIDACCESS) ',' (id2=id_access | v2=VOIDACCESS) ',' (id3=id_access | v3=VOIDACCESS) ']' -> ^(COORDACCESS $id1? $v1? $id2? $v2? $id3? $v3?)
            ;

id_access   :	(id=ID -> $id) ('.' id_atr -> ^(ATTRIB $id id_atr))?
            ;

id_atr  :   LAT
        |   LNG
        |   ALT
        ;

// A function call has a lits of arguments in parenthesis (possibly empty)
funcall :   builtin '(' expr_list? ')' -> ^(DEFFUNC builtin ^(ARGLIST expr_list?))
        |   ID '(' expr_list? ')' -> ^(FUNCALL ID ^(ARGLIST expr_list?))
        ;

builtin :   GET_POS
        |   MOVE
        |   FORWARD
        |   ROTATE
        |   TAKEOFF
        |   LAND
        |   SLEEP
        |   UPF
        |   DOWNF
        |   RIGHT
        |   LEFT
        |   BACKWARDS
        |   LOOKAT
        |   PRINT
        |   MOVETO 
        |   SET_DIR
        |   GET_DIR
        |   NORTH  
        |   SOUTH  
        |   EAST   
        |   WEST   
        ;

// A list of expressions separated by commas
expr_list:  expr (','! expr)*
        ;

// Built-in function tokens
GET_POS :   'getPos';
GET_DIR :   'getDir';
SET_DIR :   'setDir';
MOVE    :   'move';
MOVETO  :   'moveTo';
ROTATE  :   'rotate';
TAKEOFF :   'takeOff';
LAND    :   'land';
SLEEP   :   'sleep';
UPF     :   'up';
DOWNF   :   'down';
RIGHT   :   'right';
LEFT    :   'left';
FORWARD :   'forward';
BACKWARD:   'backward';
LOOKAT  :   'lookAt';
PRINT   :   'print';
NORTH   :   'north';
SOUTH   :   'south';
EAST    :   'east';
WEST    :   'west';

// Default tokens
NL  : '\n' '\n'*;
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
WHILE	: 'while' ;
DO	    : 'do' ;
DEF	    : 'def' ;
RETURN	: 'return' ;
TRUE    : 'true' ;
FALSE   : 'false';
LAT     : 'lat';
LNG     : 'lng';
ALT     : 'alt';
VOIDACCESS  : '_';
ID  	:	('a'..'z'|'A'..'Z'|'_') ('a'..'z'|'A'..'Z'|'0'..'9'|'_')* ;
NUM     :	'0'..'9'+ ('.' '0'..'9'+|);

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
        ) {$channel=HIDDEN;}
    	;
//        | '\n'


