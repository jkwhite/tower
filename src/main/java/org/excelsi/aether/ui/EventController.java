package org.excelsi.aether.ui;


import org.excelsi.aether.Event;


// NEXT: EventController -> Controller; Controller -> ChangeController;
// standardize factories
public interface EventController<E extends Event> {
    void handle(E e);
}
