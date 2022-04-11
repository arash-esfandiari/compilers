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
    public static final int AC = 0;
    public static final int AC1 = 1;
    public static final int FP = 5; // Frame Pointer
    public static final int GP = 6; // Global Pointer, point to the beggining of global data section
    public static final int PC = 7; // Program Counter
    public static final int ofpOF = 0;
    public static final int retOF = -1;
    public static final int initOF = -2;

    private int mainEntry;
    private int globalOffset;
    private int emitLoc;
    private int highEmitLoc;
    private String codeName;

    private static final int IN_ADDR = 4;
    private static final int OUT_ADDR = 7;

    public CodeGenerator(String codeName) {
        mainEntry = 0;
        globalOffset = 0;
        emitLoc = 0;
        highEmitLoc = 0;
        this.codeName = codeName;
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

    // save the location you'll skip
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
        String code = "   " + emitLoc + ":     " + op + "  " + registr + "," + (address - (emitLoc + 1)) + "(" + PC
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

        /* Prelude */
        emitRM("LD", GP, 0, AC, "Load GP with max address"); // reg[gp] = 1023
        emitRM("LDA", FP, 0, GP, "Copy GP to FP"); // reg[fp] = 1023
        emitRM("ST", 0, 0, 0, "Clear location 0"); // dMem[0] = 0
        int savedLoc = emitSkip(1);

        /* Input function */
        emitComment("Code for input routine");
        emitRM("ST", 0, -1, FP, "Store return");
        emitRO("IN", 0, 0, 0, "");
        emitRM("LD", PC, -1, FP, "Return caller");

        /* Output function */
        emitComment("Code for output routine");
        emitRM("ST", 0, -1, FP, "Store return");
        emitRM("LD", 0, -2, FP, "Load output value");
        emitRO("OUT", 0, 0, 0, "");
        emitRM("LD", 7, -1, FP, "Return caller");
        int savedLoc2 = emitSkip(0);

        /* Jump around I/O routines */
        emitBackup(savedLoc);
        emitRM_Abs("LDA", PC, savedLoc2, "Jump around I/O functions");
        emitRestore();
        emitComment("End of prelude");
        visit(decList, 0, false);
        if (mainEntry != 0) {
            System.out.println("Main function does not exist!");
        }
        // Finale
        emitComment("Finale");
        emitRM("ST", FP, globalOffset, FP, "Push old frame pointer");
        emitRM("LDA", FP, globalOffset, FP, "Push frame");
        emitRM("LDA", 0, 1, PC, "Load ac with ret ptr");
        emitRM_Abs("LDA", PC, mainEntry, "Jump to main location");
        emitRM("LD", FP, 0, FP, "Pop frame");
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
    public void visit(SimpleVar exp, int offset, boolean isAddress) {

    }

    public void visit(IndexVar exp, int offset, boolean isAddress) {

    }

    /* Expressions */
    public void visit(NilExp exp, int offset, boolean isAddress) {

    }

    public void visit(VarExp exp, int offset, boolean isAddress) {

        if (exp.variable != null) {
            exp.variable.accept(this, offset, false);
        }
        writeToFile("VarExp: ");
    }

    public void visit(IntExp exp, int offset, boolean isAddress) {
        indent(level);
        writeToFile("IntExp: " + exp.value);
    }

    public void visit(CallExp exp, int o, boolean isAddress) {
        indent(level);
        writeToFile("CallExp: " + exp.func, true);
        level++;
        if (exp.args != null)
            exp.args.accept(this, level);
    }

    public void visit(OpExp exp, int level, boolean isAddress) {

        exp.left.accept(this, level, false);
        exp.right.accept(this, level, false);
    }

    public void visit(AssignExp exp, int level, boolean isAddress) {

        exp.lhs.accept(this, level, false);
        exp.rhs.accept(this, level, false);
    }

    public void visit(IfExp exp, int level, boolean isAddress) {

        exp.test.accept(this, level, false);
        exp.thenpart.accept(this, level, false);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level, false);
    }

    public void visit(WhileExp exp, int level, boolean isAddress) {
        if (exp.test != null)
            exp.test.accept(this, level, false);
        if (exp.body != null)
            exp.body.accept(this, level, false);
    }

    public void visit(ReturnExp exp, int level, boolean isAddress) {

        if (exp.exp != null)
            exp.exp.accept(this, level, false);
    }

    public void visit(CompoundExp exp, int level, boolean isAddress) {

        if (exp.decs != null && exp.exps != null)
            level++;

        if (exp.decs != null)
            exp.decs.accept(this, level, false);
        if (exp.exps != null)
            exp.exps.accept(this, level, false);
    }

    /* Declarations */
    public void visit(FunctionDec exp, int level, boolean isAddress) {
        if (exp.func.equals("main"))
            mainEntry = emitLoc;

        if (exp.params != null)
            exp.params.accept(this, level, false);

        if (exp.body != null)
            exp.body.accept(this, level, false);
    }

    public void visit(SimpleDec exp, int level, boolean isAddress) {

    }

    public void visit(ArrayDec exp, int level, boolean isAddress) {

    }

}
