package org.excelsi.aether.ui.jfx;


import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;

import org.excelsi.aether.ui.Hud;
import org.excelsi.aether.ui.LogicEvent;


public abstract class HudNode extends Group implements Hud {
    private String _transition;
    private List<EventHandler<LogicEvent>> _logicHandlers;


    public HudNode() {
    }

    public HudNode(final String style) {
        getStyleClass().add(style);
    }

    public final void addLogicHandler(final EventHandler<LogicEvent> h) {
        if(_logicHandlers==null) {
            _logicHandlers = new CopyOnWriteArrayList<>();
        }
        _logicHandlers.add(h);
    }

    public final void removeLogicHandler(final EventHandler<LogicEvent> h) {
        _logicHandlers.remove(h);
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

    protected final void addModalHandler(final Object notify, final Node remove) {
        addLogicHandler(new EventHandler<LogicEvent>() {
            @Override public void handle(LogicEvent le) {
                le.consume();
                getChildren().remove(remove);
                removeLogicHandler(this);
                synchronized(notify) {
                    notify.notify();
                }
            }
        });
    }
}
