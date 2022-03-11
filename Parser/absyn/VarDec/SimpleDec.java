package absyn;

public class SimpleDec extends VarDec {

    public SimpleDec(int row, int col, NameTy typ, String name) {
        super(row, col, typ, name);
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
