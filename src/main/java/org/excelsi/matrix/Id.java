package org.excelsi.matrix;


/**
 * Imbues objects with unique identifiers.
 */
public abstract class Id implements Typed {
    private static long _nextId;

    private final String _id;


    public Id() {
        _id = nextId();
    }

    @Override public String getId() {
        return _id;
    }

    private static String nextId() {
        return Long.toString(_nextId++);
    }
}
