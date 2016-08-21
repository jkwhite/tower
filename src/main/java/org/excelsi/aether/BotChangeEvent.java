package org.excelsi.aether;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;


public class BotChangeEvent<E> extends ChangeEvent<E> {
    private final Bot _b;


    public BotChangeEvent(Object source, Bot b, E from, E to) {
        super(source, "bot", from, to);
        _b = b;
    }

    public Bot getBot() {
        return _b;
    }
}
