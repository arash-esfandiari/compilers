package absyn.Exps;

import absyn.AbsynVisitor;

public class AssignExp extends Exp {
    public VarExp lhs;
    public Exp rhs;

    public AssignExp(int row, int col, VarExp lhs, Exp rhs) {
        super(row, col);
        this.lhs = lhs;
        this.rhs = rhs;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
