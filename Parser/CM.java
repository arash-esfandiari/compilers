/*
  Created by: Arash Esfandiari and Josh MacCaskil
  File Name: CM.java
  To Build: 
  After the scanner, cminus.flex, and the parser, cminus.cup, have been created.
    javac CM.java
  
  To Run: 
    java -classpath ../java-cup-11b.jar:. CM gcd.cm

  where gcd.cm is an test input file for the tiny language.
*/

import java.io.*;
import absyn.*;
import java.io.File;

class CM {
    public static boolean SHOW_TREE = false;
    public static boolean SHOW_SYMBOL_TABLE = false;
    public static boolean GENERATE_CODE = false;

    static public void main(String argv[]) {
        /* Start the parser */
        String codeName;
        int firstIndex = argv[1].indexOf(".");
        if (firstIndex != -1) {
            codeName = argv[1].substring(0, firstIndex); // this will give abc
        }
        /* Create Tree files */
        File absTreeFile = new File("syntaxTree.abs");
        File symbolTableFile = new File("symbolTable.sym");
        absTreeFile.delete();
        symbolTableFile.delete();
        /* Determine flag */
        try {
            for (String arg : argv)
                if (arg.equals("-a")) {
                    SHOW_TREE = true;
                } else if (arg.equals("-s")) {
                    SHOW_SYMBOL_TABLE = true;
                } else if (arg.equals("-c")) {
                    GENERATE_CODE = true;
                }
            /* Build syntax tree */
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn absTree = (Absyn) (p.parse().value);

            /* Save abs tree to syntaxTree.abs */
            if ((SHOW_TREE || SHOW_SYMBOL_TABLE) && absTree != null) {
                absTreeFile.createNewFile();
                ShowTreeVisitor visitor = new ShowTreeVisitor();
                absTree.accept(visitor, 0);
            }
            /* Perform semantic analysis and save symbolTree */
            if (SHOW_SYMBOL_TABLE && absTree != null) {
                absTreeFile.createNewFile();
                symbolTableFile.createNewFile();
                SemanticAnalyzer analyzer = new SemanticAnalyzer();
                absTree.accept(analyzer, 0);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}
