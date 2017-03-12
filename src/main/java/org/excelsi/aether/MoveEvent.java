package org.excelsi.aether;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Typed;
import org.excelsi.matrix.Bot;


public class MoveEvent extends BotChangeEvent<NHSpace> {
    public MoveEvent(Typed source, NHBot b, NHSpace from, NHSpace to) {
        super(source, b, from, to);
    }
}
