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

class CM {
    public static boolean SHOW_TREE = false;
    public static boolean SHOW_SYMBOL_TABLE = false;
    public static boolean GENERATE_CODE = false;

    static public void main(String argv[]) {
        /* Start the parser */
        try {
            for (int i = 0; i < argv.length; i++)
                if (argv[i].equals("-a")) {
                    SHOW_TREE = true;
                } else if (argv[i].equals("-s")) {
                    SHOW_SYMBOL_TABLE = true;
                } else if (argv[i].equals("-c")) {
                    GENERATE_CODE = true;
                }
            parser p = new parser(new Lexer(new FileReader(argv[0])));
            Absyn result = (Absyn) (p.parse().value);
            if (SHOW_TREE && result != null) {
                System.out.println("The abstract syntax tree is:");
                ShowTreeVisitor visitor = new ShowTreeVisitor();
                result.accept(visitor, 0);
            }
        } catch (Exception e) {
            /* do cleanup here -- possibly rethrow e */
            e.printStackTrace();
        }
    }
}
