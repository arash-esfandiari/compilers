package absyn.Lists;

import absyn.Absyn;
import absyn.AbsynVisitor;
import absyn.VarDecs.VarDec;

public class VarDecList extends Absyn {
    public VarDec head;
    public VarDecList tail;

    public VarDecList(VarDec head, VarDecList tail) {
        this.head = head;
        this.tail = tail;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
