package org.excelsi.aether;


import java.util.List;

import org.excelsi.matrix.Typed;
import org.excelsi.matrix.MSpace;


public class SpaceKnowledgeEvent extends KnowledgeEvent {
    private final List<MSpace> _spaces;


    public SpaceKnowledgeEvent(Typed source, final Stage stage, final NHBot b, final String kind, final List<MSpace> ms) {
        super(source, stage, b, kind);
        _spaces = ms;
    }

    public List<MSpace> getSpaces() {
        return _spaces;
    }

    @Override public String toString() {
        return "SpaceKnowledgeEvent::{source:"+getSource()+", kind:"+getKind()+", bot:"+getBot()+", spaces:"+_spaces+"}";
    }
}
