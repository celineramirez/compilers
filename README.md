A compiler built with ANTLR and Java to parse a toy imperative programming language called MICRO. See "Little.g4" for the syntax of this language.
Input: a MICRO file
Lexer - Converts MICRO code to tokens that are used for input in the parser.
Parser - Parses input code. A context-free grammar is defined and used to generate code.

Driver.java - Initialize lexer, parser, and hash table.
SimpleTableBuilder.java - Store parsed MICRO code in a hash table
AbstractSyntaxTree.java - Build a syntax tree from traversing the hash table. When read in post-order, this produces 3 address code.
Intermediate code is then translated into assembly code.
