package org.excelsi.aether.ui;


import org.excelsi.aether.AddEvent;
import org.excelsi.aether.ChangeEvent;


public interface Controller<E> {
    void added(SceneContext c, AddEvent<E> e);
    void removed(SceneContext c, E e);
    void changed(SceneContext c, ChangeEvent<E> e);
}
