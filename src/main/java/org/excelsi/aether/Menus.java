package org.excelsi.aether;


import java.util.List;
import java.util.ArrayList;


public class Menus {
    public static SelectionMenu<Item> asMenu(final ItemConstraints c, final NHBot b) {
        return asMenu(c, b, false);
    }

    public static SelectionMenu<Item> asMenu(final ItemConstraints c, final NHBot b, final boolean remove) {
        List<MenuItem> is = new ArrayList<>();
        for(Item i:c.getContainer().getItem()) {
            if(c.getFilter().accept(i, b)) {
                is.add(new MenuItem<Item>(c.getContainer().keyFor(i), i.toString(), i));
            }
        }
        return new SelectionMenu<Item>(remove, is.toArray(new MenuItem[0]));
    }
}
