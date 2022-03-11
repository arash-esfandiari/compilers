package absyn;

abstract class Var extends Absyn {
    public String name;

    public Var(int row, int col, String name) {
        this.name = name;
        this.row = row;
        this.col = col;
    }
}
