# C- Compiler

## Authors 
- Arash Esfandiari 
- Josh McCaskil
### Course 
- CIS*4650, Compilers, University of Guelph 
- Winter 2022 
## Description:

The goal of this project is to create a new programming language called "C-" or "CMINUS". "C-" is very similar to "C", but has far less many features. To write your own programming language is to write a compiler for with specified set of grammar rules, parse the program based on those rules, and generate the required assembly code.  
This program is created with under the supervision and help of professor "Fei Song" at the University of Guelph, CIS*4650 Compilers. 

## How to Build and Test

Makefile in the in the "src" folder contains all commands required to build the program. cd to src, type "$build" to build the program. 
To Generate the required file including Anotated Tree, Symbol Table, and Assembly Code for fac.cm (short for c minus) type: 
    java -cp ../java-cup-11b.jar:. CM ../TestFiles/fac.cm -c

Flags: 
-a: creates only the syntax tree file.
-s: creates the annoated tree and symbol table files.
-c: create the  annoated tree, symbol table, and assembly code files.


The testing file can be changed to any of those in the TestingFiles folder by replacing 1.cm with the desired file.



## Testing Plan

Ten testing files are provided which were used to test the correctness of this project. The files: sort.cm, gcd.cm and fac.cm are 3 correct cminus programs. The other 10 files [0-9].cm were created to to demonstrate the error handling for the project where each of the files 1-4 contain up to 3 errors that are handled by either the SemanticAnalyzer.java for semantic errors or the Scanner.java for syntactic errors.  
A description for each of these errors can be found at the top of each testing file [0-9].cm.


1.cm - No Errors
2.cm - No Errors
3.cm - No Errors
4.cm - No Errors
5.cm - Syntactic Error (Missing Semi-Colon Line 9)
6.cm - Syntactic Error (Invalid Comparison Lines 13 and 18)
7.cm - Syntactic Error (Error in If Statement Line 11)
8.cm - Semantic Error (Conditions for If and While Statements must be int Lines 10 and 11)
9.cm - Various Errors (Lines 10, 13, 16, 17)
0.cm - Runtime Error (Array Index Out of Bounds Line 12)


## Improvements

Improvements for this project would include testing for various different types of runtime errors, as specified in the Project Report Document.



## Semantic errors caught
    - Catches invalid operation 
    - Catches invalid if test 
    - Catches invalid void declaration (void x[])
    - Catches variable redeclaration in the same scope
    - Catches invalid call to undifines function
    - Catches invalid use of undeclared variable
    



## Checkpoints 
### First 
The first  checkpoint, we use the JFLEX scanner in combination with the CUP (Construction of Useful Parsers) library tocreate a LALR parser generator for Java, to scan, tokenize, parse, and finally build a syntax tree for our newly created program.
### Second

### Third 