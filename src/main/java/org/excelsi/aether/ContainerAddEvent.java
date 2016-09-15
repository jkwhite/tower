package org.excelsi.aether;


import org.excelsi.matrix.Bot;
import org.excelsi.matrix.Direction;
import org.excelsi.aether.Item;
import org.excelsi.aether.NHSpace;


public class ContainerAddEvent extends AddEvent<NHSpace,Item> {
    public enum Type { dropped, added };
    private final int _idx;
    private final boolean _inc;
    private final Type _type;


    public ContainerAddEvent(NHSpace source, Item i, int idx, boolean incremented, Type type) {
        super(source, "container", source, i);
        _idx = idx;
        _inc = incremented;
        _type = type;
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

    public Type getAddType() {
        return _type;
    }
}
