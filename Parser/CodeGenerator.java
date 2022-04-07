import absyn.*;
import absyn.Decs.*;
import absyn.Exps.*;
import absyn.Lists.*;
import absyn.VarDecs.*;
import absyn.Vars.*;

import java.io.FileWriter;
import java.io.IOException;

public class CodeGenerator implements AbsynVisitor {

    public int mainEntry, globalOffset;
    public String codeName;

    public static final int IN_ADDR = 4;
    public static final int OUT_ADDR = 7;

    /* Registers */
    public static final int PC = 7; // Program Counter
    public static final int GP = 6; // Global Pointer, point to the beggining of global data section
    public static final int FP = 5; // Frame Pointer
    public static final int AC = 0;
    public static final int AC1 = 1;

    static int emitLoc = 0;
    static int highEmitLoc = 0;

    public CodeGenerator(String codeName) {
        this.codeName = codeName;
    }

    private void writeToFile(String string) {
        try {
            FileWriter myWriter = new FileWriter(codeName + ".tm", true);
            myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    public static int emitSkip(int distance) {
        int i = emitLoc;
        emitLoc += distance;
        if (highEmitLoc < emitLoc)
            highEmitLoc = emitLoc;
        return i;
    }

    public static void emitBackup(int loc) {
        if (loc > highEmitLoc)
            emitComment("BUG in emitBackup");
        emitLoc = loc;
    }

    public static void emitRestore() {
        emitLoc = highEmitLoc;
    }

    // public static void emitRM_Abs( char *op, int r, int a, char *c ) {
    // fprintf( codeName, "%3d: %5s %d, %d(%d) ", emitLoc, op, r, a - (emitLoc + 1),
    // pc);
    // fprintf( codeName, "\t%s\n", c );
    // ++emitLoc;
    // if( highEmitLoc < emitLoc )
    // highEmitLoc = emitLoc;
    // }

    public void visit(DecList decList, int level, boolean isAddress) {

        /* Start Prelude */
        emitRM("LD", GP, 0, AC, "Load GP with max address"); // reg[gp] = 1023
        emitRM("LDA", FP, 0, GP, "Copy GP to FP"); // reg[fp] = 1023
        emitRM("ST", 0, 0, 0, "Clear location 0"); // dMem[0] = 0
        int savedLoc = emitSkip(1);

        /* Input function */
        emitComment("Jump around I/O functions here");
        emitComment("Code for input routine");
        emitRM("ST", 0, -1, FP, "Store return");
        emitOp("IN", 0, 0, 0, "");
        emitRM("LD", PC, -1, FP, "Return caller");

        /* Code for output routine */
        emitComment("Code for output routine");
        emitRM("ST", 0, -1, FP, "Store return");
        emitRM("LD", 0, -2, FP, "Load output value");
        emitOp("OUT", 0, 0, 0, "");
        emitRM("LD", 7, -1, FP, "Return caller");
        int savedLoc2 = emitSkip(0);

        /* Jump around I/O routines */
        emitBackup(savedLoc);
        emitRM_Abs("LDA", PC, savedLoc2, "Jump around I/O functions");
        emitRestore();
        emitComment("End of standard prelude");
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, level, false);
            }
            decList = decList.tail;
        }
    }

    public void visit(NameTy exp, int level, boolean isAddress) {

    }

    /* Vars */
    public void visit(SimpleVar exp, int level, boolean isAddress) {

    }

    public void visit(IndexVar exp, int level, boolean isAddress) {

    }

    /* Expressions */
    public void visit(NilExp exp, int level, boolean isAddress) {

    }

    public void visit(VarExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("VarExp: ", true);
        level++;

        if (exp.variable != null) {
            exp.variable.accept(this, level, false);
        }
    }

    public void visit(IntExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("IntExp: " + exp.value, true);
    }

    public void visit(CallExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("CallExp: " + exp.func, true);
        level++;
        if (exp.args != null)
            exp.args.accept(this, level);
    }

    public void visit(OpExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("OpExp:", false);
        switch (exp.op) {
            case OpExp.PLUS:
                writeToFile(" + ", true);
                break;
            case OpExp.MINUS:
                writeToFile(" - ", true);
                break;
            case OpExp.TIMES:
                writeToFile(" * ", true);
                break;
            case OpExp.DIV:
                writeToFile(" / ", true);
                break;
            case OpExp.EQ:
                writeToFile(" == ", true);
                break;
            case OpExp.NEQ:
                writeToFile(" != ", true);
                break;
            case OpExp.LT:
                writeToFile(" < ", true);
                break;
            case OpExp.LTEQ:
                writeToFile(" <= ", true);
                break;
            case OpExp.GT:
                writeToFile(" > ", true);
                break;
            case OpExp.GTEQ:
                writeToFile(" >= ", true);
                break;
            default:
                writeToFile("Invalid operator at line " + exp.row + " and column " + exp.col, true);
        }
        level++;
        exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void visit(AssignExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("AssignExp:", true);
        level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(IfExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("IfExp:", true);
        level++;
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level);
    }

    public void visit(WhileExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("WhileExp: ", true);
        level++;
        if (exp.test != null)
            exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
    }

    public void visit(ReturnExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("ReturnExp: ", true);
        level++;

        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit(CompoundExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("CompoundExp: ", true);

        if (exp.decs != null && exp.exps != null)
            level++;

        if (exp.decs != null)
            exp.decs.accept(this, level);
        if (exp.exps != null)
            exp.exps.accept(this, level);
    }

    /* Declarations */
    public void visit(FunctionDec exp, int level, boolean isAddress) {
        indent(level);
        if (exp.typ.typ == NameTy.VOID)
            writeToFile("FunctionDec: " + exp.func + " VOID", true);
        else if (exp.typ.typ == NameTy.INT)
            writeToFile("FunctionDec: " + exp.func + " INT", true);

        level++;

        if (exp.params != null)
            exp.params.accept(this, level);

        if (exp.body != null)
            exp.body.accept(this, level);
    }

    public void visit(SimpleDec exp, int level, boolean isAddress) {
        indent(level);
        if (exp.typ.typ == NameTy.VOID)
            writeToFile("SimpleDec: " + exp.name + " VOID", true);
        else if (exp.typ.typ == NameTy.INT)
            writeToFile("SimpleDec: " + exp.name + " INT", true);
    }

    public void visit(ArrayDec exp, int level, boolean isAddress) {
        indent(level);
        String ty = new String("");

        if (exp.typ.typ == NameTy.VOID) {
            ty = new String("VOID");
        } else if (exp.typ.typ == NameTy.INT) {
            ty = new String("INT");
        }

        if (exp.size != null) {
            writeToFile("ArrayDec: " + exp.name + "[" + exp.size.value + "]" + " - " + ty, true);
        } else
            writeToFile("ArrayDec: " + exp.name + "[]" + " - " + ty, true);
    }

    /* Lists */

    public void visit(VarDecList varDecList, int level, boolean isAddress) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, level);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int level, boolean isAddress) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, level);
            }
            expList = expList.tail;
        }
    }
}
