package org.excelsi.aether.ui;


import org.excelsi.aether.Event;


@FunctionalInterface
public interface ControllerFactory {
    public static ControllerFactory constant(final Controller c) {
        return (e)->{return c;};
    }

    Controller createController(Event e);
}
