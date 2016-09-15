package org.excelsi.aether.ui;


import javafx.event.EventType;

import org.excelsi.aether.Event;


public final class LogicEvent extends javafx.event.Event {
    public static final EventType<LogicEvent> TYPE = new EventType<>("LOGIC");
    private final SceneContext _c;
    private final Event _e;


    public LogicEvent(final SceneContext c, final Event e) {
        super(TYPE);
        _c = c;
        _e = e;
    }

    public Event e() {
        return _e;
    }

    public Event getE() {
        return e();
    }

    public SceneContext ctx() {
        return _c;
    }

    @Override public String toString() {
        return "logic::{e:"+_e+"}";
    }

    public void consume() {
        //System.err.println("**** CONSUMED **** "+this);
        super.consume();
    }
}
