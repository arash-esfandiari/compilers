import absyn.*;
import absyn.Decs.*;
import absyn.Exps.*;
import absyn.Lists.*;
import absyn.VarDecs.*;
import absyn.Vars.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CodeGenerator implements AbsynVisitor {

    /* Registers */
    public static final int ac = 0;
    public static final int ac1 = 1;
    public static final int fp = 5; // Frame Pointer
    public static final int gp = 6; // Global Pointer, point to the beggining of global data section
    public static final int pc = 7; // Program Counter
    public static final int ofpFO = 0;
    public static final int retFO = -1;
    public static final int initFO = -2;

    private int mainEntry;
    private int globalOffset;
    private int emitLoc;
    private int highEmitLoc;
    private String codeName;
    public HashMap<String, Dec> map;

    private static final int IN_ADDR = 4;
    private static final int OUT_ADDR = 7;

    public CodeGenerator(String codeName) {
        mainEntry = 0;
        globalOffset = 0;
        emitLoc = 0;
        highEmitLoc = 0;
        this.codeName = codeName;
        map = new HashMap<String, Dec>();
    }

    // write string to related output file
    private void writeToFile(String string) {
        try {
            FileWriter myWriter = new FileWriter("Output" + "/" + codeName + ".tm", true);
            myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    // move line "distance" units forward
    public int emitSkip(int distance) {
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
        return i;
    }

    //
    public void emitBackup(int loc) {
        if (loc > highEmitLoc)
            emitComment("BUG in emitBackup");
        emitLoc = loc;
    }

    // restore location
    public void emitRestore() {
        emitLoc = highEmitLoc;
    }

    public void emitRM_Abs(String op, int registr, int address, String comment) {
        String code = "   " + emitLoc + ":     " + op + "  " + registr + "," + (address - (emitLoc + 1)) + "(" + pc
                + ")";
        writeToFile(code);
        writeToFile("\t" + comment + "\n");
        ++emitLoc;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    // Emit Register Memory
    public void emitRM(String op, int register, int offset, int register2, String comment) {
        String code = "   " + emitLoc + ":     " + op + "  " + register + "," + offset + "(" + register2 + ")";
        writeToFile(code);
        writeToFile("\t" + comment + "\n");
        ++emitLoc;
        if (highEmitLoc < emitLoc) {
            highEmitLoc = emitLoc;
        }
    }

    // Emit Register Only
    public void emitRO(String op, int r, int s, int t, String comment) {
        String code = "   " + emitLoc + ":     " + op + " " + r + "," + s + "," + t;
        writeToFile(code);
        ++emitLoc;
        writeToFile("\t" + comment + "\n");
    }

    public void emitComment(String comment) {
        writeToFile("* " + comment + "\n");
    }

    public void visit(DecList decList) {

        emitComment("C-Minus Compilation to TM Code");
        emitComment("File: " + codeName + ".tm");
        /* Prelude */
        emitRM("LD", gp, 0, ac, "Load GP with max address"); // reg[gp] = 1023
        emitRM("LDA", fp, 0, gp, "Copy GP to FP"); // reg[fp] = 1023
        emitRM("ST", 0, 0, 0, "Clear location 0"); // dMem[0] = 0
        int savedLoc = emitSkip(1);

        /* Input function */
        emitComment("Code for input routine");
        emitRM("ST", ac, -1, fp, "Store return");
        emitRO("IN", ac, 0, 0, "");
        emitRM("LD", pc, -1, fp, "Return caller");

        /* Output function */
        emitComment("Code for output routine");
        emitRM("ST", ac, -1, fp, "Store return");
        emitRM("LD", ac, -2, fp, "Load output value");
        emitRO("OUT", ac, 0, 0, "");
        emitRM("LD", pc, -1, fp, "Return caller");
        int savedLoc2 = emitSkip(0);

        /* Jump around I/O routines */
        emitBackup(savedLoc);
        emitRM_Abs("LDA", pc, savedLoc2, "Jump around I/O functions");
        emitRestore();
        emitComment("End of prelude");
        visit(decList, 0, false);
        if (mainEntry == 0) {
            System.out.println("Main function does not exist!");
        }
        emitComment("Main Function");
        int savedLoc3 = emitSkip(1);
        emitRM("ST", ac, -1, fp, "Save return address");
        emitRM("LD", pc, -1, fp, "Load output value");
        emitBackup(savedLoc3);
        emitRM("LDA", pc, 2, pc, "Jump forward to finale"); // reg[fp] = 1023
        // Finale
        emitComment("Finale");
        emitRM("ST", fp, globalOffset + ofpFO, fp, "Push old frame pointer");
        emitRM("LDA", fp, globalOffset, fp, "Push frame");
        emitRM("LDA", ac, 1, pc, "Load ac with ret ptr");
        emitRM_Abs("LDA", pc, mainEntry, "Jump to main location");
        emitRM("LD", fp, ofpFO, fp, "Pop frame");
        emitRO("HALT", 0, 0, 0, "Halt execution");
        emitComment("Finished execution");
    }
    /* Lists */

    public void visit(DecList decList, int offset, boolean isAddress) {
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, offset, false);
            }
            decList = decList.tail;
        }

    }

    public void visit(VarDecList varDecList, int offset, boolean isAddress) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, offset, true);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int offset, boolean isAddress) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, offset, false);
            }
            expList = expList.tail;
        }
    }

    public void visit(NameTy exp, int offset, boolean isAddress) {

    }

    /* Vars */
    public void visit(SimpleVar v, int offset, boolean isAddress) {
        // Retrieve function address
        SimpleDec dec;
        dec = (SimpleDec) map.get(v.name);
    }

    public void visit(IndexVar v, int offset, boolean isAddress) {
        ArrayDec dec;
        dec = (ArrayDec) map.get(v.name);

        v.index.accept(this, offset, isAddress);
        // String temp = newtemp();
        // codestr += temp + “ = ” + tree.index.temp + “ * elem_size(” +tree.name + “)”;
        // emitCode( codestr );
        // String temp2 = newtemp();
        // codestr += temp2 + “ = &” + tree.name + “ + ” + temp;
        // emitCode( codestr );
        // tree.temp = temp2;
        // tree.isAddr = true;
    }

    /* Expressions */
    public void visit(NilExp exp, int offset, boolean isAddress) {

    }

    public void visit(VarExp exp, int offset, boolean isAddress) {

        if (exp.variable != null) {
            exp.variable.accept(this, offset, false);
        }

    }

    public void visit(IntExp exp, int offset, boolean isAddress) {
        emitRM("LDC", ac, exp.value, 0, "load const");
    }

    public void visit(CallExp exp, int offset, boolean isAddress) {
        // Retrieve function address
        FunctionDec dec;
        dec = (FunctionDec) map.get(exp.func);

        emitRM("ST", ac, retFO, fp, "store return address");
        if (exp.args != null)
            exp.args.accept(this, offset + initFO, false);

        emitRM("ST", fp, offset + ofpFO, fp, "Store current fp");
        emitRM("LDA", fp, offset, fp, "Push new frame");
        emitRM("LDA", ac, 1, pc, "Save return in ac");
        emitRM_Abs("LDA", pc, dec.funAddr, "Relative jump to function entry");
        emitRM("LD", fp, ofpFO, fp, "Pop current frame");

        emitRM("LD", pc, retFO, fp, "return to caller");
    }

    public void visit(OpExp exp, int offset, boolean isAddress) {
        exp.left.accept(this, offset, false);
        exp.right.accept(this, offset, false);
        // exp.temp = newtemp();
        // codestr += tree.temp + “ = ”;
        // if( exp.left.isAddr )
        // codestr += “*” ;
        // codestr += exp.left.temp + “ + ”;
        // if( exp.right.isAddr )
        // codestr += “*”;
        // codestr += tree.right.temp;
        // emitCode( codestr );
    }

    public void visit(AssignExp exp, int level, boolean isAddress) {
        exp.lhs.accept(this, level, false);
        exp.rhs.accept(this, level, false);
        // exp.temp = exp.lhs.temp;
        // exp.isAddr = tree.lhs.isAddr;
        // if( exp.isAddr )
        // codestr += “*” ;
        // codestr += tree.temp + “ = ”;
        // if( exp.rhs.isAddr )
        // codestr += “*”;
        // codestr += tree.right.temp;
        // emitCode( codestr );
    }

    public void visit(IfExp exp, int level, boolean isAddress) {
        exp.test.accept(this, level, false);

        // lab1 = genLabel();
        // if( exp.test.value == 0 )
        // codestr += “if_false false goto ” + lab1;
        // else
        // codestr += “if_false true goto ” + lab1;
        // emitCode( codestr );
        // exp.thenpart.accept(this, level, false);
        // if( exp.else != null ) {
        // lab2 = genLabel();
        // codestr += “goto ” + lab2;
        // emitCode( codestr );
        // }
        // codestr += “label ” + lab1;
        // emitCode( codestr );
        // if (exp.elsepart != null) {
        // exp.elsepart.accept(this, level, false);
        // codestr += “label ” + lab2;
        // emitCode( codestr );
        // }
    }

    public void visit(WhileExp exp, int level, boolean isAddress) {

        // lab1 = genLabel();
        // codestr += “label” + lab1;
        // emitCode( codestr );
        // if (exp.test != null)
        // exp.test.accept(this, level, false);
        // lab2 = genLabel();
        // if( ((IntExp)exp.test).value == 0 )
        // codestr += “if_false false goto ” + lab2;
        // else
        // codestr += “if_false true goto ” + lab2;
        // emitCode( codestr );
        // if (exp.body != null)
        // exp.body.accept(this, level, false);
        // codestr += “goto ” + lab1;
        // emitCode( codestr );
        // codestr += “label ” + lab2;
        // emitCode( codestr );
    }

    public void visit(ReturnExp exp, int level, boolean isAddress) {

        if (exp.exp != null)
            exp.exp.accept(this, level, false);
    }

    public void visit(CompoundExp exp, int level, boolean isAddress) {

        if (exp.decs != null && exp.exps != null)

            if (exp.decs != null)
                exp.decs.accept(this, level, false);
        if (exp.exps != null)
            exp.exps.accept(this, level, false);
    }

    /* Declarations */
    public void visit(FunctionDec dec, int level, boolean isAddress) {
        if (dec.func.equals("main"))
            mainEntry = emitLoc;

        if (dec.params != null)
            dec.params.accept(this, level, false);

        if (dec.body != null)
            dec.body.accept(this, level, false);
        map.put(dec.func, dec);
    }

    public void visit(SimpleDec dec, int offset, boolean isAddress) {
        if (dec.nestLevel == 0) {
            dec.offset = --globalOffset;

        } else {
            dec.offset = offset;
            offset--;
        }
        map.put(dec.name, dec);
    }

    public void visit(ArrayDec dec, int offset, boolean isAddress) {
        if (dec.nestLevel == 0) {
            dec.offset = globalOffset;
            // globalOffset -= dec.size.value;

        } else {
            dec.offset = offset;
            // offset -= dec.size.value;
        }
        map.put(dec.name, dec);
    }

}
