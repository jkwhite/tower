package org.excelsi.aether;


import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;


public class OrientEvent extends BotChangeEvent<Direction> {
    public OrientEvent(Object source, NHBot b, Direction from, Direction to) {
        super(source, b, from, to);
    }
}
