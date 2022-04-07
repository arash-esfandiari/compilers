package absyn.VarDecs;

import absyn.NameTy;
import absyn.Decs.Dec;

abstract public class VarDec extends Dec {
    public String name;
    public int offset;
    public int nestLevel;

    public VarDec(int row, int col, NameTy typ, String name) {
        this.row = row;
        this.col = col;
        this.typ = typ;
        this.name = name;
    }
}
