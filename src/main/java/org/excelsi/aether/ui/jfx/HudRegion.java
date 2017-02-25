package org.excelsi.aether.ui.jfx;


import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import org.excelsi.aether.ui.Hud;
import org.excelsi.aether.ui.LogicEvent;


public abstract class HudRegion extends Region implements Hud {
    private final Logger _logger = LoggerFactory.getLogger(getClass());
    private String _transition;
    private List<EventHandler<LogicEvent>> _logicHandlers;


    public HudRegion() {
    }

    public final void addLogicHandler(final EventHandler<LogicEvent> h) {
        if(_logicHandlers==null) {
            _logicHandlers = new ArrayList<>(1);
        }
        _logicHandlers.add(h);
    }

    @Override public final void onEvent(LogicEvent le) {
        if(_logicHandlers!=null) {
            for(EventHandler<LogicEvent> h:_logicHandlers) {
                h.handle(le);
            }
        }
    }

    public void setTransition(final String t) {
        _transition = t;
    }

    public String getTransition() {
        return _transition;
    }

    protected void transition(final Region n, final EventHandler<ActionEvent> onFinished) {
        if(null!=_transition) {
            Transitions.transition(_transition, n, onFinished);
        }
        else if(null!=onFinished) {
            onFinished.handle(null);
        }
    }

    protected final void addModalHandler(final Object notify) {
        addLogicHandler((le)->{
            le.consume();
            ((Group)getParent()).getChildren().remove(this);
            synchronized(notify) {
                notify.notify();
            }
        });
    }

    protected final Logger log() {
        return _logger;
    }
}
