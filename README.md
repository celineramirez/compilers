A compiler built with ANTLR and Java to parse a toy imperative programming language called MICRO. See "Little.g4" for the syntax of this language.<br/><br/>
Input: a MICRO file<br/>
Lexer - Converts MICRO code to tokens that are used for input in the parser.<br/>
Parser - Parses input code. A context-free grammar is defined and used to generate code.<br/><br/>

Driver.java - Initialize lexer, parser, and hash table.<br/>
SimpleTableBuilder.java - Store parsed MICRO code in a hash table<br/>
AbstractSyntaxTree.java - Build a syntax tree from traversing the hash table. When read in post-order, this produces 3 address code.<br/>
Intermediate code is then translated into assembly code.
