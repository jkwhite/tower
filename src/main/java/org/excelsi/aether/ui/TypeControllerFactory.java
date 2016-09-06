package org.excelsi.aether.ui;


import java.util.Map;

import org.excelsi.aether.Event;


public class TypeControllerFactory extends Enloggened implements ControllerFactory {
    private final Map<String,ControllerFactory> _cfs;


    public TypeControllerFactory(Map<String,ControllerFactory> cfs) {
        _cfs = cfs;
    }

    @Override public Controller createController(final Event e) {
        final ControllerFactory cf = _cfs.get(e.getType());
        return cf!=null?cf.createController(e):null;
    }
}
