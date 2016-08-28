package org.excelsi.aether.ui;


import org.excelsi.aether.AddEvent;
import org.excelsi.aether.ChangeEvent;
import org.excelsi.aether.RemoveEvent;


public interface Controller<C,E> {
    void added(SceneContext c, AddEvent<C,E> e);
    void removed(SceneContext c, RemoveEvent<C,E> e);
    void changed(SceneContext c, ChangeEvent<C,E> e);
}
