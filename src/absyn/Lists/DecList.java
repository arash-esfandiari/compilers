package absyn.Lists;

import absyn.Absyn;
import absyn.AbsynVisitor;
import absyn.Decs.Dec;

public class DecList extends Absyn {
    public Dec head;
    public DecList tail;

    public DecList(Dec head, DecList tail) {
        this.head = head;
        this.tail = tail;
    }

    public void accept(AbsynVisitor visitor, int level, boolean isAddress) {
        visitor.visit(this, level, isAddress);
    }
}
