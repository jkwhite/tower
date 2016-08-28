package org.excelsi.aether;


public class BotChangeEvent<E> extends ChangeEvent<NHBot,E> {
    private final NHBot _b;


    public BotChangeEvent(Object source, NHBot b, E from, E to) {
        super(source, "bot", b, from, to);
        _b = b;
    }

    public NHBot getBot() {
        return _b;
    }
}
