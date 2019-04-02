package org.excelsi.aether.ui.lemur;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import com.jme3.scene.Node;
import org.excelsi.aether.Event;
import org.excelsi.aether.EventBus;
import org.excelsi.aether.ui.SceneContext;


public class Dispatcher extends Enloggened implements EventBus.Handler {
    private final Node _gui;
    private final SceneContext _ctx;
    private final Map<String,List<Controller>> _controls;


    public Dispatcher(Node gui, SceneContext ctx) {
        _gui = gui;
        _ctx = ctx;
        _controls = new HashMap<>();
    }

    public void attach(String type, Controller c) {
        List<Controller> cs = _controls.get(type);
        if(cs==null) {
            cs = new LinkedList<Controller>();
            _controls.put(type, cs);
        }
        cs.add(c);
    }

    public void detach(String type, Controller c) {
        if(_controls.containsKey(type)) {
            _controls.get(type).remove(c);
        }
    }

    @Override public void handleEvent(Event e) {
        l().info("dispatching "+e);
        List<Controller> cs = _controls.get(e.getType());
        if(cs!=null) {
            for(Controller c:cs) {
                l().info("dispatching to "+c);
                if(c.consume(e, _gui, _ctx)) {
                    l().info("consumed; halting");
                    break;
                }
            }
        }
        else {
            l().info("no controllers");
        }
    }
}
