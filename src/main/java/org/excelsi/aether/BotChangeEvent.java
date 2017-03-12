package org.excelsi.aether;


import org.excelsi.matrix.Typed;


public class BotChangeEvent<E> extends ChangeEvent<NHBot,E> {
    private final NHBot _b;


    public BotChangeEvent(Typed source, NHBot b, E from, E to) {
        super(source, "bot", b, from, to);
        _b = b;
    }

    public NHBot getBot() {
        return _b;
    }
}
