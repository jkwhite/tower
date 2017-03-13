package org.excelsi.aether.ui.jfx;


import java.util.LinkedList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.excelsi.matrix.Typed;
import org.excelsi.aether.Event;


public class Messages {
    public static final int UI_PX_OFFSET = 38;
    public static final int TIME_MS_OFFSET = 500;

    private static final Messages MESSAGES = new Messages();

    private final Map<Typed,List<Event>> _stacks = new HashMap<>();


    private Messages() {}

    public static Messages instance() {
        return MESSAGES;
    }

    public synchronized int stack(final Event e) {
        List<Event> ms = _stacks.get(e.getSource());
        if(ms==null) {
            ms = new LinkedList<>();
            _stacks.put(e.getSource(), ms);
        }
        ms.add(e);
        return ms.size()-1;
    }

    public synchronized void unstack(final Event e) {
        List<Event> ms = _stacks.get(e.getSource());
        if(ms!=null) {
            ms.remove(e);
            if(ms.isEmpty()) {
                _stacks.remove(e.getSource());
            }
        }
    }
}
