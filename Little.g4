lexer grammar Little;

KEYWORD:('PROGRAM'|'BEGIN'|'END'|'FUNCTION'|'READ'|'WRITE'|'IF'|'ELSE'|'ENDIF'|'WHILE'|'ENDWHILE'|'CONTINUE'|'BREAK'|'RETURN'|'INT'|'VOID'|'STRING'|'FLOAT');

IDENTIFIER: [A-Za-z]+[A-Za-z]*[0-9]*;
INTLITERAL: [0-9]+;
FLOATLITERAL: [0-9]*[.][0-9]+;
STRINGLITERAL: '"' ~["]* '"';
COMMENT: '--' ~[\r\n]* '\r'? '\n' -> skip;
OPERATOR: (':='|'+'|'-'|'*'|'/'| '='| '!='| '<'| '>' |'('|')'|';'|','|'<='|'>=');
WS: [ \t\r\n]+ -> skip;
