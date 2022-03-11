import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

    final static int SPACES = 4;

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            System.out.print(" ");
    }

    public void visit(NameTy nameTy, int level) {
        indent(level);
        System.out.println("NameTy: " + nameTy.typ);
    }

    public void visit(SimpleVar simpleVar, int level) {
        indent(level);
        System.out.println("SimpleVar: " + simpleVar.name);
    }

    public void visit(IndexVar indexVar, int level) {
        indent(level);
        System.out.println("IndexVar: " + indexVar.name);
        System.out.println("IndexVar: " + indexVar.index);
    }

    public void visit(ExpList expList, int level) {
        while (expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        }
    }

    public void visit(DecList decList, int level) {
        while (decList != null) {
            decList.head.accept(this, level);
            decList = decList.tail;
        }
    }

    public void visit(VarDecList varDecList, int level) {
        while (varDecList != null) {
            varDecList.head.accept(this, level);
            varDecList = varDecList.tail;
        }
    }

    public void visit(AssignExp exp, int level) {
        indent(level);
        System.out.println("AssignExp:");
        level++;
        exp.lhs.accept(this, level);
        exp.rhs.accept(this, level);
    }

    public void visit(IfExp exp, int level) {
        indent(level);
        System.out.println("IfExp:");
        level++;
        exp.test.accept(this, level);
        exp.thenpart.accept(this, level);
        if (exp.elsepart != null)
            exp.elsepart.accept(this, level);
    }

    public void visit(IntExp exp, int level) {
        indent(level);
        System.out.println("IntExp: " + exp.value);
    }

    public void visit(OpExp exp, int level) {
        indent(level);
        System.out.print("OpExp:");
        switch (exp.op) {
            case OpExp.PLUS:
                System.out.println(" + ");
                break;
            case OpExp.MINUS:
                System.out.println(" - ");
                break;
            case OpExp.TIMES:
                System.out.println(" * ");
                break;
            case OpExp.DIV:
                System.out.println(" / ");
                break;
            case OpExp.EQ:
                System.out.println(" = ");
                break;
            case OpExp.NEQ:
                System.out.println(" != ");
                break;
            case OpExp.LT:
                System.out.println(" < ");
                break;
            case OpExp.LTEQ:
                System.out.println(" <= ");
                break;
            case OpExp.GT:
                System.out.println(" > ");
                break;
            case OpExp.GTEQ:
                System.out.println(" >= ");
                break;
            default:
                System.out.println("Unrecognized operator at line " + exp.row + " and column " + exp.col);
        }
        level++;
        exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void visit(VarExp varExp, int level) {
        indent(level);
        System.out.println("VarExp: ");
    }

    public void visit(NilExp nilExp, int level) {
        indent(level);
        System.out.println("NilExp: ");
    }

    public void visit(CallExp callExp, int level) {
        indent(level);
        System.out.print("CallExp:");

    }

    public void visit(WhileExp exp, int level) {

    }

    public void visit(ReturnExp exp, int level) {

    }

    public void visit(CompoundExp exp, int level) {

    }

    public void visit(FunctionDec exp, int level) {

    }

    public void visit(SimpleDec exp, int level) {

    }

    public void visit(ArrayDec exp, int level) {

    }

}
