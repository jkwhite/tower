package org.excelsi.aether.ui;


import java.util.Map;

import org.excelsi.aether.Event;


public class InstanceControllerFactory extends Enloggened implements ControllerFactory {
    private final Map<Class,ControllerFactory> _cfs;


    public InstanceControllerFactory(Map<Class,ControllerFactory> cfs) {
        _cfs = cfs;
    }

    @Override public Controller createController(final Event e) {
        final ControllerFactory cf = _cfs.get(e.getClass());
        if(cf==null) {
            log().error("unhandled event type: "+e);
        }
        return cf.createController(e);
    }
}
