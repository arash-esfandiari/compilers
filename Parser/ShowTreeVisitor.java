import absyn.*;
import java.io.FileWriter;
import java.io.IOException;

public class ShowTreeVisitor implements AbsynVisitor {

    final static int SPACES = 4;

    private void writeToFile(String string, boolean nextLine) {
        try {
            FileWriter myWriter = new FileWriter("syntaxTree.abs", true);
            if (nextLine) {
                myWriter.write(string + "\n");
            } else
                myWriter.write(string);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("FileWriter unsuccessful");
            e.printStackTrace();
        }
    }

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            writeToFile(" ", false);
    }

    public void visit(NameTy exp, int level) {
        if (exp.typ == 0) {
            writeToFile("VOID", true);
        }
        if (exp.typ == 0) {
            writeToFile("INT", true);
        }
    }

    /* Vars */
    public void visit(SimpleVar exp, int level) {
        indent(level);
        writeToFile("SimpleVar: " + exp.name, true);
    }

    public void visit(IndexVar exp, int level) {
        indent(level);
        writeToFile("IndexVar: " + exp.name, true);
        level++;
        exp.index.accept(this, level);
    }

    /* Expressions */
    public void visit(NilExp exp, int level) {
        indent(level);
        writeToFile("NilExp:", true);
    }

    public void visit(VarExp exp, int level) {
        indent(level);
        writeToFile("VarExp: ", true);
        level++;

        if (exp.variable != null) {
            exp.variable.accept(this, level);
        }
    }

    public void visit(IntExp exp, int level) {
        indent(level);
        writeToFile("IntExp: " + exp.value, true);
    }

    public void visit(CallExp exp, int level) {
        indent(level);
        writeToFile("CallExp: " + exp.func, true);
        level++;
        if (exp.args != null)
            exp.args.accept(this, level);
    }

    public void visit(OpExp exp, int level) {
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

    public void visit(AssignExp exp, int level) {
        indent(level);
        writeToFile("AssignExp:", true);
        level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(IfExp exp, int level) {
        indent(level);
        writeToFile("IfExp:", true);
        level++;
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level);
    }

    public void visit(WhileExp exp, int level) {
        indent(level);
        writeToFile("WhileExp: ", true);
        level++;
        if (exp.test != null)
            exp.test.accept(this, level);
        if (exp.body != null)
            exp.body.accept(this, level);
    }

    public void visit(ReturnExp exp, int level) {
        indent(level);
        writeToFile("ReturnExp: ", true);
        level++;

        if (exp.exp != null)
            exp.exp.accept(this, level);
    }

    public void visit(CompoundExp exp, int level) {
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
    public void visit(FunctionDec exp, int level) {
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

    public void visit(SimpleDec exp, int level) {
        indent(level);
        if (exp.typ.typ == NameTy.VOID)
            writeToFile("SimpleDec: " + exp.name + " VOID", true);
        else if (exp.typ.typ == NameTy.INT)
            writeToFile("SimpleDec: " + exp.name + " INT", true);
    }

    public void visit(ArrayDec exp, int level) {
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
    public void visit(DecList decList, int level) {
        while (decList != null) {
            if (decList.head != null) {
                decList.head.accept(this, level);
            }
            decList = decList.tail;
        }
    }

    public void visit(VarDecList varDecList, int level) {
        while (varDecList != null) {
            if (varDecList.head != null) {
                varDecList.head.accept(this, level);
            }
            varDecList = varDecList.tail;
        }
    }

    public void visit(ExpList expList, int level) {
        while (expList != null) {
            if (expList.head != null) {
                expList.head.accept(this, level);
            }
            expList = expList.tail;
        }
    }
}
