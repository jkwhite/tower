package org.excelsi.aether;


import java.util.List;

import org.excelsi.matrix.Typed;
import org.excelsi.matrix.Bot;


public class BotKnowledgeEvent extends KnowledgeEvent {
    private final List<Bot> _bots;


    public BotKnowledgeEvent(Typed source, final Stage stage, final NHBot b, final String kind, final List<Bot> bots) {
        super(source, stage, b, kind);
        _bots = bots;
    }

    public List<Bot> getBots() {
        return _bots;
    }

    @Override public String toString() {
        return "BotKnowledgeEvent::{source:"+getSource()+", kind:"+getKind()+", bot:"+getBot()+", bots:"+_bots+"}";
    }
}
