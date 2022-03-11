package absyn;

public interface AbsynVisitor {
    public void visit(NameTy ty, int level);

    public void visit(SimpleVar ty, int level);

    public void visit(IndexVar ty, int level);

    public void visit(NilExp exp, int level);

    public void visit(VarExp exp, int level);

    public void visit(IntExp exp, int level);

    public void visit(CallExp exp, int level);

    public void visit(OpExp exp, int level);

    public void visit(AssignExp exp, int level);

    public void visit(IfExp exp, int level);

    public void visit(WhileExp exp, int level);

    public void visit(ReturnExp exp, int level);

    public void visit(CompoundExp exp, int level);

    public void visit(FunctionDec dec, int level);

    public void visit(SimpleDec dec, int level);

    public void visit(ArrayDec dec, int level);

    public void visit(DecList dec, int level);

    public void visit(VarDecList dec, int level);

    public void visit(ExpList dec, int level);
}
