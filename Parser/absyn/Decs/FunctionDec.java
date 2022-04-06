package absyn.Decs;

import absyn.AbsynVisitor;
import absyn.NameTy;
import absyn.Exps.CompoundExp;
import absyn.Lists.VarDecList;

public class FunctionDec extends Dec {
    public String func;
    public VarDecList params;
    public CompoundExp body;

    public FunctionDec(int row, int col, NameTy result, String func, VarDecList params, CompoundExp body) {
        this.row = row;
        this.col = col;
        this.typ = result;
        this.func = func;
        this.params = params;
        this.body = body;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
