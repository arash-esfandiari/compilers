package absyn.Exps;

import absyn.AbsynVisitor;

public class IntExp extends Exp {
    public int value;

    public IntExp(int row, int col, int value) {
        super(row, col);
        this.value = value;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
