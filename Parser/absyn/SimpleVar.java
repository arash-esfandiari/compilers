package absyn;

public class SimpleVar extends Var {

    public SimpleVar(int row, int col, String name) {
        super(row, col, name);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
