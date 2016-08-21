package org.excelsi.aether;


public class MenuItem<E> {
    private final String _key;
    private final String _description;
    private final E _item;


    public MenuItem(String key, String description, E item) {
        _key = key;
        _description = description;
        _item = item;
    }

    public String key() {
        return _key;
    }

    public String description() {
        return _description;
    }

    public E item() {
        return _item;
    }
}
