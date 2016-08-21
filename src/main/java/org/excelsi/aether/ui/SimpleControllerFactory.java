package org.excelsi.aether.ui;


import org.excelsi.aether.Level;
import org.excelsi.aether.Event;


public class SimpleControllerFactory implements ControllerFactory {
    @Override public Controller createController(final Event e) {
        switch(e.getType()) {
            case "bot":
                return new BotController();
            case "level":
                return new LevelController();
            default:
        }
        throw new IllegalArgumentException("uncontrollable type "+e.getType());
    }
}
