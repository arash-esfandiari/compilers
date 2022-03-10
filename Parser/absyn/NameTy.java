package absyn;

public class NameTy extends Absyn {

    public NameTy(int row, int col, int typ) {
        this.row = row;
        this.col = col;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
