package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class ActionEvent extends Event {
    private final Action _a;


    public ActionEvent(Typed source, Action a) {
        super(source);
        _a = a;
    }

    public String getType() {
        return "action";
    }

    public Action action() {
        return _a;
    }

    public Action getAction() {
        return action();
    }

    @Override public String toString() {
        return "action::{a:"+_a+"}";
    }
}
