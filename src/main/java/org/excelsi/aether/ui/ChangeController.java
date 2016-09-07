package org.excelsi.aether.ui;


import org.excelsi.aether.Event;
import org.excelsi.aether.AddEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.RemoveEvent;


public abstract class ChangeController<C,E> extends Enloggened implements Controller {
    @Override public final void handle(final SceneContext c, final Event e) {
        if(e instanceof ChangeEvent) {
            changed(c, (ChangeEvent)e);
        }
        else if(e instanceof AddEvent) {
            added(c, (AddEvent)e);
        }
        else if(e instanceof RemoveEvent) {
            removed(c, (RemoveEvent)e);
        }
    }

    abstract protected void added(SceneContext c, AddEvent<C,E> e);
    abstract protected void removed(SceneContext c, RemoveEvent<C,E> e);
    abstract protected void changed(SceneContext c, ChangeEvent<C,E> e);
}
