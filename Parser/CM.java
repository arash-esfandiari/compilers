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
        String codeName = "";
        if (argv.length > 0) {
            int firstIndex = argv[0].lastIndexOf(".");
            int lastIndex = argv[0].lastIndexOf("/");
            if (firstIndex != -1 && lastIndex != -1) {
                codeName = argv[0].substring(lastIndex + 1, firstIndex);
            } else if (firstIndex != -1) {
                codeName = argv[0].substring(0, firstIndex);
            } else {
                System.out.println("Please enter program name");
            }
        }

        /* Create text files */
        File absTreeFile = new File(codeName + ".abs");
        File symbolTableFile = new File(codeName + ".sym");
        File machineCodeFile = new File(codeName + ".tm");
        // absTreeFile.delete();
        // symbolTableFile.delete();
        // machineCodeFile.delete();
        /* Determine flag */
        try {
            for (String arg : argv)
                if (arg.equals("-a")) {
                    SHOW_TREE = true;
                } else if (arg.equals("-s")) {
                    SHOW_SYMBOL_TABLE = true;
                    SHOW_TREE = true;
                } else if (arg.equals("-c")) {
                    SHOW_SYMBOL_TABLE = true;
                    SHOW_TREE = true;
                    GENERATE_CODE = true;
                }
            /* Build syntax tree */
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn absTree = (Absyn) (p.parse().value);

            /* Save abs tree to syntaxTree.abs */
            if (SHOW_TREE && absTree != null) {
                absTreeFile.createNewFile();
                ShowTreeVisitor visitor = new ShowTreeVisitor(codeName);
                absTree.accept(visitor, 0, false);
            }
            /* Perform semantic analysis and save symbolTree */
            if (SHOW_SYMBOL_TABLE && absTree != null) {
                symbolTableFile.createNewFile();
                SemanticAnalyzer analyzer = new SemanticAnalyzer(codeName);
                absTree.accept(analyzer, 0, false);
            }
            if (GENERATE_CODE && absTree != null) {
                machineCodeFile.createNewFile();
                // CodeGenerator generator = new CodeGenerator(codeName);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}
