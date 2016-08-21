package org.excelsi.aether;


import org.excelsi.matrix.MSpace;
import org.excelsi.matrix.Bot;


public class MoveEvent extends BotChangeEvent<MSpace> {
    public MoveEvent(Object source, Bot b, MSpace from, MSpace to) {
        super(source, b, from, to);
    }
}
