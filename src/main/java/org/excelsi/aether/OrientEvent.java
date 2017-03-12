package org.excelsi.aether;


import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;
import org.excelsi.matrix.Typed;


public class OrientEvent extends BotChangeEvent<Direction> {
    public OrientEvent(Typed source, NHBot b, Direction from, Direction to) {
        super(source, b, from, to);
    }
}
