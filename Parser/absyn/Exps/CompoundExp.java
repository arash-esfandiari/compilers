package absyn.Exps;

import absyn.AbsynVisitor;
import absyn.Lists.ExpList;
import absyn.Lists.VarDecList;

public class CompoundExp extends Exp {

    public VarDecList decs;
    public ExpList exps;

    public CompoundExp(int row, int col, VarDecList decs, ExpList exps) {
        super(row, col);
        this.decs = decs;
        this.exps = exps;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
