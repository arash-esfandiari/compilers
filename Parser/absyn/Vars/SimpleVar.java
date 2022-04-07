package absyn.Vars;

import absyn.AbsynVisitor;

public class SimpleVar extends Var {

    public SimpleVar(int row, int col, String name) {
        super(row, col, name);
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
