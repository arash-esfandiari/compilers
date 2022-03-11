import absyn.*;

public class ShowTreeVisitor implements AbsynVisitor {

    /* Indentation */
    final static int SPACES = 4;

    private void indent(int level) {
        for (int i = 0; i < level * SPACES; i++)
            System.out.print(" ");
    }

    /* Types */
    public void visit(NameTy ty, int level) {
        String type;

        if (ty.typ == 0) {
            type = "VOID";
        } else if (ty.typ == 1) {
            type = "INT";
        } else {
            type = "NONE";
        }

        System.out.print(type);
    }

    /************************** Declarations **************************/

    public void visit(DecList DecList, int level) {
        while (DecList != null) {
            DecList.head.accept(this, level);
            DecList = DecList.tail;
        }
    }

    public void visit(VarDecList VarDecList, int level) {
        while (VarDecList != null) {
            VarDecList.head.accept(this, level);
            VarDecList = VarDecList.tail;
        }
    }

    public void visit(SimpleDec dec, int level) {
        indent(level);
        System.out.print("SimpleDec: ");
        dec.typ.accept(this, level);
        System.out.println(" " + dec.name);

    }

    public void visit(ArrayDec dec, int level) {
        indent(level);
        System.out.print("ArrayDec: ");
        dec.typ.accept(this, level);
        System.out.print(" " + dec.name + " ");
        dec.size.accept(this, level);

    }

    public void visit(FunctionDec dec, int level) {
        indent(level);
        System.out.print("FunctionDec: ");
        level++;
        dec.result.accept(this, level);
        System.out.println(" " + dec.func + " ");
        // Print Params:
        dec.params.accept(this, ++level);
        dec.body.accept(this, level);

    }

    /************************** Expressions **************************/

    public void visit(ExpList expList, int level) {
        while (expList != null) {
            expList.head.accept(this, level);
            expList = expList.tail;
        }
    }

    public void visit(NilExp exp, int level) {
        indent(level);
        System.out.println("NilExp: ");
    }

    public void visit(IntExp exp, int level) {
        indent(level);
        System.out.println("IntExp: " + exp.value);
    }

    public void visit(VarExp exp, int level) {
        indent(level);
        System.out.println("VarExp: ");
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
        if (exp.elsepart != null) {
            exp.elsepart.accept(this, level);
        }
    }

    public void visit(WhileExp exp, int level) {
        indent(level);
        System.out.println("WhileExp:");
        level++;
        exp.test.accept(this, level);
        exp.body.accept(this, level);

    }

    public void visit(OpExp exp, int level) {
        indent(level);
        System.out.println("OpExp:");

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
            case OpExp.GT:
                System.out.println(" > ");
                break;
            case OpExp.GTEQ:
                System.out.println(" >= ");
                break;
            case OpExp.LT:
                System.out.println(" < ");
                break;
            case OpExp.LTEQ:
                System.out.println(" <= ");
                break;
            default:
                System.out.println("Unrecognized operator at line " + exp.row + " and column " + exp.col);
        }
        level++;
        exp.left.accept(this, level);
        exp.right.accept(this, level);
    }

    public void visit(ReturnExp exp, int level) {
        indent(level);
        System.out.println("ReturnExp: " + exp.exp);
    }

    public void visit(CallExp exp, int level) {
        indent(level);
        System.out.println("CallExp: " + exp.func);
        exp.args.accept(this, level);
    }

    public void visit(CompoundExp exp, int level) {
        indent(level);
        System.out.println("CompoundExp: ");
        exp.decs.accept(this, level);
        exp.exps.accept(this, level);
    }

    /************************** Vars **************************/

    public void visit(SimpleVar simpleVar, int level) {
        indent(level);
        System.out.println("SimpleVar: " + simpleVar.name);
    }

    public void visit(IndexVar indexVar, int level) {
        indent(level);
        System.out.println("IndexVar: " + indexVar.name);
        indexVar.index.accept(this, level);
    }
}