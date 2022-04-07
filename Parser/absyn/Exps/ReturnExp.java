package absyn.Exps;

import absyn.AbsynVisitor;

public class ReturnExp extends Exp {

    public Exp exp;

    public ReturnExp(int row, int col, Exp exp) {
        super(row, col);
        this.exp = exp;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
