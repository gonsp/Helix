grammar Helix;

options {
    output = AST;
}

@header {
package parser;
}

@lexer::header {
package parser;
}


// A program is a list of functions
prog	: ('hello'^)*
        ;

