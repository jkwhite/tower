package org.excelsi.aether;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;


public class BotAttributeChangeEvent<E> extends ChangeEvent<Bot,E> {
    private final Bot _b;
    private final String _attr;


    public BotAttributeChangeEvent(Object source, Bot b, String attr, E from, E to) {
        super(source, "bot", b, from, to);
        _b = b;
        _attr = attr;
    }

    public Bot getBot() {
        return _b;
    }

    public String getAttribute() {
        return _attr;
    }
}
