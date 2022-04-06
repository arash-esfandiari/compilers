package absyn.Exps;

import absyn.AbsynVisitor;

public class NilExp extends Exp {
    public NilExp(int row, int col) {
        super(row, col);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
