package org.excelsi.aether;


import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHSpace;


public class ContainerAddEvent extends AddEvent<NHSpace,Item> {
    private final int _idx;
    private final boolean _inc;


    public ContainerAddEvent(NHSpace source, Item i, int idx, boolean incremented) {
        super(source, "container", source, i);
        _idx = idx;
        _inc = incremented;
    }

    @Override public String getType() {
        return "item";
    }

    public int getIndex() {
        return _idx;
    }

    public boolean getIncremented() {
        return _inc;
    }
}
