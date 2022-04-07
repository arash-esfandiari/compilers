import absyn.*;
import absyn.Decs.*;
import absyn.Exps.*;
import absyn.Lists.*;
import absyn.VarDecs.*;
import absyn.Vars.*;

import java.io.FileWriter;
import java.io.IOException;

public class ShowTreeVisitor implements AbsynVisitor {

    final static int SPACES = 4;
    public String codeName;

    public ShowTreeVisitor(String codeName) {
        this.codeName = codeName;
    }

    private void writeToFile(String string) {
        try {
            FileWriter myWriter = new FileWriter(codeName + ".abs", true);
            myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            writeToFile(" ");
    }

    public void visit(NameTy exp, int level, boolean isAddress) {
        if (exp.typ == 0) {
            writeToFile("VOID\n");
        }
        if (exp.typ == 0) {
            writeToFile("INT\n");
        }
    }

    /* Vars */
    public void visit(SimpleVar exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("SimpleVar: " + exp.name + "\n");
    }

    public void visit(IndexVar exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("IndexVar: " + exp.name + "\n");
        level++;
        exp.index.accept(this, level, false);
    }

    /* Expressions */
    public void visit(NilExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("NilExp:\n");
    }

    public void visit(VarExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("VarExp: \n");
        level++;

        if (exp.variable != null) {
            exp.variable.accept(this, level, false);
        }
    }

    public void visit(IntExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("IntExp: " + exp.value + "\n");
    }

    public void visit(CallExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("CallExp: " + exp.func + "\n");
        level++;
        if (exp.args != null)
            exp.args.accept(this, level, false);
    }

    public void visit(OpExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("OpExp:");
        switch (exp.op) {
            case OpExp.PLUS:
                writeToFile(" + \n");
                break;
            case OpExp.MINUS:
                writeToFile(" - \n");
                break;
            case OpExp.TIMES:
                writeToFile(" * \n");
                break;
            case OpExp.DIV:
                writeToFile(" / \n");
                break;
            case OpExp.EQ:
                writeToFile(" == \n");
                break;
            case OpExp.NEQ:
                writeToFile(" != \n");
                break;
            case OpExp.LT:
                writeToFile(" < \n");
                break;
            case OpExp.LTEQ:
                writeToFile(" <= \n");
                break;
            case OpExp.GT:
                writeToFile(" > \n");
                break;
            case OpExp.GTEQ:
                writeToFile(" >= \n");
                break;
            default:
                writeToFile("Invalid operator at line " + exp.row + " and column " + exp.col + "\n");
        }
        level++;
        exp.left.accept(this, level, false);
        exp.right.accept(this, level, false);
    }

    public void visit(AssignExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("AssignExp:\n");
        level++;
        exp.lhs.accept(this, level, false);
        exp.rhs.accept(this, level, false);
    }

    public void visit(IfExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("IfExp:\n");
        level++;
        exp.test.accept(this, level, false);
        exp.thenpart.accept(this, level, false);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level, false);
    }

    public void visit(WhileExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("WhileExp:\n");
        level++;
        if (exp.test != null)
            exp.test.accept(this, level, false);
        if (exp.body != null)
            exp.body.accept(this, level, false);
    }

    public void visit(ReturnExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("ReturnExp:\n");
        level++;

        if (exp.exp != null)
            exp.exp.accept(this, level, false);
    }

    public void visit(CompoundExp exp, int level, boolean isAddress) {
        indent(level);
        writeToFile("CompoundExp:\n");

        if (exp.decs != null && exp.exps != null)
            level++;

        if (exp.decs != null)
            exp.decs.accept(this, level, false);
        if (exp.exps != null)
            exp.exps.accept(this, level, false);
    }

    /* Declarations */
    public void visit(FunctionDec exp, int level, boolean isAddress) {
        indent(level);
        if (exp.typ.typ == NameTy.VOID)
            writeToFile("FunctionDec: " + exp.func + " VOID" + "\n");
        else if (exp.typ.typ == NameTy.INT)
            writeToFile("FunctionDec: " + exp.func + " INT" + "\n");

        level++;

        if (exp.params != null)
            exp.params.accept(this, level, false);

        if (exp.body != null)
            exp.body.accept(this, level, false);
    }

    public void visit(SimpleDec exp, int level, boolean isAddress) {
        indent(level);
        if (exp.typ.typ == NameTy.VOID)
            writeToFile("SimpleDec: " + exp.name + " VOID" + "\n");
        else if (exp.typ.typ == NameTy.INT)
            writeToFile("SimpleDec: " + exp.name + " INT" + "\n");
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
            writeToFile("ArrayDec: " + exp.name + "[" + exp.size.value + "]" + " - " + ty + "\n");
        } else
            writeToFile("ArrayDec: " + exp.name + "[]" + " - " + ty + "\n");
    }

    /* Lists */
    public void visit(DecList decList, int level, boolean isAddress) {
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, level, false);
            }
            decList = decList.tail;
        }
    }

    public void visit(VarDecList varDecList, int level, boolean isAddress) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, level, false);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int level, boolean isAddress) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, level, false);
            }
            expList = expList.tail;
        }
    }
}
