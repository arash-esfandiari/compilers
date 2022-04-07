package absyn;

import absyn.Decs.*;
import absyn.Exps.*;
import absyn.Lists.*;
import absyn.VarDecs.*;
import absyn.Vars.*;

public interface AbsynVisitor {
    public void visit(NameTy ty, int level, boolean isAddress);

    public void visit(SimpleVar ty, int level, boolean isAddress);

    public void visit(IndexVar ty, int level, boolean isAddress);

    public void visit(NilExp exp, int level, boolean isAddress);

    public void visit(VarExp exp, int level, boolean isAddress);

    public void visit(IntExp exp, int level, boolean isAddress);

    public void visit(CallExp exp, int level, boolean isAddress);

    public void visit(OpExp exp, int level, boolean isAddress);

    public void visit(AssignExp exp, int level, boolean isAddress);

    public void visit(IfExp exp, int level, boolean isAddress);

    public void visit(WhileExp exp, int level, boolean isAddress);

    public void visit(ReturnExp exp, int level, boolean isAddress);

    public void visit(CompoundExp exp, int level, boolean isAddress);

    public void visit(FunctionDec dec, int level, boolean isAddress);

    public void visit(SimpleDec dec, int level, boolean isAddress);

    public void visit(ArrayDec dec, int level, boolean isAddress);

    public void visit(DecList dec, int level, boolean isAddress);

    public void visit(VarDecList dec, int level, boolean isAddress);

    public void visit(ExpList dec, int level, boolean isAddress);
}
