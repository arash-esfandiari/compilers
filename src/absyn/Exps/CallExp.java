package absyn.Exps;

import absyn.AbsynVisitor;
import absyn.Lists.ExpList;

public class CallExp extends Exp {
    public String func;
    public ExpList args;

    public CallExp(int row, int col, String func, ExpList args) {
        super(row, col);
        this.func = func;
        this.args = args;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
