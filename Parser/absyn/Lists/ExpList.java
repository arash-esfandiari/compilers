package absyn.Lists;

import absyn.Absyn;
import absyn.AbsynVisitor;
import absyn.Exps.Exp;

public class ExpList extends Absyn {
    public Exp head;
    public ExpList tail;

    public ExpList(Exp head, ExpList tail) {
        this.head = head;
        this.tail = tail;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
