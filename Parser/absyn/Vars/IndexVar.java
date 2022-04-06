package absyn.Vars;

import absyn.AbsynVisitor;
import absyn.Exps.Exp;

public class IndexVar extends Var {
    public Exp index;

    public IndexVar(int row, int col, String name, Exp index) {
        super(row, col, name);
        this.index = index;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
